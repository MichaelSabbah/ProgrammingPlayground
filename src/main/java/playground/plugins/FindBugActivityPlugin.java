package playground.plugins;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.dal.ActivityDao;
import playground.logic.Entities.Activity.ActivityEntity;

public class FindBugActivityPlugin implements ActivityPlugin {
	
	private ActivityDao activityDao;
	private ObjectMapper jackson;
	private final int POINTS = 7;
	
	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}
	
	@Autowired
	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}

	@Override
	public Object activate(ActivityEntity activityEntity) {
		// TODO Auto-generated method stub
		return null;
	}

}
