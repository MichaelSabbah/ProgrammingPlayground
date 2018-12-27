package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import playground.layout.to.ElementTO;
import playground.layout.to.UserTO;
import playground.logic.Entities.Element.ElementEntity;
import playground.logic.Entities.User.UserEntity;
import playground.logic.helpers.Role;
import playground.logic.services.ElementService;
import playground.logic.services.UserService;

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
	public void testPostElementSuccessfully() throws Exception{
		
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


	@Test(expected=Exception.class)
	public void testPostElementByUnauthorizedUser() throws Exception{

		//Given - 
		//User of type PLAYER exist
		//No elements exist
		createAuthroizedUser(Role.PLAYER,authManagerEmail);
		
		//When
		ElementTO elementTo = new ElementTO();
		elementTo.setName("element1");
		elementTo.setType("Ad Board");
		restTemplate.postForObject(url + "/{playground}/{email}",elementTo,ElementTO.class,authUserPlayground,authManagerEmail);

		//Then The response is status <> 2xx		
	}

	//TODO - Michael - Add test for post element without existing user - Add gerkin accordingly

	@Test
	@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
	public void testUpdateElementSuccessfully() throws Exception{
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
	
	@Test(expected=Exception.class)
	public void testUpdateNotExistsElement() throws Exception{
		
		//Given - 
		//User of type MANAGER exist
		//No elements exist
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		
		//When
		ElementTO elementTORequest = new ElementTO();
		elementTORequest.setType("Quiz");
		restTemplate.put(url + "/{playground}/{email}/{playground}/{id}", 
				elementTORequest, authUserPlayground,authManagerEmail,"playground",1);

		//Then The response is status <> 2xx
	}
	
	//TODO - Michael - Add test for updating not exist element with unauthorized user
	
	@Test
	@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
	public void testGetElementByIdSuccessfully() throws Exception{
		
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

	@Test(expected=Exception.class)
	public void testGetNotExistsElement() throws Exception{
		
		//Given - 
		//Database contains user
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		//No elements exist
		
		//When
		restTemplate.getForObject(url + "/{playground}/{email}/{playground}/{id}",
				ElementTO.class, authUserPlayground,authPlayerEmail,"playground",1);

		//Then The response is status <> 2xx
	}

	@Test
	public void testGetAllElementsSuccessfully() throws Exception{
		
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


	@Test(expected=Exception.class)
	public void testGetAllElementsWithUnauthorizedUser() throws Exception{
		
		//Given - 
		//No user exists
		//And database contains element
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);

		//When
		restTemplate.getForObject(url + "/{playground}/{email}/all",
				ElementTO.class, authUserPlayground,authPlayerEmail);

		//Then The response is status <> 2xx
	}

	@Test
	public void testGetAllElementsByLocationAndDistanceSuccessfully() throws Exception{

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

	@Test(expected=Exception.class)
	public void testGetAllElementsByLocationAndNegativeDistance() throws Exception{
		
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

		elementService.addNewElement(authPlayerEmail,authUserPlayground,elementEntity);

		//When
		restTemplate.getForObject(url + "{playground}/{email}/near/{x}/{y}{distance}", 
				ElementTO.class, 
				"playground","test@user.com",2,2,-1);

		//Then The response is status <> 2xx
	}


	@Test
	public void testGetAllElementsByAttributeSuccessfully() throws Exception{
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

	@Test(expected=Exception.class)
	public void  testGetAllElementsWithNonExistingElementValue() throws Exception{
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
		restTemplate.getForObject(url + "{playground}/{email}/search/{attributeName}/{value}", 
				ElementTO.class, 
				"playground","test@user.com","color","blue");

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

	@Test(expected=Exception.class)
	public void testPostExistsUser() throws Exception
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
		newUserForm.setEmail("player@user.com");
		newUserForm.setUsername("player2");
		newUserForm.setRole(Role.PLAYER.name());
		newUserForm.setAvatar("avatar.jpg");
		this.restTemplate.postForObject(this.usersUrl, newUserForm, UserTO.class);

		//Then The response is status <> 2xx
	}

	//TODO - Option - Add tests for post new MANAGER
	
	@Test
	public void testGetUserConfirmSuccessfully() throws Exception
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

	@Test(expected=Exception.class)
	public void testGetUserConfirmWithInvalidCode() throws Exception
	{
		//Given
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail(authManagerEmail);
		userEntity.setUsername("manager");
		userEntity.setRole(Role.MANAGER.name());
		userEntity.setPlayground(authUserPlayground);
		int confirmCode = this.userService.addUser(userEntity).getConfirmCode();

		//When
		this.restTemplate.getForObject(this.usersUrl+"/confirm/{playground}/{email}/{code}",
				UserTO.class, authUserPlayground,authManagerEmail,confirmCode - 1);

		//Then The response is status <> 2xx
	}

	@Test
	public void testGetUserDetailsSuccessfully() throws Exception
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

	@Test(expected=Exception.class)
	public void testGetUserDetailsWithInvalidEmail() throws Exception
	{
		//Given - Database is empty
		
		//When
		this.restTemplate.getForObject(this.usersUrl+"/login/{playground}/{email}", UserTO.class, "playground","wrong@user.com");

		//Then The response is status <> 2xx
	}
	
	//TODO - Michael - Add test for get user details with existing user (wrong email/unauthorized...)

	@Test
	public void testUpdateUserDetailsSuccessfully() throws Exception
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

	@Test(expected=Exception.class)
	public void testUpdateUserDetailsWithNonExistingUser() throws Exception{
		//Given - 
		//Database is empty
		
		//When
		UserTO userTO= new UserTO();
		userTO.setEmail("unexistingUser@user.com");
		userTO.setPlayground(authUserPlayground);
		userTO.setRole(Role.PLAYER.name());
		this.restTemplate.put(this.usersUrl+"/{playground}/{email}", userTO, "playground","unexistingUser@user.com");

		//Then The response is status <> 2xx
	}

	private void createAuthroizedUser(Role role,String userEmail) throws Exception {
		UserEntity userEntity = new UserEntity(userEmail,authUserPlayground);
		userEntity.setRole(role.name());
		this.userService.addUser(userEntity);
		this.userService.confirmUser(userEntity);
	}
}