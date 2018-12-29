package playground.plugins;

import playground.dal.ActivityDao;
import playground.logic.Entities.Activity.ActivityEntity;

public class PostNewMessagePlugin implements ActivityPlugin {
	
	private ActivityDao activityDao;
	
	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}
	
	@Override
	public Object activate(ActivityEntity activityEntity) {
		return activityDao.save(activityEntity);
	}
}
