package playground.logic.jpa;

import java.util.List;

import org.springframework.stereotype.Service;

import playground.logic.ElementAlreadyExistsException;
import playground.logic.ElementEntity;
import playground.logic.ElementNotFoundException;
import playground.logic.ElementService;

//@Service
public class JpaElementService implements ElementService{

	@Override
	public ElementEntity addNewElement(ElementEntity element) throws ElementAlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ElementEntity updateElement(String playground, String id, ElementEntity entityUpdates)
			throws ElementNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ElementEntity getElementById(String playground, String id) throws ElementNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ElementEntity> getAllElements() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		
	}

}
