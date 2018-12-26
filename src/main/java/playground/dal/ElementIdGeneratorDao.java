package playground.dal;

import org.springframework.data.repository.CrudRepository;

import playground.logic.Entities.Element.ElementIdGenerator;

public interface ElementIdGeneratorDao extends CrudRepository<ElementIdGenerator, Integer>{

}
