package playground.logic.services;

public interface EmailService {
	public void sendEmail(String email, String mailMessageSubject, String mailMessageText) throws Throwable;
}
