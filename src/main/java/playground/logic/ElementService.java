package playground.logic;

import java.util.List;

public interface ElementService {
	
	public ElementEntity addNewElement(ElementEntity element) throws ElementAlreadyExistsException;
	public ElementEntity updateElement(String playground, String id, ElementEntity entityUpdates) throws ElementNotFoundException;
	public ElementEntity getElementById(String playground, String id) throws ElementNotFoundException;
	public List<ElementEntity> getAllElements(); //Without pagination, 
	public List<ElementEntity> getElementsByDistance(int x, int y, int distance) throws ElementNotFoundException;
	public List<ElementEntity> getElementsByAttribute(String attributeName, String value);
	public void cleanup();
	
}
