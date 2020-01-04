package beans.cloudprovider;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class Reader {
	
	private static Gson g = new Gson();
	public static HashMap<String, VirtualMachine> virtMachines = new HashMap<String, VirtualMachine>();
	public static ArrayList<VirtualMachine> virtMachineList = new ArrayList<VirtualMachine>();
	public static HashMap<String, Category > categories = new HashMap<String, Category>();
	public static ArrayList<Category> categoryList = new ArrayList<Category>();
	
	
	public Reader() {
		
		try {
			readCategories("./data/categories.json");
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			readVM("./data/virtMachines.json");
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void readVM(String file) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		JsonReader reader = new JsonReader(new FileReader(file));
		VirtualMachine[] data = g.fromJson(reader, VirtualMachine[].class);
		for (VirtualMachine vm:data) {
			virtMachines.put(vm.getName(), vm);
			virtMachineList.add(vm);
		}
		
	}

	private static void readCategories(String file) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		JsonReader reader = new JsonReader(new FileReader(file));
	    Category[] data = g.fromJson(reader, Category[].class);
		for (Category c:data) {
			 categories.put(c.getName(), c);
			 categoryList.add(c);
		}
		
	}
}
