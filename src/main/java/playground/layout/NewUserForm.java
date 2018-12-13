package playground.layout;

import playground.logic.UserEntity;

public class NewUserForm {

	private String email;
	private String username;
	private String avatar;
	private String role;

	public NewUserForm() {

	}

	public NewUserForm(String email, String username, String avatar, String role) {
		super();
		this.email = email;
		this.username = username;
		this.avatar = avatar;
		this.role = role;
	}

	public UserEntity toUserEntity()
	{
		UserEntity userEntity = new UserEntity(this.getEmail(), 
				this.getUsername(), this.getAvatar(), this.getRole());
		return userEntity;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	@Override
	public String toString() {
		return "NewUserForm [email=" + email + ", username=" + username + ", avatar=" + avatar + ", role=" + role + "]";
	}

}
