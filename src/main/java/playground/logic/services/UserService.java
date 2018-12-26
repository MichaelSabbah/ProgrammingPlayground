package playground.logic.services;

import playground.logic.Entities.User.UserEntity;

public interface UserService {

	public UserEntity addUser(UserEntity user) throws Exception;//No need
	public UserEntity confirmUser(UserEntity user) throws Exception;//No need
	public UserEntity loginUser(UserEntity user) throws Exception;//No need
	public void updateUser(String userEmail,String userPlayground, UserEntity updateUser)throws Exception;//Admin
	public void cleanAll();//No need

}
