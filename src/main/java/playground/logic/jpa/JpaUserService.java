package playground.logic.jpa;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.BasicAuthentication;
import playground.dal.UserDao;
import playground.logic.Entities.User.UserEntity;
import playground.logic.exceptions.conflict.UserAlreadyExistsException;
import playground.logic.exceptions.notacceptable.InvalidConfirmCodeException;
import playground.logic.exceptions.notfound.UserNotFoundException;
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
	public UserEntity addUser(UserEntity user) throws Exception {
		if(!this.userDao.existsById(user.getEmail())) {
			user.setConfirmCode(this.rnd.nextInt(VERIFICATION_RANGE));
			return this.userDao.save(user);
		}
		throw new UserAlreadyExistsException("User Already Exists");
	}

	@Override
	public UserEntity confirmUser(UserEntity user) throws Exception {
		UserEntity userToVerify = this.userDao.findById(user.getEmail()).orElseThrow(()->new UserNotFoundException("User Not Exists"));
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
		
		this.userDao.save(userToVerify);
		return userToVerify;
	}

	@Override
	public UserEntity loginUser(UserEntity user) throws Exception {
		UserEntity userToVerify = this.userDao.findById(user.getEmail()).orElseThrow(()->new UserNotFoundException("User Not Exists"));
		if(userToVerify == null)
		{
			throw new UserNotFoundException("User Not Exists");
		}
		
		if(!userToVerify.getPlayground().equals(user.getPlayground())) {
			throw new UserNotFoundException("User Not Exists");
		}
		return userToVerify;
	}

	@Override
	@BasicAuthentication
	@Transactional
	public void updateUser(String userEmail,String userPlayground, UserEntity updateUser)throws Exception {
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
	public void cleanAll() {
		this.userDao.deleteAll();

	}
}