package playground.logic.Exceptions;

public class UserNotExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserNotExistsException(String message)
	{
		super(message);
	}
}
