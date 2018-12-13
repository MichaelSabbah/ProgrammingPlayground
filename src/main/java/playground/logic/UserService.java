package playground.logic;

public interface UserService {

	public UserEntity addUser(UserEntity user) throws Exception;
	public UserEntity confirmUser(UserEntity user) throws Exception;
	public UserEntity loginUser(UserEntity user) throws Exception;
	public void updateUser(UserEntity user)throws Exception;
	public void cleanAll();

}
