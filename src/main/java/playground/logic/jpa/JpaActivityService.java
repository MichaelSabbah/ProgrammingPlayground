//<<<<<<< HEAD
//package playground.logic.jpa;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Service;
//
//import playground.aop.IsElementExists;
//import playground.aop.PlayerAuthentication;
//import playground.dal.ActivityDao;
//import playground.dal.ActivityIdGeneratorDao;
//import playground.logic.Entities.Activity.ActivityEntity;
//import playground.logic.Entities.Activity.ActivityIdGenerator;
//import playground.logic.exceptions.NoActivityTypeException;
//import playground.logic.services.ActivityService;
//import playground.plugins.ActivityPlugable;
//
//@Service
//public class JpaActivityService implements ActivityService {
//
//	private ApplicationContext spring;
//	private ActivityDao activityDao;
//	private ActivityIdGeneratorDao activityIdGeneratorDao;
//	
//	@Autowired
//	public JpaActivityService(ApplicationContext spring, ActivityDao activityDao,
//			ActivityIdGeneratorDao activityIdGeneratorDao) {
//		this.spring = spring;
//		this.activityDao = activityDao;
//		this.activityIdGeneratorDao = activityIdGeneratorDao;
//	}
//
//	@Override
//	@IsElementExists
//	@PlayerAuthentication
//	public Object invokeActivity(String userEmail,String userPlayground,String elementId,String elementPlayground,ActivityEntity activityEntity) throws Exception {
//		String activityType = activityEntity.getType();
//		if(activityType == null || activityType.isEmpty())
//		{
//			throw new NoActivityTypeException("NO_ACTIVITY_TYPE_SPECIFIED");
//		}
//		try
//		{
//			int id = activityIdGeneratorDao.save(new ActivityIdGenerator()).getId();
//			activityEntity.setId(id);
//			
//			activityDao.save(activityEntity);
//			
//			String className = "playground.plugins." + activityType + "Plugin";
//			Class<?> theClass = Class.forName(className);
//			ActivityPlugable activityPlugin = (ActivityPlugable) this.spring.getBean(theClass);
//			return activityPlugin.ActivateActivity(activityEntity);
//		}
//		catch (ClassNotFoundException e) {
//			throw new NoActivityTypeException("NO_SUCH_ACTIVITY_TYPE");
//		}
//	}
//
//}
//=======
package playground.logic.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import playground.aop.IsElementExists;
import playground.dal.ActivityDao;
import playground.dal.ActivityIdGeneratorDao;
import playground.logic.Entities.Activity.ActivityEntity;
import playground.logic.Entities.Activity.ActivityIdGenerator;
import playground.logic.exceptions.notfound.ActivityTypeNotFoundException;
import playground.logic.services.ActivityService;
import playground.plugins.ActivityPlugable;

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
	//@PlayerAuthentication
	public Object invokeActivity(String userEmail,String userPlayground,String elementId,String elementPlayground,ActivityEntity activityEntity) throws Exception {
		String activityType = activityEntity.getType();
		if(activityType == null || activityType.isEmpty())
		{
			throw new ActivityTypeNotFoundException("NO_ACTIVITY_TYPE_SPECIFIED");
		}
		try
		{
			int id = activityIdGeneratorDao.save(new ActivityIdGenerator()).getId();
			activityEntity.setId(id);
			
			activityDao.save(activityEntity);
			
			String className = "playground.plugins." + activityType + "Plugin";
			Class<?> theClass = Class.forName(className);
			ActivityPlugable activityPlugin = (ActivityPlugable) this.spring.getBean(theClass);
			return activityPlugin.ActivateActivity(activityEntity);
		}
		catch (ClassNotFoundException e) {
			throw new ActivityTypeNotFoundException("NO_SUCH_ACTIVITY_TYPE");
		}
	}

}
//>>>>>>> efd8261bf159a3c66d95d838e79bc51a5e5c8eb5
