package playground.plugins;

import org.springframework.stereotype.Component;

import playground.logic.Entities.Activity.ActivityEntity;

@Component
public class PostNewMessagePlugin implements ActivityPlugin {

	@Override
	public Object activate(ActivityEntity activityEntity) {
		//Empty - Only save the activity to DB
		return null;
	}
}
