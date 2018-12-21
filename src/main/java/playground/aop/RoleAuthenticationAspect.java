package playground.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.dal.UserDao;

@Component
@Aspect
public class RoleAuthenticationAspect {

	private UserDao userDao;
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	@Before("@annotation(playground.aop.RoleAuthentication)")
	public void roleValidation() {
		//userDao.findById(arg0)
	}
	
	
}
