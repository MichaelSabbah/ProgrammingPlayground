package playground.logic.Entities.Element;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.logic.exceptions.internal.InternalErrorException;

@Entity
@IdClass(ElementId.class)
public class ElementEntity{

	private Map<String,Object> attributes;
	private Double x;
	private Double y;
	private String name;
	private Date createDate;
	private Date expirationDate;
	private String type;
	private String creatorPlayground;
	private String creatorEmail;
	private int id;
	private String playground;

	public ElementEntity(String playground, int id, String name, Date createDate, Date expirationDate,
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
		this.attributes = new HashMap<String, Object>();
	}

	public ElementEntity()
	{
		this.attributes = new HashMap<String, Object>();
		this.x = 0.0;
		this.y = 0.0;
	}

	@Id
	@Column(name="id", nullable=false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Id
	@Column(name="playground", nullable=false)
	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
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

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Lob
	public String getJsonAttributes() throws Throwable {
		try {
			return new ObjectMapper().writeValueAsString(this.attributes);
		} catch (Exception ex) {
			throw new InternalErrorException(ex.getMessage());
		}
	}

	public void setJsonAttributes(String jsonAttributes) throws Throwable {
		try {
			this.attributes = new ObjectMapper().readValue(jsonAttributes, Map.class);
		} catch (Exception ex) {
			throw new InternalErrorException(ex.getMessage());
		}
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
				this.getId() == (other.getId());
	}

	@Override
	public String toString() {
		return "ElementEntity [attributes=" + attributes + ", x=" + x + ", y=" + y + ", name=" + name + ", createDate="
				+ createDate + ", expirationDate=" + expirationDate + ", type=" + type + ", creatorPlayground="
				+ creatorPlayground + ", creatorEmail=" + creatorEmail + ", id=" + id + ", playground=" + playground
				+ "]";
	}


}
