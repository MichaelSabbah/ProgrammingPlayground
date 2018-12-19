package playground.logic.jpa;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import playground.dal.UserDao;
import playground.logic.Entities.UserEntity;
import playground.logic.Exceptions.InvalidConfirmCodeException;
import playground.logic.Exceptions.UserExistsException;
import playground.logic.Exceptions.UserNotExistsException;
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
		throw new UserExistsException("User Already Exists");
	}

	@Override
	public UserEntity confirmUser(UserEntity user) throws Exception {
		UserEntity userToVerify = this.userDao.findById(user.getEmail()).orElseThrow(()->new UserNotExistsException("User Not Exists"));
		if(!(userToVerify.getPlayground().equals(user.getPlayground())))
		{
			throw new UserNotExistsException("User Not Exists");
		}
		
		if(userToVerify.getConfirmCode() == user.getConfirmCode()) {
			userToVerify.setConfirmCode(-1);	
		}
		else {
			throw new InvalidConfirmCodeException();
		}
		
		this.userDao.save(userToVerify);
		return userToVerify;
	}

	@Override
	public UserEntity loginUser(UserEntity user) throws Exception {
		UserEntity userToVerify = this.userDao.findById(user.getEmail()).orElseThrow(()->new UserNotExistsException("User Not Exists"));
		if(userToVerify == null)
		{
			throw new UserNotExistsException("User Not Exists");
		}
		
		if(!userToVerify.getPlayground().equals(user.getPlayground())) {
			throw new UserNotExistsException("User Not Exists");
		}
		return userToVerify;
	}

	@Override
	@Transactional
	public void updateUser(String userEmail,String userPlayground, UserEntity updateUser)throws Exception {
		UserEntity localUser = this.userDao.findById(updateUser.getEmail()).orElseThrow(()->new UserNotExistsException("User Not Exists"));

		if(updateUser.getPlayground()!= null && updateUser.getPlayground() != localUser.getPlayground())
		{
			localUser.setPlayground(updateUser.getPlayground());
		}

		if(updateUser.getUsername()!= null && updateUser.getUsername() != localUser.getUsername())
		{
			localUser.setUsername(updateUser.getUsername());
		}

		if(updateUser.getAvatar()!= null && updateUser.getAvatar() != localUser.getAvatar())
		{
			localUser.setAvatar(updateUser.getAvatar());
		}

		if(updateUser.getRole()!= null && updateUser.getRole() != localUser.getRole())
		{
			localUser.setRole(updateUser.getRole());
		}

		if(updateUser.getPoints() != localUser.getPoints())
		{
			localUser.setPoints(updateUser.getPoints());
		}
		
		this.userDao.save(localUser);
	}

	@Override
	@Transactional
	public void cleanAll() {
		this.userDao.deleteAll();

	}
}