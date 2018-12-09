package playground.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import playground.logic.ElementEntity;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, String>{
	
	public List<ElementEntity> findAllByJsonAttributesContaining(
			@Param("jsonAttribute") String jsonAttribute, 
			Pageable pageable);
	
//	@Query("SELECT u FROM User u WHERE u.?1 = ?2 LIMIT ?3 OFFSET ?4")
//	public List<ElementEntity> findAllByAttribute(
//			String attribute,
//			String value,
//			int size,
//			int page);

}
