package beans.cloudprovider;

import java.util.ArrayList;

public class SendVMO {
	private String nameVM;
	private String category;
	private int categoryCoreNumber;
	private int categoryRAM;
	private int categoryGPU;
	private String nameORG;
	private ArrayList<Activity> activityLog;
	private ArrayList<String> drives;
	
	public SendVMO() {}

	public SendVMO(String name,int core, int ram,int gpu,String nameORG) {
		super();
		this.setNameVM(name);
		this.setCategoryCoreNumber(core);
		this.setCategoryRAM(ram);
		this.setCategoryGPU(gpu);
		this.setNameORG(nameORG);
	}



	public SendVMO(String nameVM, String category, int categoryCoreNumber, int categoryRAM, int categoryGPU,
			String nameORG, ArrayList<Activity> activityLog, ArrayList<String> drives) {
		super();
		this.nameVM = nameVM;
		this.category = category;
		this.categoryCoreNumber = categoryCoreNumber;
		this.categoryRAM = categoryRAM;
		this.categoryGPU = categoryGPU;
		this.nameORG = nameORG;
		this.activityLog = activityLog;
		this.drives = drives;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public ArrayList<Activity> getActivityLog() {
		return activityLog;
	}

	public void setActivityLog(ArrayList<Activity> activityLog) {
		this.activityLog = activityLog;
	}

	public ArrayList<String> getDrives() {
		return drives;
	}

	public void setDrives(ArrayList<String> drives) {
		this.drives = drives;
	}
	
	public String getNameVM() {
		return nameVM;
	}

	public void setNameVM(String nameVM) {
		this.nameVM = nameVM;
	}

	public int getCategoryCoreNumber() {
		return categoryCoreNumber;
	}

	public void setCategoryCoreNumber(int i) {
		this.categoryCoreNumber = i;
	}

	public int getCategoryRAM() {
		return categoryRAM;
	}

	public void setCategoryRAM(int categoryRAM) {
		this.categoryRAM = categoryRAM;
	}

	public int getCategoryGPU() {
		return categoryGPU;
	}

	public void setCategoryGPU(int categoryGPU) {
		this.categoryGPU = categoryGPU;
	}

	public String getNameORG() {
		return nameORG;
	}

	public void setNameORG(String nameORG) {
		this.nameORG = nameORG;
	}

	

	
}
