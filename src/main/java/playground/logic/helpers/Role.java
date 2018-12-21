package playground.logic.helpers;

public enum Role {
	MANAGER , PLAYER;
	
	public static Role getRoleByString(String role) {
		return Role.valueOf(Role.class, role.toUpperCase());
	}
}
