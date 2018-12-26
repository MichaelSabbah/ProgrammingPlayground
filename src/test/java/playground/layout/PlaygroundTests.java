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

import playground.dal.ElementIdGeneratorDao;
import playground.layout.to.ElementTO;
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

	/*@Test
	public void testGetAllElementsByLocationAndDistanceSuccessfully() throws Exception{
		//Given
		UserEntity userEntity= new UserEntity("test@user.com","playground");
		this.userService.addUser(userEntity);
		this.userService.confirmUser(userEntity);
		
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setX(1.0);
		elementEntity.setY(1.0);
		//elementEntity.setId("1");
		elementEntity.setPlayground("playground");
		elementEntity.setCreatorPlayground("playground");
		elementEntity.setCreatorEmail("test@user.com");
		elementEntity.setElementId(elementEntity.getPlayground() + "@" + elementEntity.getId());

		elementService.addNewElement("bla","bla",elementEntity);

		//When
		ElementTO[] actuallyReturned = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/near/{x}/{y}/{distance}", 
				ElementTO[].class, 
				"playground","test@user.com",2,2,3);

		//Then
		assertThat(actuallyReturned)
		.isNotNull()
		.hasSize(1);
	}

	@Test(expected=Exception.class)
	public void testGetAllElementsByLocationAndNegativeDistance() throws Exception{
		//Given - 
		//No elements exist
		//Confirmed user exist
		UserEntity userEntity= new UserEntity("test@user.com","playground");
		this.userService.addUser(userEntity);
		this.userService.confirmUser(userEntity);
		
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setX(1.0);
		elementEntity.setY(1.0);
		elementEntity.setId("1");
		elementEntity.setPlayground("playground");
		elementEntity.setCreatorPlayground("playground");
		elementEntity.setCreatorEmail("test@user.com");
		elementEntity.setElementId(elementEntity.getPlayground() + "@" + elementEntity.getId());

		elementService.addNewElement("bla","bla",elementEntity);

		//When
		restTemplate.getForObject(url + "{playground}/{email}/near/{x}/{y}{distance}", 
				ElementTO.class, 
				"playground","test@user.com",2,2,-1);

		//Then The response is status <> 2xx
	}


	@Test
	public void testGetAllElementsByAttributeSuccessfully() throws Exception{
		//Given
		UserEntity userEntity= new UserEntity("test@user.com","playground");
		this.userService.addUser(userEntity);
		this.userService.confirmUser(userEntity);
		
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Quiz1");
		elementEntity.setId("1");
		elementEntity.setPlayground("playground");
		elementEntity.setCreatorPlayground("playground");
		elementEntity.setCreatorEmail("test@user.com");
		HashMap<String,Object> attributesMap = new HashMap<>();
		attributesMap.put("quizTime", "60");
		elementEntity.setAttributes(attributesMap);
		elementEntity.setElementId(elementEntity.getPlayground() + "@" + elementEntity.getId());
		this.elementService.addNewElement("bla","bla",elementEntity);


		//When
		ElementTO[] actuallyReturned = restTemplate.getForObject(url + "/{userPlayground}/{email}/search/{attributeName}/{value}", 
				ElementTO[].class, 
				"playground","test@user.com","quizTime","60");

		//Then
		assertThat(actuallyReturned)
		.isNotNull()
		.hasSize(1);
	}

	@Test(expected=Exception.class)
	public void  testGetAllElementsWithNonExistingElementValue() throws Exception{
		//Given - 
		//No elements exist
		//Confirmed user exist
		UserEntity userEntity= new UserEntity("test@user.com","playground");
		this.userService.addUser(userEntity);
		this.userService.confirmUser(userEntity);

		//When
		restTemplate.getForObject(url + "{playground}/{email}/search/{attributeName}/{value}", 
				ElementTO.class, 
				"playground","test@user.com","quizTime","60");

		//Then The response is status <> 2xx		
	}

	@Test
	public void testPostUserSuccessfully() {
		//Given - Database is empty

		//When
		NewUserForm newUserForm = new NewUserForm();
		newUserForm.setEmail("test@user.com");
		newUserForm.setUsername("test");
		newUserForm.setRole(Role.MANAGER.name());
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
		userEntity.setEmail("test@user.com");
		userEntity.setUsername("test");
		userEntity.setRole(Role.MANAGER);
		this.userService.addUser(userEntity);

		//When
		NewUserForm newUserForm = new NewUserForm();
		newUserForm.setEmail("test@user.com");
		newUserForm.setUsername("test");
		newUserForm.setRole("admin");
		newUserForm.setAvatar("smiley.jpg");
		this.restTemplate.postForObject(this.usersUrl, newUserForm, UserTO.class);

		//Then The response is status <> 2xx
	}

	@Test
	public void testGetUserconfirmSuccessfully() throws Exception
	{
		//Given
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail("test@user.com");
		userEntity.setUsername("test");
		userEntity.setRole(Role.MANAGER);
		userEntity.setPlayground("playground");
		userEntity.setAvatar("smiley.jpg");
		int confirmCode = this.userService.addUser(userEntity).getConfirmCode();

		//When
		UserTO userTO = this.restTemplate.getForObject(this.usersUrl+"/confirm/{playground}/{email}/{code}", UserTO.class, "playground","test@user.com",confirmCode);

		//Then
		assertThat(userTO)
		.isNotNull()
		.extracting("email", "username","playground", "role","avatar")
		.containsExactly(userEntity.getEmail(), userEntity.getUsername(),userEntity.getPlayground(),userEntity.getRole().name(),userEntity.getAvatar());
	}

	@Test(expected=Exception.class)
	public void testGetUserConfirmWithInvalidCode() throws Exception
	{
		//Given
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail("test@user.com");
		userEntity.setUsername("test");
		userEntity.setRole(Role.MANAGER);
		userEntity.setPlayground("playground");
		userEntity.setConfirmCode(777);
		this.userService.addUser(userEntity);

		//When
		this.restTemplate.getForObject(this.usersUrl+"/confirm/{playground}/{email}/{code}", UserTO.class, "playground","test@user.com",666);

		//Then The response is status <> 2xx

	}

	@Test
	public void testGetUserDetailsSuccessfully() throws Exception
	{
		//Given
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail("test@user.com");
		userEntity.setUsername("test");
		userEntity.setRole(Role.MANAGER);
		userEntity.setPlayground("playground");
		this.userService.addUser(userEntity);

		//When
		UserTO userTO = this.restTemplate.getForObject(this.usersUrl+"/login/{playground}/{email}", UserTO.class, "playground","test@user.com");

		//Then
		assertThat(userTO)
		.isNotNull()
		.extracting("email", "username","playground","role")
		.containsExactly(userEntity.getEmail(), userEntity.getUsername(),userEntity.getPlayground(),userEntity.getRole().name());

	}

	@Test(expected=Exception.class)
	public void testGetUserDetailsWithInvalidEmail() throws Exception
	{
		//Given - Database is empty

		//When
		this.restTemplate.getForObject(this.usersUrl+"/login/{playground}/{email}", UserTO.class, "playground","test@user.com");

		//Then The response is status <> 2xx
	}


	@Test
	public void testUpdateUserDetailsSuccessfully() throws Exception
	{
		//Given
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail("test@user.com");
		userEntity.setUsername("test");
		userEntity.setRole(Role.MANAGER);
		userEntity.setPlayground("playground");
		userEntity.setAvatar("smiley.jpg");
		this.userService.addUser(userEntity);

		//When
		UserTO userTO = new UserTO(userEntity);
		userTO.setAvatar("smile.jpg");
		userTO.setUsername("test2");
		this.restTemplate.put(this.usersUrl+"/{playground}/{email}", userTO, "playground","test@user.com");

		//Then The response is status 2xx

	}

	@Test(expected=Exception.class)
	public void testUpdateUserDetailsWithNonExistingUser() throws Exception
	{
		//Given - Database is empty
		
		//When
		UserTO userTO= new UserTO();
		userTO.setEmail("test@user.com");
		userTO.setUsername("test2");
		userTO.setRole("admin");
		userTO.setAvatar("smile.jpg");
		this.restTemplate.put(this.usersUrl+"/{playground}/{email}", userTO, "playground","test@user.com");

		//Then The response is status <> 2xx
	}*/

	private void createAuthroizedUser(Role role,String userEmail) throws Exception {
		UserEntity userEntity = new UserEntity(userEmail,authUserPlayground);
		userEntity.setRole(role.name());
		this.userService.addUser(userEntity);
		this.userService.confirmUser(userEntity);
	}
}