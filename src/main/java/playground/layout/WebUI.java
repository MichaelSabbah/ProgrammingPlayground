package playground.layout;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import playground.layout.ActivityTO;
import playground.layout.ElementTO;
import playground.layout.UserTO;
import playground.logic.ElementAlreadyExistsException;
import playground.logic.ElementEntity;
import playground.logic.ElementNotFoundException;
import playground.logic.ElementService;
import playground.layout.NewUserForm;

@RestController
public class WebUI {
	
	/*private String defaultUserName;	
	@Value("${name.of.user.to.be.greeted:Anonymous}")
	public void setDefaultUserName(String defaultUserName) {
		this.defaultUserName = defaultUserName;
	}*/
	
	private ElementService elementService;
	
	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/users",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public UserTO register (@RequestBody NewUserForm newUserForm) {
		return new UserTO();
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/users/confirm/{playground}/{email}/{code}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO verifyRegistration(@PathVariable("playground") String playground,
									@PathVariable("playground") String email,
									@PathVariable("playground") String code) {
		return new UserTO();
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/users/login/{playground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserTO login(@PathVariable("playground") String playground,
						@PathVariable("email") String email) {
		return new UserTO();
	}
	
	@RequestMapping(
			method=RequestMethod.PUT,
			path="/playground/users/{playground}/{email}",
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateUser(@PathVariable("playground") String playground,
							   @PathVariable("email") String email,
							   @RequestBody UserTO userTo){
		
	}
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/playground/elements/{userPlayground}/{email}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO addNewElement(@PathVariable("userPlayground") String userPlayground,
								   @PathVariable("email") String email,
								   @RequestBody ElementTO elementTo) throws ElementAlreadyExistsException {
		
		
		//Check if the user is manager
		
		ElementEntity elementEntity = elementService.addNewElement(elementTo.toEntity());		
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
		
		elementService.updateElement(playground, id, elementTo.toEntity());
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/{playground}/{id}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO getElement(@PathVariable("userPlayground") String userPlayground,
										@PathVariable("email") String email,
										@PathVariable("playground") String playground,
										@PathVariable("id") String id) throws ElementNotFoundException {
		
			return new ElementTO(elementService.getElementById(playground, id));
	}
		
	@RequestMapping(
			method=RequestMethod.GET,
			path="/playground/elements/{userPlayground}/{email}/all",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getAllElementsByPlayer(@PathVariable("userPlayground") String userPlayground,
										@PathVariable("email") String email) {
		return 
		this.elementService.getAllElements() 
			.stream() 
			.map(ElementTO::new) 
			.collect(Collectors.toList()) 
			.toArray(new ElementTO[0]);		
	}
	
	@RequestMapping(
			method = RequestMethod.GET,
			path = "/playground/elements/{userPlayground}/{email}/near/{x}/{y}/{distance}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getNearByElementsByLocation(@PathVariable("userPlayground")String userPlayground,
			@PathVariable("email")String email,@PathVariable("x")String x,
			@PathVariable("y")String y,@PathVariable("distance")String distance) throws NumberFormatException, ElementNotFoundException{
		return 
		this.elementService.getElementsByDistance(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(distance)) // MessageEntity List
			.stream() 
			.map(ElementTO::new) 
			.collect(Collectors.toList()) 
			.toArray(new ElementTO[0]);	
	}
	
	@RequestMapping(
			method = RequestMethod.GET,
			path = "/playground/elements/{userPlayground}/{email}/search/{attributeName}/{value}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementTO[] getElementsByAttribute(@PathVariable("userPlayground")String userPlayground,
			@PathVariable("email")String email,@PathVariable("attributeName")String attributeName,
			@PathVariable("value")String value){
		
		return elementService.getElementsByAttribute(attributeName, value)
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
	
	
}








