package playground.aop;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import playground.dal.UserDao;
import playground.logic.Entities.User.UserEntity;
import playground.logic.exceptions.NotAuthorizeUserException;
import playground.logic.helpers.Role;

//@Component
//@Aspect
public class ManagerAuthenticationAspect {

	private UserDao userDao;
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	@Before("@annotation(playground.aop.ManagerAuthentication) && args(email,playground,..)")
	public void roleValidation(JoinPoint joinPoint, String email, String playground) throws Throwable {
		List<UserEntity> user = userDao.findByEmailAndPlaygroundAndConfirmCodeAndRole(email,playground,-1,Role.MANAGER.name());
		
		if(user.size() == 0) {
			throw new NotAuthorizeUserException("The user is not a manager");
		}
	}
	
	
}
