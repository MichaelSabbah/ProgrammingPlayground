package playground.logic;

import java.util.List;

public interface ElementService {
	
	public ElementEntity addNewElement(ElementEntity element) throws ElementAlreadyExistsException;
	public ElementEntity updateElement(String playground, String id, ElementEntity entityUpdates) throws ElementNotFoundException;
	public ElementEntity getElementById(String playground, String id) throws ElementNotFoundException;
	public List<ElementEntity> getAllElements(int size, int page); //Without pagination, 
	public List<ElementEntity> getElementsByDistance(int x, int y, int distance,int size,int page) throws ElementNotFoundException;
	public List<ElementEntity> getElementsByAttribute(String attributeName, String value,int size, int page);
	public void cleanup();
	
}
