package playground.logic.helpers;

public class PlaygroundConsts {

	//General app consts
	public static final String APP_NAME = "PLAYGROUND";
	public static final String PLAYGROUND_NAME = "Programming Playground";

	//More attributes keys
	public static final String ANSWER_KEY = "answer";
	public static final String MESSAGE_CREATED_DATE_KEY = "created";

	//Attributes keys
	public static final String NAME_KEY = "name";
	public static final String TYPE_KEY = "type";

	//Points
	public static final int MULTICHOICES_QUESTION_POINTS = 5;
	public static final int WRONG_ANSWER_POINTS = 1;
	public static final int SHOW_SOLUTION_COST = 2; 

	//Activity types
	public static final String POST_MESSAGE_TYPE_NAME = "PostNewMessage";

	//Default values
	public static final int SIZE_DEFAULT = 10;
	public static final int PAGE_DEFAULT = 0;


	//Code verification 
	public static final String VERFICATION_MAIL_SUBJECT = "ProgrammingPlayground - Verification code";
	public static final String VERFICATION_MAIL_TEXT= "Your ProgrammingPlayground Verifcation code is:\n";
	public static final int START_VERIFICATION_RANGE = 100000;
	public static final int END_VERIFICATION_RANGE = 900000;

	//Feedback consts
	public static final String GOOD_FEEDBACK = "good";
	public static final String BAD_FEEDBACK = "bad";

}
