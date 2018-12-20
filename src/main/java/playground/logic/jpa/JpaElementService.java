package playground.logic.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.aop.BasicAuthentication;
import playground.dal.ElementDao;
import playground.logic.Entities.ElementEntity;
import playground.logic.Exceptions.ElementAlreadyExistsException;
import playground.logic.Exceptions.ElementNotFoundException;
import playground.logic.services.ElementService;

@Service
public class JpaElementService implements ElementService{

	private ElementDao elements;

	@Autowired
	public void setElementService(ElementDao elements) {
		this.elements = elements;
	}

	@Override
	@Transactional
	public ElementEntity addNewElement(String userEmail,String userPlaygorund,ElementEntity element) throws ElementAlreadyExistsException {
		if(!elements.existsById(element.getElementId())){
			return this.elements.save(element);
		}
		throw new ElementAlreadyExistsException();
	}

	@Override
	@Transactional
	public ElementEntity updateElement(String userEmail,String userPlaygorund,String playground, String id, ElementEntity entityUpdates)
			throws ElementNotFoundException {

		ElementEntity existing = null;
		ElementEntity tempElement = new ElementEntity();
		tempElement.setPlayground(playground);
		tempElement.setId(id);

		String elementId = playground + "@" + id;
		if(elements.existsById(elementId)){
			existing = this.elements.findById(elementId)
					.orElseThrow(()->
					new ElementNotFoundException("no element with id: " + elementId));
		}else {
			throw new ElementNotFoundException("no element with id: " + elementId);
		}

		if(entityUpdates.getAttributes() != null && !entityUpdates.getAttributes().isEmpty()) {
			existing.setAttributes(entityUpdates.getAttributes());
		}

		if(entityUpdates.getCreateDate() != null &&
				!entityUpdates.getCreateDate().equals(existing.getCreateDate())) {
			existing.setCreateDate(entityUpdates.getCreateDate());
		}

		if(entityUpdates.getCreatorEmail() != null &&
				!entityUpdates.getCreatorEmail().equals(existing.getCreatorEmail())) {	
			existing.setCreatorEmail(entityUpdates.getCreatorEmail());
		}

		if(entityUpdates.getCreatorPlayground() != null &&
				!entityUpdates.getCreatorPlayground().equals(existing.getCreatorPlayground())) {	
			existing.setCreatorPlayground(entityUpdates.getCreatorPlayground());
		}

		if(entityUpdates.getExpirationDate() != null &&
				!entityUpdates.getExpirationDate().equals(existing.getExpirationDate())) {
			existing.setExpirationDate(entityUpdates.getExpirationDate());
		}

		if(entityUpdates.getName() != null  &&
				entityUpdates.equals(existing.getName())) {   
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
		String elementId = playground + "@" + id;

		return 
				this.elements.findById(elementId)
				.orElseThrow(()->
				new ElementNotFoundException(
						"no element with id: " + elementId));
	}

	@Override
	@Transactional(readOnly=true)
	@BasicAuthentication
	public List<ElementEntity> getAllElements(String userEmail,String userPlaygorund,int size, int page) {
		return
				this.elements.findAll(
						PageRequest.of(page, size, Direction.DESC, "createDate"))
				.getContent();
	}

	@Override
	@Transactional(readOnly=true)
	@BasicAuthentication
	public List<ElementEntity> getElementsByDistance(String userEmail,String userPlaygorund,int x, int y, int distance,int size,int page) throws ElementNotFoundException {
		List<ElementEntity> elements = this.elements.findAll(
				PageRequest.of(page, size))
				.getContent();

		for(int i = 0 ; i < elements.size() ; i++) {
			if(!isInDistance(x, y, elements.get(i).getX(), elements.get(i).getY(), distance)) {
				elements.remove(i);
			}
		}

		return elements;
	}

	@Override
	@Transactional(readOnly=true)
	@BasicAuthentication
	public List<ElementEntity> getElementsByAttribute(String userEmail,String userPlaygorund,String attributeName, String value,int size, int page) {		
		String jsonAttribute = "\"" + attributeName + "\""  + ":" + "\"" + value + "\"";
		return this.elements.findAllByJsonAttributesContaining(jsonAttribute,PageRequest.of(page, size));

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
