package playground.layout.to;

import playground.logic.Entities.User.UserEntity;
import playground.logic.helpers.Role;

public class UserTO {

	private String email;
	private String playground;
	private String username;
	private String avatar;
	private String role;
	private long points;

	public UserTO() {}

	public UserTO(UserEntity userEntity)
	{
		this.email = userEntity.getEmail();
		this.playground = userEntity.getPlayground();
		this.username = userEntity.getUsername();
		this.avatar= userEntity.getAvatar();
		this.role= userEntity.getRole();
		this.points= userEntity.getPoints();
	}

	public UserTO(String email, String playground, String username, String avatar, String role, long points) {
		super();
		this.email = email;
		this.playground = playground;
		this.username = username;
		this.avatar = avatar;
		this.role = role;
		this.points = points;
	}

	public UserEntity toUserEntity()
	{
		UserEntity userEntity = new UserEntity(this.getEmail(), this.getPlayground(), 
				this.getUsername(), this.getAvatar(), this.getRole() /*Enum.valueOf(Role.class,this.getRole())*/, this.getPoints());
		return userEntity;
	}

	public UserTO(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

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
	public String toString() {
		return "UserTO [email=" + email + ", playground=" + playground + ", username=" + username + ", avatar=" + avatar
				+ ", role=" + role + ", points=" + points + "]";
	}

}
