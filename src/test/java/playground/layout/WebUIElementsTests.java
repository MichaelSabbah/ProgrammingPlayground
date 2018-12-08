package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.logic.ElementAlreadyExistsException;
import playground.logic.ElementEntity;
import playground.logic.ElementService;
import playground.logic.Location;
import playground.logic.UserEntity;
import playground.logic.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class WebUIElementsTests {
	@LocalServerPort
	private int port;
	
	private String url;
	private String usersUrl;
	
	private RestTemplate restTemplate;
	
	private ObjectMapper jsonMapper; 
	
	@Autowired
	private ElementService elementService;
	
	@Autowired
	private UserService userService;
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + this.port + "/playground/elements";
		this.usersUrl = "http://localhost:" + this.port + "/playground/users";
		
		// Jackson init
		this.jsonMapper = new ObjectMapper();
	}
	
	@Before
	public void setup() {
		
	}

	@After
	public void teardown() {
		this.elementService.cleanup();
		this.userService.cleanAll();
	}

	@Test
	public void testServerIsBootingCorrectly() throws Exception {
		
	}
	
	@Test
	public void testPostElementSuccessfully() throws Exception{
		
		//Given the server is up and databases is empty
		
		//When I POST /playground/elements/playground/test@user.com
		ElementTO elementTo = new ElementTO();
		elementTo.setName("element1");
		elementTo.setType("Ad Board");
		ElementTO actualReturnedValue = restTemplate.postForObject(url + "/{playground}/{email}",elementTo,ElementTO.class,"playground","test@user.com");
		
		ElementEntity elementEntity = elementTo.toEntity();
		/*Then The response is:
		{“name”:”element1”,
			“type”:”Ad Board”,
			“id”:”1”,
			“playground”:”playground”,
			“creatorPlayground”:”playground”,
			“creatorEmail”:”test@user.com”}*/
		assertThat(actualReturnedValue)
		.isNotNull()
		.extracting("name","type","id","playground","creatorPlayground","creatorEmail")
		.containsExactly(elementEntity.getName(),elementEntity.getType(),elementEntity.getId(),elementEntity.getPlayground(),
				elementEntity.getCreatorPlayground(),elementEntity.getCreatorEmail());
	}
	
	@Test(expected=Exception.class)
	public void testPostExistsElement() throws Exception{
		/*Given the server is up
		And database contains element 
		{“name”:”element1”,
		“type”:”Ad Board”,
		“id”:”1”,
		“playground”:”playground”,
		“creatorPlayground”:”playground”,
		“creatorEmail”:”test@user.com”}*/
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setId("1");
		elementEntity.setPlayground("playground");
		elementEntity.setCreatorPlayground("playground");
		elementEntity.setCreatorEmail("test@user.com");
		
		elementService.addNewElement(elementEntity);
		
		/*When I POST /playground/elements/playground/test@user.com 
		With headers:
		Accept:application/json
		Content-Type: application/json
		With Body:
		{“name”:”element1”,
		“type”:”Ad Board”}*/
		//When I POST /playground/elements/playground/test@user.com
		ElementTO elementTo = new ElementTO();
		elementTo.setName("element1");
		elementTo.setType("Ad Board");
		ElementTO actualReturnedValue = restTemplate.postForObject(url + "/{playground}/{email}",elementTo,ElementTO.class,"playground","test@user.com");
		
		
		/*Then The response status <> 2xx with Body:*/		
	}
	
	
	@Test
	public void testUpdateElementSuccessfully() throws ElementAlreadyExistsException{
		/*Given the server is up
		And database contains: 
		{“name”:”element1”,
		“type”:”Ad Board”,
		“id”:”1”,
		“playground”:”playground”,
		“creatorPlayground”:”playground”,
		“creatorEmail”:”test@user.com”}*/
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setId("1");
		elementEntity.setPlayground("playground");
		elementEntity.setCreatorPlayground("playground");
		elementEntity.setCreatorEmail("test@user.com");
		
		elementService.addNewElement(elementEntity);
		/*When I PUT /playground/elements/playground/test@user.com/playground/1
		With headers:
		Accept:application/json
		Content-Type: application/json
		With Body:
		{“type”:”Quiz”}*/
		ElementTO elementTORequest = new ElementTO();
		elementEntity.setType("Quiz");
		restTemplate.put(url + "/{playground}/{email}/{playground}/{id}", 
				elementTORequest, "playground","test@user.com","playground","1");

		//Then The response is status  2xx
	}
	
	@Test(expected=Exception.class)
	public void testUpdateNotExistsElement() throws Exception{
		/*Given the server is up
			And database is empty*/
		
		
		/*When I PUT /playground/elements/playground/test@user.com/playground/1
		With headers:
		Accept:application/json
		Content-Type: application/json
		With Body:
		{“type”:”Quiz”}*/
		ElementTO elementTORequest = new ElementTO();
		elementTORequest.setType("Quiz");
		restTemplate.put(url + "/{playground}/{email}/{playground}/{id}", 
		elementTORequest, "playground","test@user.com","playground",1);
		
		/*Then The response is status <>  2xx*/



	}
	
	@Test
	public void testGetElementSuccessfully() throws Exception{
		/*Given the server is up
		And database contains: 
		{“name”:”element1”,
		“type”:”Ad Board”,
		“id”:”1”,
		“playground”:”playground”,
		“creatorPlayground”:”playground”,
		“creatorEmail”:”test@user.com”}*/
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setId("1");
		elementEntity.setPlayground("playground");
		elementEntity.setCreatorPlayground("playground");
		elementEntity.setCreatorEmail("test@user.com");
		
		elementService.addNewElement(elementEntity);
		
		/*When I GET /playground/elements/playground/test@user.com/playground/1
		With headers:
		Accept:application/json
		Content-Type: application/json*/
		ElementTO actuallyReturned = restTemplate.getForObject(url + "/{userPlayground}/{email}/{playground}/{id}",
								  ElementTO.class, "playground","test@user.com","playground",1);
		
		/*Then The response is:
		{“name”:”element1”,
		“type”:”Ad Board”,
		“id”:”1”,
		“playground”:”playground”,
		“creatorPlayground”:”playground”,
		“creatorEmail”:”test@user.com”}*/
		assertThat(actuallyReturned)
		.isNotNull()
		.extracting("name","type","id","playground","creatorPlayground","creatorEmail")
		.containsExactly("element1","Ad Board","1","playground","playground","test@user.com");

	}
	
	@Test(expected=Exception.class)
	public void testGetNotExistsElement() throws Exception{
		/*Given the server is up
			And database is empty*/
		
		/*When I GET /playground/elements/playground/test@user.com/playground/1
		With headers:
		Accept:application/json
		Content-Type: application/json*/
		ElementTO actuallyReturned = restTemplate.getForObject(url + "/{playground}/{email}/{playground}/{id}",
		ElementTO.class, "playground","test@user.com","playground",1);
		
		/*Then The response is status <>  2xx
		And Body:
		{“message”:”no such element”}*/
		

	}
	
	@Test
	public void testGetAllElementsSuccessfully() throws ElementAlreadyExistsException{
		/*
		 * Given the server is up
		And database contains: 
		{“name”:”element1”,
		“type”:”Ad Board”,
		“id”:”1”,
		“playground”:”playground”,
		“creatorPlayground”:”playground”,
		“creatorEmail”:”test@user.com”}*/
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setId("1");
		elementEntity.setPlayground("playground");
		elementEntity.setCreatorPlayground("playground");
		elementEntity.setCreatorEmail("test@user.com");
		
		elementService.addNewElement(elementEntity);
		
		/*When I GET /playground/elements/playground/test@user.com/all
		With headers:
		Accept:application/json
		Content-Type: application/json*/
		ElementTO[] actuallyReturned = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/all",
		ElementTO[].class, "playground","test@user.com");
		
		/*Then The response is:
		[{“name”:”element1”,
		“type”:”Ad Board”,
		“id”:”1”,
		“playground”:”playground”,
		“creatorPlayground”:”playground”,
		“creatorEmail”:”test@user.com”}]*/
		assertThat(actuallyReturned)
		.isNotNull();
		
