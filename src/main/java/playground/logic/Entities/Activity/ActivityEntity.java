package playground.logic.Entities.Activity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.logic.exceptions.internal.InternalErrorException;

@Entity
@IdClass(ActivityId.class)
public class ActivityEntity {
	private String playground;
	private int id;
	private String elementPlayground;
	private String elementId;
	private String type;
	private String playerPlayground;
	private String playerEmail;
	private Map<String,Object> attributes;

	public ActivityEntity(String playground, int id, String elementPlayground, String elementId, String type,
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

	public ActivityEntity() {
		this.attributes = new HashMap<String,Object>();	
	}

	@Id
	@Column(name="playground", nullable=false)
	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}

	@Id
	@Column(name="id", nullable=false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
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
		} catch (Exception e) {
			throw new InternalErrorException(e.getMessage());
		}
	}

	public void setJsonAttributes(String jsonAttributes) throws Throwable {
		try {
			this.attributes = new ObjectMapper().readValue(jsonAttributes, Map.class);
		} catch (Exception e) {
			throw new InternalErrorException(e.getMessage());
		}
	}

	@Override
	public boolean equals(Object obj) {
		ActivityEntity other = (ActivityEntity)obj;
		return this.getPlayground().equals(other.getPlayground()) && 
				this.getId() == (other.getId());
	}
}
