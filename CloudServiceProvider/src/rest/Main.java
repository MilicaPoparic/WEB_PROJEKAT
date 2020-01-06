package rest;
import beans.cloudprovider.SendVMO;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import beans.cloudprovider.Category;
import beans.cloudprovider.CategoryToAdd;
import beans.cloudprovider.Organization;
import beans.cloudprovider.Reader;
import beans.cloudprovider.SendVMO;
import beans.cloudprovider.User;
import beans.cloudprovider.UserToLog;
import beans.cloudprovider.VirtualMachine;
import spark.Session;
import ws.WsHandler;

public class Main {
    
	private static Gson g = new Gson();
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static Reader r = new Reader(); 
	private static Category c = new Category();
	private static Organization o = new Organization();
	private static void writeToFiles(ArrayList<Object> listForWrite, String string) throws IOException {
		String json = gson.toJson(listForWrite);
		FileWriter file = new FileWriter(string);
		file.write(json);
		file.close();
	}
	
	public static void main(String[] args) throws IOException {
		port(8080);
		webSocket("/ws", WsHandler.class);		
		
		
		staticFiles.externalLocation(new File("./static").getCanonicalPath()); 

	
		get("/test", (req, res) -> {
			return "Works";
		});

		get("/rest/getRole", (req, res) -> {
			res.type("application/json");
			User user = req.session(true).attribute("user");
			if (user !=null) {
				return g.toJson(user.getRole());
			}
			else {
				return ("OK");
			}
			});
		
		//za kategoriju da ide forbidden
		get("/rest/checkRole", (req, res) -> {
			res.type("application/json");
			User user = req.session(true).attribute("user");
			if (user !=null) {
				if (!user.getRole().toString().equals("superAdmin")) {
					res.status(403);
				}
			}
			else {
				res.status(403);
			}
			return ("OK");
			});
		
		get("/rest/virtualne", (req, res) -> {		
			res.type("application/json");
			Session ss = req.session(true);
			User user = ss.attribute("user");
			if (user!=null) {
			if (user.getRole().toString().equals("user")) {
				ArrayList<SendVMO> listOfVMO = loadVMOUser(user);
				return g.toJson(listOfVMO);
			}
			else if  (user.getRole().toString().equals("superAdmin")){
				ArrayList<SendVMO> listOfVMO = loadVMO();
				return g.toJson(listOfVMO);
			}
			else {
				ArrayList<SendVMO> listOfVMO = loadVMOUser(user);
				return g.toJson(listOfVMO);
			}
		}
		else {
				ArrayList<SendVMO> listOfVMO = new ArrayList<SendVMO>();
				return g.toJson(listOfVMO);
			}
			
			
		});
		

		get("/rest/getOrganizations", (req, res) -> {

			res.type("application/json");
			Session ss = req.session(true);
			User user = ss.attribute("user");
			if (user!=null) {
			if  (user.getRole().toString().equals("superAdmin")){
				return g.toJson(r.organizations);
			}
			else {
				String org = user.getOrganization();
				ArrayList<Organization> organizationL = new ArrayList<Organization>();
				if (r.organizations.get(org)!=null) {
					organizationL.add(r.organizations.get(org));
				}
				return g.toJson(organizationL);
			}
			}
			else {
				return ("OK");
			}
		
			
		});

		get("/rest/getOrganization", (req, res) -> {
			res.type("application/json");
			return g.toJson(o);
		});
		
		get("/rest/testLogin", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User user = ss.attribute("user");
			if (user == null) {
				res.status(400);
			} else {
				res.status(200);
			}
			return ("OK");
		});
		
		get("/rest/getCategories", (req,res)-> {
			res.type("application/json");
			return g.toJson(r.categoryList);
		});
		get("/rest/getCategory", (req,res)-> {
			res.type("application/json");
			return g.toJson(c);
			
		});
				
