package playground.plugins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.javafx.collections.MappingChange.Map;

import playground.dal.ActivityDao;
import playground.dal.ElementDao;
import playground.logic.Entities.Activity.ActivityEntity;
import playground.logic.Entities.Element.ElementEntity;
import playground.logic.Entities.Element.ElementId;
import playground.logic.exceptions.notfound.ElementNotFoundException;
@Component
public class ShowSolutionPlugin implements ActivityPlugin{
	ElementDao elements;
	ActivityDao activities;
	@Autowired
	public ShowSolutionPlugin(ElementDao eDao,ActivityDao aDao) {
		this.elements=eDao;
		this.activities=aDao;
	}

	@Override
	public Object activate(ActivityEntity activityEntity) throws Exception {
		Answer answer=new Answer();
			ElementEntity element=getElementById(activityEntity.getElementId());
			java.util.Map<String, Object> map=element.getAttributes();
			if(map.get("solution")!=null)
			{
				String answerString=(String) map.get("solution");
				answer.setAnswer(answerString);
				return answer;
			}
			else
				throw new InvalidAnswerException();
		activities.save(activityEntity);
		return answer;
	}

	public ElementDao getElements() {
		return elements;
	}

	public void setElements(ElementDao elements) {
		this.elements = elements;
	}

	public ActivityDao getActivities() {
		return activities;
	}

	public void setActivities(ActivityDao activities) {
		this.activities = activities;
	}

	public ElementEntity getElementById(String id) throws ElementNotFoundException {

		ElementId elementId = new ElementId();
		elementId.setId(Integer.parseInt(id));

		ElementEntity element = this.elements.findById(elementId)//this.elements.findByIdAndPlayground(Integer.parseInt(id), playground)
				.orElseThrow(()->
				new ElementNotFoundException("no element with id: " + id));

		if(element == null) {
			throw new ElementNotFoundException("no element with id: " + id );
		}

		return element;
	}	
}
