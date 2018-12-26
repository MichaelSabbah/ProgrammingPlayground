package playground.logic.exceptions;

public class InvalidConfirmCodeException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidConfirmCodeException()
	{
		super();
	}
	
	public InvalidConfirmCodeException(String message)
	{
		super(message);
	}
	
	

}
