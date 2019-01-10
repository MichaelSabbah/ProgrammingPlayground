package playground.plugins;

import java.util.Date;

import org.springframework.stereotype.Component;

import playground.logic.Entities.Activity.ActivityEntity;
import playground.logic.helpers.PlaygroundConsts;

@Component
public class PostNewMessagePlugin implements ActivityPlugin {

	@Override
	public Object activate(ActivityEntity activityEntity) {
		activityEntity.getAttributes().put(PlaygroundConsts.MESSAGE_CREATED_DATE_KEY, new Date());
		return null;
	}
}
