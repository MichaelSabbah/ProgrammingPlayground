package playground.logic.exceptions.notacceptable;

public class InvalidAnswerException extends NotAcceptableException{

	private static final long serialVersionUID = 1L;

	public InvalidAnswerException(){}
	
	public InvalidAnswerException(String message){
		super(message);
	}
}
