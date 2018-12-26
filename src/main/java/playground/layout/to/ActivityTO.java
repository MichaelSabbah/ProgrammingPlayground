package playground.layout.to;

import java.util.Map;

import playground.logic.Entities.Activity.ActivityEntity;

public class ActivityTO {

	private String playground;
	private String id;
	private String elementPlayground;
	private String elementId;
	private String type;
	private String playerPlayground;
	private String playerEmail;
	private Map<String,Object> attributes;

	public ActivityTO() {}

	public ActivityTO(String playground, String id, String elementPlayground, String elementId, String type,
			String playerPlayground, String playerEmail, Map<String, Object> attributes) {
		this.playground = playground;
		this.id = id;
		this.elementPlayground = elementPlayground;
		this.elementId = elementId;
		this.type = type;
		this.playerPlayground = playerPlayground;
		this.playerEmail = playerEmail;
		this.attributes = attributes;
	}

	public ActivityTO(ActivityEntity activityEntity)
	{
		this.playground = activityEntity.getPlayground();
		this.id = activityEntity.getId()+"";
		this.elementPlayground = activityEntity.getElementPlayground();
		this.elementId = activityEntity.getElementId();
		this.type = activityEntity.getType();
		this.playerPlayground = activityEntity.getPlayerPlayground();
		this.playerEmail = activityEntity.getPlayerEmail();
		this.attributes = activityEntity.getAttributes();
	}

	public ActivityEntity toEntity()
	{
		ActivityEntity activityEntity = new ActivityEntity(this.playground, Integer.parseInt(this.id), this.elementPlayground, this.elementId, this.type, 
				this.playerPlayground, this.playerEmail, this.attributes);
		return activityEntity;
	}

	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getElementPlayground() {
		return elementPlayground;
	}

	public void setElementPlayground(String elementPlayground) {
		this.elementPlayground = elementPlayground;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPlayerPlayground() {
		return playerPlayground;
	}

	public void setPlayerPlayground(String playerPlayground) {
		this.playerPlayground = playerPlayground;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
}
