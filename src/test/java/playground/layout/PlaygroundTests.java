package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.constraints.AssertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import playground.layout.to.ActivityTO;
import playground.layout.to.ElementTO;
import playground.layout.to.UserTO;
import playground.logic.Entities.Element.ElementEntity;
import playground.logic.Entities.User.UserEntity;
import playground.logic.helpers.Role;
import playground.logic.services.ElementService;
import playground.logic.services.UserService;
import playground.plugins.Feedback;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PlaygroundTests {
	
	@LocalServerPort
	private int port;
	
	private String url;
	private String usersUrl;
	
	private String authManagerEmail;
	private String authPlayerEmail;
	private String authUserPlayground;

	private RestTemplate restTemplate;

    @Autowired
    private ApplicationContext applicationContext;
	
	@Autowired
	private ElementService elementService;

	@Autowired
	private UserService userService;
	
	//@Autowired
	//private ElementIdGeneratorDao elementIdGeneratorDao;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + this.port + "/playground/elements";
		this.usersUrl = "http://localhost:" + this.port + "/playground/users";
		
		//Details for Manager and Player
		//Used for validation on elements operation 
		this.authManagerEmail = "manager@user.com";
		this.authPlayerEmail = "player@user.com";
		this.authUserPlayground = "playground";
	}
	
	private void checkHttpStatusCode(HttpStatus current,HttpStatus expected,String exceptionMessage) throws Exception
	{
		if(current == expected)
		{
			throw new OKException(exceptionMessage);
		}
		throw new BadException(exceptionMessage);
	}
	
	
	@Before
	public void before() throws SQLException
	{
		this.elementService.cleanAll();
		this.userService.cleanAll();
		
		//DbTestUtil.resetAutoIncrementColumns(applicationContext, "element_id_generator");
		//Reset the auto increment element id column
		//DbTestUtil.resetAutoIncrementColumns(applicationContext, "element_id_generator");
		//this.elementIdGeneratorDao.deleteAll();
	}
	
	@After
	public void after() throws SQLException {
		this.elementService.cleanAll();
		this.userService.cleanAll();

		//DbTestUtil.resetAutoIncrementColumns(applicationContext, "element_id_generator");
		//this.elementIdGeneratorDao.deleteAll();
	}
	

	@Test
	public void testServerIsBootingCorrectly() throws Exception {

	}

	//Elements tests
	
	@Test
	public void testPostElementSuccessfully() throws Throwable{
		
		Map<String,Object> attributes = new HashMap<String,Object>();
		attributes.put("testKey","testValue");
		
		//Given - 
		//User of type manager exist
		//No elements exist
		createAuthroizedUser(Role.MANAGER,authManagerEmail);	
		
		//When
		ElementTO elementTo = new ElementTO();
		elementTo.setName("element1");
		elementTo.setType("Ad Board");
		elementTo.setAttributes(attributes);
		ElementTO actualReturnedValue = restTemplate.postForObject(url + "/{userPlayground}/{email}",elementTo,ElementTO.class,authUserPlayground,authManagerEmail);

		//Then
		//TODO - Michael - Think about id checking 
 		assertThat(actualReturnedValue)
		.isNotNull()
		.extracting("name","type","playground","creatorPlayground","creatorEmail")
		.containsExactly(elementTo.getName(),elementTo.getType(),"playground",
				authUserPlayground,authManagerEmail);
	}


	@Test(expected=OKException.class)
	public void testPostElementByUnauthorizedUser() throws Throwable{

		//Given - 
		//User of type PLAYER exist
		//No elements exist
		createAuthroizedUser(Role.PLAYER,authManagerEmail);
		
		//When
		ElementTO elementTo = new ElementTO();
		elementTo.setName("element1");
		elementTo.setType("Ad Board");
		try {
			restTemplate.postForObject(url + "/{playground}/{email}",elementTo,ElementTO.class,authUserPlayground,authManagerEmail);
		}
		catch (HttpClientErrorException e) {
			HttpStatus httpStatus = e.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.UNAUTHORIZED,e.getMessage());
		}
		//Then The response is status <> 2xx		
	}

	//TODO - Michael - Add test for post element without existing user - Add gerkin accordingly

	@Test
	@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
	public void testUpdateElementSuccessfully() throws Throwable{
		//Given
		//Database contain user of type manager
		//Database contain element
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");

		elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);
		
		//When
		ElementTO elementTo = new ElementTO();
		elementTo.setName("element1");
		elementTo.setType("Quiz");
		restTemplate.put(url + "/{playground}/{email}/{playground}/{id}",
				elementTo, authUserPlayground,authManagerEmail,"playground","1");
		
		//Then the status code is 200
		ElementEntity actualReturnedValue = this.elementService.getElementById(authManagerEmail, 
				authUserPlayground, "playground", "1");
 		assertThat(actualReturnedValue)
		.isNotNull()
		.extracting("name","type","playground","creatorPlayground","creatorEmail")
		.containsExactly(elementTo.getName(),elementTo.getType(),"playground",
				authUserPlayground,authManagerEmail);
		
	}
	
	@Test(expected=OKException.class)
	public void testUpdateNotExistsElement() throws Throwable{
		
		//Given - 
		//User of type MANAGER exist
		//No elements exist
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		
		//When
		ElementTO elementTORequest = new ElementTO();
		elementTORequest.setType("Quiz");
		try {
		restTemplate.put(url + "/{playground}/{email}/{playground}/{id}", 
				elementTORequest, authUserPlayground,authManagerEmail,"playground",1);
		}
		catch(HttpClientErrorException ex)
		{
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_FOUND, ex.getMessage());
		}

		//Then The response is status <> 2xx
	}
	
	//TODO - Michael - Add test for updating not exist element with unauthorized user
	
	@Test
	@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
	public void testGetElementByIdSuccessfully() throws Throwable{
		
		//Given - 
		//Database contains user
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		
		//And database contains element
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);
		
		//When
		ElementTO actuallyReturned = restTemplate.getForObject(url + "/{userPlayground}/{email}/{playground}/{id}",
				ElementTO.class, authUserPlayground,authPlayerEmail,"playground",1);

		//Then
		assertThat(actuallyReturned)
		.isNotNull()
		.extracting("name","type","id","playground","creatorPlayground","creatorEmail")
		.containsExactly(elementEntity.getName(),elementEntity.getType(),"1",authUserPlayground,authUserPlayground,authManagerEmail);

	}
	
	//TODO - Michael - Add test for get element by id with user of type MANAGER

	@Test(expected=OKException.class)
	public void testGetNotExistsElement() throws Throwable{
		
		//Given - 
		//Database contains user
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		//No elements exist
		
		//When
		try {
		restTemplate.getForObject(url + "/{playground}/{email}/{playground}/{id}",
				ElementTO.class, authUserPlayground,authPlayerEmail,"playground",1);
		}
		catch(HttpClientErrorException ex)
		{
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_FOUND, ex.getMessage());
		}

		//Then The response is status <> 2xx
	}

	@Test
	public void testGetAllElementsSuccessfully() throws Throwable{
		
		//Given - 
		//Database contains user
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		//And database contains element
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		
		elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);

		elementEntity.setName("element2");
		elementEntity.setType("Quiz");

		elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);

		//When
		ElementTO[] actuallyReturned = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/all",
				ElementTO[].class, authUserPlayground,authPlayerEmail);

		//Then
		assertThat(actuallyReturned)
		.isNotNull()
		.hasSize(2);
		
		//TODO - Michael - Add test for the 2 elements 
		
	}


	@Test(expected=OKException.class)
	public void testGetAllElementsWithUnauthorizedUser() throws Throwable{
		
		//Given - 
		//No user exists
		//And database contains element
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);
		
		//When
		try {
		restTemplate.getForObject(url + "/{playground}/{email}/all",
				ElementTO[].class, authUserPlayground,authPlayerEmail);
		}
		catch(HttpClientErrorException ex)
		{
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.UNAUTHORIZED, ex.getMessage());
		}

		//Then The response is status <> 2xx
	}

	@Test
	public void testGetAllElementsByLocationAndDistanceSuccessfully() throws Throwable{

		//Given - 
		//Database contains user
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		//And database contains element
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setX(1.0);
		elementEntity.setY(1.0);
		elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);

		//When
		ElementTO[] actuallyReturned = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/near/{x}/{y}/{distance}", 
				ElementTO[].class, 
				authUserPlayground,authPlayerEmail,2,2,3);

		//Then
		assertThat(actuallyReturned)
		.isNotNull()
		.hasSize(1);
		
		//TODO - Michael - Add test for the elements array that return
	}
	
	
	
	@Test(expected=OKException.class)
	public void testGetAllElementsByLocationAndNegativeDistance() throws Throwable{
		
		//Given - 
		//Database contains user
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		//And database contains element
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setX(1.0);
		elementEntity.setY(1.0);

		elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);

		//When
		try {
		restTemplate.getForObject(url + "/{playground}/{email}/near/{x}/{y}/{distance}", 
				ElementTO[].class, 
				authUserPlayground,authPlayerEmail,2,2,-1);
		}
		catch(HttpClientErrorException ex)
		{
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
		}

		//Then The response is status <> 2xx
	}


	@Test
	public void testGetAllElementsByAttributeSuccessfully() throws Throwable{
		//Given - 
		//Database contains user
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		//And database contains element
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setX(1.0);
		elementEntity.setY(1.0);
		Map<String,Object> moreAttributes = new HashMap<String,Object>();
		moreAttributes.put("quizTime", "60");
		elementEntity.setAttributes(moreAttributes);
		elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);

		//When
		ElementTO[] actuallyReturned = restTemplate.getForObject(url + "/{userPlayground}/{email}/search/{attributeName}/{value}", 
				ElementTO[].class, 
				authUserPlayground,authPlayerEmail,"quizTime","60");

		//Then
		assertThat(actuallyReturned)
		.isNotNull()
		.hasSize(1);
	}

	@Test(expected=OKException.class)
	public void  testGetAllElementsWithNonExistingElementValue() throws Throwable{
		//Given - 
		//Database contains user
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		//And database contains element
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Quiz");
		elementEntity.setX(1.0);
		elementEntity.setY(1.0);
		Map<String,Object> moreAttributes = new HashMap<String,Object>();
		moreAttributes.put("quizTime", "60");
		elementEntity.setAttributes(moreAttributes);
		elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);

		//When
		try {
		restTemplate.getForObject(url + "/{playground}/{email}/search/{attributeName}/{value}", 
				ElementTO[].class, 
				authUserPlayground,authPlayerEmail,"color","blue");
		}
		catch(HttpClientErrorException ex)
		{
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_FOUND, ex.getMessage());
		}

		//Then The response is status <> 2xx		
	}

	//Users tests
	
	@Test
	public void testPostUserSuccessfully() {
		//Given - Database is empty

		//When
		NewUserForm newUserForm = new NewUserForm();
		newUserForm.setEmail(authPlayerEmail);
		newUserForm.setUsername("player");
		newUserForm.setRole(Role.PLAYER.name());
		newUserForm.setAvatar("smiley.jpg");
		UserTO userToResponse = this.restTemplate.postForObject(this.usersUrl, newUserForm, UserTO.class);

		//Then
		assertThat(userToResponse)
		.isNotNull()
		.extracting("email", "username", "role","avatar")
		.containsExactly(newUserForm.getEmail(), newUserForm.getUsername(),newUserForm.getRole(),newUserForm.getAvatar());
		
	}

	@Test(expected=OKException.class)
	public void testPostExistsUser() throws Throwable
	{
		//Given
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail(authPlayerEmail);
		userEntity.setUsername("player");
		userEntity.setRole(Role.PLAYER.name());
		userEntity.setAvatar("smiley.jpg");
		this.userService.addUser(userEntity);

		//When
		NewUserForm newUserForm = new NewUserForm();
		newUserForm.setEmail(authPlayerEmail);
		newUserForm.setUsername("player2");
		newUserForm.setRole(Role.PLAYER.name());
		newUserForm.setAvatar("avatar.jpg");
		try {
		this.restTemplate.postForObject(this.usersUrl, newUserForm, UserTO.class);
		}
		catch(HttpClientErrorException ex)
		{
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.CONFLICT, ex.getMessage());
		}

		//Then The response is status <> 2xx
	}

	//TODO - Option - Add tests for post new MANAGER
	
	@Test
	public void testGetUserConfirmSuccessfully() throws Throwable
	{
		//Given - 
		//Database contains user:
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail(authManagerEmail);
		userEntity.setUsername("manager");
		userEntity.setRole(Role.MANAGER.name());
		userEntity.setPlayground("playground");
		userEntity.setAvatar("smiley.jpg");		
		int confirmCode = this.userService.addUser(userEntity).getConfirmCode();

		//When
		UserTO actuallyReturned = this.restTemplate.getForObject(this.usersUrl+"/confirm/{playground}/{email}/{code}", UserTO.class, "playground",authManagerEmail,confirmCode);

		//Then
		assertThat(actuallyReturned)
		.isNotNull()
		.extracting("email", "username","playground", "role","avatar")
		.containsExactly(userEntity.getEmail(), userEntity.getUsername(),userEntity.getPlayground(),userEntity.getRole(),userEntity.getAvatar());
	}

	@Test(expected=OKException.class)
	public void testGetUserConfirmWithInvalidCode() throws Throwable
	{
		//Given
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail(authManagerEmail);
		userEntity.setUsername("manager");
		userEntity.setRole(Role.MANAGER.name());
		userEntity.setPlayground(authUserPlayground);
		int confirmCode = this.userService.addUser(userEntity).getConfirmCode();

		//When
		try
		{
		this.restTemplate.getForObject(this.usersUrl+"/confirm/{playground}/{email}/{code}",
				UserTO.class, authUserPlayground,authManagerEmail,confirmCode - 1);
		}
		catch(HttpClientErrorException ex)
		{
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
		}

		//Then The response is status <> 2xx
	}

	@Test
	public void testGetUserDetailsSuccessfully() throws Throwable
	{
		//Given - 
		//Database contains confirmed user
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail(authPlayerEmail);
		userEntity.setPlayground(authUserPlayground);
		userEntity.setUsername("palyer");
		userEntity.setRole(Role.PLAYER.name());
		int confirmCode = this.userService.addUser(userEntity).getConfirmCode();
		System.err.println("confirm code: " + confirmCode);
		userEntity.setConfirmCode(confirmCode);
		this.userService.confirmUser(userEntity);
		
		//When
		UserTO actuallyReturned = this.restTemplate.getForObject(this.usersUrl+"/login/{playground}/{email}", UserTO.class, authUserPlayground,authPlayerEmail);

		//Then
		assertThat(actuallyReturned)
		.isNotNull()
		.extracting("email", "username","playground","role")
		.containsExactly(userEntity.getEmail(), userEntity.getUsername(),userEntity.getPlayground(),userEntity.getRole());

	}

	@Test(expected=OKException.class)
	public void testGetUserDetailsWithInvalidEmail() throws Exception
	{
		//Given - Database is empty
		
		//When
		try {
		this.restTemplate.getForObject(this.usersUrl+"/login/{playground}/{email}", UserTO.class, "playground","wrong@user.com");
		}
		catch(HttpClientErrorException ex)
		{
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_FOUND, ex.getMessage());
		}
		//Then The response is status <> 2xx
	}
	
	//TODO - Michael - Add test for get user details with existing user (wrong email/unauthorized...)

	@Test
	public void testUpdateUserDetailsSuccessfully() throws Throwable
	{
		//Given - 
		//Database contains user:
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail(authManagerEmail);
		userEntity.setUsername("manager");
		userEntity.setRole(Role.MANAGER.name());
		userEntity.setPlayground(authUserPlayground);
		userEntity.setAvatar("smiley.jpg");
		int confirmCode = this.userService.addUser(userEntity).getConfirmCode();
		userEntity.setConfirmCode(confirmCode);
		this.userService.confirmUser(userEntity);
		
		//When
		UserTO userTO = new UserTO(userEntity);
		userTO.setAvatar("smile.jpg");
		userTO.setUsername("manager2");
		this.restTemplate.put(this.usersUrl+"/{playground}/{email}", userTO, authUserPlayground,authManagerEmail);

		//Then The response is status 2xx
		//TODO - Michael - Add test for the updated user details 

	}

	@Test(expected=OKException.class)
	public void testUpdateUserDetailsWithNonExistingUser() throws Exception{
		//Given - 
		//Database is empty
		
		//When
		UserTO userTO= new UserTO();
		userTO.setEmail("unexistingUser@user.com");
		userTO.setPlayground(authUserPlayground);
		userTO.setRole(Role.PLAYER.name());
		try {
		this.restTemplate.put(this.usersUrl+"/{playground}/{email}", userTO, "playground","unexistingUser@user.com");
		}
		catch(HttpClientErrorException ex)
		{
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_FOUND, ex.getMessage());
		}
		//Then The response is status <> 2xx
	}
	
	@Test
	public void testFindBugInBadCodeActivityPluginSuccessfully() throws Throwable {
		// Given
		createAuthroizedUser(Role.PLAYER, authPlayerEmail);
		createAuthroizedUser(Role.MANAGER, authManagerEmail);
		
		Map<String, Object> attribute = new HashMap<>();
		attribute.put("code", "int x = 5; char a = 'w' + 'z'; double name = \"Hello\"; float num = 5.5;");
		attribute.put("answer", "double name = \"Hello\"");

		ElementEntity element = new ElementEntity();
		element.setName("Bad Code");
		element.setPlayground("playground");
		
		element.setType("FindTheBug");
		element.setAttributes(attribute);
		
		ElementEntity temp = elementService.addNewElement(authManagerEmail, authUserPlayground, element);
		
		// When
		ActivityTO postActivity = new ActivityTO();
		postActivity.setType("FindBugActivity");
		postActivity.setElementId(String.valueOf(temp.getId()));
		postActivity.setElementPlayground(temp.getPlayground());
		
		Map<String, Object> actTemp = new HashMap<>();
		actTemp.put("answer", "double name = \"Hello\"");
		
		Feedback feedback = this.restTemplate.postForObject("/playground/activities/{userPlayground}/{email}", postActivity, Feedback.class, authUserPlayground, authPlayerEmail);
		
		// Then
		assertThat(feedback)
		.isNotNull()
		.extracting("answer")
		.containsExactly(element.getAttributes().get("answer"));
	}

	private void createAuthroizedUser(Role role,String userEmail) throws Throwable {
		UserEntity userEntity = new UserEntity(userEmail,authUserPlayground);
		userEntity.setRole(role.name());
		int confirmCode = this.userService.addUser(userEntity).getConfirmCode();
		userEntity.setConfirmCode(confirmCode);
		this.userService.confirmUser(userEntity);
	}
}