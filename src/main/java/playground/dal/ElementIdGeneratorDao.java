package playground.dal;

import org.springframework.data.repository.CrudRepository;

import playground.logic.helpers.ElementIdGenerator;

public interface ElementIdGeneratorDao extends CrudRepository<ElementIdGenerator, Integer>{

}
