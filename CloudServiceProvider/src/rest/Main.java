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
import beans.cloudprovider.Drive;
import beans.cloudprovider.DriveSearch;
import beans.cloudprovider.DriveType;
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
	private static Drive d = new Drive();
	private static ArrayList<Drive>  retDrive = new ArrayList<Drive>();

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
		get("/rest/getDrives", (req,res)-> {
			res.type("application/json");
			Session ss = req.session(true);
			User user = ss.attribute("user");
			if (user!=null) {
				if  (user.getRole().toString().equals("superAdmin")){
					return g.toJson(r.driveList);
				}
				else {
					String org = user.getOrganization();//ovim dobijam koja je org
					
					ArrayList<Drive> discsList = new ArrayList<Drive>();
					if (r.organizations.get(org)!=null) {
						for(String fromO: r.organizations.get(org).getResources()) {
							if(r.drives.get(fromO)!=null) {
								discsList.add(r.drives.get(fromO));
							}
						}
						
					}
					return g.toJson(discsList);
				}
			}
			else {
				return ("OK");
			}
		});
		get("/rest/getDrive", (req,res)-> {
			res.type("application/json");
			return g.toJson(d);
			
		});
		get("/rest/getDVM", (req,res)-> {
			res.type("application/json");
			HashMap<String, Integer> dvm = new HashMap<String, Integer>();
			for(VirtualMachine vmm: r.virtMachineList) {
				dvm.put(vmm.getName(), r.virtMachineList.indexOf(vmm));
			}
			return g.toJson(dvm);
			
		});
		get("/rest/getDTypes", (req,res)-> {
			res.type("application/json");
			HashMap<String, Integer> dc = new HashMap<String, Integer>();
			for(DriveType dType : DriveType.values()) {
				dc.put(dType.name(),dType.ordinal());
			}
			
			return g.toJson(dc);
			
		});
		
		get("/rest/getSearchDrives", (req,res)-> {
			res.type("application/json");
			return g.toJson(retDrive);
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
			Category cc = checkCategory(cat);
			if(c != null) {
				res.status(200);
				r.categories.put(cc.getName(),cc);
				r.categoryList.add(cc);
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
		
		post("/rest/viewDrives", (req,res)-> {
			res.type("application/json");
			return"OK";
		});
		post("/rest/detailDrive", (req,res)-> {
			res.type("application/json");
			String reqData = req.body();
            Drive detailD = g.fromJson(reqData, Drive.class);
            if(r.drives.containsKey(detailD.getName())) {
            	d =r.drives.get(detailD.getName()); 
            }
			return"OK";
		});
	
		post("/rest/forChangeDrive", (req,res)-> {
			res.type("application/json");
			String reqData = req.body();
			JsonObject jsonObject = new JsonParser().parse(reqData).getAsJsonObject();
			JsonElement name = (JsonElement) ((JsonObject) jsonObject).get("name");
			JsonElement driveType = (JsonElement) ((JsonObject) jsonObject).get("driveType");
			JsonElement capacity = (JsonElement) ((JsonObject) jsonObject).get("capacity");
			JsonElement nameVM = (JsonElement) ((JsonObject) jsonObject).get("nameVM");
			int indikator =0;
			DriveType set = null;
			String setVM = null;
			for (DriveType dt : DriveType.values()) {
		        if (dt.name().equals(driveType.getAsString()) && driveType.getAsString()!=null) {
		            indikator=1;
		            set =DriveType.valueOf(driveType.getAsString());
		        }
		    }
			if(indikator == 0) {
				res.status(400);
			}
			int ind2 =0;
			for (VirtualMachine v :r.virtMachines.values()) {
		        if (v.getName().equals(nameVM.getAsString()) && nameVM.getAsString()!=null) {
		        	ind2=1;
		        	setVM =v.getName();
		        }
		    }
			if(ind2 == 0) {
				res.status(400);
			}
			if(capacity.getAsInt()<=0) {
				res.status(400);
			}
			Drive drive = new Drive(name.getAsString(),set, capacity.getAsInt(), setVM);
			if(drive.getName() == null) {
				drive.setName(d.getName());
			}
			if(drive.getDriveType()==null) {
				drive.setDriveType(d.getDriveType());
			}
			if(drive.getCapacity()==0) {
				drive.setCapacity(d.getCapacity());
			}
			if(drive.getVirtualMachine()==null) {
				drive.setVirtualMachine(d.getVirtualMachine());
			}
			if(r.drives.containsKey(drive.getName()) && drive.getName()!= d.getName()) {
				res.status(400);//ne mozes mi menjati ime u ime koje je vec bilo, sori bato
			}
			else {
				res.status(200);
				refreshDiscVM(drive);
				refreshDiscORG(drive);
				refreshDisc(drive);		
				
				
				writeToFiles((ArrayList<Object>)(Object) r.virtMachineList, "./data/virtMachines.json"); //automatski refresh
				writeToFiles((ArrayList<Object>)(Object) r.organizationList, "./data/organizations.json"); //automatski refresh
				writeToFiles((ArrayList<Object>)(Object) r.driveList, "./data/disc.json"); //automatski refresh
				
				d =null;
			}
			return "OK";
		});
		post("/rest/addDrive", (req, res) -> {
			res.type("application/json");
			return ("OK");
		});
		post("/rest/addNewDrive", (req, res) -> {
			res.type("application/json");
			String reqData = req.body();
			Drive drive = g.fromJson(reqData, Drive.class);
			Drive dCheck = checkDrive(drive);
			if(dCheck != null) {
				res.status(200);
				r.drives.put(drive.getName(),drive);
				r.driveList.add(drive);
				writeToFiles((ArrayList<Object>)(Object) r.driveList, "./data/disc.json"); //automatski refresh
			}
			else {
				res.status(400);
			}
			return ("OK");
		});
		post("/rest/removeDrive", (req, res) -> {
			res.type("application/json");
			String reqData = req.body();
			Drive drive = g.fromJson(reqData, Drive.class);
			boolean valid = false;
			boolean valid1 = false;
			valid = checkDriveInOrg(drive.getName());
			valid1 =checkDriveInVM(drive.getName()); 
			if(valid==true ||valid1 ==true ) 
			{
				res.status(400);
			}
			else {
				res.status(200);
				removeDrive(drive);
				writeToFiles((ArrayList<Object>)(Object) r.driveList, "./data/disc.json"); //automatski refresh
			}
			return ("OK");
		});
		post("/rest/filterDrive", (req, res) -> {
			res.type("application/json");
			String reqData =req.body();
			DriveSearch drivS = g.fromJson(reqData, DriveSearch.class);
			retDrive = checkParamSearch(drivS);

			
			if(!retDrive.isEmpty()) {
				res.status(200);
				
			}else{
				res.status(400);
			}
			return ("OK");
		});
		
	}
	

	private static ArrayList<Drive> checkParamSearch(DriveSearch retDrive) {
		ArrayList<Drive>  ret = new ArrayList<Drive>();
		boolean indikator = false;
		boolean indikator2 = false;
		boolean indikator3 = false;
		boolean indikator4 = false;
		int forBreak = 0;
		if(!retDrive.name.equals("null")) {
			forBreak= 5;
			for(Drive dC: r.drives.values()) {
				if(dC.getName().equalsIgnoreCase(retDrive.name)) {
					forBreak = 2;
					indikator =true;
					ret.add(r.drives.get(dC.getName()));
				}
			}
		}
		if(forBreak==5) {
			ret = new ArrayList<Drive>();
			return ret;
		}else {
			if(retDrive.too-retDrive.fromm <0) {
				//ne sme ovo da dozvoli
			}
			if(retDrive.fromm != 0) {
				for(Drive dC: r.drives.values()) {
					if(dC.getCapacity()>=retDrive.fromm && !ret.contains(dC) && !indikator) {//ne znaci nista 
						ret.add(r.drives.get(dC.getName()));
						indikator2 = true;
					}else if(dC.getCapacity()>=retDrive.fromm  && ret.contains(dC) && indikator) {//definissan je 
	                    troughtDiscLower(ret, retDrive.fromm);
					}
					
				}
			}
			if(retDrive.too !=0) {
				for(Drive dC: r.drives.values()) {
					if(dC.getCapacity()<=retDrive.too && !ret.contains(dC) && !indikator && !indikator2) {//nije definisan jos 
						ret.add(r.drives.get(dC.getName()));
						indikator3 = true;
					}else if(dC.getCapacity()<=retDrive.too && ret.contains(dC) && (indikator || indikator2)) {//definissan je 
						troughtDiscUpper(ret, retDrive.too);
					}
					
				}
			}
			if(!retDrive.checked1.equals("null")) {
				for(Drive dC: r.drives.values()) {		
					if(dC.getDriveType().equals(DriveType.valueOf(retDrive.checked1)) && !ret.contains(dC)&& !indikator && !indikator2 && !indikator3) {//nije definisan jos 
						ret.add(r.drives.get(dC.getName()));
						indikator4 = true;
						
					}else if(dC.getDriveType().equals(DriveType.valueOf(retDrive.checked1)) || (indikator || indikator2 || indikator3)) {//definissan je 
						troughtDiscType(ret, retDrive.checked1);
						
					}
					
				}
			}
			if(!retDrive.checked2.equals("null")) {
				for(Drive dC: r.drives.values()) {
					if(dC.getVirtualMachine().equals(retDrive.checked2) && !ret.contains(dC)&& !indikator && !indikator2 && !indikator3  && !indikator4) {//nije definisan jos 
						ret.add(r.drives.get(dC.getName()));
						
					}else if(dC.getVirtualMachine().equals(retDrive.checked2) || (indikator || indikator2 ||indikator3 ||indikator4)) {//definissan je 
						troughtDiscVM(ret, retDrive.checked2);
						
					}
				}
			}
			return ret;
			
		}
		
	}

	private static void troughtDiscType(ArrayList<Drive> ret, String check1) {
		for (Drive drive : new ArrayList<Drive>(ret)) {
			if(!drive.getDriveType().equals(DriveType.valueOf(check1))) {
				ret.remove(drive);
			}
		}
	}
	private static void troughtDiscVM(ArrayList<Drive> ret, String check2) {
		for(Drive drive: new ArrayList<Drive>(ret)) {
			if(!drive.getVirtualMachine().equals(check2)) {
				ret.remove(drive);
			}
		}
		
	}
	private static void troughtDiscUpper(ArrayList<Drive> ret, int to) {
		for(Drive drive: new ArrayList<Drive>(ret)) {
			if(drive.getCapacity()>to) {
				ret.remove(drive);
			}
		}
	}

	private static void troughtDiscLower(ArrayList<Drive> ret, int from) {
		for(Drive drive: new ArrayList<Drive>(ret)) {
			if(drive.getCapacity()<from) {
				ret.remove(drive);
			}
		}	
	}

	private static void removeDrive(Drive drive) {
		HashMap<String,Drive> mapDrive = new HashMap<String,Drive>();
		for(Drive dd: r.drives.values()) {
			if(!dd.getName().equals(drive.getName())) {
				mapDrive.put(dd.getName(),dd);
			}
		}
		r.drives.clear();
		for(Drive v: mapDrive.values()) {
			r.drives.put(v.getName(),v);
			
		}
		mapDrive.clear();
		r.driveList.clear();
		for(Drive v: r.drives.values()) {
			r.driveList.add(v);
		}
	}

	private static boolean checkDriveInVM(String name) {
		boolean i = false;
		for(VirtualMachine vm : r.virtMachines.values()) 
		{
			if(vm.getDrives().contains(name))
			{
				i=true;
			}
		}
		return i;
	}

	private static boolean checkDriveInOrg(String name) {
		boolean i = false;
		for(Organization org : r.organizations.values()) 
		{
			if(org.getResources().contains(name))
			{
				i=true;
			}
		}
		return i;
	}

	private static Drive checkDrive(Drive drive) {
		if(r.drives.containsKey(drive.getName()) || drive.getName()==null) {
			return null;
		}
		int ind =0;
		for (DriveType dt : DriveType.values()) {
	        if (dt.name().equalsIgnoreCase(drive.getDriveType().toString())){
	            ind=1;
	        }
	    }
		if(ind ==0) {
			return null;
		}
		if(drive.getCapacity() <= 0) {
			return null;
		}
	
		return drive;
	}

	private static void refreshDisc(Drive drive) {
		for(Drive driv: r.drives.values()) {
			if(driv.getName().equals(d.getName())) {
				driv.setName(drive.getName());
				driv.setCapacity(drive.getCapacity());
				driv.setDriveType(drive.getDriveType());
				driv.setVirtualMachine(drive.getVirtualMachine());
			}
		}
		r.driveList.clear();
		for(Drive v: r.drives.values()) {
			r.driveList.add(v);
			
		}
		
	}

	private static void refreshDiscORG(Drive drive) {
		for(Organization org : r.organizations.values()) 
		{
			if(org.getResources().contains(d.getName())) //getDrives je lista
			{
				int index =org.getResources().indexOf(d.getName());
				org.getResources().set(index, drive.getName());//setujem samo naziv 
			}
			
		}
		r.organizationList.clear();
		for(Organization v: r.organizations.values()) {
			r.organizationList.add(v);
		}
		
	}

	private static void refreshDiscVM(Drive drive) {
		for(VirtualMachine vm : r.virtMachines.values()) 
		{
			if(vm.getDrives().contains(d.getName())) //getDrives je lista
			{
				int index =vm.getDrives().indexOf(d.getName());
				vm.getDrives().set(index, drive.getName());//setujem samo naziv 
			}
			
		}
		r.virtMachineList.clear();
		for(VirtualMachine v: r.virtMachines.values()) {
			r.virtMachineList.add(v);
		}
		
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
