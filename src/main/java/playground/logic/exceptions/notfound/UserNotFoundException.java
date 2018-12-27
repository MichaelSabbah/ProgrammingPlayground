package playground.logic.exceptions.notfound;

public class UserNotFoundException extends NotFoundException {

	private static final long serialVersionUID = 1L;
	
	public UserNotFoundException() {}
	public UserNotFoundException(String message){super(message);}
}
