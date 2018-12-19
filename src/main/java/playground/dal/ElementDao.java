package playground.dal;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import playground.logic.Entities.ElementEntity;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, String>{

	public List<ElementEntity> findAllByJsonAttributesContaining(
			@Param("jsonAttribute") String jsonAttribute, 
			Pageable pageable);
}
