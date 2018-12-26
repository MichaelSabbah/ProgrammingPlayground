package playground.layout;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.Exceptions.ElementNotFoundException;
import playground.logic.Exceptions.NotAuthorizeUserException;
import playground.logic.services.ElementService;
import playground.logic.services.UserService;
import playground.layout.NewUserForm;
import playground.layout.to.ActivityTO;
import playground.layout.to.ElementTO;
import playground.layout.to.UserTO;

@RestController
public class WebUI {

	private ElementService elementService;
	private UserService userService;

	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	@Autowired
	private void setUserService(UserService userService){
		this.userService = userService;
	}
	
	@RequestMapping(//V
			method=RequestMethod.POST,
			path="/playground/users",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public UserTO register (@RequestBody NewUserForm newUserForm) throws Exception {
		UserEntity userEntity = newUserForm.toUserEntity();
		return new UserTO(this.userService.addUser(userEntity));
	}

	@RequestMapping(//V
			method=RequestMethod.GET,
			path="/playground/users/confirm/{playground}/{email}/{code}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO verifyRegistration(@PathVariable("playground") String playground,
			@PathVariable("email") String email,
			@PathVariable("code") String code) throws Exception {
		UserEntity userEntity = new UserEntity(email,playground,Integer.parseInt(code));
		return new UserTO(this.userService.confirmUser(userEntity));
	}

	@RequestMapping(//V
			method=RequestMethod.GET,
			path="/playground/users/login/{playground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO login(@PathVariable("playground") String playground,
			@PathVariable("email") String email) throws Exception {
		UserEntity userEntity = new UserEntity(email,playground);
		return new UserTO(this.userService.loginUser(userEntity));
	}

	@RequestMapping(//?
			method=RequestMethod.PUT,
			path="/playground/users/{playground}/{email}",
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateUser(@PathVariable("playground") String playground,
			@PathVariable("email") String email,
			@RequestBody UserTO userTo) throws Exception{
		UserEntity userEntity = userTo.toUserEntity();
		userEntity.setEmail(email);
		userEntity.setPlayground(playground);
		this.userService.updateUser(email,playground,userEntity);

	}

	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/elements/{userPlayground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO addNewElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@RequestBody ElementTO elementTo) throws NotAuthorizeUserException{
		ElementEntity elementEntity;
			elementEntity = elementService.addNewElement(email,userPlayground,elementTo.toEntity());	
		return new ElementTO(elementEntity);
	}

	@RequestMapping(
			method=RequestMethod.PUT,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("playground") String playground,
			@PathVariable("id") String id,
			@RequestBody ElementTO elementTo) throws ElementNotFoundException{
		elementService.updateElement(email,userPlayground,playground, id, elementTo.toEntity());
	}

	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO getElement(@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email,
			@PathVariable("playground") String playground,
			@PathVariable("id") String id) throws ElementNotFoundException {

		return new ElementTO(elementService.getElementById(email,userPlayground,playground, id));
	}

	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/all",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElementsByPlayer(@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page,
			@PathVariable("userPlayground") String userPlayground,
			@PathVariable("email") String email) {
		return 
				this.elementService.getAllElements(email,userPlayground,size,page) 
				.stream() 
				.map(ElementTO::new) 
				.collect(Collectors.toList()) 
				.toArray(new ElementTO[0]);		
	}

	@RequestMapping(
			method = RequestMethod.GET,
			path = "/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getNearElementsByLocation(
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page,
			@PathVariable("userPlayground")String userPlayground,
			@PathVariable("email")String email,@PathVariable("x")Integer x,
			@PathVariable("y")Integer y,@PathVariable("distance")Integer distance) throws NumberFormatException, ElementNotFoundException{
		return 
				this.elementService.getElementsByDistance(email,userPlayground,x, y, distance,size,page) // MessageEntity List
				.stream() 
				.map(ElementTO::new) 
				.collect(Collectors.toList()) 
				.toArray(new ElementTO[0]);	
	}

	@RequestMapping(
			method = RequestMethod.GET,
			path = "/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getElementsByAttribute(
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page,
			@PathVariable("userPlayground")String userPlayground,
			@PathVariable("email")String email,@PathVariable("attributeName")String attributeName,
			@PathVariable("value")String value){

		return elementService.getElementsByAttribute(email,userPlayground,attributeName, value, size, page)
				.stream()
				.map(ElementTO::new)
				.collect(Collectors.toList())
				.toArray(new ElementTO[0]);

	}

	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/activities/{userPlayground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public Object postActivity(@RequestBody ActivityTO activityTO,@PathVariable("userPlayground")String userPlayground,
			@PathVariable("email")String email) {
		
		return new ActivityTO();
	}


	
//	@ExceptionHandler
//	@ResponseStatus(HttpStatus.NOT_FOUND)
//	public ErrorMessage ExceptionHandler (Exception e) {
//		String message = e.getMessage();
//		if (message == null) {
//			message = "There is no relevant message";
//		}
//		return new ErrorMessage(message);
//	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorMessage NotAuthorizeUserExceptionHandler (NotAuthorizeUserException e) {
		String message = e.getMessage();
		if (message == null) {
			message = "Not Authorize User";
		}
		return new ErrorMessage(message);
	}
}
