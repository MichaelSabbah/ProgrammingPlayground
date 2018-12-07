package playground.logic;

public class ElementNotFoundException extends Exception{

	private static final long serialVersionUID = 3084555940193442485L;

	public ElementNotFoundException() {
		super();
	}

	public ElementNotFoundException(String message) {
		super(message);
	}

	public ElementNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ElementNotFoundException(Throwable cause) {
		super(cause);
	}

}
