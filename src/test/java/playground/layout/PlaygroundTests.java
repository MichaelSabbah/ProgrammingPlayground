package playground.layout;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
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
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import playground.layout.to.ActivityTO;
import playground.layout.to.ElementTO;
import playground.layout.to.UserTO;
import playground.logic.Entities.Activity.ActivityEntity;
import playground.logic.Entities.Element.ElementEntity;
import playground.logic.Entities.User.UserEntity;
import playground.logic.helpers.PlaygroundConsts;
import playground.logic.helpers.Role;
import playground.logic.services.ActivityService;
import playground.logic.services.ElementService;
import playground.logic.services.UserService;
import playground.plugins.AdMessage;
import playground.plugins.Answer;
import playground.plugins.Feedback;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class PlaygroundTests {

	@LocalServerPort
	private int port;

	private String url;
	private String usersUrl;
	private String activitiesUrl;

	private String authManagerEmail;
	private String authPlayerEmail;
	private String authUserPlayground;

	private Date futureDate;
	private Date pastDate;

	private RestTemplate restTemplate;

	@Autowired
	private ElementService elementService;

	@Autowired
	private UserService userService;

	@Autowired
	private ActivityService activityService;


	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + this.port + "/playground/elements";
		this.usersUrl = "http://localhost:" + this.port + "/playground/users";
		this.activitiesUrl = "http://localhost:" + this.port + "/playground/activities";
		this.authManagerEmail = "manager@user.com";
		this.authPlayerEmail = "player@user.com";
		this.authUserPlayground = PlaygroundConsts.PLAYGROUND_NAME;

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		this.futureDate = calendar.getTime();
		this.pastDate = new Date(1);
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
	}

	@After
	public void after() throws SQLException {
		this.elementService.cleanAll();
		this.userService.cleanAll();
	}

	@Test
	public void testServerIsBootingCorrectly() throws Exception {

	}

	//Elements tests

	@Test
	public void testPostElementSuccessfully() throws Throwable{
		//Given
		Map<String,Object> attributes = new HashMap<String,Object>();
		attributes.put("testKey","testValue");

		createAuthroizedUser(Role.MANAGER,authManagerEmail);	

		//When
		ElementTO elementTo = new ElementTO();
		elementTo.setName("element1");
		elementTo.setType("Ad Board");
		elementTo.setExpirationDate(new Date());
		elementTo.setAttributes(attributes);
		ElementTO actualReturnedValue = restTemplate.postForObject(url + "/{userPlayground}/{email}",elementTo,ElementTO.class,authUserPlayground,authManagerEmail);

		//Then
		assertThat(actualReturnedValue)
		.isNotNull()
		.extracting("name","type","playground","creatorPlayground","creatorEmail")
		.containsExactly(elementTo.getName(),elementTo.getType(),authUserPlayground,
				authUserPlayground,authManagerEmail);
	}


	@Test(expected=OKException.class)
	public void testPostElementByUnauthorizedUser() throws Throwable{

		//Given
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
		//Then		
	}

	@Test
	public void testUpdateElementSuccessfully() throws Throwable{
		//Given
		createAuthroizedUser(Role.MANAGER,authManagerEmail);

		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");

		elementEntity = elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);

		//When
		ElementTO elementTo = new ElementTO();
		elementTo.setName("element1");
		elementTo.setType("Quiz");
		restTemplate.put(url + "/{playground}/{email}/{playground}/{id}",
				elementTo, authUserPlayground,authManagerEmail,authUserPlayground,elementEntity.getId());

		//Then
		ElementEntity actualReturnedValue = this.elementService.getElementById(authManagerEmail, 
				authUserPlayground, authUserPlayground, elementEntity.getId()+"");
		assertThat(actualReturnedValue)
		.isNotNull()
		.extracting("name","type","playground","creatorPlayground","creatorEmail")
		.containsExactly(elementTo.getName(),elementTo.getType(),authUserPlayground,
				authUserPlayground,authManagerEmail);

	}

	@Test(expected=OKException.class)
	public void testUpdateNotExistsElement() throws Throwable{

		//Given
		createAuthroizedUser(Role.MANAGER,authManagerEmail);

		//When
		ElementTO elementTORequest = new ElementTO();
		elementTORequest.setType("Quiz");
		try {

			restTemplate.put(url + "/{playground}/{email}/{playground}/{id}", 
					elementTORequest, authUserPlayground,authManagerEmail,authUserPlayground,"1");

		}
		catch(HttpClientErrorException ex)
		{
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_FOUND, ex.getMessage());
		}

		//Then
	}

	@Test
	public void testGetElementByIdSuccessfully() throws Throwable{

		//Given
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);

		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setExpirationDate(futureDate);
		elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);

		//When
		ElementTO actuallyReturned = restTemplate.getForObject(url + "/{userPlayground}/{email}/{playground}/{id}",
				ElementTO.class, authUserPlayground,authPlayerEmail,authUserPlayground,elementEntity.getId());

		//Then
		assertThat(actuallyReturned)
		.isNotNull()
		.extracting("name","type","id","playground","creatorPlayground","creatorEmail")
		.containsExactly(elementEntity.getName(),elementEntity.getType(),elementEntity.getId() + "",authUserPlayground,authUserPlayground,authManagerEmail);

	}

	@Test(expected=OKException.class)
	public void testGetNotExistsElement() throws Throwable{

		//Given 
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);

		//When
		try {
			restTemplate.getForObject(url + "/{playground}/{email}/{playground}/{id}",
					ElementTO.class, authUserPlayground,authPlayerEmail,authUserPlayground,1);
		}
		catch(HttpClientErrorException ex)
		{
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_FOUND, ex.getMessage());
		}

		//Then
	}

	@Test
	public void testGetAllElementsSuccessfully() throws Throwable{

		//Given
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setExpirationDate(futureDate);
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
	}

	@Test
	public void testGetAllElementsWhileAllElementsAreExpired() throws Throwable{

		//Given
		createAuthroizedUser(Role.PLAYER, authPlayerEmail);
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setExpirationDate(pastDate);
		elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);

		//When
		ElementTO[] actuallyReturned = this.restTemplate.getForObject(this.url + "/{userPlayground}/{email}/all",
				ElementTO[].class, authUserPlayground,authPlayerEmail);

		//Then
		assertThat(actuallyReturned)
		.isNotNull()
		.hasSize(0);
	}

	@Test(expected=OKException.class)
	public void testGetAllElementsWithUnauthorizedUser() throws Throwable{

		//Given
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setExpirationDate(futureDate);
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

		//Then
	}

	@Test
	public void testGetAllElementsByLocationAndDistanceSuccessfully() throws Throwable{

		//Given
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setExpirationDate(futureDate);
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
	}



	@Test(expected=OKException.class)
	public void testGetAllElementsByLocationAndNegativeDistance() throws Throwable{

		//Given
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setExpirationDate(futureDate);
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

		//Then
	}


	@Test
	public void testGetAllElementsByAttributeSuccessfully() throws Throwable{
		//Given
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		//And database contains element
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Ad Board");
		elementEntity.setExpirationDate(futureDate);
		elementEntity.setX(1.0);
		elementEntity.setY(1.0);
		Map<String,Object> moreAttributes = new HashMap<String,Object>();
		moreAttributes.put("quizTime", "60");
		elementEntity.setAttributes(moreAttributes);
		elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);

		//When
		ElementTO[] actuallyReturned = restTemplate.getForObject(url + "/{userPlayground}/{email}/search/{attributeName}/{value}", 
				ElementTO[].class, 
				authUserPlayground,authPlayerEmail,"name","element1");

		//Then
		assertThat(actuallyReturned)
		.isNotNull()
		.hasSize(1);
	}

	@Test(expected=OKException.class)
	public void  testGetAllElementsWithNonExistingElementAttribute() throws Throwable{
		//Given
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("element1");
		elementEntity.setType("Quiz");
		elementEntity.setExpirationDate(futureDate);
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

		//Then	
	}

	//Users tests

	@Test
	public void testPostUserSuccessfully() {
		//Given

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
		userEntity.setPlayground(PlaygroundConsts.PLAYGROUND_NAME);
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

		//Then
	}

	@Test
	public void testGetUserConfirmSuccessfully() throws Throwable{
		//Given
		UserEntity userEntity= new UserEntity();
		userEntity.setEmail(authManagerEmail);
		userEntity.setUsername("manager");
		userEntity.setRole(Role.MANAGER.name());
		userEntity.setPlayground(authUserPlayground);
		userEntity.setAvatar("smiley.jpg");		
		int confirmCode = this.userService.addUser(userEntity).getConfirmCode();

		//When
		UserTO actuallyReturned = this.restTemplate.getForObject(this.usersUrl+"/confirm/{playground}/{email}/{code}", UserTO.class, authUserPlayground, authManagerEmail,confirmCode);

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

		//Then
	}

	@Test
	public void testGetUserDetailsSuccessfully() throws Throwable
	{
		//Given
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
	public void testGetUserDetailsWithInvalidEmail() throws Throwable
	{
		//Given

		//When
		try {
			this.restTemplate.getForObject(this.usersUrl+"/login/{playground}/{email}", UserTO.class, authUserPlayground,"wrong@user.com");
		}
		catch(HttpClientErrorException ex)
		{
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_FOUND, ex.getMessage());
		}
		//Then
	}

	@Test
	public void testUpdateUserDetailsSuccessfully() throws Throwable
	{
		//Given
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

		//Then

	}

	@Test(expected=OKException.class)
	public void testUpdateUserDetailsWithNonExistingUser() throws Exception{
		//Given

		//When
		UserTO userTO= new UserTO();
		userTO.setEmail("unexistingUser@user.com");
		userTO.setPlayground(authUserPlayground);
		userTO.setRole(Role.PLAYER.name());
		try {
			this.restTemplate.put(this.usersUrl+"/{playground}/{email}", userTO, authUserPlayground,"unexistingUser@user.com");
		}
		catch(HttpClientErrorException ex){
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.UNAUTHORIZED, ex.getMessage());
		}

		//Then
	}


	//Activity tests

	@Test
	public void testAnswerTheQuestionActivityPluginSuccessfully() throws Throwable{
		//Given
		createAuthroizedUser(Role.PLAYER, authPlayerEmail);
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("Question");
		elementEntity.setType("MultipleChoiceQuestion");
		Map<String,Object> moreAttributes = new HashMap<String,Object>();
		moreAttributes.put("question", "the question");
		moreAttributes.put("a", "answer a");
		moreAttributes.put("b", "answer b");
		moreAttributes.put("c", "answer c");
		moreAttributes.put("d", "answer d");
		moreAttributes.put("answer", "a");
		elementEntity.setAttributes(moreAttributes);
		ElementEntity exisingElementEntity = elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);

		//When
		ActivityTO activityTO = new ActivityTO();
		activityTO.setElementId(String.valueOf(exisingElementEntity .getId()));
		activityTO.setElementPlayground(exisingElementEntity.getPlayground());
		activityTO.setType("AnswerTheQuestion");
		moreAttributes = new HashMap<String,Object>();
		moreAttributes.put(PlaygroundConsts.ANSWER_KEY, "a");
		activityTO.setAttributes(moreAttributes);

		Feedback feedbackReturend  = this.restTemplate.postForObject(this.activitiesUrl+"/{userPlayground}/{email}",
				activityTO, Feedback.class, authUserPlayground,authPlayerEmail);

		//Then
		assertThat(feedbackReturend)
		.isNotNull()
		.extracting("feedback")
		.containsExactly(PlaygroundConsts.GOOD_FEEDBACK);
	}

	@Test(expected=OKException.class)
	public void testAnswerTheQuestionActivityPluginWithoutAnswerAttribute() throws Throwable{
		//Given
		createAuthroizedUser(Role.PLAYER, authPlayerEmail);
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("Question");
		elementEntity.setType("MultipleChoiceQuestion");
		Map<String,Object> moreAttributes = new HashMap<String,Object>();
		moreAttributes.put("question", "the question");
		moreAttributes.put("a", "answer a");
		moreAttributes.put("b", "answer b");
		moreAttributes.put("c", "answer c");
		moreAttributes.put("d", "answer d");
		moreAttributes.put("answer", "a");
		elementEntity.setAttributes(moreAttributes);
		ElementEntity exisingElementEntity = elementService.addNewElement(authManagerEmail,authUserPlayground,elementEntity);

		//When
		ActivityTO postActivity = new ActivityTO();
		postActivity.setElementId(String.valueOf(exisingElementEntity .getId()));
		postActivity.setElementPlayground(exisingElementEntity.getPlayground());
		postActivity.setType("AnswerTheQuestion");
		try {
			this.restTemplate.postForObject(this.activitiesUrl+"/{userPlayground}/{email}",
					postActivity, Feedback.class, authUserPlayground,authPlayerEmail);
		}catch(HttpClientErrorException ex){
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
		}

		//Then
	}

	@Test
	public void testFindBugInBadCodeActivityPlugin() throws Throwable {
		// Given
		createAuthroizedUser(Role.PLAYER, authPlayerEmail);
		createAuthroizedUser(Role.MANAGER, authManagerEmail);

		Map<String, Object> attribute = new HashMap<String,Object>();
		attribute.put("code", "int x = 5; char a = 'w' + 'z'; double name = \"Hello\"; float num = 5.5;");
		attribute.put("answer", "double name = \"Hello\"");

		ElementEntity element = new ElementEntity();
		element.setName("Bad Code");
		element.setPlayground(authUserPlayground);

		element.setType("FindTheBug");
		element.setAttributes(attribute);

		ElementEntity temp = elementService.addNewElement(authManagerEmail, authUserPlayground, element);

		// When
		ActivityTO postActivity = new ActivityTO();
		postActivity.setType("FindBugActivity");
		postActivity.setElementId(String.valueOf(temp.getId()));
		postActivity.setElementPlayground(temp.getPlayground());

		Map<String, Object> actTemp = new HashMap<String, Object>();
		actTemp.put("answer", "double name = \"Hello\"");
		postActivity.setAttributes(actTemp);

		Feedback feedback = this.restTemplate.postForObject(this.activitiesUrl+"/{userPlayground}/{email}", postActivity, Feedback.class, authUserPlayground, authPlayerEmail);

		// Then
		assertThat(feedback)
		.isNotNull()
		.extracting("feedback")
		.containsExactly(PlaygroundConsts.GOOD_FEEDBACK);
	}

	@Test(expected=OKException.class)
	public void testFindBugInBadCodeActivityPluginFailed() throws Throwable {
		// Given
		createAuthroizedUser(Role.PLAYER, authPlayerEmail);
		createAuthroizedUser(Role.MANAGER, authManagerEmail);

		Map<String, Object> attribute = new HashMap<String, Object>();
		attribute.put("code", "int x = 5; char a = 'w' + 'z'; double name = \"Hello\"; float num = 5.5;");
		attribute.put("answer", "double name = \"Hello\"");

		ElementEntity element = new ElementEntity();
		element.setName("Bad Code");
		element.setPlayground(authUserPlayground);

		element.setType("FindTheBug");
		element.setAttributes(attribute);

		ElementEntity temp = elementService.addNewElement(authManagerEmail, authUserPlayground, element);

		// When
		ActivityTO postActivity = new ActivityTO();
		postActivity.setType("FindBugActivity");
		postActivity.setElementId(String.valueOf(temp.getId()));
		postActivity.setElementPlayground(temp.getPlayground());

		try {
			this.restTemplate.postForObject(this.activitiesUrl+"/{userPlayground}/{email}", postActivity, Feedback.class, authUserPlayground, authPlayerEmail);
		} catch (HttpClientErrorException ex) {
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
		}

		//Then
	}

	@Test
	public void testPostNewMessageActivitySuccessfully() throws Throwable {
		//Given
		createAuthroizedUser(Role.PLAYER, authPlayerEmail);
		createAuthroizedUser(Role.MANAGER, authManagerEmail);
		ElementEntity adBoardElement = new ElementEntity();
		adBoardElement.setName("Ad Board");
		adBoardElement.setType("AdBoard");
		ElementEntity temp = this.elementService.addNewElement(authManagerEmail, authUserPlayground, adBoardElement);

		// When
		ActivityTO postActivity = new ActivityTO();
		postActivity.setType("PostNewMessage");
		postActivity.setElementId(String.valueOf(temp.getId()));
		postActivity.setElementPlayground(temp.getPlayground());
		Map<String, Object> actTemp = new HashMap<String, Object>();
		actTemp.put("message", "The message");
		postActivity.setAttributes(actTemp);

		this.restTemplate.postForObject(this.activitiesUrl+"/{userPlayground}/{email}",
				postActivity, Nullable.class, authUserPlayground, authPlayerEmail);

		//Then
	}

	@Test
	public void testGetMessagesActivitySuccessfully() throws Throwable
	{
		//Given
		String regularEmail = "player@user.com";
		String managerEmail = "manager@user.com";
		createAuthroizedUser(Role.PLAYER,regularEmail);
		createAuthroizedUser(Role.MANAGER,managerEmail);

		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("Message");
		elementEntity.setType("Messages");
		elementEntity.setPlayground(authUserPlayground);
		elementEntity.setCreatorEmail(regularEmail);
		elementEntity = elementService.addNewElement(managerEmail,authUserPlayground,elementEntity);

		ActivityEntity activityEntity = new ActivityEntity();
		activityEntity.setPlayground(authUserPlayground);
		activityEntity.setElementId(elementEntity.getId()+"");
		activityEntity.setElementPlayground(elementEntity.getPlayground());
		activityEntity.setPlayerEmail(regularEmail);
		activityEntity.setPlayerPlayground(authUserPlayground);
		activityEntity.setType("PostNewMessage");
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("message", "bla bla bla bla");
		activityEntity.setAttributes(map);
		activityService.invokeActivity(regularEmail, authUserPlayground, elementEntity.getId()+"", elementEntity.getPlayground(), activityEntity);

		//When
		ActivityEntity activityEntity2 = new ActivityEntity();
		activityEntity2.setPlayground(authUserPlayground);
		activityEntity2.setElementId(elementEntity.getId()+"");
		activityEntity2.setElementPlayground(elementEntity.getPlayground());
		activityEntity2.setPlayerEmail(regularEmail);
		activityEntity2.setPlayerPlayground(authUserPlayground);
		activityEntity2.setType("GetMessages");
		AdMessage[] messages = this.restTemplate.postForObject(this.activitiesUrl+"/{userPlayground}/{email}", activityEntity2, AdMessage[].class, authUserPlayground, regularEmail);

		//Then
		AdMessage adMessage = messages[0];
		assertThat(adMessage.getMessage()).isEqualTo("bla bla bla bla");
	}

	@Test(expected=OKException.class)
	public void testGetMessagesActivityWithoutElement() throws Throwable
	{
		//Given
		String regularEmail = "player@user.com";

		//When
		ActivityEntity activityEntity = new ActivityEntity();
		activityEntity.setPlayground(authUserPlayground);
		activityEntity.setPlayerEmail(regularEmail);
		activityEntity.setPlayerPlayground(authUserPlayground);
		activityEntity.setType("GetMessages");
		activityEntity.setElementId("1");
		activityEntity.setElementPlayground(authUserPlayground);
		try {
			this.restTemplate.postForObject(this.activitiesUrl+"/{userPlayground}/{email}", activityEntity, AdMessage[].class, authUserPlayground, regularEmail);
		}
		catch (HttpClientErrorException ex) {
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_FOUND, ex.getMessage());
		}

		//Then
	}


	@Test(expected=OKException.class)
	public void testPostNewMessageActivityByManagerUnsuccessfully() throws Throwable {
		//Given
		createAuthroizedUser(Role.MANAGER, authManagerEmail);
		//And database contains AdBoard element
		ElementEntity adBoardElement = new ElementEntity();
		adBoardElement.setName("Ad Board");
		adBoardElement.setType("AdBoard");
		ElementEntity temp = this.elementService.addNewElement(authManagerEmail, authUserPlayground, adBoardElement);

		// When
		ActivityTO postActivity = new ActivityTO();
		postActivity.setType("PostNewMessage");
		postActivity.setElementId(String.valueOf(temp.getId()));
		postActivity.setElementPlayground(temp.getPlayground());
		Map<String, Object> actTemp = new HashMap<String,Object>();
		actTemp.put("message", "The message");
		postActivity.setAttributes(actTemp);

		try {
			this.restTemplate.postForObject(this.activitiesUrl+"/{userPlayground}/{email}",
					postActivity, Nullable.class, authUserPlayground, authManagerEmail);
		}catch (HttpClientErrorException ex) {
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.UNAUTHORIZED, ex.getMessage());
		}
		//Then
	}

	@Test
	public void testShowSolutionActivitySuccessfully() throws Throwable {
		//Given
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("Question");
		elementEntity.setType("MultipleChoiceQuestion");
		Map<String,Object> moreAttributes = new HashMap<String,Object>();
		moreAttributes.put("question", "the question");
		moreAttributes.put("a", "answer a");
		moreAttributes.put("b", "answer b");
		moreAttributes.put("c", "answer c");
		moreAttributes.put("d", "answer d");
		moreAttributes.put("answer", "a");
		elementEntity.setAttributes(moreAttributes);
		ElementEntity temp = elementService.addNewElement(authManagerEmail, authUserPlayground, elementEntity);

		//When
		ActivityTO postActivity = new ActivityTO();
		postActivity.setType("ShowSolution");
		postActivity.setElementId(String.valueOf(temp.getId()));
		postActivity.setElementPlayground(temp.getPlayground());
		Answer answer = this.restTemplate.postForObject(this.activitiesUrl+"/{userPlayground}/{email}",
				postActivity, Answer.class, authUserPlayground, authPlayerEmail);

		//Then
		assertThat(answer)
		.isNotNull()
		.extracting("answer")
		.containsExactly("a");		
	}

	@Test(expected=OKException.class)
	public void testShowSolutionOfElementWithoutAnswerAttribute() throws Throwable {
		//Given
		createAuthroizedUser(Role.PLAYER,authPlayerEmail);
		createAuthroizedUser(Role.MANAGER,authManagerEmail);
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setName("AdBoard");
		elementEntity.setType("MultipleChoiceQuestion");
		Map<String,Object> moreAttributes = new HashMap<String,Object>();
		moreAttributes.put("question", "the question");
		moreAttributes.put("a", "answer a");
		moreAttributes.put("b", "answer b");
		moreAttributes.put("c", "answer c");
		moreAttributes.put("d", "answer d");
		elementEntity.setAttributes(moreAttributes);
		ElementEntity temp = elementService.addNewElement(authManagerEmail, authUserPlayground, elementEntity);

		//When
		ActivityTO postActivity = new ActivityTO();
		postActivity.setType("ShowSolution");
		postActivity.setElementId(String.valueOf(temp.getId()));
		postActivity.setElementPlayground(temp.getPlayground());

		try {
			this.restTemplate.postForObject(this.activitiesUrl+"/{userPlayground}/{email}",
					postActivity, Answer.class, authUserPlayground, authPlayerEmail);
		}catch (HttpClientErrorException ex) {
			HttpStatus httpStatus = ex.getStatusCode();
			this.checkHttpStatusCode(httpStatus, HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
		}
		//Then
	}

	private void createAuthroizedUser(Role role,String userEmail) throws Throwable {
		UserEntity userEntity = new UserEntity(userEmail,authUserPlayground);
		userEntity.setRole(role.name().toLowerCase());
		int confirmCode = this.userService.addUser(userEntity).getConfirmCode();
		userEntity.setConfirmCode(confirmCode);
		this.userService.confirmUser(userEntity);
	}
}