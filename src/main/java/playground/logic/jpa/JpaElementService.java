package playground.logic.jpa;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.BasicAuthentication;
import playground.aop.ManagerAuthentication;
import playground.dal.ElementDao;
import playground.dal.ElementIdGeneratorDao;
import playground.logic.Entities.Element.ElementEntity;
import playground.logic.Entities.Element.ElementId;
import playground.logic.Entities.Element.ElementIdGenerator;
import playground.logic.exceptions.ElementNotFoundException;
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
	public ElementEntity updateElement(String userEmail,String userPlaygorund,String playground, String id, ElementEntity entityUpdates)
			throws ElementNotFoundException {

		ElementEntity existing = null;
		
		ElementId elementId = new ElementId();
		elementId.setId(Integer.parseInt(id));
		elementId.setPlayground(playground);
		
		existing = this.elements.findById(elementId)//this.elements.findByIdAndPlayground(Integer.parseInt(id), playground)
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
	public ElementEntity getElementById(String userEmail,String userPlaygorund,String playground, String id) throws ElementNotFoundException {
		
		ElementId elementId = new ElementId();
		elementId.setId(Integer.parseInt(id));
		elementId.setPlayground(playground);
		
		ElementEntity element = this.elements.findById(elementId)//this.elements.findByIdAndPlayground(Integer.parseInt(id), playground)
				.orElseThrow(()->
				 new ElementNotFoundException("no element with playground: " + playground + " and id: " + id));
		
		if(element == null) {
			throw new ElementNotFoundException("no element with playground: " + playground + " and id: " + id );
		}
		
		return element;
		
		
//				this.elements.findById(elementId)
//				.orElseThrow(()->
//				new ElementNotFoundException(
//						"no element with id: " + elementId));
	}

	@Override
	@Transactional(readOnly=true)
	@BasicAuthentication
	public List<ElementEntity> getAllElements(String userEmail,String userPlayground,int size, int page) {
		//return
		Page<ElementEntity> elementsReturned = this.elements.findAll(
				PageRequest.of(page, size, Direction.DESC, "name"));
		
		System.err.println(elementsReturned.toString());
		System.err.println("content: " + elementsReturned.getContent().toString());
		return elementsReturned.getContent();
		
		//.getContent();
	}

	@Override
	@Transactional(readOnly=true)
	@BasicAuthentication
	public List<ElementEntity> getElementsByDistance(String userEmail,String userPlaygorund,int x, int y, int distance,int size,int page) throws ElementNotFoundException {
//		List<ElementEntity> elements = this.elements.findAll(
//				PageRequest.of(page, size))
//				.getContent();
		if(distance < 0) {
			throw new ElementNotFoundException("Not valid distance");
			//TODO - Michael - Check about the exception should be thrown here
		}
		
		List<ElementEntity> elementsByDistance = this.elements.findAllByXBetween(
				x - distance, x + distance, PageRequest.of(page, size));

//		for(int i = 0 ; i < elements.size() ; i++) {
//			if(!isInDistance(x, y, elements.get(i).getX(), elements.get(i).getY(), distance)) {
//				elements.remove(i);
//			}
//		}

		return elementsByDistance;
	}

	@Override
	@Transactional(readOnly=true)
	@BasicAuthentication
	public List<ElementEntity> getElementsByAttribute(String userEmail,String userPlaygorund,String attributeName, String value,int size, int page) throws ElementNotFoundException {		
		String jsonAttribute = "\"" + attributeName + "\""  + ":" + "\"" + value + "\"";
		List<ElementEntity> elementsReturned = this.elements.findAllByJsonAttributesContaining(jsonAttribute,PageRequest.of(page, size));
		if(elementsReturned.size() == 0) {
			throw new ElementNotFoundException("Element with this attribute name and value is not existing");
		}
		return elementsReturned;

	}

	@Override
	@Transactional
	public void cleanAll() {
		elements.deleteAll();

	}

	private boolean isInDistance(double x1, double y1, double x2, double y2, double distance){
		return Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1),2)) <= distance;
	}

}
