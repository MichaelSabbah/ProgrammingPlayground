package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import playground.layout.to.ElementTO;
import playground.layout.to.UserTO;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.Exceptions.ElementAlreadyExistsException;
import playground.logic.services.ElementService;
import playground.logic.services.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class PlaygroundTests {
	@LocalServerPort
	private int port;

	private String url;
	private String usersUrl;

	private RestTemplate restTemplate;

	@Autowired
	private ElementService elementService;

	@Autowired
	private UserService userService;

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + this.port + "/playground/elements";
		this.usersUrl = "http://localhost:" + this.port + "/playground/users";
	}

	@After
	public void after() {
		this.elementService.cleanAll();
		this.userService.cleanAll();
	}
	
	@Before
	public void before()
	{
		this.elementService.cleanAll();
		this.userService.cleanAll();
	}

	@Test
	public void testServerIsBootingCorrectly() throws Exception {

	}

	@Test
	public void testPostElementSuccessfully() throws Exception{

		//Given - Database is empty

		//When
		ElementTO elementTo = new ElementTO();
		elementTo.setName("element1");
		elementTo.setType("Ad Board");
		ElementTO actualReturnedValue = restTemplate.postForObject(url + "/{playground}/{email}",elementTo,ElementTO.class,"playground","test@user.com");

		ElementEntity elementEntity = elementTo.toEntity();

		//Then
		assertThat(actualReturnedValue)
		.isNotNull()
		.extracting("name","type","id","playground","creatorPlayground","creatorEmail")
		.containsExactly(elementEntity.getName(),elementEntity.getType(),elementEntity.getId(),elementEntity.getPlayground(),
				elementEntity.getCreatorPlayground(),elementEntity.getCreatorEmail());
	}

	@Test(expected=Exception.class)
	public void testPostExistsElement() throws Exception{
		//Given 
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setId("1");
		elementEntity.setPlayground("playground");
		elementEntity.setCreatorPlayground("playground");
		elementEntity.setCreatorEmail("test@user.com");

		elementService.addNewElement("bla","bla",elementEntity);

		//When
		ElementTO elementTo = new ElementTO();
		elementTo.setName("element1");
		elementTo.setType("Ad Board");
		restTemplate.postForObject(url + "/{playground}/{email}",elementTo,ElementTO.class,"playground","test@user.com");

		//Then The response is status <> 2xx		
	}


	@Test
	public void testUpdateElementSuccessfully() throws ElementAlreadyExistsException{
		//Given
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setId("1");
		elementEntity.setPlayground("playground");
		elementEntity.setCreatorPlayground("playground");
		elementEntity.setCreatorEmail("test@user.com");
		elementEntity.setElementId(elementEntity.getPlayground() + "@" + elementEntity.getId());

		elementService.addNewElement("bla","bla",elementEntity);
		//When
		ElementTO elementTORequest = new ElementTO();
		elementEntity.setType("Quiz");
		restTemplate.put(url + "/{playground}/{email}/{playground}/{id}", 
				elementTORequest, "playground","test@user.com","playground","1");

		//Then The response is status <> 2xx
	}

	@Test(expected=Exception.class)
	public void testUpdateNotExistsElement() throws Exception{
		//Given - Database is empty

		//When
		ElementTO elementTORequest = new ElementTO();
		elementTORequest.setType("Quiz");
		restTemplate.put(url + "/{playground}/{email}/{playground}/{id}", 
				elementTORequest, "playground","test@user.com","playground",1);

		//Then The response is status <> 2xx
	}

	@Test
	public void testGetElementSuccessfully() throws Exception{
		//Given
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setId("1");
		elementEntity.setPlayground("playground");
		elementEntity.setCreatorPlayground("playground");
		elementEntity.setCreatorEmail("test@user.com");
		elementEntity.setElementId(elementEntity.getPlayground() + "@" + elementEntity.getId());

		elementService.addNewElement("bla","bla",elementEntity);

		//When
		ElementTO actuallyReturned = restTemplate.getForObject(url + "/{userPlayground}/{email}/{playground}/{id}",
				ElementTO.class, "playground","test@user.com","playground",1);

		//Then
		assertThat(actuallyReturned)
		.isNotNull()
		.extracting("name","type","id","playground","creatorPlayground","creatorEmail")
		.containsExactly("element1","Ad Board","1","playground","playground","test@user.com");

	}

	@Test(expected=Exception.class)
	public void testGetNotExistsElement() throws Exception{
		//Given - Database is empty

		//When
		restTemplate.getForObject(url + "/{playground}/{email}/{playground}/{id}",
				ElementTO.class, "playground","test@user.com","playground",1);

		//Then The response is status <> 2xx
	}

	@Test
	public void testGetAllElementsSuccessfully() throws Exception{
		// Given
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setId("1");
		elementEntity.setPlayground("playground");
		elementEntity.setCreatorPlayground("playground");
		elementEntity.setCreatorEmail("test@user.com");
		elementEntity.setElementId(elementEntity.getPlayground() + "@" + elementEntity.getId());
		
		UserEntity user = new UserEntity("test@user.com","playground");
		userService.addUser(user);
		userService.confirmUser(user);

		elementService.addNewElement("bla","bla",elementEntity);

		//When
		ElementTO[] actuallyReturned = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/all",
				ElementTO[].class, "playground","test@user.com");

		//Then
		assertThat(actuallyReturned)
		.isNotNull();
	}


	@Test(expected=Exception.class)
	public void testGetAllElementsWithInvalidEmail() throws Exception{
		//Given - Database is empty

		//When
		restTemplate.getForObject(url + "/{playground}/{email}/all",
				ElementTO.class, "playground","test@user.com");

		//Then The response is status <> 2xx
	}

	@Test
	public void testGetAllElementsByLocationAndDistanceSuccessfully() throws Exception{
		//Given
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
		//Given
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
		//Given - Database is empty

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

		//Then The response is status <> 2xx
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

		//Then The response is status <> 2xx

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

		//Then The response is status <> 2xx
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
	}


}