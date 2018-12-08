package playground.logic.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playground.dal.ElementDao;
import playground.logic.ElementAlreadyExistsException;
import playground.logic.ElementEntity;
import playground.logic.ElementNotFoundException;
import playground.logic.ElementService;

//@Service
public class JpaElementService implements ElementService{
	
	private ElementDao elements;
	
	@Autowired
	public void setElementService(ElementDao elements) {
		this.elements = elements;
	}
	
	@Override
	@Transactional
	public ElementEntity addNewElement(ElementEntity element) throws ElementAlreadyExistsException {
		if(!elements.existsById(element.getElementId())){
			return this.elements.save(element);
		}
		throw new ElementAlreadyExistsException();
	}

	@Override
	@Transactional
	public ElementEntity updateElement(String playground, String id, ElementEntity entityUpdates)
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
	@Transactional
	public ElementEntity getElementById(String playground, String id) throws ElementNotFoundException {
		String elementId = playground + "@" + id;
		
		return 
			this.elements.findById(elementId)
			.orElseThrow(()->
				new ElementNotFoundException(
					"no element with id: " + elementId));
	}

	@Override
	public List<ElementEntity> getAllElements() {
		
		return elements
				.findAll();
	}

	@Override
	public List<ElementEntity> getElementsByDistance(int x, int y, int distance) throws ElementNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ElementEntity> getElementsByAttribute(String attributeName, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cleanup() {
		elements.deleteAll();
		
	}

}
