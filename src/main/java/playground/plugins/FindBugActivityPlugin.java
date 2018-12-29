
package playground.plugins;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.InternalError;
import playground.dal.ElementDao;
import playground.dal.UserDao;
import playground.logic.Entities.Activity.ActivityEntity;
import playground.logic.Entities.Element.ElementEntity;
import playground.logic.Entities.Element.ElementId;
import playground.logic.Entities.User.UserEntity;
import playground.logic.exceptions.notacceptable.InvalidAnswerException;
import playground.logic.exceptions.notfound.ElementNotFoundException;
import playground.logic.exceptions.notfound.UserNotFoundException;
import playground.logic.helpers.PlaygroundConsts;

@Component
public class FindBugActivityPlugin implements ActivityPlugin {
	
	private ElementDao elementDao;
	private UserDao userDao;
	private ObjectMapper jackson;
	
	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}
	
	@Autowired
	public FindBugActivityPlugin(ElementDao elementDao, UserDao userDao) {
		this.elementDao = elementDao;
		this.userDao = userDao;
	}

	@Override
	public Object activate(ActivityEntity activityEntity) throws InvalidAnswerException, ElementNotFoundException, UserNotFoundException {
		Answer userAnswer;
		Feedback feedback = new Feedback();
		
		try {
			userAnswer = this.jackson.readValue( 
					activityEntity.getJsonAttributes(),
					Answer.class);

		} catch (JsonParseException e) {
			throw new InternalError("Json parsing error");
		} catch (JsonMappingException e) {
			throw new InternalError("Json mapping error");
		} catch (IOException e) {
			throw new InternalError("IO error");
		} catch (Throwable e) {
			throw new InternalError(e.getMessage());
		}
		
		//Answer must be with the 'answer' attribute
		if (userAnswer == null || userAnswer.getAnswer() == null) {
			throw new InvalidAnswerException("Answer is invalid");
		}			
		
		//Check if user answer is correct
		
		//Get relevant element
		ElementId elementId = new ElementId();
		String playground = activityEntity.getElementPlayground();
		int id = Integer.parseInt(activityEntity.getElementId());
		elementId.setId(id);
		elementId.setPlayground(playground);
		
		ElementEntity element = elementDao.findById(elementId)
				.orElseThrow(() ->
				 new ElementNotFoundException("no element with playground: " + playground  + " and id: " + id));
		
		String correctAnswerAsString = (String)element.getAttributes().get(PlaygroundConsts.ANSWER_KEY);
		if("null".equals(correctAnswerAsString)) {
			throw new InvalidAnswerException("Answer is invalid");
		}
		
		//Get the relevant user
		UserEntity user = userDao.findById(activityEntity.getPlayerEmail())
				.orElseThrow(()->
				new UserNotFoundException("No use with email: " + activityEntity.getPlayerEmail()));
		
		//Get the correct answer
		Answer correctAnswer = new Answer();
		correctAnswer.setAnswer(correctAnswerAsString);
		
		//Update user points
		if(userAnswer.equals(correctAnswer)) {
			feedback.setFeedback("You right");
			user.setPoints(user.getPoints() + PlaygroundConsts.MULTICHOICES_QUESTION_POINTS);
		}else {
			feedback.setFeedback("Wrong answer");
			long userNewPoints = user.getPoints() - PlaygroundConsts.WRONG_ANSWER_POINTS;
			if(userNewPoints < 0) {
				user.setPoints(0);
			}else {
				user.setPoints(userNewPoints);
			}
		}
		
		//Update user with new points
		userDao.save(user);
		
		return feedback;
	}

}
