package playground.logic.jpa;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.dal.UserDao;
import playground.logic.UserEntity;
import playground.logic.UserService;
import playground.logic.Exceptions.InvalidConfirmCodeException;
import playground.logic.Exceptions.UserExistsException;
import playground.logic.Exceptions.UserNotExistsException;

@Service 
public class JpaUserService implements UserService{
	private UserDao users;
	private Random rnd;
	private final int VERIFICATION_RANGE = 100;

	@Autowired
	public JpaUserService(UserDao users) {
		this.users = users;
		this.rnd = new Random();
	}

	@Override
	@Transactional
	public UserEntity addUser(UserEntity user) throws Exception {
		if(!this.users.existsById(user.getEmail())) {
			user.setConfirmCode(this.rnd.nextInt(VERIFICATION_RANGE));
			return this.users.save(user);
		}
		throw new UserExistsException("User Already Exists");
	}

	@Override
	public UserEntity confirmUser(UserEntity user) throws Exception {
		UserEntity userToVerify = this.users.findById(user.getEmail()).orElseThrow(()->new UserNotExistsException("User Not Exists"));
		if(userToVerify.getConfirmCode() == user.getConfirmCode()) {
			userToVerify.setConfirmCode(-1);
		}
		else {
			throw new InvalidConfirmCodeException();
		}
		this.updateUser(userToVerify);
		return userToVerify;
	}

	@Override
	public UserEntity loginUser(UserEntity user) throws Exception {
		UserEntity userToVerify = this.users.findById(user.getEmail()).orElseThrow(()->new UserNotExistsException("User Not Exists"));
		if(!userToVerify.getPlayground().equals(user.getPlayground())) {
			throw new UserNotExistsException("User Not Exists");
		}
		if(!userToVerify.getEmail().equals(user.getEmail())) {
			throw new UserNotExistsException("User Not Exists");
		}
		return userToVerify;
	}

	@Override
	@Transactional
	public void updateUser(UserEntity user) throws Exception {


		UserEntity localUser = this.users.findById(user.getEmail()).orElseThrow(()->new UserNotExistsException("User Not Exists"));

		if(user.getPlayground()!= null && user.getPlayground() != localUser.getPlayground())
		{
			localUser.setPlayground(user.getPlayground());
		}

		if(user.getUsername()!= null && user.getUsername() != localUser.getUsername())
		{
			localUser.setUsername(user.getUsername());
		}

		if(user.getAvatar()!= null && user.getAvatar() != localUser.getAvatar())
		{
			localUser.setAvatar(user.getAvatar());
		}

		if(user.getRole()!= null && user.getRole() != localUser.getRole())
		{
			localUser.setRole(user.getRole());
		}

		if(user.getPoints() != localUser.getPoints())
		{
			localUser.setPoints(user.getPoints());
		}

	}

	@Override
	@Transactional
	public void cleanAll() {
		this.users.deleteAll();

	}
}