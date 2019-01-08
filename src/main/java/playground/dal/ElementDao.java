package playground.dal;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import playground.logic.Entities.Element.ElementEntity;
import playground.logic.Entities.Element.ElementId;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, ElementId>{
	
	public List<ElementEntity> findAllByJsonAttributesContainingAndExpirationDateAfter(
			@Param("jsonAttribute") String jsonAttribute, 
			@Param("expirationDate")Date date,
			Pageable pageable);
	
	
	public List<ElementEntity> findAllByNameEqualsAndExpirationDateAfter(
			@Param("name") String name,
			@Param("expirationDate")Date date,
			Pageable pageable);
	
	public List<ElementEntity> findAllByTypeEqualsAndExpirationDateAfter(
			@Param("type") String type,
			@Param("expirationDate")Date date,
			Pageable pageable);
	
	public Optional<ElementEntity> findByIdAndPlaygroundAndExpirationDateAfter(
			@Param("id") int id,
			@Param("playground") String playground,
			@Param("expirationDate")Date date);

	public Page<ElementEntity> findAllByXBetweenAndYBetweenAndExpirationDateAfter(
			double xMin,
			double xMax,
			double yMin,
			double yMax,
			Date date,
			Pageable pageable);
	
	public Page<ElementEntity> findAllByExpirationDateAfter( 
			Date date,
			Pageable pageable);
}
