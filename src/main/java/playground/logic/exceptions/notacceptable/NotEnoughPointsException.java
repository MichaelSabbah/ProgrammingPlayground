package playground.logic.exceptions.notacceptable;

public class NotEnoughPointsException extends NotAcceptableException {

	private static final long serialVersionUID = 1L;

	public NotEnoughPointsException(){}
	
	public NotEnoughPointsException(String message){super(message);}
	
}
