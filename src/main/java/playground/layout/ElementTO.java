package playground.layout;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import playground.logic.ElementEntity;
import playground.logic.Location;

public class ElementTO {

	private String playground;
	private String id;
	private Location location;
	private String name;
	private Date createDate;
	private Date expirationDate;
	private String type;
	private Map<String,Object> attributes;
	private String creatorPlayground;
	private String creatorEmail;

	public ElementTO(String playground, String id, String name, Date createDate, Date expirationDate,
			String type, String creatorPlayground, String creatorEmail) {
		this.playground = playground;
		this.id = id;
		this.location = new Location();
		this.name = name;
		this.createDate = createDate;
		this.expirationDate = expirationDate;
		this.type = type;
		this.creatorPlayground = creatorPlayground;
		this.creatorEmail = creatorEmail;
		this.attributes = new HashMap<String, Object>();
	}

	public ElementTO(ElementEntity elementEntity){
		this();
		if(elementEntity != null) {
			this.playground = elementEntity.getPlayground();
			this.id = elementEntity.getId();
			this.createDate = elementEntity.getCreateDate();
			this.creatorEmail = elementEntity.getCreatorEmail();
			this.creatorPlayground = elementEntity.getCreatorPlayground();
			this.location = new Location(elementEntity.getX(),elementEntity.getY());
			this.name = elementEntity.getName();
			this.expirationDate = elementEntity.getExpirationDate();
			this.type = elementEntity.getType();
			this.attributes = elementEntity.getAttributes();
		}
	}

	public ElementTO()
	{
		this.attributes = new HashMap<String, Object>();
		this.location = new Location();
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

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public String getCreatorPlayground() {
		return creatorPlayground;
	}

	public void setCreatorPlayground(String creatorPlayground) {
		this.creatorPlayground = creatorPlayground;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	public ElementEntity toEntity(){
		ElementEntity elementEntity = new ElementEntity();
		elementEntity.setAttributes(this.attributes);
		elementEntity.setCreateDate(this.createDate);
		elementEntity.setCreatorEmail(this.creatorEmail);
		elementEntity.setCreatorPlayground(this.creatorPlayground);
		elementEntity.setExpirationDate(this.expirationDate);
		elementEntity.setId(this.id);
		elementEntity.setX(this.getLocation().getX());
		elementEntity.setY(this.getLocation().getY());
		elementEntity.setName(this.getName());
		elementEntity.setPlayground(this.getPlayground());
		elementEntity.setType(this.getType());
		elementEntity.setElementId(this.playground + "@" + this.id);
		return elementEntity;
	}
}
