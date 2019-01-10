package playground.plugins;

import java.util.Date;
public class AdMessage {

	private String message;
	private String avatar;
	private Date created;
	private String userName;
	
	
	public AdMessage() {}

	public AdMessage(String message, String avatar, Date created, String userName) {
		this.message = message;
		this.avatar = avatar;
		this.created = created;
		this.userName = userName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
