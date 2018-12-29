package playground.logic.jpa;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javassist.NotFoundException;
import playground.aop.BasicAuthentication;
import playground.aop.ManagerAuthentication;
import playground.aop.PlaygroundLogger;
import playground.dal.ElementDao;
import playground.dal.ElementIdGeneratorDao;
import playground.logic.Entities.Element.ElementEntity;
import playground.logic.Entities.Element.ElementId;
import playground.logic.Entities.Element.ElementIdGenerator;
import playground.logic.exceptions.notacceptable.InvalidFormatException;
import playground.logic.exceptions.notacceptable.NotAcceptableException;
import playground.logic.exceptions.notfound.ElementNotFoundException;
import playground.logic.exceptions.notfound.UserNotFoundException;
import playground.logic.exceptions.unauthorized.UnauthorizedUserException;
import playground.logic.services.ElementService;

@Service
public class JpaElementService implements ElementService{

	private ElementDao elements;
	private ElementIdGeneratorDao elementIdGeneratorDao;

	@Autowired
	public void setElementService(ElementDao elements) {
		this.elements = elements;
	}

	@Autowired
	public void setElementIdGeneratorDao(ElementIdGeneratorDao elementIdGeneratorDao) {
		this.elementIdGeneratorDao = elementIdGeneratorDao;
	}

	@Override
	@Transactional
	@ManagerAuthentication
	@PlaygroundLogger
	public ElementEntity addNewElement(String userEmail,String userPlayground,ElementEntity element) {	

		//Get element id from idGenerator
		int id = elementIdGeneratorDao.save(new ElementIdGenerator()).getId();

		//Set element id - compose key
		element.setPlayground(userPlayground);
		element.setId(id);

		//Initialize element fields
		element.setCreatorEmail(userEmail);
		element.setCreatorPlayground(userPlayground);
		element.setCreateDate(new Date());

		return this.elements.save(element);
	}

	@Override
	@Transactional
	@ManagerAuthentication
	@PlaygroundLogger
	public ElementEntity updateElement(String userEmail,String userPlaygorund,String playground, String id, ElementEntity entityUpdates)
			throws Throwable {

		ElementEntity existing = null;

		ElementId elementId = new ElementId();
		elementId.setId(Integer.parseInt(id));
		elementId.setPlayground(playground);
		
		existing = this.elements.findById(elementId)
		.orElseThrow(()->
		new ElementNotFoundException("no element with playground: " + playground + " and id: " + id));

		if(entityUpdates.getAttributes() != null && !entityUpdates.getAttributes().isEmpty()) {
			existing.setAttributes(entityUpdates.getAttributes());
		}

		if(entityUpdates.getExpirationDate() != null &&
				!entityUpdates.getExpirationDate().equals(existing.getExpirationDate())) {
			existing.setExpirationDate(entityUpdates.getExpirationDate());
		}

		if(entityUpdates.getName() != null  &&
				entityUpdates.getName().equals(existing.getName())) {   
			existing.setName(entityUpdates.getName());
		}

		if(entityUpdates.getType() != null &&
				!entityUpdates.getType().equals(existing.getType())) { 
			existing.setType(entityUpdates.getType());
		}

		return this.elements.save(existing);
	}

	@Override
	@Transactional(readOnly=true)
	@BasicAuthentication
	@PlaygroundLogger
	public ElementEntity getElementById(String userEmail,String userPlaygorund,String playground, String id) throws Throwable {

		ElementId elementId = new ElementId();
		elementId.setId(Integer.parseInt(id));
		elementId.setPlayground(playground);
		
		ElementEntity element = this.elements.findById(elementId)//this.elements.findByIdAndPlayground(Integer.parseInt(id), playground)
				.orElseThrow(()->
				 new ElementNotFoundException("No element with playground: " + playground + " and id: " + id));
		return element;
	}

	@Override
	@Transactional(readOnly=true)
	@BasicAuthentication
	@PlaygroundLogger
	public List<ElementEntity> getAllElements(String userEmail,String userPlayground,int size, int page) {
		Page<ElementEntity> elementsReturned = this.elements.findAll(
				PageRequest.of(page, size, Direction.DESC, "name"));
		return elementsReturned.getContent();
	}

	@Override
	@Transactional(readOnly=true)
	@BasicAuthentication
	@PlaygroundLogger
	public List<ElementEntity> getElementsByDistance(String userEmail,String userPlaygorund,int x, int y, int distance,int size,int page) throws Throwable {
		if(distance < 0) {
			throw new InvalidFormatException("Invalid distance");
		}

		return this.elements.findAllByXBetweenAndYBetween(
				x - distance, x + distance, y - distance, y + distance,
				PageRequest.of(page, size))
				.getContent();
	}

	@Override
	@Transactional(readOnly=true)
	@BasicAuthentication
	@PlaygroundLogger
	public List<ElementEntity> getElementsByAttribute(String userEmail,String userPlaygorund,String attributeName, String value,int size, int page) throws Throwable {		
		String jsonAttribute = "\"" + attributeName + "\""  + ":" + "\"" + value + "\"";
		List<ElementEntity> elementsReturned = this.elements.findAllByJsonAttributesContaining(jsonAttribute,PageRequest.of(page, size));
		if(elementsReturned.size() == 0) {
			throw new ElementNotFoundException("Element with this attribute name and value is not existing");
		}
		return elementsReturned;

	}

	@Override
	@Transactional
	@PlaygroundLogger
	public void cleanAll() {
		elements.deleteAll();

	}
}
