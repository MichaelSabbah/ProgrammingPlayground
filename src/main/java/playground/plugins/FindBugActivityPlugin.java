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
import playground.logic.Entities.User.UserId;
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

		if (userAnswer == null || userAnswer.getAnswer() == null) {
			throw new InvalidAnswerException("Answer is invalid");
		}			

		ElementId elementId = new ElementId();
		String playground = activityEntity.getElementPlayground();
		int id = Integer.parseInt(activityEntity.getElementId());
		elementId.setId(id);
		elementId.setPlayground(playground);

		ElementEntity element = elementDao.findById(elementId).get();

		String correctAnswerAsString = (String)element.getAttributes().get(PlaygroundConsts.ANSWER_KEY);
		if("null".equals(correctAnswerAsString)) {
			throw new InvalidAnswerException("Answer is invalid");
		}

		UserId userId = new UserId();
		userId.setEmail(activityEntity.getPlayerEmail());
		userId.setPlayground(activityEntity.getPlayerPlayground());

		UserEntity user = userDao.findById(userId).get();

		Answer correctAnswer = new Answer();
		correctAnswer.setAnswer(correctAnswerAsString);

		if(userAnswer.equals(correctAnswer)) {
			feedback.setFeedback(PlaygroundConsts.GOOD_FEEDBACK);
			user.setPoints(user.getPoints() + PlaygroundConsts.MULTICHOICES_QUESTION_POINTS);
		}else {
			feedback.setFeedback(PlaygroundConsts.BAD_FEEDBACK);
			long userNewPoints = user.getPoints() - PlaygroundConsts.WRONG_ANSWER_POINTS;
			if(userNewPoints < 0) {
				user.setPoints(0);
			}else {
				user.setPoints(userNewPoints);
			}
		}
		userDao.save(user);
		return feedback;
	}
}
