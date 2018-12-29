package playground.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;

import playground.dal.ActivityDao;
import playground.dal.ElementDao;
import playground.dal.UserDao;
import playground.logic.Entities.Activity.ActivityEntity;
import playground.logic.Entities.Element.ElementId;
import playground.logic.exceptions.notfound.ElementNotFoundException;
import playground.logic.exceptions.notfound.UserNotFoundException;
import playground.logic.helpers.PlaygroundConsts;

@Component
public class GetMessagesPlugin implements ActivityPlugin {

	private ActivityDao activityDao;
	private ElementDao elementDao;
	private UserDao userDao;

	@Autowired
	public GetMessagesPlugin(ActivityDao activityDao,ElementDao elementDao,UserDao userDao) {
		this.activityDao = activityDao;
		this.elementDao = elementDao;
		this.userDao = userDao;
	}

	@Override
	public Object activate(ActivityEntity activityEntity) throws Exception {
		Map<String,Object> attributes = activityEntity.getAttributes();
		int page;
		int size;

		userDao.findById(activityEntity.getPlayerEmail())
		.orElseThrow(()-> new UserNotFoundException());
		elementDao.findById(new ElementId(activityEntity.getElementPlayground(), 
				Integer.parseInt(activityEntity.getElementId())))
		.orElseThrow(()-> new ElementNotFoundException());

		if(attributes.containsKey("page"))
		{
			page =(int)attributes.get("page");
		}
		else
		{
			page = PlaygroundConsts.PAGE_DEFAULT;
		}

		if(attributes.containsKey("size"))
		{
			size =(int)attributes.get("size");
		}
		else
		{
			size = PlaygroundConsts.SIZE_DEFAULT;
		}

		Page<ActivityEntity> activityEntityPage = activityDao.findAllByType(PlaygroundConsts.POST_MESSAGE_TYPE_NAME, PageRequest.of(page, size, Direction.DESC, "id"));
		List<ActivityEntity> activityEntityList = activityEntityPage.getContent();
		ArrayList<AdMessage> adMessages = new ArrayList<AdMessage>();
		if(activityEntityList != null && activityEntityList.size() != 0)
		{
			activityEntityList.stream().forEach(activity ->
			{
				if(activity.getAttributes().containsKey("message"))
				{
					String Admessage =(String)activity.getAttributes().get("message");
					adMessages.add(new AdMessage(Admessage));
				}
			});
		}

		return adMessages.toArray(new AdMessage[0]);
	}
}
