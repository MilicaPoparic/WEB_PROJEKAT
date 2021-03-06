package beans.cloudprovider;

import java.util.ArrayList;

public class Drive {
	private String name;
	private DriveType driveType;
	private String nameOrg; //naziv org-dodao u poslednjem trenutku WTF
	private int capacity;
	private String nameVM; //naziv VM
	
	public Drive() {}
	public Drive(String name, DriveType driveType,String nn, int capacity) {
		super();
		this.name = name;
		this.driveType = driveType;
		this.nameOrg = nn;
		this.capacity = capacity;
		this.nameVM = null;
	}
	public Drive(String name, DriveType driveType,String nn, int capacity, String virtualMachines) {
		super();
		this.name = name;
		this.driveType = driveType;
		this.nameOrg = nn;
		this.capacity = capacity;
		this.nameVM = virtualMachines;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DriveType getDriveType() {
		return driveType;
	}

	public void setDriveType(DriveType driveType) {
		this.driveType = driveType;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getVirtualMachine() {
		return nameVM;
	}

	public void setVirtualMachine(String virtualMachines) {
		this.nameVM = virtualMachines;
	}
	public String getNameOrg() {
		return nameOrg;
	}
	public void setNameOrg(String nameOrg) {
		this.nameOrg = nameOrg;
	}
	
}
