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
import playground.aop.PlaygroundLogger;
import playground.dal.ElementDao;
import playground.dal.ElementIdGeneratorDao;
import playground.logic.Entities.Element.ElementEntity;
import playground.logic.Entities.Element.ElementId;
import playground.logic.Entities.Element.ElementIdGenerator;
import playground.logic.Entities.User.UserEntity;
import playground.logic.exceptions.notacceptable.InvalidFormatException;
import playground.logic.exceptions.notfound.ElementNotFoundException;
import playground.logic.helpers.PlaygroundConsts;
import playground.logic.helpers.Role;
import playground.logic.services.ElementService;
import playground.logic.services.UserService;

@Service
public class JpaElementService implements ElementService{

	private ElementDao elements;
	private UserService users;
	private ElementIdGeneratorDao elementIdGeneratorDao;

	@Autowired
	public void setElementService(ElementDao elements, UserService users) {
		this.elements = elements;
		this.users = users;
	}

	@Autowired
	public void setElementIdGeneratorDao(ElementIdGeneratorDao elementIdGeneratorDao) {
		this.elementIdGeneratorDao = elementIdGeneratorDao;
	}

	@Override
	@Transactional
	@ManagerAuthentication
	@PlaygroundLogger
	public ElementEntity addNewElement(String userEmail,String userPlayground,ElementEntity element) {	
		
		int id = elementIdGeneratorDao.save(new ElementIdGenerator()).getId();
		element.setPlayground(userPlayground);
		element.setId(id);
		element.setCreatorEmail(userEmail);
		element.setCreatorPlayground(userPlayground);
		element.setCreateDate(new Date());

		return this.elements.save(element);
	}

	@Override
	@Transactional
	@ManagerAuthentication
	@PlaygroundLogger
	public ElementEntity updateElement(String userEmail,String userPlaygorund,String playground, String id, ElementEntity entityUpdates)
			throws Throwable {

		ElementEntity existing = null;

		ElementId elementId = new ElementId();
		elementId.setId(Integer.parseInt(id));
		elementId.setPlayground(playground);

		existing = this.elements.findById(elementId)
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
	@PlaygroundLogger
	public ElementEntity getElementById(String userEmail,String userPlayground,String playground, String id) throws Throwable {

		ElementId elementId = new ElementId();
		elementId.setId(Integer.parseInt(id));
		elementId.setPlayground(playground);

		ElementEntity element = this.elements.findById(elementId)
				.orElseThrow(()->
				 new ElementNotFoundException("No element with playground: " + playground + " and id: " + id));
		
		UserEntity user = new UserEntity();
		user.setEmail(userEmail);
		user.setPlayground(userPlayground);

		if(users.loginUser(user).getRole().equals(Role.PLAYER.name().toLowerCase())) {
			if(element.getExpirationDate().compareTo(new Date()) > 0)
				return element;
			else
				throw new ElementNotFoundException("No element with playground: " + playground + " and id: " + id);
		}else {
			return element;
		}
	}

	@Override
	@Transactional(readOnly=true)
	@BasicAuthentication
	@PlaygroundLogger
	public List<ElementEntity> getAllElements(String userEmail,String userPlayground,int size, int page) throws Throwable {
		
		UserEntity user = new UserEntity();
		user.setEmail(userEmail);
		user.setPlayground(userPlayground);
		
		Date date = null;
		
		if(users.loginUser(user).getRole().equals(Role.PLAYER.name().toLowerCase())) {
			date = new Date();
		}else {
			date = new Date(1);
		}
		
		Page<ElementEntity> elementsReturned = this.elements.findAllByExpirationDateAfter(
				date,
				PageRequest.of(page, size, Direction.DESC, "name"));
		
		return elementsReturned.getContent();
	}

	@Override
	@Transactional(readOnly=true)
	@BasicAuthentication
	@PlaygroundLogger
	public List<ElementEntity> getElementsByDistance(String userEmail,String userPlayground,int x, int y, int distance,int size,int page) throws Throwable {
		
		if(distance < 0) {
			throw new InvalidFormatException("Invalid distance");
		}
		
		UserEntity user = new UserEntity();
		user.setEmail(userEmail);
		user.setPlayground(userPlayground);
		
		Date date = null;
		
		if(users.loginUser(user).getRole().equals(Role.PLAYER.name().toLowerCase())) {
			date = new Date();
		}else {
			date = new Date(1);
		}
		
		List<ElementEntity> elementsReturned = this.elements.findAllByXBetweenAndYBetweenAndExpirationDateAfter(
				x - distance, x + distance, y - distance, y + distance,
				date,
				PageRequest.of(page, size))
				.getContent();
		
		if(elementsReturned.size() == 0) {
			throw new ElementNotFoundException("No elements exist in this range");
		}
		return elementsReturned;
	}

	@Override
	@Transactional(readOnly=true)
	@BasicAuthentication
	@PlaygroundLogger
	public List<ElementEntity> getElementsByAttribute(String userEmail,String userPlayground,String attributeName, String value,int size, int page) throws Throwable {		
		
		UserEntity user = new UserEntity();
		user.setEmail(userEmail);
		user.setPlayground(userPlayground);
		
		Date date = null;
		if(users.loginUser(user).getRole().equals(Role.PLAYER.name().toLowerCase())) {
			date = new Date();
		}else {
			date = new Date(1);
		}
		
		List<ElementEntity> elementsReturned;
		
		switch(attributeName) {
			case PlaygroundConsts.NAME_KEY:
				elementsReturned = this.elements.findAllByNameEqualsAndExpirationDateAfter(value,date, PageRequest.of(page, size));
				break;
			
			case PlaygroundConsts.TYPE_KEY:
				elementsReturned = 
				this.elements.findAllByTypeEqualsAndExpirationDateAfter(value,date,PageRequest.of(page, size));
				break;
			
			default:
				throw new ElementNotFoundException("Element with this attribute name and value is not existing");
		}

		return elementsReturned;
	}

	@Override
	@Transactional
	@PlaygroundLogger
	public void cleanAll() {
		elements.deleteAll();
	}
}
