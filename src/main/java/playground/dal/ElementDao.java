package playground.dal;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import playground.logic.Entities.Element.ElementEntity;
import playground.logic.Entities.Element.ElementId;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, ElementId>{

	public List<ElementEntity> findAllByJsonAttributesContaining(
			@Param("jsonAttribute") String jsonAttribute, 
			Pageable pageable);
	
	public List<ElementEntity> findByIdAndPlayground(
			@Param("id") int id,
			@Param("playground") String playground);

}
