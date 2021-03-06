package playground.logic.Entities.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(UserId.class)
public class UserEntity {

	private String email;
	private String playground;
	private String username;
	private String avatar;
	private String role;
	private long points;
	private int confirmCode;

	public UserEntity() {}


	public UserEntity(String email, String playground) {
		super();
		this.email = email;
		this.playground = playground;
	}


	public UserEntity(String email, String playground, int confirmCode) {
		super();
		this.email = email;
		this.playground = playground;
		this.confirmCode = confirmCode;
	}


	public UserEntity(String email, String playground, String username, String avatar, String role, long points) {
		super();
		this.email = email;
		this.playground = playground;
		this.username = username;
		this.avatar = avatar;
		this.role = role;
		this.points = points;
	}

	public UserEntity(String email, String username, String avatar, String role) {
		super();
		this.email = email;
		this.username = username;
		this.avatar = avatar;
		this.role = role;
	}

	public int getConfirmCode() {
		return confirmCode;
	}

	public void setConfirmCode(int confirmCode) {
		this.confirmCode = confirmCode;
	}

	@Id
	@Column(name="email", nullable=false)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Id
	@Column(name="playground", nullable=false)
	public String getPlayground() {
		return playground;
	}

	public void setPlayground(String playground) {
		this.playground = playground;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public long getPoints() {
		return points;
	}

	public void setPoints(long points) {
		this.points = points;
	}

	@Override
	public boolean equals(Object obj) {
		UserEntity userObj = (UserEntity)obj;
		if(this.getEmail().equals(userObj.getEmail()))
		{
			return true;
		}
		return false;
	}

}
