package playground.logic.exceptions;

public class NotAuthorizeUserException extends Exception{

	private static final long serialVersionUID = 3084555940193442485L;

	public NotAuthorizeUserException() {
		super();
	}

	public NotAuthorizeUserException(String message) {
		super(message);
	}
}
