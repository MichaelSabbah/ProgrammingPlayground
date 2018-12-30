package playground.logic.exceptions.conflict;

public class UserAlreadyExistsException extends ConflictException {

	private static final long serialVersionUID = 1L;

	public UserAlreadyExistsException() {};
	public UserAlreadyExistsException(String message) {
		super(message);
	}
}