		post("/rest/login", (req, res) -> {
			res.type("application/json");
			String payload = req.body(); 
			UserToLog u = g.fromJson(payload, UserToLog.class);
			Session ss = req.session(true);
			User user = testLogin(u);
			ss.attribute("user", user);
			if (user != null) {
				res.status(200);
			}
			else {
				res.status(400);
			}
			return ("OK");
		});
		
		post("/rest/logout", (req,res)-> {
			res.type("application/json");
			Session ss = req.session(true);
			User user = ss.attribute("user");	
			if (user != null) {
				ss.invalidate();
			}
			return ("OK");
		});
		
		post("/rest/requestAddOrg", (req,res)-> {
			res.type("application/json");
			return ("OK");
		});

		post("/rest/captureOrg", (req,res)-> {
			res.type("application/json");
			String payload = req.body(); 
			Organization checkMe = g.fromJson(payload, Organization.class);
			if (r.organizations.get(checkMe.getName())!=null) {
				o = r.organizations.get(checkMe.getName());
			}
			return ("OK");
		});
	
		
		post("/rest/addOrganization", (req,res)-> {
			res.type("application/json");
			String payload = req.body(); 
			Organization org = g.fromJson(payload, Organization.class);
			
			if (org!=null && r.organizations.get(org.getName())==null) {
				r.organizationList.add(org);
				r.organizations.put(org.getName(),org);
				res.status(200);
				writeToFiles((ArrayList<Object>)(Object) r.organizationList, "./data/organizations.json"); 
			}
			else {
				res.status(400);
			}
			return ("OK");
		});
		
		post("/rest/changeOrg", (req,res)-> {
			res.type("application/json");
			String payload = req.body(); 
			System.out.println(payload);
			Organization org = g.fromJson(payload, Organization.class);
			if (!o.getName().equals(org.getName()) && r.organizations.get(org.getName())!=null) {
				res.status(400);
			}
			else {
				res.status(200);
				refreshOrgData(org);
				writeToFiles((ArrayList<Object>)(Object) r.organizationList, "./data/organizations.json"); 
				
			}
			return ("OK");
		
		});
		
		//stavicu mu da brise onu koju je oznacio za izmenu!
		post("/rest/deleteOrg", (req,res)-> {
			res.type("application/json");
			String payload = req.body(); 
			System.out.println(payload);
			Organization org = g.fromJson(payload, Organization.class);
			deleteOrgData();
			writeToFiles((ArrayList<Object>)(Object) r.organizationList, "./data/organizations.json"); 
			return ("OK");
		});
		
		post("/rest/category", (req,res)-> {
			res.type("application/json");
			return"OK";
		});
		
	
		post("/rest/addCategory", (req,res)-> {
			res.type("application/json");
			return"OK";
		});
		
		post("/rest/addNewCategory", (req, res) -> {
			res.type("application/json");
			String reqData = req.body();
			CategoryToAdd cat = g.fromJson(reqData, CategoryToAdd.class);
			Category c = checkCategory(cat);
			if(c != null) {
				res.status(200);
				r.categoryList.add(c);
				writeToFiles((ArrayList<Object>)(Object) r.categoryList, "./data/categories.json"); //automatski refresh
			}
			else {
				res.status(400);
			}
			return ("OK");
		});
		post("/rest/categoryDetail", (req,res)-> {
			res.type("application/json");
			String reqData = req.body();
			JsonObject jsonObject = new JsonParser().parse(reqData).getAsJsonObject();
			JsonElement object = (JsonElement) jsonObject.get("category"); 
			JsonElement nameID = (JsonElement) ((JsonObject) object).get("name"); 
			JsonElement CPU = (JsonElement) ((JsonObject) object).get("coreNumber"); 
			JsonElement RAM = (JsonElement) ((JsonObject) object).get("RAM"); 
			JsonElement GPU = (JsonElement) ((JsonObject) object).get("GPUcores"); 
			
			c = new Category(nameID.getAsString(),CPU.getAsInt(),RAM.getAsInt(),GPU.getAsInt());
			
			return "OK";
		});
		
