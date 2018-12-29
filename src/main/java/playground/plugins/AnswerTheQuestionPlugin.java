package playground.plugins;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.InternalError;

import playground.dal.ActivityDao;
import playground.dal.UserDao;
import playground.logic.Entities.Activity.ActivityEntity;
import playground.logic.exceptions.notacceptable.InvalidAnswerException;

@Component
public class AnswerTheQuestionPlugin implements ActivityPlugin{
	
	private final int SCORE = 5;
	
	private ActivityDao activityDao;
	private UserDao userDao;
	private ObjectMapper jackson;
	
	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}
	
	@Autowired
	public AnswerTheQuestionPlugin(ActivityDao activityDao, UserDao userDao) {
		this.activityDao = activityDao;
		this.userDao = userDao;
	}

	@Override
	public Object activate(ActivityEntity activityEntity) throws InvalidAnswerException {
		
			Answer answer;
			try {
					
				answer = this.jackson.readValue( 
						activityEntity.getJsonAttributes(),
						Answer.class);
				
			} catch (JsonParseException e) {
				throw new InternalError("Json parsing error");
			} catch (JsonMappingException e) {
				throw new InternalError("Json mapping error");
			} catch (IOException e) {
				throw new InternalError("IO error");
			}
			
			if (answer.getAnswer() == null) {
				throw new InvalidAnswerException("Answer is invalid");
			}

		return activityDao.save(activityEntity);
	}
}
