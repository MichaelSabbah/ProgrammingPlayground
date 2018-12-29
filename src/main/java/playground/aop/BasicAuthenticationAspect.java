package playground.aop;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.dal.UserDao;
import playground.logic.Entities.User.UserEntity;
import playground.logic.exceptions.unauthorized.UnauthorizedUserException;

@Component
@Aspect
public class BasicAuthenticationAspect {

	private UserDao userDao;

	@Autowired
	private void setUserService(UserDao userDao){
		this.userDao = userDao;
	}

	@Before("@annotation(playground.aop.BasicAuthentication) && args(userEmail,userPlayground,..)")
	public void validateAuthorizedUser(JoinPoint joinPoint,String userEmail,String userPlayground) throws Throwable {
		List<UserEntity> user = userDao.findByEmailAndPlaygroundAndConfirmCode(userEmail,userPlayground,-1);
		if(user.size() == 0){
			throw new UnauthorizedUserException();
		}
	}
}
