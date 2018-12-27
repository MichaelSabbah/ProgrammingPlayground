/*package playground.logic.stubs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;

import playground.logic.UserEntity;
import playground.logic.UserService;
import playground.logic.Exceptions.InvalidConfirmCodeException;
import playground.logic.Exceptions.UserExistsException;
import playground.logic.Exceptions.UserNotExistsException;
/*
//@Service
public class DummyUserService implements UserService{

	private List<UserEntity> users;
	Random rand;


	@PostConstruct
	public void init()
	{
		this.users = Collections.synchronizedList(new ArrayList<>());
		this.rand =new Random();
	}

	@Override
	public UserEntity addUser(UserEntity user) throws Exception {
		if(this.users.contains(user)==false)
		{
			this.users.add(user);

			int code = rand.nextInt(100);
			user.setConfirmCode(code);
			return user;
		}
		throw new UserExistsException("User Already Exists");
	}

	@Override
	public UserEntity confirmUser(UserEntity user) throws Exception {
		int index = this.users.indexOf(user);
		if(index > -1)
		{
			UserEntity localUser = this.users.get(index);
			if(localUser.getConfirmCode() != -1 && localUser.getConfirmCode() == user.getConfirmCode() && localUser.getPlayground().equals(user.getPlayground()))
			{
				localUser.setConfirmCode(-1);
				return localUser;
			}
			else
			{
				throw new InvalidConfirmCodeException();
			}
		}
		throw new UserNotExistsException("User Not Exists");
	}

	@Override
	public UserEntity loginUser(UserEntity user) throws Exception {
		int index = this.users.indexOf(user);
		if(index == -1)
		{
			throw new UserNotExistsException("User Not Exists");
		}
		return this.users.get(index);
	}

	@Override
	public void updateUser(UserEntity user) throws Exception {

		synchronized (this.users) {
			int index = this.users.indexOf(user);
			if(index == -1)
			{
				throw new UserNotExistsException("User Not Exists");
			}
			UserEntity localUser = this.users.get(index);
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

	}

	@Override
	public void cleanAll() {
		this.users.clear();

	}

}*/
