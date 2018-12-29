package playground.plugins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.dal.ActivityDao;
import playground.logic.Entities.Activity.ActivityEntity;

@Component
public class PostNewMessagePlugin implements ActivityPlugin {

	private ActivityDao activityDao;
	@Autowired
	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}

	@Override
	public Object activate(ActivityEntity activityEntity) {
		return activityDao.save(activityEntity);
	}
}
