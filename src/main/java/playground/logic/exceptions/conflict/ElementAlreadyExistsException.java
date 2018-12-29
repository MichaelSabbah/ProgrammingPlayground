package playground.logic.exceptions.conflict;

public class ElementAlreadyExistsException extends ConflictException{

	private static final long serialVersionUID = 1L;

	public ElementAlreadyExistsException() {}
	public ElementAlreadyExistsException(String message) {
		super(message);
	}
}
