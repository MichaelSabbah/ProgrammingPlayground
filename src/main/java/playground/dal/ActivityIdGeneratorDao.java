package playground.dal;

import org.springframework.data.repository.CrudRepository;

import playground.logic.Entities.Activity.ActivityIdGenerator;

public interface ActivityIdGeneratorDao extends CrudRepository<ActivityIdGenerator, Integer> {

}
