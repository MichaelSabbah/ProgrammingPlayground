package playground.logic.Exceptions;

public class ElementAlreadyExistsException extends Exception{

	private static final long serialVersionUID = 4646082382096574539L;

	public ElementAlreadyExistsException() {
	}

	public ElementAlreadyExistsException(String message) {
		super(message);
	}
}
