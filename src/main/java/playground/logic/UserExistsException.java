package playground.logic;

public class UserExistsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public UserExistsException()
	{
		super("user already exists");
	}
 
}
