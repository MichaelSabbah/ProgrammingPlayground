package playground.logic.services;

import java.util.List;

import playground.logic.Entities.ElementEntity;
import playground.logic.Exceptions.ElementAlreadyExistsException;
import playground.logic.Exceptions.ElementNotFoundException;

public interface ElementService {

	public ElementEntity addNewElement(String userEmail,String userPlayground,ElementEntity element) throws ElementAlreadyExistsException;//Admin
	public ElementEntity updateElement(String userEmail,String userPlaygorund,String playground, String id, ElementEntity entityUpdates) throws ElementNotFoundException;//Admin
	public ElementEntity getElementById(String userEmail,String userPlaygorund,String playground, String id) throws ElementNotFoundException;//Normal User
	public List<ElementEntity> getAllElements(String userEmail,String userPlaygorund,int size, int page);//Normal User
	public List<ElementEntity> getElementsByDistance(String userEmail,String userPlaygorund,int x, int y, int distance,int size,int page) throws ElementNotFoundException;//Normal User
	public List<ElementEntity> getElementsByAttribute(String userEmail,String userPlaygorund,String attributeName, String value,int size, int page);////Normal User
	public void cleanAll();//No need

}