		post("/rest/forChange", (req,res)-> {
			res.type("application/json");
			String reqData = req.body();
			System.out.println(reqData);
			CategoryToAdd cat = g.fromJson(reqData, CategoryToAdd.class);
			if(cat.nameID == null) {
				cat.nameID =c.getName();
			}
			if(cat.numCPU == 0) {
				cat.numCPU=c.getCoreNumber();
			}
			if(cat.numRAM ==0) {
				cat.numRAM =c.getRAM();
			}
			if(cat.numGPU ==0) {
				cat.numGPU =c.getGPUcores();
			}
			Category category = checkCategoryChange(cat);
			
			if(category == null) {
				res.status(400);
			}
			else {
				res.status(200);
				checkCategoryChangeVM(category);
				categoryChange(category);		
				
				writeToFiles((ArrayList<Object>)(Object) r.categoryList, "./data/categories.json"); //automatski refresh
				writeToFiles((ArrayList<Object>)(Object) r.virtMachineList, "./data/virtMachines.json"); //automatski refresh
				
				c =null;
			}
			return "OK";
		});
		post("/rest/removeCategory", (req,res)-> {
			res.type("application/json");
			String reqData = req.body();
			JsonObject jsonObject = new JsonParser().parse(reqData).getAsJsonObject();
			JsonElement object = (JsonElement) jsonObject.get("category"); 
			JsonElement nameID = (JsonElement) ((JsonObject) object).get("name"); 
            String key = nameID.getAsString();
    		int i = checkCategoryExistVM(key);
			if(i == 1) {
				res.status(400);
			}
			else {
				res.status(200);
				removeCategory(key);
				writeToFiles((ArrayList<Object>)(Object) r.categoryList, "./data/categories.json"); //automatski refresh
    		}
			return "OK";
		});
	}
	private static int checkCategoryExistVM(String nameID) {
		int i = 0;
		for(VirtualMachine vm : r.virtMachines.values()) 
		{
			if(vm.getCategory().getName().contains(nameID))
			{
				i=1;
			}
		}
		return i;
	}
	private static void checkCategoryChangeVM(Category category) {
		for(VirtualMachine vm : r.virtMachines.values()) 
		{
			if(vm.getCategory().getName().equalsIgnoreCase(c.getName())) 
			{
				vm.getCategory().setName(category.getName());
				vm.getCategory().setGPUcores(category.getGPUcores());
				vm.getCategory().setCoreNumber(category.getCoreNumber());
				vm.getCategory().setRAM(category.getRAM());
			}
			
		}
		r.virtMachineList.clear();
		for(VirtualMachine v: r.virtMachines.values()) {
			r.virtMachineList.add(v);
		}
	}
	private static void categoryChange(Category category) {
		for(Category categ: r.categories.values()) {
			if(categ.getName().equals(c.getName())) {
				categ.setName(category.getName());
				categ.setGPUcores(category.getGPUcores());
				categ.setRAM(category.getRAM());
				categ.setCoreNumber(category.getCoreNumber());
			}
		}
		r.categoryList.clear();
		for(Category v: r.categories.values()) {
			r.categoryList.add(v);
			
		}
	}
	private static void removeCategory(String key) {
		HashMap<String,Category> mapCategory = new HashMap<String,Category>();
		for(Category v: r.categories.values()) {
			if(!v.getName().equals(key)) {
				mapCategory.put(v.getName(),v);
			}
		}
		r.categories.clear();
		for(Category v: mapCategory.values()) {
			r.categories.put(v.getName(),v);
			
		}
		mapCategory.clear();
		r.categoryList.clear();
		for(Category v: r.categories.values()) {
			r.categoryList.add(v);
			
		}
		
	}
	
			
	private static Category checkCategory(CategoryToAdd cat) {
		
		if(r.categories.containsKey(cat.nameID) || cat.nameID.isEmpty()) {
			return null;
		}
		if(cat.numCPU <= 0) {
			return null;
		}
		if(cat.numRAM <=0) {
			return null;
		}
		Category categ = new Category(cat.nameID,cat.numCPU,cat.numRAM,cat.numGPU);
		
		return categ;
	}
	private static Category checkCategoryChange(CategoryToAdd cat) {
		
		if(r.categories.containsKey(cat.nameID) && !cat.nameID.equalsIgnoreCase(c.getName())) {
			return null;
		}
		if(cat.numCPU <= 0) {
			return null;
		}
		if(cat.numRAM <=0) {
			return null;
		}
		Category categ = new Category(cat.nameID,cat.numCPU,cat.numRAM,cat.numGPU);
		
		return categ;
	}
	private static User testLogin(UserToLog u) {
		if (r.users.isEmpty()) {
			return null; //nema korisnika
		}
		if(r.users.get(u.email) != null) {
			if (r.users.get(u.email).getPassword().equals(u.password)) {
				return r.users.get(u.email);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	private static void deleteOrgData() throws IOException {
		r.organizationList.remove(o);
		r.organizations.remove(o.getName(), o);
		for (User user : r.userList) {
			if (user.getOrganization()!=null) {
				if (user.getOrganization().equals(o.getName())) {
				}
				if (r.users.get(user.getEmail()).getOrganization().equals(o.getName())) {
				}
			}
			
		}
		writeToFiles((ArrayList<Object>)(Object) r.userList, "./data/users.json");
	}
	private static void refreshOrgData(Organization org) throws IOException {
		r.organizationList.remove(o);
		r.organizationList.add(org);
		r.organizations.remove(o.getName(), o);
		r.organizations.put(org.getName(), org);
		for (User user : r.userList) {
			if  (user.getOrganization() != null) {
				if (user.getOrganization().equals(o.getName())) {
					user.setOrganization(org.getName());
				}
				if (r.users.get(user.getEmail()).getOrganization().equals(o.getName())) {
					r.users.get(user.getEmail()).setOrganization(org.getName());
				}
			}
			else {
				continue;
			}
		}
		writeToFiles((ArrayList<Object>)(Object) r.userList, "./data/users.json");
		
	}
	
	private static ArrayList<SendVMO> loadVMO() {
		ArrayList<SendVMO> listOfVMO = new ArrayList<SendVMO>();
		for(VirtualMachine vm : r.virtMachineList) {
			SendVMO sv = new SendVMO();
			int i = 0;
			sv.setCategoryCoreNumber(vm.getCategory().getCoreNumber());
			sv.setCategoryGPU(vm.getCategory().getGPUcores());
			sv.setCategoryRAM(vm.getCategory().getRAM());
			sv.setNameVM(vm.getName());
			
			for(Organization os : r.organizations.values()) {
				if(os.getResources().contains(vm.getName()))
				{
					i++;
					sv.setNameORG(os.getName());
					break;
				}
			}
			if(i ==0) {
				sv.setNameORG("Nema organizaciju");
			}
			listOfVMO.add(sv);		
		}
		return listOfVMO;
	}
	private static ArrayList<SendVMO> loadVMOUser(User user) {
		ArrayList<SendVMO> data = new ArrayList<SendVMO>();
		//prvo mi treba organizacija kojoj pripada!
		ArrayList<VirtualMachine> machines = new ArrayList<VirtualMachine>();
		String organization = r.users.get(user.getEmail()).getOrganization();
		for (String resource : r.organizations.get(organization).getResources()) {
			for (VirtualMachine vm : r.virtMachineList) {
				if (vm.getName().equals(resource)) {
					machines.add(vm);
				}
			}
		}
		for (VirtualMachine vm : machines) {
			SendVMO sendData = new SendVMO(vm.getName(),vm.getCategory().getCoreNumber(), vm.getCategory().getRAM(),vm.getCategory().getGPUcores(),organization);
			data.add(sendData);
		}
		return data;
	}


}
