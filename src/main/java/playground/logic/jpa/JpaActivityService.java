package playground.logic.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import playground.aop.IsElementExists;
import playground.aop.PlayerAuthentication;
import playground.aop.PlaygroundLogger;
import playground.dal.ActivityDao;
import playground.dal.ActivityIdGeneratorDao;
import playground.logic.Entities.Activity.ActivityEntity;
import playground.logic.Entities.Activity.ActivityIdGenerator;
import playground.logic.exceptions.notfound.ActivityTypeNotFoundException;
import playground.logic.services.ActivityService;
import playground.plugins.ActivityPlugin;

@Service
public class JpaActivityService implements ActivityService {

	private ApplicationContext spring;
	private ActivityDao activityDao;
	private ActivityIdGeneratorDao activityIdGeneratorDao;

	@Autowired
	public JpaActivityService(ApplicationContext spring, ActivityDao activityDao,
			ActivityIdGeneratorDao activityIdGeneratorDao) {
		this.spring = spring;
		this.activityDao = activityDao;
		this.activityIdGeneratorDao = activityIdGeneratorDao;
	}

	@Override
	@IsElementExists
	@PlayerAuthentication
	@PlaygroundLogger
	public Object invokeActivity(String userEmail,String userPlayground,String elementId,String elementPlayground,ActivityEntity activityEntity) throws Exception {
		String activityType = activityEntity.getType();
		if(activityType == null || activityType.isEmpty()){
			throw new ActivityTypeNotFoundException("NO_ACTIVITY_TYPE_SPECIFIED");
		}
		try{
			int id = activityIdGeneratorDao.save(new ActivityIdGenerator()).getId();
			activityEntity.setId(id);

			activityDao.save(activityEntity);

			String className = "playground.plugins." + activityType + "Plugin";
			Class<?> theClass = Class.forName(className);
			ActivityPlugin activityPlugin = (ActivityPlugin) this.spring.getBean(theClass);
			return activityPlugin.activate(activityEntity);
		}
		catch (ClassNotFoundException e) {
			throw new ActivityTypeNotFoundException("NO_SUCH_ACTIVITY_TYPE");
		}
	}
}
