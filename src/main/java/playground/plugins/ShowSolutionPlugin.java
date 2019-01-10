package playground.plugins;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import playground.dal.ElementDao;
import playground.dal.UserDao;
import playground.logic.Entities.Activity.ActivityEntity;
import playground.logic.Entities.Element.ElementEntity;
import playground.logic.Entities.Element.ElementId;
import playground.logic.Entities.User.UserEntity;
import playground.logic.exceptions.notacceptable.InvalidAnswerException;
import playground.logic.exceptions.notfound.ElementNotFoundException;
import playground.logic.exceptions.notfound.UserNotFoundException;
import playground.logic.helpers.PlaygroundConsts;

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
			Map<String, Object> map=element.getAttributes();
			if(map.get(PlaygroundConsts.ANSWER_KEY)!=null){
				List<UserEntity> usersList=users.findByEmailAndPlayground(activityEntity.getPlayerEmail(),activityEntity.getPlayerPlayground());
				if(usersList.size()>0){
					UserEntity user=usersList.get(0);
					if(user.getPoints()>=2) {
						user.setPoints(user.getPoints()-2);
						users.save(user);
					}
				}else { 
					throw new UserNotFoundException("No user with email: " + activityEntity.getPlayerEmail());
				}
				String answerString=map.get(PlaygroundConsts.ANSWER_KEY)+"";
				answer.setAnswer(answerString);
				
				return answer;
			}
			else
				throw new InvalidAnswerException("Invalid answer");
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
