package beans.cloudprovider;

import java.util.ArrayList;

public class Organization {
	private String name;
	private String caption;
	private String logo; //url za logo slika
	private ArrayList<String> users;
	private ArrayList<String> resources;
	
	public Organization() {}
	
	public Organization(String name) {
		this.name = name;
		this.caption = null;
		this.logo = null;
		this.users = null;
		this.resources = null;
	}
	
	public Organization(String name, String caption, String logo) {
		super();
		this.name = name;
		this.caption = caption;
		this.logo = logo;
		this.users = new ArrayList<String>();
		this.resources = new ArrayList<String>();
	}
	
	public Organization(String name, String caption, String logo, ArrayList<String> users) {
		super();
		this.name = name;
		this.caption = caption;
		this.logo = logo;
		this.users = users;
		this.resources = new ArrayList<String>();
	}

	public Organization(String name, String caption, String logo, ArrayList<String> users, ArrayList<String> resources) {
		super();
		this.name = name;
		this.caption = caption;
		this.logo = logo;
		this.users = users;
		this.resources = resources;
	}




	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public ArrayList<String> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<String> users) {
		this.users = users;
	}

	public ArrayList<String> getResources() {
		return resources;
	}

	public void setResources(ArrayList<String> resources) {
		this.resources = resources;
	}
	
	
	
	
}
