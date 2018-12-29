package playground.logic.exceptions.unauthorized;

public class UnauthorizedUserException extends UnauthorizedException{

	private static final long serialVersionUID = 1L;

	public UnauthorizedUserException() {}
	public UnauthorizedUserException(String message) {super(message);}
}
