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
	public static HashMap<String, User> users = new HashMap<String, User>();
	public static ArrayList<User> userList = new ArrayList<User>();
	public static HashMap<String, VirtualMachine> virtMachines = new HashMap<String, VirtualMachine>();
	public static ArrayList<VirtualMachine> virtMachineList = new ArrayList<VirtualMachine>();
	public static HashMap<String, Organization> organizations = new HashMap<String, Organization>();
	public static ArrayList<Organization> organizationList = new ArrayList<Organization>();
	public static HashMap<String, Drive> drives = new HashMap<String, Drive>();
	public static ArrayList<Drive> driveList = new ArrayList<Drive>();
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
		
		try {
			readDrives("./data/disc.json");
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			readOrganizations("./data/organizations.json");
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			readUsers("./data/users.json");
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void readUsers(String file) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		JsonReader reader = new JsonReader(new FileReader(file));
		User[] data = g.fromJson(reader, User[].class);
		for (User u:data) {
			users.put(u.getEmail(), u);
			userList.add(u);
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
	
	private static void readOrganizations(String file) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		JsonReader reader = new JsonReader(new FileReader(file));
		Organization[] data = g.fromJson(reader, Organization[].class);
		for (Organization org:data) {
			organizations.put(org.getName(), org);
			organizationList.add(org);
		}
	}
	
	private static void readDrives(String file) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		JsonReader reader = new JsonReader(new FileReader(file));
		Drive[] data = g.fromJson(reader, Drive[].class);
		for (Drive d:data) {
			drives.put(d.getName(), d);
			driveList.add(d);
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
