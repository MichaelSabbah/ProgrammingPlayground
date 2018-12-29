package playground.plugins;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.dal.ElementDao;
import playground.dal.UserDao;
import playground.logic.Entities.Activity.ActivityEntity;
import playground.logic.Entities.Element.ElementEntity;
import playground.logic.Entities.Element.ElementId;
import playground.logic.Entities.User.UserEntity;
import playground.logic.exceptions.notacceptable.InvalidAnswerException;
import playground.logic.exceptions.notacceptable.NotEnoughPointsException;
import playground.logic.exceptions.notfound.ElementNotFoundException;
import playground.logic.exceptions.notfound.UserNotFoundException;

@Component
public class ShowSolutionPlugin implements ActivityPlugin{
	ElementDao elements;
	UserDao users;
	@Autowired
	public ShowSolutionPlugin(ElementDao eDao,UserDao uDao) {
		this.elements=eDao;
		this.users=uDao;
	}

	@Override
	public Object activate(ActivityEntity activityEntity) throws Exception {
		Answer answer=new Answer();
			ElementEntity element=getElementById(activityEntity.getPlayground(), activityEntity.getElementId());
			java.util.Map<String, Object> map=element.getAttributes();
			if(map.get("solution")!=null){
				List<UserEntity> usersList=users.findByEmailAndPlayground(activityEntity.getPlayerEmail(),activityEntity.getPlayerPlayground());
				if(usersList.size()>0){
					UserEntity user=usersList.get(0);
					if(user.getPoints()>=2) {
						user.setPoints(user.getPoints()-2);
						users.save(user);
				}
					//else
						//throw new NotEnoughPointsException();
				}
				else throw new UserNotFoundException();
				String answerString=(String) map.get("solution");
				answer.setAnswer(answerString);
				
				return answer;
			}
			else
				throw new InvalidAnswerException();
	}

	public ElementDao getElements() {
		return elements;
	}

	public void setElements(ElementDao elements) {
		this.elements = elements;
	}
	public ElementEntity getElementById(String playground, String id) throws ElementNotFoundException {

		ElementId elementId = new ElementId();
		elementId.setPlayground(playground);
		elementId.setId(Integer.parseInt(id));

		ElementEntity element = this.elements.findById(elementId)//this.elements.findByIdAndPlayground(Integer.parseInt(id), playground)
				.orElseThrow(()->
				new ElementNotFoundException("No element with id: " + id));
		return element;
	}	
}