//		.extracting("name","type","id","playground","creatorPlayground","creatorEmail")
//		.containsExactly("element1","Ad Board","1","playground","playground","test@user.com");


	}

	
	@Test(expected=Exception.class)
	public void testGetAllElementsWithInvalidEmail() throws Exception{
		/*Given the server is up
		And database is empty*/
		
		/*When I GET /playground/elements/playground/test@user.com/all
		With headers:
		Accept:application/json
		Content-Type: application/json*/
		ElementTO actuallyReturned = restTemplate.getForObject(url + "/{playground}/{email}/all",
		ElementTO.class, "playground","test@user.com");
		
		//Then The response is status <>  2xx
	}
	
	@Test
	public void testGetAllElementsByLocationAndDistanceSuccessfully() throws Exception{
		/*Given the server is up
		And database contains: 
		{“name”:”element1”,
		“type”:”Ad Board”,
		“location” : {“x”:1 , “y”:1 }, 
		“id”:”1”,
		“playground”:”playground”,
		“creatorPlayground”:”playground”,
		“creatorEmail”:”test@user.com”}*/
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
//		elementEntity.setLocation(new Location(1,1));
		elementEntity.setX(1.0);
		elementEntity.setY(1.0);
		elementEntity.setId("1");
		elementEntity.setPlayground("playground");
		elementEntity.setCreatorPlayground("playground");
		elementEntity.setCreatorEmail("test@user.com");
		
		elementService.addNewElement(elementEntity);
		
		/*When I GET /playground/elements/playground/test@user.com/near/2/2/3
		With headers:
		Accept:application/json
		Content-Type: application/json*/
		ElementTO[] actuallyReturned = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/near/{x}/{y}/{distance}", 
															  ElementTO[].class, 
															  "playground","test@user.com",2,2,3);
		
		/*Then The response is:
		{“name”:”element1”,
		“type”:”Ad Board”,
		“id”:”1”,
		“playground”:”playground”,
		“location” : {“x”:1 , “y”:1 },
		“creatorPlayground”:”playground”,
		“creatorEmail”:”test@user.com”}*/
		assertThat(actuallyReturned)
		.isNotNull();
