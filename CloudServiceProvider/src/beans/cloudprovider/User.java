package beans.cloudprovider;

import java.util.ArrayList;
import java.util.Date;

public class User {
	private String email;  
	private String name; 
	private String surname; 
	private String nameORG; //naziv ORG 
	private Role role;
	private String password;
	
	public User() {}
	
	//ovo je konsturktor za super admina!
	public User(String email, String password) {
		super();
		this.email = email;
		this.name = "";
		this.surname = "";
		this.nameORG = null; //mozda bude greske zbog ovoga!!!
		this.role = Role.superAdmin;
		this.password = password;
		
	}

	//ovo je konsturktor za super admina!
	public User(String email, String name, String surname, String password) {
		super();
		this.email = email;
		this.name = name;
		this.surname = surname;
		this.nameORG = null;
		this.role = Role.superAdmin;
		this.password = password;
		
	}
	
	public User(String email, String name, String surname, String organization, Role role, String password) {
		super();
		this.email = email;
		this.name = name;
		this.surname = surname;
		this.nameORG = organization;
		this.role = role;
		this.password = password;
		
	}
	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getOrganization() {
		return nameORG;
	}

	public void setOrganization(String organization) {
		this.nameORG = organization;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
