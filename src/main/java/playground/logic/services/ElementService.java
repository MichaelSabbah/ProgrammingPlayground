package playground.logic.services;

import java.util.List;

import playground.logic.Entities.Element.ElementEntity;
import playground.logic.exceptions.ElementAlreadyExistsException;
import playground.logic.exceptions.ElementNotFoundException;

public interface ElementService {

	public ElementEntity addNewElement(String userEmail,String userPlayground,ElementEntity element);//Manager
	public ElementEntity updateElement(String userEmail,String userPlaygorund,String playground, String id, ElementEntity entityUpdates) throws ElementNotFoundException;//Manager
	public ElementEntity getElementById(String userEmail,String userPlaygorund,String playground, String id) throws ElementNotFoundException;//Normal User
	public List<ElementEntity> getAllElements(String userEmail,String userPlaygorund,int size, int page);//Normal User
	public List<ElementEntity> getElementsByDistance(String userEmail,String userPlaygorund,int x, int y, int distance,int size,int page) throws NumberFormatException;//Normal User
	public List<ElementEntity> getElementsByAttribute(String userEmail,String userPlaygorund,String attributeName, String value,int size, int page) throws ElementNotFoundException;////Normal User
	public void cleanAll();//No need

}
