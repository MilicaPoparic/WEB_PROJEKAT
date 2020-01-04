package beans.cloudprovider;

public class Category {
	private String name;
	private int coreNumber;
	private int RAM;
	private int GPUcores;
	
	public Category() {}

	public Category(String name, int coreNum, int ram, int GPUcores) {
		super();
		this.name = name;
		this.coreNumber = coreNum;
		this.RAM = ram;
		this.GPUcores = GPUcores;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCoreNumber() {
		return coreNumber;
	}

	public void setCoreNumber(int coreNum) {
		this.coreNumber = coreNum;
	}

	public int getRAM() {
		return RAM;
	}

	public void setRAM(int ram) {
		RAM = ram;
	}

	public int getGPUcores() {
		return GPUcores;
	}

	public void setGPUcores(int GPUcs) {
		GPUcores = GPUcs;
	}
	
	
}