//		.extracting("name","type","name","id","playground","creatorPlayground","creatorEmail","location.x","location.y")
//		.containsExactly("element1","Ad Board",1,"playground","playground","test@user.com",1,1);
	}
	
	@Test(expected=Exception.class)
	public void testGetAllElementsByLocationAndNegativeDistance() throws Exception{
		/*Given the server is up
		And database contains: 
		{
		“name”:”element1”,
		“type”:”Ad Board”,
		“location” : {“x”:1 , “y”:1 }, 
		“id”:”1”,
		“playground”:”playground”,
		“creatorPlayground”:”playground”,
		“creatorEmail”:”test@user.com”
		}*/
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
//		elementEntity.setLocation(new Location(1,1));
		elementEntity.setX(1.0);
		elementEntity.setY(1.0);
		elementEntity.setId("1");
		elementEntity.setPlayground("playground");
		elementEntity.setCreatorPlayground("playground");
		elementEntity.setCreatorEmail("test@user.com");
		
		elementService.addNewElement(elementEntity);
		
		/*When I GET /playground/elements/playground/test@user.com/near/2/2/-1
		With headers:
		Accept:application/json
		Content-Type: application/json*/
		ElementTO actuallyReturned = restTemplate.getForObject(url + "{playground}/{email}/near/{x}/{y}{distance}", 
				  ElementTO.class, 
				  "playground","test@user.com",2,2,-1);
		
		//Then The response is status <>  2xx
	}
	
	
	@Test
	public void testGetAllElementsByAttributeSuccessfully() throws Exception{
		/*Given the server is up
		And database contains: 
		{“name”:”element1”,
		“type”:”Quiz”,
		“id”:”1”,
		“playground”:”playground”,
		“creatorPlayground”:”playground”,
		“creatorEmail”:”test@user.com”,
		“attributes”:{“quizTime”:”60”}}*/
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
		
		/*When I GET /playground/elements/playground/test@user.com/search/quizTime/60
		With headers:
		Accept:application/json
		Content-Type: application/json*/
		ElementTO[] actuallyReturned = restTemplate.getForObject(url + "/{userPlayground}/{email}/search/{attributeName}/{value}", 
				  ElementTO[].class, 
				  "playground","test@user.com","quizTime","60");
		
		/*Then The response is:
		[{“name”:”element1”,
		“type”:”Quiz”,
		“id”:”1”,
		“playground”:”playground”,
		“creatorPlayground”:”playground”,
		“creatorEmail”:”test@user.com”,
		“attributes”:{“quizTime”:”60”}}]*/
		assertThat(actuallyReturned)
		.isNotNull();
//		.extracting("name","type","id","playground","creatorPlayground","creatorEmail","attributes.quizTime")
//		.containsExactly("element1","Quiz",1,"playground","playground","test@user.com","60");
	}
	
	@Test(expected=Exception.class)
	public void  testGetAllElementsWithNonExistingElementValue() throws Exception{
		/*Given the server is up
		And database is empty*/
		
		
		/* When I GET /playground/elements/playground/test@user.com/search/quizTime/60
		With headers:
		Accept:application/json
		Content-Type: application/json*/
		ElementTO actuallyReturned = restTemplate.getForObject(url + "{playground}/{email}/search/{attributeName}/{value}", 
				  ElementTO.class, 
				  "playground","test@user.com","quizTime","60");
		
		//Then The response is: the response is: status <> 2xx		
	}

	@Test
	public void testPostUserSuccessfully() {
		//Given - Database is empty

		//When
		NewUserForm newUserForm = new NewUserForm();
		newUserForm.setEmail("test@user.com");
		newUserForm.setUsername("test");
		newUserForm.setRole("admin");
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
		userEntity.setRole("admin");
		this.userService.addUser(userEntity);

		//When
		NewUserForm newUserForm = new NewUserForm();
		newUserForm.setEmail("test@user.com");
		newUserForm.setUsername("test");
		newUserForm.setRole("admin");
		newUserForm.setAvatar("smiley.jpg");
		this.restTemplate.postForObject(this.usersUrl, newUserForm, UserTO.class);
	}

	@Test
	public void testGetUserconfirmSuccessfully() throws Exception
	{
		//Given
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail("test@user.com");
		userEntity.setUsername("test");
		userEntity.setRole("admin");
		userEntity.setPlayground("playground");
		userEntity.setAvatar("smiley.jpg");
		int confirmCode = this.userService.addUser(userEntity).getConfirmCode();

		//When
		UserTO userTO = this.restTemplate.getForObject(this.usersUrl+"/confirm/{playground}/{email}/{code}", UserTO.class, "playground","test@user.com",confirmCode);

		//Then
		assertThat(userTO)
		.isNotNull()
		.extracting("email", "username","playground", "role","avatar")
		.containsExactly(userEntity.getEmail(), userEntity.getUsername(),userEntity.getPlayground(),userEntity.getRole(),userEntity.getAvatar());
	}

	@Test(expected=Exception.class)
	public void testGetUserConfirmWithInvalidCode() throws Exception
	{
		//Given
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail("test@user.com");
		userEntity.setUsername("test");
		userEntity.setRole("admin");
		userEntity.setPlayground("playground");
		userEntity.setConfirmCode(777);
		this.userService.addUser(userEntity);

		//When
		this.restTemplate.getForObject(this.usersUrl+"/confirm/{playground}/{email}/{code}", UserTO.class, "playground","test@user.com",666);
	}

	@Test
	public void testGetUserDetailsSuccessfully() throws Exception
	{
		//Given
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail("test@user.com");
		userEntity.setUsername("test");
		userEntity.setRole("admin");
		userEntity.setPlayground("playground");
		this.userService.addUser(userEntity);

		//When
		UserTO userTO = this.restTemplate.getForObject(this.usersUrl+"/login/{playground}/{email}", UserTO.class, "playground","test@user.com");

		//Then
		assertThat(userTO)
		.isNotNull()
		.extracting("email", "username","playground","role")
		.containsExactly(userEntity.getEmail(), userEntity.getUsername(),userEntity.getPlayground(),userEntity.getRole());

	}

	@Test(expected=Exception.class)
	public void testGetUserDetailsWithInvalidEmail() throws Exception
	{
		//Given - Database is empty
		
		//When
		this.restTemplate.getForObject(this.usersUrl+"/login/{playground}/{email}", UserTO.class, "playground","test@user.com");
	}


	@Test
	public void testUpdateUserDetailsSuccessfully() throws Exception
	{
		//Given
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail("test@user.com");
		userEntity.setUsername("test");
		userEntity.setRole("admin");
		userEntity.setPlayground("playground");
		userEntity.setAvatar("smiley.jpg");
		this.userService.addUser(userEntity);
		
		//When
		UserTO userTO = new UserTO(userEntity);
		userTO.setAvatar("smile.jpg");
		userTO.setUsername("test2");
		this.restTemplate.put(this.usersUrl+"/{playground}/{email}", userTO, "playground","test@user.com");
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
	}

	
}