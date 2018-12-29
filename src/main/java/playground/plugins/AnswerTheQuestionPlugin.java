package playground.plugins;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.dal.ActivityDao;
import playground.dal.UserDao;
import playground.logic.Entities.Activity.ActivityEntity;
import playground.logic.exceptions.notacceptable.InvalidAnswerException;

@Component
public class AnswerTheQuestionPlugin implements ActivityPlugin{
	
	private final int SCORE = 5;
	private final String SOLUTION_KEY = "solution";
	
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
		
		String solution = (String)activityEntity.getAttributes().get(SOLUTION_KEY);
		
		if("null".equals(solution)) {
			throw new InvalidAnswerException("Answer not contain solution");
		}
		
		Answer answer = new Answer(solution);
		
		
		return activityDao.save(activityEntity);
	}
}
