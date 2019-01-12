package playground.logic.jpa;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.BasicAuthentication;
import playground.aop.PlaygroundLogger;
import playground.dal.UserDao;
import playground.logic.Entities.User.UserEntity;
import playground.logic.Entities.User.UserId;
import playground.logic.exceptions.conflict.UserAlreadyExistsException;
import playground.logic.exceptions.notacceptable.InvalidConfirmCodeException;
import playground.logic.exceptions.notfound.UserNotFoundException;
import playground.logic.exceptions.unauthorized.UnauthorizedUserException;
import playground.logic.helpers.PlaygroundConsts;
import playground.logic.services.EmailService;
import playground.logic.services.UserService;

@Service 
public class JpaUserService implements UserService{
	private UserDao userDao;
	private EmailService emailService;
	private Random rnd;

	@Autowired
	public JpaUserService(UserDao userDao, EmailService emailService) {
		this.userDao = userDao;
		this.emailService = emailService;
		this.rnd = new Random();
	}

	@Override
	@Transactional
	@PlaygroundLogger
	public UserEntity addUser(UserEntity user) throws Throwable {

		user.setPlayground(PlaygroundConsts.PLAYGROUND_NAME);
		UserId userId = new UserId();
		userId.setEmail(user.getEmail());
		userId.setPlayground(user.getPlayground());
		if(!this.userDao.existsById(userId)) {
			int generatedCode = generateConfirmCode();
			user.setConfirmCode(generatedCode);
			UserEntity tempUser =  this.userDao.save(user);
			emailService.sendEmail(
					user.getEmail(), 
					PlaygroundConsts.VERFICATION_MAIL_SUBJECT, 
					PlaygroundConsts.VERFICATION_MAIL_TEXT + generatedCode);

			return tempUser;
		}
		throw new UserAlreadyExistsException("User Already Exists");
	}

	@Override
	@PlaygroundLogger
	public UserEntity confirmUser(UserEntity user) throws Throwable {

		UserId userId = new UserId();
		userId.setEmail(user.getEmail());
		userId.setPlayground(user.getPlayground());

		UserEntity userToVerify = this.userDao.findById(userId)
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

		UserId userId = new UserId();
		userId.setEmail(user.getEmail());
		userId.setPlayground(user.getPlayground());

		UserEntity userToVerify = this.userDao.findById(userId)
				.orElseThrow(()->
				new UserNotFoundException("User not found"));

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

	private int generateConfirmCode()
	{
		return this.rnd.nextInt(PlaygroundConsts.END_VERIFICATION_RANGE)+PlaygroundConsts.START_VERIFICATION_RANGE;
	}
}