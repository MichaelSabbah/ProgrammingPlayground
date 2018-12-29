package playground.logic.services;

import playground.logic.Entities.Activity.ActivityEntity;

public interface ActivityService {
	public Object invokeActivity(String userEmail,String userPlayground,String elementId,String elementPlayground,ActivityEntity activityEntity) throws Throwable;
}
