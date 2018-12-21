package playground.dal;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import playground.logic.Entities.UserEntity;


public interface UserDao extends CrudRepository<UserEntity, String>{
	
	List<UserEntity> findByEmailAndPlaygroundAndConfirmCode(String email, String playground,int confirmCode);
	List<UserEntity> findByEmailAndPlayground(String email,String playground);
}
