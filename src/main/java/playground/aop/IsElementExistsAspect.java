package playground.aop;

import java.util.NoSuchElementException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.dal.ElementDao;
import playground.logic.Entities.Element.ElementId;
import playground.logic.exceptions.ElementNotFoundException;

@Component
@Aspect
public class IsElementExistsAspect {

	private ElementDao elementDao;

	@Autowired
	public void setElementDao(ElementDao elementDao) {
		this.elementDao = elementDao;
	}

	@Before("@annotation(playground.aop.IsElementExists) && args(userEmail,userPlayground,elementId,elementPlayground,..)")
	public void isElementExists(JoinPoint joinPoint,String userEmail,String userPlayground,String elementId,String elementPlayground) throws ElementNotFoundException
	{
		try
		{
			ElementId elementIds = new ElementId(elementPlayground,Integer.parseInt(elementId));
			elementDao.findById(elementIds).get();
		}
		catch(NoSuchElementException ex)
		{
			throw new ElementNotFoundException();
		}
	}
}
