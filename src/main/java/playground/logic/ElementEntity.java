package playground.logic;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ElementEntity {
	
	private String playgorund;
	private String id;
	//private Location location;
	private Double x;
	private Double y;
	private String name;
	private Date createDate;
	private Date expirationDate;
	private String type;
	private HashMap<String,Object> attributes;
	private String creatorPlayground;
	private String creatorEmail;
	
	public ElementEntity(String playgorund, String id, String name, Date createDate, Date expirationDate,
			String type, String creatorPlayground, String creatorEmail){
		this.playgorund = playgorund;
		this.id = id;
		//this.location = new Location(40, 90);
		this.x = 0.0;
		this.y = 0.0;
		this.name = name;
		this.createDate = createDate;
		this.expirationDate = expirationDate;
		this.type = type;
		this.creatorPlayground = creatorPlayground;
		this.creatorEmail = creatorEmail;
		this.attributes = new HashMap();
	}
	
	public ElementEntity()
	{
		this.attributes = new HashMap();
		//this.location = new Location(40, 50);
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public String getPlaygorund() {
		return playgorund;
	}

	public void setPlaygorund(String playgorund) {
		this.playgorund = playgorund;
	}
	
	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/*public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}*/

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

	public HashMap<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, Object> attributes) {
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
	
	@Override
	public boolean equals(Object obj) {
		ElementEntity other = (ElementEntity)obj;
		return this.getPlaygorund().equals(other.getPlaygorund()) && 
			   this.getId().equals(other.getId());
	}
}
