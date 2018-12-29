package playground.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import playground.logic.Entities.Activity.ActivityEntity;
import playground.logic.Entities.Activity.ActivityId;

public interface ActivityDao extends PagingAndSortingRepository<ActivityEntity, ActivityId> {
	public Page<ActivityEntity> findAllByType(@Param("type")String type,Pageable pageable);

}
