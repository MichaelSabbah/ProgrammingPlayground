package playground.logic.services;

import java.util.List;

import playground.logic.Entities.Element.ElementEntity;

public interface ElementService {

	public ElementEntity addNewElement(String userEmail,String userPlayground,ElementEntity element);
	public ElementEntity updateElement(String userEmail,String userPlaygorund,String playground, String id, ElementEntity entityUpdates) throws Throwable;
	public ElementEntity getElementById(String userEmail,String userPlaygorund,String playground, String id) throws Throwable;
	public List<ElementEntity> getAllElements(String userEmail,String userPlaygorund,int size, int page) throws Throwable;
	public List<ElementEntity> getElementsByDistance(String userEmail,String userPlaygorund,int x, int y, int distance,int size,int page) throws Throwable;
	public List<ElementEntity> getElementsByAttribute(String userEmail,String userPlaygorund,String attributeName, String value,int size, int page) throws Throwable;
	public void cleanAll();

}
