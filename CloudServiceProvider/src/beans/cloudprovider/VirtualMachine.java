package beans.cloudprovider;

import java.util.ArrayList;

public class VirtualMachine {
	private String name;
	private Category category;
	private ArrayList<String> drives;
	private ArrayList<Activity> activityLog;
	
	public VirtualMachine() {}

	public VirtualMachine(String name, Category category) {
		super();
		this.name = name;
		this.category = category;
		this.drives = new ArrayList<String>();
		this.activityLog = new ArrayList<Activity>();
	}
	
	public VirtualMachine(String name, Category category, ArrayList<String> drives) {
		super();
		this.name = name;
		this.category = category;
		this.drives = drives;
		this.activityLog = new ArrayList<Activity>();
	}
	

	public VirtualMachine(String name, Category category, ArrayList<String> drives, ArrayList<Activity> activityLog) {
		super();
		this.name = name;
		this.category = category;
		this.drives = drives;
		this.activityLog = activityLog;
	}
	

	public ArrayList<Activity> getActivityLog() {
		return activityLog;
	}

	public void setActivityLog(ArrayList<Activity> activityLog) {
		this.activityLog = activityLog;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public ArrayList<String> getDrives() {
		return drives;
	}

	public void setDrives(ArrayList<String> drives) {
		this.drives = drives;
	}
	
	
}
