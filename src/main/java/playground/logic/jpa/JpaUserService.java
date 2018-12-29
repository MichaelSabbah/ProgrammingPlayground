package playground.logic.jpa;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.BasicAuthentication;
import playground.aop.PlaygroundLogger;
import playground.dal.UserDao;
import playground.logic.Entities.User.UserEntity;
//import playground.logic.exceptions.UserNotExistsException;
//<<<<<<< HEAD
//import playground.logic.exceptions.InvalidConfirmCodeException;
//import playground.logic.exceptions.NotAuthorizeUserException;
//import playground.logic.exceptions.UserExistsException;
//import playground.logic.exceptions.UserNotExistsException;
//=======
import playground.logic.exceptions.conflict.UserAlreadyExistsException;
import playground.logic.exceptions.notacceptable.InvalidConfirmCodeException;
import playground.logic.exceptions.notfound.UserNotFoundException;
import playground.logic.exceptions.unauthorized.UnauthorizedUserException;
//>>>>>>> efd8261bf159a3c66d95d838e79bc51a5e5c8eb5
import playground.logic.services.UserService;

@Service 
public class JpaUserService implements UserService{
	private UserDao userDao;
	private Random rnd;
	private final int VERIFICATION_RANGE = 100;

	@Autowired
	public JpaUserService(UserDao userDao) {
		this.userDao = userDao;
		this.rnd = new Random();
	}

	@Override
	@Transactional
	@PlaygroundLogger
	public UserEntity addUser(UserEntity user) throws Throwable {
		if(!this.userDao.existsById(user.getEmail())) {
			user.setConfirmCode(this.rnd.nextInt(VERIFICATION_RANGE));
			return this.userDao.save(user);
		}
		throw new UserAlreadyExistsException("User Already Exists");
	}

	@Override
	@PlaygroundLogger
	public UserEntity confirmUser(UserEntity user) throws Throwable {
		//TODO - Michael - Add query in DAO to find user by id(email) and confirmCode (Check if aspect can be good)
		UserEntity userToVerify = this.userDao.findById(user.getEmail())
				.orElseThrow(()->
				new UserNotFoundException("User not found"));

		if(!(userToVerify.getPlayground().equals(user.getPlayground())))
		{
			throw new UserNotFoundException("User Not Exists");
		}

		if(userToVerify.getConfirmCode() == user.getConfirmCode()) {
			userToVerify.setConfirmCode(-1);	
		}
		else {
			throw new InvalidConfirmCodeException("not valid code");
		}

		return this.userDao.save(userToVerify);
	}

	@Override
	@PlaygroundLogger
	public UserEntity loginUser(UserEntity user) throws Throwable {
		//TODO - Michael - Add query in DAO to find user by id(email) and confirmCode (Check if aspect can be good)
		UserEntity userToVerify = this.userDao.findById(user.getEmail()).orElseThrow(()->new UserNotFoundException("User not found"));

		//Think again about playground checking
		if(!userToVerify.getPlayground().equals(user.getPlayground())) {
			throw new UserNotFoundException("User Not Exists");
		}

		if(userToVerify.getConfirmCode() != -1) {
			throw new UnauthorizedUserException("User is not confirmed");
		}

		return userToVerify;
	}

	@Override
	@Transactional
	@BasicAuthentication
	@PlaygroundLogger
	public void updateUser(String userEmail,String userPlayground, UserEntity updateUser){
		UserEntity dbUser = this.userDao.findByEmailAndPlayground(userEmail, userPlayground).get(0);
		if(updateUser.getUsername()!= null && updateUser.getUsername() != dbUser.getUsername())
		{
			dbUser.setUsername(updateUser.getUsername());
		}

		if(updateUser.getAvatar()!= null && updateUser.getAvatar() != dbUser.getAvatar())
		{
			dbUser.setAvatar(updateUser.getAvatar());
		}

		this.userDao.save(dbUser);
	}

	@Override
	@Transactional
	@PlaygroundLogger
	public void cleanAll() {
		this.userDao.deleteAll();
	}
}