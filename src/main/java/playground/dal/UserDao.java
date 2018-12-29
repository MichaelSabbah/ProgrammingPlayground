package playground.dal;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import playground.logic.Entities.User.UserEntity;

public interface UserDao extends CrudRepository<UserEntity, String>{
	
	public List<UserEntity> findByEmailAndPlaygroundAndConfirmCode(
			@Param("email") String email,
			@Param("playground") String playground,
			@Param("confirmCode") int confirmCode);
	
	public List<UserEntity> findByEmailAndPlaygroundAndConfirmCodeAndRole(
			@Param("email") String email, 
			@Param("playground") String playground,
			@Param("confirmCode") int confirmCode, 
			@Param("role") String role);
	
	public List<UserEntity> findByEmailAndPlayground(
			@Param("email") String email,
			@Param("playground") String playground);
}
