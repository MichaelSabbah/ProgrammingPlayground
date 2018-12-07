package playground.logic;

public class UserNotExists extends Exception {
	
	private static final long serialVersionUID = 1L;

	public UserNotExists()
	{
		super("user not exists");
	}
}
