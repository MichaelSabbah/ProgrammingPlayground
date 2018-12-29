package playground.plugins;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.dal.ActivityDao;
import playground.logic.Entities.Activity.ActivityEntity;

@Component
public class AnswerTheQuestion implements ActivityPlugin{
	
	private ActivityDao activityDao;
	private ObjectMapper jackson;
	
	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}
	
	@Autowired
	public void setActivityDao(ActivityDao activityDao){
		this.activityDao = activityDao;
	}
	
	@Override
	public Object activate(ActivityEntity activityEntity) {
		
		
		return activityDao.save(activityEntity);
	}
}
