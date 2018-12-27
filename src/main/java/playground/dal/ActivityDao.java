package playground.dal;

import org.springframework.data.repository.PagingAndSortingRepository;

import playground.logic.Entities.Activity.ActivityEntity;
import playground.logic.Entities.Activity.ActivityId;

public interface ActivityDao extends PagingAndSortingRepository<ActivityEntity, ActivityId> {

}
