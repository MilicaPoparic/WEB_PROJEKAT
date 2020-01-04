package beans.cloudprovider;

import java.util.ArrayList;

public class SendVMO {
	private String nameVM;
	private int categoryCoreNumber;
	private int categoryRAM;
	private int categoryGPU;
	private String nameORG;
	
	public SendVMO() {}

	public SendVMO(String name,int core, int ram,int gpu,String nameORG) {
		super();
		this.setNameVM(name);
		this.setCategoryCoreNumber(core);
		this.setCategoryRAM(ram);
		this.setCategoryGPU(gpu);
		this.setNameORG(nameORG);
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
