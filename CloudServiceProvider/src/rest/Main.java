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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jws.soap.SOAPBinding.Use;

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
import beans.cloudprovider.VMAdd;
import beans.cloudprovider.VMFilter;
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
	private static User u = new User();
	private static SendVMO  v = new SendVMO ();
	private static ArrayList<Drive>  retDrive = new ArrayList<Drive>();
	private static ArrayList<VirtualMachine> retVMs = new ArrayList<VirtualMachine>();
	private static ArrayList<VirtualMachine> retVMHelper;
	
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
			if (user != null) {
				return g.toJson(user.getRole());
			}
			return ("OK");
			});
		
		get("/rest/checkRole", (req, res) -> {
			res.type("application/json");
			User user = req.session(true).attribute("user");
			if (user != null) {
				if (!user.getRole().toString().equals("superAdmin")) {
					res.status(403);
					return gson.toJson(user);
				}}
			res.status(403);
			return ("OK");
			});
		
		get("/rest/virtualne", (req, res) -> {		
			res.type("application/json");
			User user = req.session().attribute("user");
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
		return g.toJson(new ArrayList<SendVMO>());
		});
		
		get("/rest/getUsers", (req, res) -> {
			res.type("application/json");
			User user = req.session().attribute("user");
			return gson.toJson(takeUsers(user));
		});

		get("/rest/getOrganizations", (req, res) -> {
			res.type("application/json");
			User user = req.session().attribute("user");
			return gson.toJson(takeOrganizations(user));
		});

		get("/rest/getUser", (req, res) -> {
			res.type("application/json");
			return g.toJson(u);
		});
		
		//ovo je moglo tipa onde gde vraca ulogu da vraca usera pa da uzima ulogu, a ovde vraca usera!!
		get("/rest/getLoggedInUser", (req, res) -> {
			res.type("application/json");
			User loggedUser = req.session().attribute("user");
			return g.toJson(loggedUser);
		});
		
		get("/rest/getOrganization", (req, res) -> {
			res.type("application/json");
			return g.toJson(o);
		});
		
		get("/rest/testLogin", (req, res) -> {
			res.type("application/json");
			User user = req.session().attribute("user");
			if (user == null) {
				res.status(400);
				return ("OK");
			} 
			res.status(200);
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
		//
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
		get("rest/getVM", (req,res)-> {
			res.type("application/json");
			return g.toJson(v);	
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
		
		get("/rest/getSearchVMS", (req,res)-> {
			res.type("application/json");
			User user = req.session().attribute("user");
			ArrayList<SendVMO> listOfVMO =null;
			if (user!=null) {
				if (user.getRole().toString().equals("user")) {
					listOfVMO = loadVMOSUser(user);
				}
				else if  (user.getRole().toString().equals("superAdmin")){
					listOfVMO = loadVMOS();
					
				}
				else {
					listOfVMO = loadVMOSUser(user);
					
				}
			}
			return g.toJson(listOfVMO);
		});
		
		get("/rest/getOrganizationsForVM", (req,res)-> {
			res.type("application/json");
			User user = req.session().attribute("user");
			ArrayList<Organization> arrOrg = new ArrayList<Organization>();
			if(user!=null) {
				if(user.getRole().toString().equals("superAdmin")) {
					return  g.toJson(r.organizationList);
				}
				else if(user.getRole().toString().equals("admin")) {
					return g.toJson(user.getOrganization());

				}
			}
			return "OK";
		});
		get("/rest/getCategoriesForVM", (req,res)-> {
			res.type("application/json");
			return g.toJson(r.categoryList);
		});
		get("/rest/getDrivesForVM", (req,res)-> {
			res.type("application/json");
			HashMap<String,String> hashDrive = machOrgDrive();	
			return g.toJson(hashDrive);
		});
		
		post("/rest/login", (req, res) -> {
			res.type("application/json");
			UserToLog u = g.fromJson(req.body(), UserToLog.class);
			User user = testLogin(u);
			req.session().attribute("user", user);
			if (user != null) {
				res.status(200);
				return ("OK");
			}
			res.status(400);
			return ("OK");
		});
		
		post("/rest/logout", (req,res)-> {
			res.type("application/json");
			User user = req.session().attribute("user");	
			if (user != null) {
				req.session().invalidate();
			}
			return ("OK");
		});
		
		post("/rest/captureOrg", (req,res)-> {
			res.type("application/json");
			Organization checkMe = g.fromJson(req.body(), Organization.class);
			if (r.organizations.get(checkMe.getName())!=null) {
				o = r.organizations.get(checkMe.getName());
			}
			return ("OK");
		});
		
		post("/rest/captureVM", (req,res)-> {
			res.type("application/json");
			SendVMO checkMe = g.fromJson(req.body(), SendVMO.class);
			if (checkMe !=null) {
				v = checkMe;
			}
			return ("OK");
		});
		
		post("/rest/captureUser", (req,res)-> {
			res.type("application/json");
			User catchMe = g.fromJson(req.body(), User.class);
			if (r.users.get(catchMe.getEmail())!=null) {
				u = r.users.get(catchMe.getEmail());
			}
			return ("OK");
		});
	
		post("/rest/addOrganization", (req,res)-> {
			res.type("application/json");
			Organization org = g.fromJson(req.body(), Organization.class);
			if (org!=null && r.organizations.get(org.getName())==null) {
				org.setUsers(new ArrayList<String>());
				org.setResources(new ArrayList<String>());
				manageListsOrg(org, false);
				res.status(200);
				writeToFiles((ArrayList<Object>)(Object) r.organizationList, "./data/organizations.json"); 
				return ("OK");
			}
			res.status(400);
			return ("OK");
		});
		
		post("/rest/addUser", (req,res)-> {
			res.type("application/json");
			User loggedIn = req.session().attribute("user");
			User newUser = g.fromJson(req.body(), User.class);
			if (newUser!=null && r.users.get(newUser.getEmail())==null) {
				ArrayList<String> users = new ArrayList<String>();
				if (loggedIn.getRole().toString().equals("admin")) 
					newUser.setOrganization(loggedIn.getOrganization());
				r.organizations.get(newUser.getOrganization()).getUsers().add(newUser.getEmail());
				manageListsUser(newUser, false);
				res.status(200);
				refreshFiles();
				return ("OK");
			}
			res.status(400);
			return ("OK");
		});
		
		post("/rest/changeVM", (req,res)-> {
			res.type("application/json");
			SendVMO vm = gson.fromJson(req.body(),SendVMO.class);
			if (!v.getNameVM().equals(vm.getNameVM()) && r.virtMachines.get(vm.getNameVM())==null) {
				VirtualMachine old = r.virtMachines.get(v.getNameVM());
				VirtualMachine virtual = r.virtMachines.get(v.getNameVM());
				virtual.setName(vm.getNameVM());
				manageListsVM(old, true);
				manageListsVM(virtual, false);
				changeDrivesVM(v.getNameVM(), vm.getNameVM());
				changeOrgVM(v, vm);
				writeDependencies();
				//v.setNameVM(vm.getNameVM()); 
				//System.out.println(v.getNameVM());
				res.status(200);
				return ("OK");
			}
			if (!v.getNameVM().equals(vm.getNameVM()) && r.virtMachines.get(vm.getNameVM())!=null) {
				res.status(400);
				return ("OK");
			}
			return ("OK");
		});
		post("/rest/changeOrg", (req,res)-> {
			res.type("application/json");
			Organization org = g.fromJson(req.body(), Organization.class);
			if (!o.getName().equals(org.getName()) && r.organizations.get(org.getName())!=null) {
				res.status(400);
				return ("OK");
			}
			res.status(200);
			refreshOrgData(org);
			refreshFiles();
			return ("OK");
		
		});
		
		post("/rest/changeUser", (req,res)-> {
			res.type("application/json");
			User change= g.fromJson(req.body(), User.class);
			res.status(200);
			changeUser(change);
			writeToFiles((ArrayList<Object>)(Object) r.userList, "./data/users.json"); 
			return ("OK");
		
		});
		
		post("/rest/changeProfile", (req,res)-> {
			res.type("application/json");
			User change = g.fromJson(req.body(), User.class);
			User loggedIn = req.session().attribute("user");
			change.setOrganization(loggedIn.getOrganization());
			if (!change.getEmail().equals(loggedIn.getEmail())) {	//ako menja email
				manageListsUser(loggedIn, true);
				if (r.users.get(change.getEmail())==null) { //ako email nije zauzet vec
					res.status(200);
					manageListsUser(change, false);
					removeOrgUser(loggedIn); 
					r.organizations.get(change.getOrganization()).getUsers().add(change.getEmail());
					for (Organization o:r.organizationList) {
						if (o.getName().equals(change.getOrganization())) {
							o.getUsers().add(change.getEmail());
						}
					}
				}
				//else {
					//res.status(400);
				//}
			}
			//ako nije menjan email
			else {
				changeUser(change);
			}
			refreshFiles();
			return ("OK");
		
		});
		
		post("/rest/deleteOrg", (req,res)-> {
			res.type("application/json");
			Organization org = g.fromJson(req.body(), Organization.class);
			deleteOrgData();
			refreshFiles();
			return ("OK");
		});
		
		post("/rest/deleteUser", (req,res)-> {
			res.type("application/json");
			User deleteMe = g.fromJson(req.body(), User.class);
			User loggedIn = req.session().attribute("user");
			if (!deleteMe.getEmail().equals(loggedIn.getEmail())) {
				deleteUserData();
				res.status(200);
				return ("OK");
			}
			res.status(400);
			return ("OK");
		});

		post("/rest/addNewCategory", (req, res) -> {
			res.type("application/json");
			Category cat = checkSendDataCateg(req.body());
			Category cc = null;
			if(cat !=null) {
				cc = checkCategory(cat);
			}
			if(c != null && cc!=null) {
				res.status(200);
				refreshCategory(cc);
			}
			else {
				res.status(400);
			}
			return ("OK");
		});

		post("/rest/categoryDetail", (req,res)-> {
			
			res.type("application/json");
			c = g.fromJson(req.body(),Category.class);
	
			return "OK";
		});
		
		post("/rest/forChange", (req,res)-> {
			res.type("application/json");

			Category cat =  checkSendDataCateg(req.body());
			
			if(cat.getName() == "null") {
				cat.setName(c.getName());
			}
			if(cat.getCoreNumber() == 0) {
				cat.setCoreNumber(c.getCoreNumber());
			}
			if(cat.getRAM() == 0) {
				cat.setRAM(c.getRAM());
			}
			if(cat.getGPUcores() == 0) {
				cat.setGPUcores(c.getGPUcores());
			}
			Category category = checkCategory(cat);
			
			if(category == null) {
				res.status(400);
			}
			else {
				res.status(200);
				refreshReferencedCategory(category);
				c = null;
			}
			return "OK";
		});
		
		post("/rest/removeCategory", (req,res)-> {
			res.type("application/json");
			Category cat= g.fromJson(req.body(), Category.class);
    		int i = checkCategoryExistVM(cat.getName());
			if(i == 1) {
				res.status(400);
			}
			else {
				res.status(200);
				refreshRemovedCategory(cat.getName());
    		}
			return "OK";
		});

		post("/rest/detailDrive", (req,res)-> {
			res.type("application/json");
            Drive detailD = g.fromJson(req.body(), Drive.class);
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
		
		post("/rest/addNewDrive", (req, res) -> {
			res.type("application/json");
			Drive drive = g.fromJson(req.body(), Drive.class);
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
			Drive drive = g.fromJson(req.body(), Drive.class);
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
		post("/rest/filterVM", (req, res) -> {
			res.type("application/json");
			System.out.println(req.body());
			VMFilter filter = gson.fromJson(req.body(), VMFilter.class);
			retVMs = checkParamSearch(filter);	
			if(!retVMs.isEmpty()) {
				res.status(200);
				
			}else{
				res.status(400);
			}
			return ("OK");
		});
		post("/rest/viewVirt", (req, res) -> {
			res.type("application/json");
			return ("OK");
		});
		post("/rest/viewDrives", (req, res) -> {
			res.type("application/json");
			return ("OK");
		});
		post("/rest/addVM", (req, res) -> {
			res.type("application/json");
			return ("OK");
		});
		post("/rest/addNewVM", (req, res) -> {
			res.type("application/json");
	        VMAdd newVM = g.fromJson(req.body(), VMAdd.class);
	        VMAdd vm = checkReqFields(newVM);
	        if(vm!=null) {
	        	res.status(200);
	        	asRefresh(vm);
	        	return ("OK");
	        }
	        res.status(400);
			return ("OK");
		});
		
	}
	private static void refreshRemovedCategory(String name)throws IOException {
		removeCategory(name);
		writeToFiles((ArrayList<Object>)(Object) r.categoryList, "./data/categories.json"); 
		
	}

	private static void refreshReferencedCategory(Category category)throws IOException {
		checkCategoryChangeVM(category);
		categoryChange(category);		
		writeToFiles((ArrayList<Object>)(Object) r.categoryList, "./data/categories.json");
		writeToFiles((ArrayList<Object>)(Object) r.virtMachineList, "./data/virtMachines.json"); 
		
	}

	private static void refreshCategory(Category cc) throws IOException {
		r.categories.put(cc.getName(),cc);
		r.categoryList.add(cc);
		writeToFiles((ArrayList<Object>)(Object) r.categoryList, "./data/categories.json"); 
		
	}

	private static Category checkSendDataCateg(String reqData) throws Exception{
        
		int coreNumber =0;
	    int RAM=0;
	    int	GPUcores=0;
		JsonObject jsonObject = new JsonParser().parse(reqData).getAsJsonObject();
		
		String nameCK = ((JsonElement) ((JsonObject) jsonObject).get("nameID")).toString();
        String name = null;
        if (!nameCK.toString().isEmpty()) {
            name = nameCK.replace("\"", "");
        }
        JsonElement core =((JsonObject) jsonObject).get("numCPU");
       
        if (checkIt(core)) {
       		coreNumber = core.getAsInt();
        } 
        JsonElement ramm =((JsonObject) jsonObject).get("numRAM");
        if (checkIt(ramm)) {
        	RAM = ramm.getAsInt();
        } 
        JsonElement gpu =((JsonObject) jsonObject).get("numGPU");
        if (checkIt(gpu)) {
        	GPUcores = gpu.getAsInt();
        } 
        System.out.println(RAM+" "+coreNumber+" "+GPUcores);
		Category category = new Category(name, coreNumber, RAM,GPUcores);
	
		return category;
	}

	private static boolean checkIt(JsonElement ramm){
		    try {
		        ramm.getAsInt();
		        return true;
		    }
		    catch( Exception e ) {
		        return false;
		    }
	}

	private static void asRefresh(VMAdd vm) throws IOException {
		
		Category cat = r.categories.get(vm.nameC);
		VirtualMachine v = null;
		if(vm.nameD.isEmpty()) {
			 v = new VirtualMachine(vm.name, cat);
		}else
		{
            changeRefVM(vm.nameD,vm.name);
			v = new VirtualMachine(vm.name, cat,vm.nameD);
		}
		addToOrg(vm.nameOrg,vm.name);
		
		r.virtMachines.put(vm.name, v);
		r.virtMachineList.clear();
		for(VirtualMachine virt: r.virtMachines.values()) {
			r.virtMachineList.add(virt);
		}
		writeToFiles((ArrayList<Object>)(Object) r.virtMachineList, "./data/virtMachines.json");
		
	}

	private static void addToOrg(String nameOrg, String name) throws IOException {
		for(Organization dd: r.organizations.values()) {
			if(dd.getName().equals(nameOrg)) {
				dd.getResources().add(name);
			}
		}
		r.organizationList.clear();
		for(Organization dd: r.organizations.values()) {
			r.organizationList.add(dd);
		}
		writeToFiles((ArrayList<Object>)(Object) r.organizationList, "./data/organizations.json");
	}

	private static void changeRefVM(ArrayList<String> nameD, String name) throws IOException {
		for(Drive dd: r.drives.values()) {
			for(String i : nameD) {
				if(dd.getName().equals(i)) {
					dd.setVirtualMachine(name);
				}
			}
		}
		r.driveList.clear();
		for(Drive dd: r.drives.values()) {
			r.driveList.add(dd);
		}
		writeToFiles((ArrayList<Object>)(Object) r.driveList, "./data/disc.json");
	}

	private static VMAdd checkReqFields(VMAdd newVM) {
		for(String vms : r.virtMachines.keySet()) {
			if(vms.equalsIgnoreCase(newVM.name))//ukoliko postoji ne moze
				{
				   return null;
				}
		}
		for(Drive dd: r.drives.values()) {
			if(dd.getName().equalsIgnoreCase(newVM.name)) {  //ovo ne bi smelo, zato sto su diskovi i vm, resursi
				return null;
			}
			
		}
		if(!newVM.name.equals("null") && !newVM.nameC.equals("null")) {
			return newVM;
		}
		return null;
	}

	private static HashMap<String,String> machOrgDrive() {
		HashMap<String,String> helpSD = new HashMap<String,String>();
		for(Organization or: r.organizations.values()){
			for(Drive driv: r.drives.values()) {
				if(driv.getVirtualMachine().equals("null") && 
						or.getResources().contains(driv.getName())) {
					helpSD.put(driv.getName(),or.getName());
				}
			}
		}
		return helpSD;
	}

	private static ArrayList<SendVMO> loadVMOSUser(User user) {
		ArrayList<SendVMO> data = new ArrayList<SendVMO>();
		ArrayList<VirtualMachine> machines = new ArrayList<VirtualMachine>();
		String organization = r.users.get(user.getEmail()).getOrganization();
		for (String resource : r.organizations.get(organization).getResources()) {
			for (VirtualMachine vm : retVMs) {
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

	private static ArrayList<SendVMO> loadVMOS() {
		ArrayList<SendVMO> listOfVMO = new ArrayList<SendVMO>();
		for(VirtualMachine vm : retVMs) {
			SendVMO sv = new SendVMO();
			int i = 0;
			sv.setCategoryCoreNumber(vm.getCategory().getCoreNumber());
			sv.setCategoryGPU(vm.getCategory().getGPUcores());
			sv.setCategoryRAM(vm.getCategory().getRAM());
			sv.setNameVM(vm.getName());
			
			for(Organization os : r.organizations.values()) {
				if (os.getResources()!=null) {
					if(os.getResources().contains(vm.getName()))
					{
						i++;
						sv.setNameORG(os.getName());
						break;
					}
				}
			}
			if(i ==0) {
				sv.setNameORG("Nema organizaciju");
			}
			listOfVMO.add(sv);		
		}
		return listOfVMO;
	}

	private static ArrayList<VirtualMachine> checkParamSearch(VMFilter filter) {
		retVMHelper = new ArrayList<VirtualMachine>();
		int ind = 0;
		int ind1 = 0;
		int ind2 = 0;
		int ind3 = 0;
		int ind4 =0;
		int ind5 =0;
		int ind6 =0;
		boolean  validation = false;
		if(!emptySearch(filter)) {
			
			if(!filter.name.equals("null")) {
				ind = checkVMName(filter.name);
			}

			if(ind==0 && filter.fromm!=0) {
				ind1 = checkVMFromEmpty(filter.fromm);
			}
			if(ind == 1 && filter.fromm!=0) {
				System.out.println("dalje");
				ind1 = checkVMFrom(filter.fromm);
			}
			validation = (ind==0 && ind1==1) || (ind==1 && ind1==0) ||(ind==1 && ind1==1);
						
			if(validation && filter.too!=0) {//vec nesto filtrirao
				
				ind2 = checkVMTo(filter.too);
			}
			validation = (ind==0 && ind1==0); 

			if(validation && filter.too!=0) {//znaci ovo je prvi filter
		
				ind2 = checkVMToEmpty(filter.too);
			}
			
			validation = ((ind==0 && ind1==1) || (ind==1 && ind1==0) ||(ind==1 && ind1==1)) || ind2==1;
			
			if(validation && filter.fromm1!=0) {
				ind3 = checkVMFrom1(filter.fromm1);
			}
			validation = (ind==0 && ind1==0 && ind2==0);
			
			if(validation && filter.fromm1!=0) {
				ind3 = checkVMFrom1Empty(filter.fromm1); 
			}
		
			validation = ((ind==0 && ind1==1) || (ind==1 && ind1==0) ||(ind==1 && ind1==1)) || ind2==1 || ind3==1;
			
			if(validation && filter.too1!=0) {
				ind4 = checkVMTo1(filter.too1);
			}
			validation = (ind==0 && ind1==0 && ind2==0 && ind3 ==0); 
			
			if(validation && filter.too1!=0) {
				ind4 = checkVMTo1Empty(filter.too1); 
			}
			
			validation = ((ind==0 && ind1==1) || (ind==1 && ind1==0) ||(ind==1 && ind1==1)) || ind2==1 || ind3==1 || ind4 ==1;
			
			if(validation && filter.fromm2!=0) {
				ind5= checkVMFrom2(filter.fromm2);
			}
			validation = (ind==0 && ind1==0 && ind2==0 && ind3 ==0 && ind4 ==0); 
			
			if(validation && filter.fromm2!=0) {
				ind5 = checkVMFrom2Empty(filter.fromm2); 
			}
						
			validation = ((ind==0 && ind1==1) || (ind==1 && ind1==0) ||(ind==1 && ind1==1)) || ind2==1 
					|| ind3==1 || ind4 ==1 || ind5==1;
			
			if(validation && filter.too2!=0) {
				checkVMToo2(filter.too2);
			}
			validation = (ind==0 && ind1==0 && ind2==0 && ind3 ==0 && ind4 ==0 && ind5 ==0); 
			
			if(validation && filter.too2!=0) {
				 checkVMToo2Empty(filter.too2); 
			}
		}
		
		return retVMHelper;
	}

	private static void checkVMToo2Empty(int too2) {
		for(VirtualMachine vm : r.virtMachines.values()) 
		{
			if(vm.getCategory().getGPUcores() <= too2)
			{
				retVMHelper.add(vm);
			}
		}		
	}

	private static void checkVMToo2(int too2) {
		ArrayList<VirtualMachine> satisfiedConditions= new ArrayList<VirtualMachine>();
		for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
		{
			if(vm.getCategory().getGPUcores()<= too2)
			{
				satisfiedConditions.add(vm);
			}
		}
		retVMHelper.clear();  //svakako ocisti
		if(!satisfiedConditions.isEmpty()) {
			retVMHelper = satisfiedConditions;
		}	
	}

	private static int checkVMFrom2Empty(int fromm2) {
		boolean ind =false;
		for(VirtualMachine vm : r.virtMachines.values()) 
		{
			if(vm.getCategory().getGPUcores() >= fromm2)
			{
				retVMHelper.add(vm);
				ind = true;
			}
		}
		if(ind) {
			return 1;
		}
		return 2;           //usao i nije nasao
	}

	private static int checkVMFrom2(int fromm2) {
		ArrayList<VirtualMachine> satisfiedConditions= new ArrayList<VirtualMachine>();
		for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
		{
			if(vm.getCategory().getGPUcores()>= fromm2)
			{
				satisfiedConditions.add(vm);
			}
		}
		retVMHelper.clear();  //svakako ocisti
		if(satisfiedConditions.isEmpty()) {
			return 2;
		}
		retVMHelper = satisfiedConditions;
		return 1;
	}

	private static int checkVMTo1Empty(int too1) {
		boolean ind =false;
		for(VirtualMachine vm : r.virtMachines.values()) 
		{
			if(vm.getCategory().getRAM() <= too1)
			{
				retVMHelper.add(vm);
				ind =true;
			}
		}
		if(ind) {
			return 1;
		}
		return 2;           //usao i nije nasao
	}

	private static int checkVMTo1(int too1) {
		ArrayList<VirtualMachine> satisfiedConditions= new ArrayList<VirtualMachine>();
		for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
		{
			if(vm.getCategory().getRAM()<= too1)
			{
				satisfiedConditions.add(vm);
			}
		}
		retVMHelper.clear();  //svakako ocisti
		if(satisfiedConditions.isEmpty()) {
			return 2;
		}
		retVMHelper = satisfiedConditions;
		return 1;
	
	}

	private static int checkVMFrom1Empty(int fromm1) {
		boolean ind =false;
		for(VirtualMachine vm : r.virtMachines.values()) 
		{
			if(vm.getCategory().getRAM() >= fromm1)
			{
				retVMHelper.add(vm);
				ind =true;
			}
		}
		if(ind) {
			return 1;
		}
		return 2;           //usao i nije nasao
	}

	private static int checkVMFrom1(int fromm1) {
		ArrayList<VirtualMachine> satisfiedConditions= new ArrayList<VirtualMachine>();
		for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
		{
			if(vm.getCategory().getRAM()>= fromm1)
			{
				satisfiedConditions.add(vm);
			}
		}
		retVMHelper.clear();  //svakako ocisti
		if(satisfiedConditions.isEmpty()) {
			return 2;
		}
		retVMHelper = satisfiedConditions;
		return 1;
	}

	private static int checkVMToEmpty(int too) {
		boolean ind =false;
		for(VirtualMachine vm : r.virtMachines.values()) 
		{
			if(vm.getCategory().getCoreNumber() <= too)
			{
				retVMHelper.add(vm);
				ind =true;
			}
		}
		if(ind) {
			return 1;
		}
		return 2;           //usao i nije nasao
	}

	private static int checkVMTo(int too) {
		ArrayList<VirtualMachine> satisfiedConditions= new ArrayList<VirtualMachine>();
		for(VirtualMachine vm :new ArrayList<VirtualMachine>(retVMHelper)) 
		{
			if(vm.getCategory().getCoreNumber()<= too)
			{
				satisfiedConditions.add(vm);
			}
		}
		retVMHelper.clear();  //svakako ocisti
		if(satisfiedConditions.isEmpty()) {
			return 2;
		}
		retVMHelper = satisfiedConditions;
		return 1;
	}

	private static int checkVMFrom(int fromm) {
		boolean ind =false;
		for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
		{
			if(vm.getCategory().getCoreNumber()>=fromm)
			{
				ind =true;
			}
		}
		if(ind) {
			return 1;
		}
		retVMHelper.clear();  //zato sto je naziv vm jedinstven i ne moze naci vise od jedne
		return 2;           //usao i nije nasao
	}

	private static int  checkVMFromEmpty(int fromm) {
		boolean ind =false;
		for(VirtualMachine vm : r.virtMachines.values()) 
		{
			if(vm.getCategory().getCoreNumber() >= fromm)
			{
				retVMHelper.add(vm);
				ind =true;
			}
		}
		if(ind) {
			return 1;    //usao i nasao
		}
		return 2;           //usao i nije nasao
	}

	private static int checkVMName(String name) {
		
		for(VirtualMachine vm : r.virtMachines.values()) 
		{
			if(vm.getName().equalsIgnoreCase(name))
			{
				retVMHelper.add(vm);
				return 1;    //usao i nasao
			}
		}
		return 2;            //usao i nije nasao
	}

	private static boolean emptySearch(VMFilter filter) {
		
		boolean validation =filter.too == 0 && filter.fromm == 0;
		boolean validation1 =filter.too1 == 0 && filter.fromm1 == 0 && validation;
		boolean validation2 =filter.too2 == 0 && filter.fromm2 == 0 && validation1;
		if(filter.name.equals(null)&&validation2) {
			return true;
		}
		return false;
	}
	private static void refreshFiles() throws IOException {
		writeToFiles((ArrayList<Object>)(Object) r.userList, "./data/users.json");
		writeToFiles((ArrayList<Object>)(Object) r.organizationList, "./data/organizations.json");
	}
	
	public static void writeDependencies() throws IOException {
		writeToFiles((ArrayList<Object>)(Object) r.virtMachineList, "./data/virtMachines.json");
		writeToFiles((ArrayList<Object>)(Object) r.driveList, "./data/disc.json");
		writeToFiles((ArrayList<Object>)(Object) r.organizationList, "./data/organizations.json");
		
	}
	//cisti listu diskova u zavisnosti od prmene vm
	private static void changeDrivesVM(String v, String vm) {
		for (Drive d : r.drives.values()) {
			if (d.getVirtualMachine().equals(v)) {
				
				d.setVirtualMachine(vm);
			}
		}
		for (Drive d : r.driveList){
			if (d.getVirtualMachine().equals(v)) {
				d.setVirtualMachine(vm);
			}
		}
	}
	private static void changeOrgVM(SendVMO v,SendVMO vm) {
		if (r.organizations.get(vm.getNameORG())!=null) {
			if (r.organizations.get(vm.getNameORG()).getResources().contains(v.getNameVM())) {
				r.organizations.get(vm.getNameORG()).getResources().remove(v.getNameVM());
				r.organizations.get(vm.getNameORG()).getResources().add(vm.getNameVM());
			}
			for (Organization organ: r.organizationList) {
				if (organ.getResources().contains(v.getNameVM())) {
					organ.getResources().remove(v.getNameVM());
					organ.getResources().add(vm.getNameVM());
				}
			}
			}
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
	private static void manageListsUser(User loggedIn, Boolean remove) {
		if (remove) {
			r.userList.remove(loggedIn);
			r.users.remove(loggedIn.getEmail(), loggedIn);
			return;
		}
		r.userList.add(loggedIn);
		r.users.put(loggedIn.getEmail(), loggedIn);
		
	}
	private static void manageListsOrg(Organization o, Boolean remove) {
		if (remove) {
			r.organizationList.remove(o);
			r.organizations.remove(o.getName(), o);
			return;
		}
		r.organizationList.add(o);
		r.organizations.put(o.getName(), o);
		
	}
	

	private static void manageListsVM(VirtualMachine v, Boolean remove)  {
		if (remove) {
			r.virtMachines.remove(v.getName(),v);
			for (String k: r.virtMachines.keySet()) {
				System.out.println(k);
			}
			r.virtMachineList.remove(v);
			return;
		}
		r.virtMachines.put(v.getName(),v);
		r.virtMachineList.add(v);
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
	private static void changeUser(User change) {
		User oldOne = r.users.get(change.getEmail());
		r.userList.remove(oldOne);
		r.userList.add(change);
		r.users.remove(oldOne.getEmail(), oldOne);
		r.users.put(change.getEmail(), change);
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
	
			
	private static Category checkCategory(Category cat) {
		for(Category check : r.categories.values()) {
			if(check.getName().equalsIgnoreCase(cat.getName())) {
				return null;
			}
		}
		if(cat.getCoreNumber() <= 0) {
			return null;
		}
		if(cat.getRAM() <=0) {
			return null;
		}
		if(cat.getGPUcores()<0) {
			return null;
		}
		return cat;
	}

	private static User testLogin(UserToLog u) {
		if (r.users.isEmpty()) {
			return null; 
		}
		if(r.users.get(u.email) != null) {
			if (r.users.get(u.email).getPassword().equals(u.password)) {
				return r.users.get(u.email);
		}}
		return null;
	}
	private static void removeOrgUser(User user) {
		for (Organization org : r.organizationList) {
			if (org.getUsers().contains(user.getEmail())) {
				org.getUsers().remove(user.getEmail());
			}
		}
		for (Organization or : r.organizations.values()) {
			if (or.getUsers().contains(user.getEmail())) {
				or.getUsers().remove(user.getEmail());
			}
		}
	}
	private static void deleteUserData() throws IOException {
		r.organizations.get(u.getOrganization()).getUsers().remove(u.getEmail());
		//ArrayList<String> newUsers = new ArrayList<String>();
		removeOrgUser(u);
		r.users.remove(u.getEmail(),u);
		r.userList.remove(u);
		refreshFiles();
		
	}
	
	private static void deleteOrgData() throws IOException {
		manageListsOrg(o, true);
		for (User user : r.userList) {
			if (user.getOrganization()!=null) {
				if (user.getOrganization().equals(o.getName())) { //ako user ima tu org moramo da je obrisemo
					user.setOrganization(null);
				}
				if (r.users.get(user.getEmail()).getOrganization().equals(o.getName())) {
					user.setOrganization(null);
				}
			}
		}
	}
	
	private static void refreshOrgData(Organization org) throws IOException {
		manageListsOrg(o, true);
		manageListsOrg(org, false);
		for (User user : r.userList) {
			if  (user.getOrganization() != null) {
				if (user.getOrganization().equals(o.getName())) {
					user.setOrganization(org.getName());
				}
				if (r.users.get(user.getEmail()).getOrganization().equals(o.getName())) {
					r.users.get(user.getEmail()).setOrganization(org.getName());
				}
			}
		}
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
			sv.setActivityLog(vm.getActivityLog());
			sv.setCategory(vm.getCategory().getName());
			sv.setDrives(vm.getDrives());
			
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
	private static ArrayList<User> takeUsers(User user) {
		ArrayList<User> userL = new ArrayList<User>();
		if (user!=null) {
			if  (user.getRole().toString().equals("superAdmin")){
				return r.userList;
			}
			else { 
				String org = user.getOrganization();
				if (r.organizations.get(org) != null )  {
					for (String email : r.organizations.get(org).getUsers()) {
						userL.add(r.users.get(email));
					}}}
			}
		return userL; 
	}
	
	//DA SMO IMALI CELE OBJEKTE NE BI NAM TREBALE OVE TAKE FJE 
	private static ArrayList<Organization> takeOrganizations(User user) {
		ArrayList<Organization> organizationL = new ArrayList<Organization>();
		if (user!=null) {
		if  (user.getRole().toString().equals("superAdmin")){
			return r.organizationList;
		}
		else {
			String org = user.getOrganization();
			if (r.organizations.get(org)!=null) {
				organizationL.add(r.organizations.get(org));
			}}
		}
		return organizationL;
	}
	private static ArrayList<SendVMO> loadVMOUser(User user) {
		ArrayList<SendVMO> data = new ArrayList<SendVMO>();
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
			SendVMO sendData = new SendVMO(vm.getName(),vm.getCategory().getName(), vm.getCategory().getCoreNumber(), vm.getCategory().getRAM(),vm.getCategory().getGPUcores(),organization, vm.getActivityLog(), vm.getDrives());
			data.add(sendData);
		}
		return data;
	}
}
