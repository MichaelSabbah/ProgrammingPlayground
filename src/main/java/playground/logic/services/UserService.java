package playground.logic.services;

import playground.logic.Entities.User.UserEntity;

public interface UserService {

	public UserEntity addUser(UserEntity user) throws Throwable;
	public UserEntity confirmUser(UserEntity user) throws Throwable;
	public UserEntity loginUser(UserEntity user) throws Throwable;
	public void updateUser(String userEmail,String userPlayground, UserEntity updateUser)throws Throwable;
	public void cleanAll();

}
