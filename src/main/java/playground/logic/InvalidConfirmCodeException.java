package playground.logic;

public class InvalidConfirmCodeException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidConfirmCodeException()
	{
		super("not valid code");
	}

}
