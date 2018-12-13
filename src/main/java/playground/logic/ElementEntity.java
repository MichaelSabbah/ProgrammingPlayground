package playground.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
public class ElementEntity {

	private String playground;
	private String id;
	private Double x;
	private Double y;
	private String name;
	private Date createDate;
	private Date expirationDate;
	private String type;
	private Map<String,Object> attributes;
	private String creatorPlayground;
	private String creatorEmail;
	private String elementId;

	public ElementEntity(String playground, String id, String name, Date createDate, Date expirationDate,
			String type, String creatorPlayground, String creatorEmail){
		this.playground = playground;
		this.id = id;
		this.x = 0.0;
		this.y = 0.0;
		this.name = name;
		this.createDate = createDate;
		this.expirationDate = expirationDate;
		this.type = type;
		this.creatorPlayground = creatorPlayground;
		this.creatorEmail = creatorEmail;
		this.elementId = playground + "@" + id;
		this.attributes = new HashMap<String, Object>();
	}

	public ElementEntity()
	{
		this.attributes = new HashMap<String, Object>();
		this.x = 0.0;
		this.y = 0.0;
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

	@Id
	public String getElementId(){
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
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

	@Transient
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Lob
	public String getJsonAttributes() {
		try {
			return new ObjectMapper().writeValueAsString(this.attributes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setJsonAttributes(String jsonAttributes) {
		try {
			this.attributes = new ObjectMapper().readValue(jsonAttributes, Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

	@Override
	public boolean equals(Object obj) {
		ElementEntity other = (ElementEntity)obj;
		return this.getPlayground().equals(other.getPlayground()) && 
				this.getId().equals(other.getId());
	}
}
