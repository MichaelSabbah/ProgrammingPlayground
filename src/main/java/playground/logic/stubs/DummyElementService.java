package playground.logic.stubs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import playground.logic.ElementAlreadyExistsException;
import playground.logic.ElementEntity;
import playground.logic.ElementNotFoundException;
import playground.logic.ElementService;

@Service
public class DummyElementService implements ElementService{

	private List<ElementEntity> elements;
	
	@PostConstruct
	public void init() {
		this.elements = Collections.synchronizedList(new ArrayList<>());
	}

	@Override
	public ElementEntity addNewElement(ElementEntity element) throws ElementAlreadyExistsException {
		if(elements.contains(element))
			throw new ElementAlreadyExistsException("Element exists with id: playground: " + element.getPlayground()
													+"id: " + element.getId());
		elements.add(element);
		
		return element;
	}

	@Override
	public ElementEntity updateElement(String playground, String id, ElementEntity entityUpdates)
			throws ElementNotFoundException {
		ElementEntity existing = null;
		synchronized (elements) {
			ElementEntity tempElement = new ElementEntity();
			existing = null;
			tempElement.setPlayground(playground);
			tempElement.setId(id);
			int elementPosition = elements.indexOf(tempElement);
			if(elementPosition > -1){
				existing = elements.get(elementPosition);
			}else {
				throw new ElementNotFoundException("Not such element with playground: " + playground + 
													" id: " + id);
			}
			
			boolean dirty = false;
			
			if(entityUpdates.getAttributes() != null && !entityUpdates.getAttributes().isEmpty()) {
				existing.setAttributes(entityUpdates.getAttributes());
				dirty = true;
			}
			
			if(entityUpdates.getCreateDate() != null &&
			   !entityUpdates.getCreateDate().equals(existing.getCreateDate())) {
				
				existing.setCreateDate(entityUpdates.getCreateDate());
				dirty = true;
			}
			
			if(entityUpdates.getCreatorEmail() != null &&
			   !entityUpdates.getCreatorEmail().equals(existing.getCreatorEmail())) {
				
				existing.setCreatorEmail(entityUpdates.getCreatorEmail());
				dirty = true;
			}
			
			if(entityUpdates.getCreatorPlayground() != null &&
			   !entityUpdates.getCreatorPlayground().equals(existing.getCreatorPlayground())) {
				
			   existing.setCreatorPlayground(entityUpdates.getCreatorPlayground());
			   dirty = true;
			}
			
			if(entityUpdates.getExpirationDate() != null &&
			   !entityUpdates.getExpirationDate().equals(existing.getExpirationDate())) {
				
			   existing.setExpirationDate(entityUpdates.getExpirationDate());
		       dirty = true;
			}
			
			/*if(entityUpdates.getLocation() != null &&
			   !entityUpdates.getLocation().equals(existing.getLocation())) {
				
			   existing.setLocation(entityUpdates.getLocation());
			   dirty = true;
			}*/
			
			if(entityUpdates.getName() != null  &&
			   entityUpdates.equals(existing.getName())) {
			   
			   existing.setName(entityUpdates.getName());
			   dirty = true;
			}
			
			if(entityUpdates.getType() != null &&
			   !entityUpdates.getType().equals(existing.getType())) {
			   
			   existing.setType(entityUpdates.getType());
			   dirty = true;
			}
			if(dirty){
				elements.set(elementPosition,existing);
			}
		}
		return existing;
	}

	@Override
	public ElementEntity getElementById(String playground, String id) throws ElementNotFoundException {
		ElementEntity tempElement = new ElementEntity();
		tempElement.setPlayground(playground);
		tempElement.setId(id);
		int elementPosition = elements.indexOf(tempElement);
		if(elementPosition < 0)
			throw new ElementNotFoundException("No such element with playground: " + playground + 
					                           " and id: " + id);
		return elements.get(elementPosition);
	}

	@Override
	public List<ElementEntity> getAllElements(int size, int page) {
		return elements;
	}

	@Override
	public List<ElementEntity> getElementsByDistance(int x, int y, int distance,int size, int page) throws ElementNotFoundException {
		if(distance < 0)
			throw new ElementNotFoundException();
		List<ElementEntity> elementsByDistance = new ArrayList<>();
		for(ElementEntity element : elements){
			if(isInDistance(x,y,element.getX(),element.getY(),distance)) {
				elementsByDistance.add(element);
			}
		}
		return elementsByDistance;
	}

	@Override
	public List<ElementEntity> getElementsByAttribute(String attributeName, String value,int size, int page) {
		List<ElementEntity> elementsByAttribute = new ArrayList<>();
		for(ElementEntity element : elements){
			if(element.getAttributes().get(attributeName).equals(value)) {
				elementsByAttribute.add(element);
			}
		}
		return elementsByAttribute;
	}
	
	private boolean isInDistance(double x1, double y1, double x2, double y2, double distance){
		return Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1),2)) <= distance;
	}

	@Override
	public void cleanup() {
		elements.clear();
	}
}















