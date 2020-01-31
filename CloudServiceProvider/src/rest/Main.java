package rest;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import beans.cloudprovider.Activity;
import beans.cloudprovider.Category;
import beans.cloudprovider.ChangeActivity;
import beans.cloudprovider.Drive;
import beans.cloudprovider.DriveSearch;
import beans.cloudprovider.DriveType;
import beans.cloudprovider.Organization;
import beans.cloudprovider.Reader;
import beans.cloudprovider.Role;
import beans.cloudprovider.User;
import beans.cloudprovider.UserToLog;
import beans.cloudprovider.VMAdd;
import beans.cloudprovider.VMFilter;
import beans.cloudprovider.VirtualMachine;
import spark.Session;
import ws.WsHandler;

public class Main {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static Gson g = new Gson();
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static Reader r = new Reader(); 
	private static Category c = new Category();
	private static Organization o = new Organization();
	private static Drive d = new Drive();
	private static User u = new User();
	private static VirtualMachine v = new VirtualMachine();
	private static ArrayList<Drive>  retDrive = new ArrayList<Drive>();
	private static ArrayList<VirtualMachine> retVMs = new ArrayList<VirtualMachine>();
	private static ArrayList<VirtualMachine> retVMHelper;
	private static String vmName=null;
	private static HashMap<String, Double> reporthash = new HashMap<String, Double>();
	
	
	private static void writeToFiles1(HashMap<String,Object> listForWrite, String string) throws IOException {
		String json = gson.toJson(listForWrite.values());
		FileWriter file = new FileWriter(string);
		file.write(json);
		file.close();
	}
	
	public static void main(String[] args) throws IOException, ParseException {
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
		
		get("/rest/checkSuperAdmin", (req, res) -> {
			res.type("application/json");
			User user = req.session(true).attribute("user");
			if (user != null) {
				if (!user.getRole().toString().equals("superAdmin")) {
					res.status(403);
					return gson.toJson(user);
				}}
			res.status(200);
			return ("OK");
			});
		
		get("/rest/checkAdmin", (req, res) -> {
			res.type("application/json");
			User user = req.session(true).attribute("user");
			if (user != null) {
				if (!user.getRole().toString().equals("admin")) {
					res.status(403);
					return gson.toJson(user);
				}}
			res.status(200);
			return ("OK");
			});
		
		get("/rest/checkSuperAdminAdmin", (req, res) -> {
			res.type("application/json");
			User user = req.session(true).attribute("user");
			if (user != null) {
				if (user.getRole().toString().equals("user")) {
					res.status(403);
					return gson.toJson(user);
				}}
			res.status(200);
			return ("OK");
			});
	
		get("/rest/virtualne", (req, res) -> {		
			res.type("application/json");
			User user = req.session().attribute("user");
			if (user!=null) {
			if (user.getRole().toString().equals("user")) {
				return g.toJson(loadVMOUser(user));
			}
			else if  (user.getRole().toString().equals("superAdmin")){
				return g.toJson(r.virtMachines.values());
			}
			else {
				return g.toJson(loadVMOUser(user));
			}
		}
		return "OK";
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
		
		get("/rest/getOrganizationss", (req, res) -> {
			res.type("application/json");
			User user = req.session().attribute("user");
			if (user!=null) {
				if (user.getRole().toString().equals("admin")) {
					return g.toJson(user.getOrganization());
				}
				else if  (user.getRole().toString().equals("superAdmin")){
					return g.toJson(r.organizations);
				}
		    }
			return "OK";
		});
		get("/rest/getUser", (req, res) -> {
			res.type("application/json");
			return g.toJson(u);
		});
		
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
			ArrayList<Category> categoryList = new ArrayList<Category>();
			for (Category cat : r.categories.values()) {
				categoryList.add(cat);
			}
			return g.toJson(categoryList);
		});
		
		get("/rest/getCategory", (req,res)-> {
			res.type("application/json");
			return g.toJson(c);
		});
		get("/rest/getDrives", (req,res)-> {
			res.type("application/json");
			Session ss = req.session(true);
			User user = ss.attribute("user");
			ArrayList<Drive> discsList = new ArrayList<Drive>();
			if (user!=null) {
				if  (user.getRole().toString().equals("superAdmin")){
					
					for (Drive d : r.drives.values()) {
						discsList.add(d);
					}
				}
				else {
					String org = user.getOrganization();
					for (Drive d : r.drives.values()) {
						if(d.getNameOrg().equals(org))
							discsList.add(d);
					}
				}
			}
			else {
				discsList.clear();
				res.status(400);
			}
			return g.toJson(discsList);
		});
		
		get("/rest/getDrive", (req,res)-> {
			res.type("application/json");
			return g.toJson(d);	
		});
		get("rest/getVM", (req,res)-> {
			res.type("application/json");
			return g.toJson(v);	
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
			ArrayList<VirtualMachine> listOfVMO =null;
			if (user!=null) {
				if (user.getRole().toString().equals("user")) {
					listOfVMO = retVMs;//loadVMOUser(user);
				}
				else if  (user.getRole().toString().equals("superAdmin")){
					listOfVMO = retVMs;
				}
				else {
					listOfVMO = retVMs;	
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
					for (Organization org: r.organizations.values()) {
						arrOrg.add(org);
					}
					return  g.toJson(arrOrg);
				}
				else if(user.getRole().toString().equals("admin")) {
					return g.toJson(user.getOrganization());

				}
			}
			return "OK";
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
			VirtualMachine checkMe = g.fromJson(req.body(), VirtualMachine.class);
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
				writeToFiles1((HashMap<String,Object>)(Object) r.organizations, "./data/organizations.json"); 
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
			VirtualMachine vm = gson.fromJson(req.body(), VirtualMachine.class);
			if (!v.getName().equals(vm.getName()) && r.virtMachines.get(vm.getName())==null) {
				changeDrivesVM(v.getName(), vm.getName());
				changeOrgVM(v, vm);
				manageListsVM(v, true);
				manageListsVM(vm, false);
				refreshAfter();
				res.status(200);
				return ("OK");
			}
			if (!v.getName().equals(vm.getName()) && r.virtMachines.get(vm.getName())!=null) {
				res.status(400);
				return ("OK");
			}
			if (vmName != null) {
				manageListsVM(r.virtMachines.get(vmName), true);
				manageListsVM(vm, false);
				writeToFiles1((HashMap<String,Object>)(Object) r.virtMachines, "./data/virtMachines.json");
				vmName = null;
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
			writeToFiles1((HashMap<String,Object>)(Object) r.users, "./data/users.json"); 
			return ("OK");
		
		});
		
		post("/rest/changeProfile", (req,res)-> {
			res.type("application/json");
			User change = g.fromJson(req.body(), User.class);
			User loggedIn = req.session().attribute("user");
			change.setOrganization(loggedIn.getOrganization());
			if (!change.getEmail().equals(loggedIn.getEmail())) {	
				manageListsUser(loggedIn, true); 
				if (r.users.get(change.getEmail())==null) { 
					res.status(200);
					manageListsUser(change, false); 
					removeOrgUser(loggedIn); 
					if (!loggedIn.getRole().toString().equals("superAdmin")) {
						r.organizations.get(change.getOrganization()).getUsers().add(change.getEmail());
					}req.session().attribute("user",change); 
				}
				else {
					res.status(400); 
				}
			}
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
		
		post("/rest/deleteVM", (req,res)-> {
			res.type("application/json");
			VirtualMachine vm = g.fromJson(req.body(), VirtualMachine.class);
			removeFromLists(vm);
			refreshAfter();
			return ("OK");
		});
		
		post("/rest/deleteActivity", (req,res)-> {
			Activity aa = g.fromJson(req.body(), Activity.class);
			ArrayList<Activity> newOne = new ArrayList<Activity>();
			if (v.getActivityLog().size()==1) { 
				vmName = v.getName();
				v.getActivityLog().clear();
				return g.toJson(v);
			}
			else {
				for (Activity a : v.getActivityLog()) {
					if (!(a.getStart().equals(aa.getStart()))) 
						newOne.add(a);
				}
			v.getActivityLog().clear();
			v.setActivityLog(newOne);
			vmName = v.getName();
			return g.toJson(v);
				}
		});
		
		//ovde sam vracala vm zbog moguce izmene naziva mada moze i ovako
		//vracam v sa svim samo promenim u v naziv na vm.getName
		post("/rest/changeActivity", (req,res)-> {
			res.type("application/json");
			VirtualMachine vm = g.fromJson(req.body(), VirtualMachine.class);
			if (v.getActive().equals("activate")) {
				v.getActivityLog().add(new Activity(new Date(), null));
				v.setActive("deactivate");
			}
			else {
				//gasi onaj koji nije bio ugasen
				for (Activity a: v.getActivityLog()) {
					if (a.getEnd()==null) {
						a.setEnd(new Date());
					}
				}
				v.setActive("activate");
			}
			vmName = v.getName();
			v.setName(vm.getName());
			return (g.toJson(v));
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
				cc = checkCategory1(cat);
			}
			else {
				res.status(400);
			}
			if(cc!=null) {
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
		
		post("/rest/captureCategory", (req,res)-> {
			res.type("application/json");
			String id = g.fromJson(req.body(), String.class);
			if (r.categories.get(id)!=null) {
				c = r.categories.get(id);
			}
			return "OK";
		});
		
		
		post("/rest/captureActivity", (req,res)->{
			ChangeActivity change = g.fromJson(req.body(), ChangeActivity.class);
			for (Activity a : v.getActivityLog()) {
				if (a.getStart().equals(change.start) && a.getEnd().equals(change.end)) {
					Date now = new Date();
					if (!change.newStart.equals("") && !change.newEnd.equals("")) {
						if (validateDate(sdf.parse(change.newStart),a) && validateDate(sdf.parse(change.newStart),a)) {
							if (sdf.parse(change.newEnd).after(sdf.parse(change.newStart))) {
								a.setStart(sdf.parse(change.newStart));
								a.setEnd(sdf.parse(change.newEnd));
								vmName = v.getName();
								res.status(200);
								return gson.toJson(v);
							}
						}
					}
					else if(!change.newStart.equals("")) {
						if (validateDate(sdf.parse(change.newStart),a)) {
							a.setStart(sdf.parse(change.newStart));
							vmName = v.getName();
							res.status(200);
							return gson.toJson(v);
						}
					}
					else if(!change.newEnd.equals("")) {
						if (validateDate(sdf.parse(change.newEnd),a)) {
							a.setStart(sdf.parse(change.newEnd));
							vmName = v.getName();
							res.status(200);
							return gson.toJson(v);
						}
					}
					//else {}
				}
			}
			res.status(400);
			vmName = v.getName();
			return gson.toJson(v);
		});
		
		post("/rest/forChange", (req,res)-> {
			res.type("application/json");
			Category cat = g.fromJson(req.body(), Category.class);
			if(cat.getName() == null) {
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
            d = g.fromJson(req.body(), Drive.class);
			return g.toJson(d);
		});
	
		post("/rest/forChangeDrive", (req,res)-> {
			res.type("application/json");
			Drive fromClientDrive = g.fromJson(req.body(), Drive.class);
	
			if(fromClientDrive.getName()==null) {
				fromClientDrive.setName(d.getName());
			}
			if(fromClientDrive.getDriveType() == null) {
				fromClientDrive.setDriveType(d.getDriveType());
			}
			if(fromClientDrive.getCapacity() == 0) {
				fromClientDrive.setCapacity(d.getCapacity());
			}
			if(fromClientDrive.getVirtualMachine()==null) {
				fromClientDrive.setVirtualMachine(d.getVirtualMachine());
			}
			Drive checkD = checkForChangeDrive(fromClientDrive);
			if(checkD ==null) {
				res.status(400);
			}
			else {
				res.status(200);
				refreshChangedDrive(fromClientDrive);				
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
				refreshNewDiscVM(drive);	
			}
			else {
				res.status(400);
			}
			return ("OK");
		});
		post("/rest/removeDrive", (req, res) -> {
			res.type("application/json");
			Drive drive = g.fromJson(req.body(), Drive.class);
			Drive ddr = removeDrive(drive);
			if(ddr==null) 
			{
				res.status(400);
			}
			else {
				res.status(200);
				refreshAfter();
			}
			return ("OK");
		});
		post("/rest/filterDrive", (req, res) -> {
			res.type("application/json");
			String reqData =req.body();
			DriveSearch drivS = g.fromJson(reqData, DriveSearch.class);
			User us =  req.session().attribute("user");
			retDrive = checkParamSearch(drivS, us.getOrganization());

			if(retDrive !=null) {
				res.status(200);
				
			}else{
				res.status(400);
			}
			return ("OK");
		});
		post("/rest/filterVM", (req, res) -> {
			res.type("application/json");
			VMFilter filter = gson.fromJson(req.body(), VMFilter.class);
			User us =  req.session().attribute("user");
			retVMs = new ArrayList<VirtualMachine>();
			retVMs = checkParamSearch(filter, us.getOrganization());	
			if(retVMs != null) {
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
		post("/rest/findReport", (req,res)-> {
			User user = req.session().attribute("user");
			ChangeActivity dates = g.fromJson(req.body(),ChangeActivity.class);
			ArrayList<VirtualMachine> machines = loadVMOUser(user);
			double total =0.0;
			if(countDrives(dates)) {
				for (VirtualMachine vm : machines) {
					ArrayList<Activity> vmActivity = checkInterval(dates, vm.getActivityLog());
					double price = getHourPrice(vm); 
					double activeHours = getActiveHours(vmActivity);
					total += price*activeHours;
					reporthash.put(vm.getName(), price*activeHours);
				}
			 }
		   if(countDrives(dates)) {
			   ArrayList<Drive> drives = getUserDrives(user);
			   for(Drive du: drives) {
					double price = getHourPriceDrive(du);
					double activeHours= getActiveHoursDrive(dates);
					total += price*activeHours;
					reporthash.put(du.getName(), price*activeHours);
				}
		   }
		    if(reporthash.size()==0 )
				res.status(400);
			else
				res.status(200);
			reporthash.put("sum", total);
			
			return (g.toJson(reporthash));
		});
		
	}
	private static boolean countDrives(ChangeActivity dates) throws ParseException {
		Date start = sdf.parse(dates.newStart);
		Date end = sdf.parse(dates.newEnd);
		if(end.getTime()-start.getTime()<=0) {
			return false;
		}
		return true;	
	}

	private static void refreshCat() throws IOException {
		writeToFiles1((HashMap<String,Object>)(Object) r.categories, "./data/categories.json");
	}
	private static void refreshDrive() throws IOException {
		writeToFiles1((HashMap<String,Object>)(Object)r.drives, "./data/disc.json"); 		
	}
	private static void refreshOrg() throws IOException {
		writeToFiles1((HashMap<String,Object>)(Object)r.organizations, "./data/organizations.json"); 		
	}
	private static void refreshVM() throws IOException {
		writeToFiles1((HashMap<String,Object>)(Object)r.virtMachines, "./data/virtMachines.json"); 
	}
	private static void refreshUser() throws IOException {
		writeToFiles1((HashMap<String,Object>)(Object) r.users, "./data/users.json");
	}
	private static void refreshAfter() throws IOException {
		refreshDrive();
		refreshOrg(); 
		refreshVM();
	}

	private static double getActiveHoursDrive(ChangeActivity dates) throws ParseException {
		Date start = sdf.parse(dates.newStart);
		Date end = sdf.parse(dates.newEnd);
		return (end.getTime()-start.getTime())/(60*60 * 1000);
	}

	private static double getHourPriceDrive(Drive du) {
		double sum =0;
		if(du.getDriveType().equals(DriveType.HDD)) {
			sum += 0.1* du.getCapacity();
		}
		else {
			sum += 0.3* du.getCapacity();
		}
		return sum;
	}

	private static ArrayList<Drive> getUserDrives(User user) {
		ArrayList<Drive> dss = new ArrayList<Drive>();
		String org = user.getOrganization();
		for (Drive d : r.drives.values()) {
			if(d.getNameOrg().equals(org))
				dss.add(d);
		}
		return dss;
	}

	private static double getActiveHours(ArrayList<Activity> vmActivity) {
		double sum = 0;
		for (Activity a : vmActivity) {
			sum += (a.getEnd().getTime()-a.getStart().getTime())/(60*60 * 1000);
		}
		return sum;
	}

	private static double getHourPrice(VirtualMachine vm) {
		double monthly = 25*(vm.getCategory().getCoreNumber())+15*(vm.getCategory().getRAM())+vm.getCategory().getGPUcores();
		return monthly/(24*30);
	}

	private static ArrayList<Activity> checkInterval(ChangeActivity dates, ArrayList<Activity> log) throws ParseException {
		Date start = sdf.parse(dates.newStart);
		Date end = sdf.parse(dates.newEnd);
		ArrayList<Activity> retVal = new ArrayList<Activity>();
		for (Activity a : log) {
			if (start.before(a.getStart()) && (end.after(a.getStart())) && (end.before(a.getEnd()) || end.equals(a.getEnd()))) {
				Activity newActivity = new Activity(a.getStart(),end);
				retVal.add(newActivity);
			}
			else if ((start.after(a.getStart()) || start.equals(a.getStart())) && (end.before(a.getEnd()) || end.equals(a.getEnd()))) {
				Activity newActivity = new Activity(start,end);
				retVal.add(newActivity);
			}
			
			else if ((start.after(a.getStart()) || start.equals(a.getStart())) && (end.after(a.getEnd())) && start.before(a.getEnd())) {
				Activity newActivity = new Activity(start,a.getEnd());
				retVal.add(newActivity);
			}
			else if(a.getStart().after(start) && a.getEnd().before(end)){
					retVal.add(a);
			}
			else {}
		}
		return retVal;
	}

	private static Drive removeDrive(Drive drive) {
		int indikat =0;
		for(Drive dremove: r.drives.values()) {
			if(dremove.getName().equals(drive.getName())) {
				indikat = 1;
				r.drives.remove(drive.getName());
				break;
			}
		}
		for(VirtualMachine vm : r.virtMachines.values()) 
		{
			if(vm.getDrives().contains(drive.getName()) && vm.getNameOrg().equalsIgnoreCase(drive.getNameOrg()))
			{
				vm.getDrives().remove(drive.getName()); 
				break;
			}
		}
		for(Organization ogg : r.organizations.values()) 
		{
			if(ogg.getName().equalsIgnoreCase(drive.getNameOrg())&& ogg.getResources().contains(drive.getName()))
			{
				indikat = 2;
				ogg.getResources().remove(drive.getName());
				break;
			}
		}
		
		if(indikat ==2)
		{
			return drive;
		}
		return null;
	}

	private static Drive checkForChangeDrive(Drive fromClientDrive) {
		for(Drive driv: r.drives.values()) {
			if(driv.getName().equalsIgnoreCase(fromClientDrive.getName()) && 
					!fromClientDrive.getName().equalsIgnoreCase(d.getName())) { //sme da promeni u prethodno ime
				return null;	
			}
		}
		if(fromClientDrive.getCapacity()<0) {
			return null;
		}
			
		return fromClientDrive;
	}

	private static void refreshChangedDrive(Drive fromClientDrive) throws IOException {
		refreshDiscVM(fromClientDrive);
		refreshDiscORG(fromClientDrive);
		refreshDisc(fromClientDrive);		
		
		refreshAfter();//nebitan naziv
		
	}

	private static void refreshNewDiscVM(Drive drive) throws IOException {
		r.drives.put(drive.getName(),drive);
		refreshDrive();
		if(drive.getVirtualMachine()!=null) {
			
			for(VirtualMachine vm: r.virtMachines.values()) {
				if(vm.getName().equalsIgnoreCase(drive.getVirtualMachine())) {
					vm.getDrives().add(drive.getName());
					refreshVM();
				}
			}
			
		}
		for(Organization o: r.organizations.values()) {
			if(o.getName().equalsIgnoreCase(drive.getNameOrg())) {
				o.getResources().add(drive.getName());
				refreshOrg();
			}
		}
	}

	private static void refreshRemovedCategory(String name)throws IOException {
		removeCategory(name);
		refreshCat(); 
		
	}

	private static void removeFromLists(VirtualMachine vm) {
		manageListsVM(vm, true);
		changeDrivesVM(vm.getName(), null);
		changeOrgVM(vm, null);
	}

	private static void refreshReferencedCategory(Category category)throws IOException {
		checkCategoryChangeVM(category);
		categoryChange(category);
		refreshCat(); 
		refreshVM();  
	}

	private static void refreshCategory(Category cc) throws IOException {
		r.categories.put(cc.getName(),cc);
		refreshCat(); 
	}

	private static Category checkSendDataCateg(String reqData) throws Exception{
	    
        Category cat = g.fromJson(reqData, Category.class);
        if(cat.getName()==null)
        {
        	return null;
        }
        	
        if(cat.getCoreNumber()<0)
        {
        	return null;
        }
        if(cat.getGPUcores()<0)
        {
        	return null;
        }
        	
        if(cat.getRAM()<0)
        {
        	return null;
        }
		return cat;
	}
	private static void asRefresh(VMAdd vm) throws IOException {
		
		Category cat = r.categories.get(vm.nameC);
		VirtualMachine v = null;
		if(vm.nameD.isEmpty()) {
			 v = new VirtualMachine(vm.name, cat,vm.nameOrg);//proveri mzd ne valja
		}else
		{
            changeRefVM(vm.nameD,vm.name);
			v = new VirtualMachine(vm.name, cat,vm.nameOrg,vm.nameD);//proveri 
		}
		addToOrg(vm.nameOrg,vm.name);
		
		r.virtMachines.put(vm.name, v);
		refreshVM(); 
		
	}

	private static void addToOrg(String nameOrg, String name) throws IOException {
		for(Organization dd: r.organizations.values()) {
			if(dd.getName().equals(nameOrg)) {
				dd.getResources().add(name);
			}
		}
		refreshOrg();
	}

	private static void changeRefVM(ArrayList<String> nameD, String name) throws IOException {
		for(Drive dd: r.drives.values()) {
			for(String i : nameD) {
				if(dd.getName().equals(i)) {
					dd.setVirtualMachine(name);
				}
			}
		}
		refreshDrive(); 
		}

	private static VMAdd checkReqFields(VMAdd newVM) {
		if(newVM.name ==null) {
			return null; //obavezno ime, org i categ
		}
		if(newVM.nameC ==null) {
			return null; //obavezno ime, org i categ
		}
		if(newVM.nameOrg ==null) {
			return null; //obavezno ime, org i categ
		}
		
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
		if(newVM.name!=null && newVM.nameOrg!=null && newVM.nameC !=null) {//mozda mi fali neki uslov a da nisam sigurna da 
																			//ce se desiti
			return newVM;
		}
		return null;
	}

	private static HashMap<String,String> machOrgDrive() {
		HashMap<String,String> helpSD = new HashMap<String,String>();
		for(Organization or: r.organizations.values()){
			for(Drive driv: r.drives.values()) {
				if(driv.getVirtualMachine()==null &&
						or.getResources().contains(driv.getName())) {
					helpSD.put(driv.getName(),or.getName());
				}
			}
		}
		return helpSD;
	}

	private static ArrayList<VirtualMachine> checkParamSearch(VMFilter filter, String organizat) {
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
			if(filter.name!=null) {
				ind = checkVMName(filter.name, organizat);
			}
			if(ind ==2) {
				return null;
			}
			ind1 = filter1(filter, ind, organizat);
			
			if(ind1 == 2) {
				return null;
			}
			validation = (ind==0) && (ind1==0); 

			ind2 = filter2(filter, validation, organizat);
			
			if(ind2 == 2) {
				return null;
			}
			
			validation = (ind==0 && ind1==0 && ind2==0);
			
			ind3 = filter3(filter, validation, organizat);
			
			if(ind3 == 2) {
				return null;
			}
			
			validation = (ind==0 && ind1==0 && ind2==0 && ind3 ==0); 
			
			ind4 = filter4(filter, validation, organizat);
			
			if(ind4 == 2) {
				return null;
			}
			
			validation = (ind==0 && ind1==0 && ind2==0 && ind3 ==0 && ind4 ==0); 
			
			ind5 = filter5(filter, validation, organizat);
			
			if(ind5 == 2) {
				return null;
			}
			validation = (ind==0 && ind1==0 && ind2==0 && ind3 ==0 && ind4 ==0 && ind5 ==0); 
			
			ind6 = filter6(filter, validation, organizat);
			
			if(ind6 ==2) {
				return null;
			}
			return retVMHelper;
		}
		return null;
		
	}
	
	private static int filter6(VMFilter filter, boolean validation, String organizat) {
		int ind6 =0;
		if(validation && filter.too2!=0) {
			 ind6 =checkVMToo2Empty(filter.too2,organizat); 
		}		
		if(!validation && filter.too2!=0) {
			ind6 =checkVMToo2(filter.too2,organizat);
		}
		return ind6;
	}

	private static int filter5(VMFilter filter, boolean validation, String organizat) {
		int ind5 = 0;
		if(validation && filter.fromm2!=0) {
			ind5 = checkVMFrom2Empty(filter.fromm2,organizat); 
		}
		
		if(!validation && filter.fromm2!=0) {
			ind5= checkVMFrom2(filter.fromm2,organizat);
		}
		return ind5;
	}

	private static int filter4(VMFilter filter, boolean validation, String organizat) {
		int ind4 =0;
		if(validation && filter.too1!=0) {
			ind4 = checkVMTo1Empty(filter.too1,organizat); 
		}
		
		if(!validation && filter.too1!=0) {
			ind4 = checkVMTo1(filter.too1,organizat);
		}
		return ind4;
	}

	private static int  filter3(VMFilter filter, boolean validation, String organizat) {
		int ind3 = 0;
		if(validation && filter.fromm1!=0) {
			ind3 = checkVMFrom1Empty(filter.fromm1,organizat); 
		}
		
		if(!validation && filter.fromm1!=0) {
			ind3 = checkVMFrom1(filter.fromm1,organizat);
		}
		return ind3;
	}

	private static int filter2(VMFilter filter, boolean validation, String organizat) {
		int ind2 =0;
		if(validation && filter.too!=0) {//znaci ovo je prvi filter
			
			ind2 = checkVMToEmpty(filter.too,organizat);
		}
						
		if(!validation && filter.too!=0) {//vec nesto filtrirao
			
			ind2 = checkVMTo(filter.too,organizat);
		}
		return ind2;
	}

	private static int filter1(VMFilter filter, int ind, String organizat) {
		int ind1 =0;
		if(ind==0 && filter.fromm!=0) {
			ind1 = checkVMFromEmpty(filter.fromm,organizat);
		}
		if(ind == 1 && filter.fromm!=0) {
			ind1 = checkVMFrom(filter.fromm,organizat);
		}
		return ind1;
	}

	private static int function() {
		if(retVMHelper.isEmpty()) {
			return 2;
		}else {
			return 1;
		}	
	}
	
	private static int checkVMToo2Empty(int too2, String organizat) {
		if(organizat!=null) {
			for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getCategory().getGPUcores() <= too2 && vm.getNameOrg().equals(organizat))
				{
					retVMHelper.add(vm);
				}
			}
		}
		else {
			for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getCategory().getGPUcores() <= too2)
				{
					retVMHelper.add(vm);
				}
			}
		}
		return function();
		
	}
	
	private static int checkVMToo2(int too2, String organizat) {
		if(organizat!=null) {
			for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
			{
				if(vm.getCategory().getGPUcores()> too2 && vm.getNameOrg().equals(organizat))
				{
					retVMHelper.remove(vm);
					break;
				}
			}
		}
		else {
			for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
			{
				if(vm.getCategory().getGPUcores()> too2)
				{
					retVMHelper.remove(vm);
					break;
				}
			}
		}
		return function();
	}

	private static int checkVMFrom2Empty(int fromm2, String organizat) {
		if(organizat!=null) {
			for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getCategory().getGPUcores() >= fromm2 && vm.getNameOrg().equals(organizat))
				{
					retVMHelper.add(vm);
				}
			}
		}
		else {
			for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getCategory().getGPUcores() >= fromm2)
				{
					retVMHelper.add(vm);
				}
			}
		}
		return function();
	}

	private static int checkVMFrom2(int fromm2, String organizat) {
		if(organizat!=null) {
			for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
			{
				if(vm.getCategory().getGPUcores()< fromm2 && vm.getNameOrg().equals(organizat))
				{
					retVMHelper.remove(vm);
				}
			}
		}else {
			for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
			{
				if(vm.getCategory().getGPUcores()< fromm2)
				{
					retVMHelper.remove(vm);
				}
			}
		}
		return function();
	}

	private static int checkVMTo1Empty(int too1, String organizat) {
		if(organizat!=null) {
			for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getCategory().getRAM() <= too1 && vm.getNameOrg().equals(organizat))
				{
					retVMHelper.add(vm);
				}
			}
		}
		else {
			for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getCategory().getRAM() <= too1)
				{
					retVMHelper.add(vm);
				}
			}
		}
		return function();
	}

	private static int checkVMTo1(int too1, String organizat) {
		if(organizat!=null) {
			for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
			{
				if(vm.getCategory().getRAM()> too1 && vm.getNameOrg().equals(organizat))
				{
					retVMHelper.remove(vm);
				}
			}
		}
		else {
			for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
			{
				if(vm.getCategory().getRAM()> too1)
				{
					retVMHelper.remove(vm);
				}
			}
		}
		return function();
	
	}

	private static int checkVMFrom1Empty(int fromm1, String organizat) {
	  if(organizat!=null) {
		  for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getCategory().getRAM() >= fromm1 && vm.getNameOrg().equals(organizat))
				{
					retVMHelper.add(vm);
					
				}
			} 
	  }else {
		  for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getCategory().getRAM() >= fromm1)
				{
					retVMHelper.add(vm);
					
				}
			}
	  }
	  return function();
	}

	private static int checkVMFrom1(int fromm1, String organizat) {
		if(organizat!=null) {
			for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
			{
				if(vm.getCategory().getRAM()< fromm1 && vm.getNameOrg().equals(organizat))
				{
					retVMHelper.remove(vm);
				}
			}
		}
		else {
			for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
			{
				if(vm.getCategory().getRAM()< fromm1)
				{
					retVMHelper.remove(vm);
				}
			}
		}
		return function();
	}

	private static int checkVMToEmpty(int too, String organizat) {
       if(organizat!=null) {
    	   for(VirtualMachine vm : r.virtMachines.values()) {
	   			if(vm.getCategory().getCoreNumber() <= too && vm.getNameOrg().equals(organizat))
	   			{
	   				retVMHelper.add(vm);
	   			}
   			}
       }else {
    	   for(VirtualMachine vm : r.virtMachines.values()) {
	   			if(vm.getCategory().getCoreNumber() <= too)
	   			{
	   				retVMHelper.add(vm);
	   			}
   			}
       }
       return function();
	}

	private static int checkVMTo(int too, String organizat) {
		if(organizat!=null) {
			for(VirtualMachine vm :new ArrayList<VirtualMachine>(retVMHelper)) 
			{
				if(vm.getCategory().getCoreNumber()> too && vm.getNameOrg().equals(organizat))
				{
					retVMHelper.remove(vm);
				}
			}
		}
		else {
			for(VirtualMachine vm :new ArrayList<VirtualMachine>(retVMHelper)) 
			{
				if(vm.getCategory().getCoreNumber()> too)
				{
					retVMHelper.remove(vm);
				}
			}
		}
		return function();
	}

	private static int checkVMFrom(int fromm, String organizat) {
		if(organizat !=null) {
			for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
			{
				if(vm.getCategory().getCoreNumber()< fromm && vm.getNameOrg().equals(organizat))
				{
					retVMHelper.remove(vm);
				}
			}
			
		}else {
			for(VirtualMachine vm : new ArrayList<VirtualMachine>(retVMHelper)) 
			{
				if(vm.getCategory().getCoreNumber()< fromm)
				{
					retVMHelper.remove(vm);
				}
			}
		}
		return function();
	}

	private static int  checkVMFromEmpty(int fromm, String organizat) {
		if(organizat!=null) {
			for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getCategory().getCoreNumber() >= fromm && vm.getNameOrg().equals(organizat))
				{
					retVMHelper.add(vm);
				}
			}
		}else {
			for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getCategory().getCoreNumber() >= fromm)
				{
					retVMHelper.add(vm);
				}
			}
		}
		return function();
	}

	private static int checkVMName(String name, String organizat) {
		if(organizat !=null) {
			for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getName().equalsIgnoreCase(name) && vm.getNameOrg().equals(organizat))
				{
					retVMHelper.add(vm);
					return 1;    //usao i nasao
				}
			}
			return 2;            //usao i nije nasao
		}
		else {
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
	}

	private static boolean emptySearch(VMFilter filter) {
		
		boolean validation =filter.too == 0 && filter.fromm == 0;
		boolean validation1 =filter.too1 == 0 && filter.fromm1 == 0 && validation;
		boolean validation2 =filter.too2 == 0 && filter.fromm2 == 0 && validation1;
		if(filter.name==null && validation2) {
			return true;
		}
		return false;
	}
	private static void refreshFiles() throws IOException {
		refreshDrive(); 
		refreshOrg(); 
	}
	

	private static void changeDrivesVM(String v, String vm) {
		for (Drive d : r.drives.values()) {
			if (d.getVirtualMachine().equals(v)) {
				d.setVirtualMachine(vm);
			}
		}
	}
	private static void changeOrgVM(VirtualMachine v,VirtualMachine vm) {
		if (r.organizations.get(v.getNameOrg())!=null) {
			if (r.organizations.get(v.getNameOrg()).getResources().contains(v.getName())) {
				r.organizations.get(v.getNameOrg()).getResources().remove(v.getName());
				if (vm!=null) {
				r.organizations.get(v.getNameOrg()).getResources().add(vm.getName());}
			}
			}
	}
	private static ArrayList<Drive> checkParamSearch(DriveSearch retDr, String organizat) {
		retDrive = new ArrayList<Drive>();
		ArrayList<Drive>  ret = new ArrayList<Drive>();
		int indikator1 = 0;
		int indikator2 = 0;
		int indikator3 = 0;
		int indikator4 = 0;
		int indikator5 = 0;
		boolean help=false;
		if(retDr.name!=null)
			indikator1 = CheckNameDrive(retDr.name, organizat);
		if(indikator1==2) {
			return null;
		}
		if(indikator1 ==0 && retDr.fromm!=0) {//ne filtrira po imenu, pocinje sa kategorijom
			indikator2 = CheckFromDrive(retDr.fromm,organizat);
		}
		if(indikator1 ==1 && retDr.fromm!=0) {//filtrira po imenu i od iz kateg
			indikator2 = CheckNameFromDrive(retDr.fromm,organizat);
		}
		if(indikator2==2) {
			return null;
		}
		help = (indikator1==0) && (indikator2== 0);
		if(help && retDr.too!=0) {   //nisu uneta prethodna 2 filtera
			indikator3 = CheckTooDrive(retDr.too,organizat);
		}
		help = (indikator1==0 && indikator2==1) || (indikator1==1 && indikator2==0) ||(indikator1==1 && indikator2==1);
		if(help && retDr.too!=0) {  //filtrirali po nekom od prethodna 2
			indikator3 = CheckNotEmptyTooDrive(retDr.too,organizat);
		}
		if(indikator3==2) {
			return null;
		}
		help = (indikator1==0) && (indikator2== 0) && (indikator3== 0);
		if(help && retDr.checked1!=null) {  
			indikator4 = CheckCheckTypeDrive(retDr.checked1,organizat);
		}
		if(!help && retDr.checked1!=null) { 
			indikator4 = CheckCheckTypeNotEmptyDrive(retDr.checked1,organizat);
		
		}
		if(indikator4==2) {
			return null;
		}
		help = (indikator1==0) && (indikator2== 0) && (indikator3== 0) &&  (indikator4== 0);
		if(help && retDr.checked2!=null) {  
			indikator5= CheckCheckVMDrive(retDr.checked2,organizat);
		}
		if(!help && retDr.checked2!=null) { 
			indikator5 = CheckCheckVMNotEmptyDrive(retDr.checked2,organizat);
		
		}
		if(indikator5==2) {
			return null;
		}
		return retDrive;
	}
		
	private static int CheckCheckVMNotEmptyDrive(String checked2, String organizat) {
		if(organizat!=null) {
			for(Drive dC: new ArrayList<Drive>(retDrive)) {
				if(checked2 !=null && dC.getVirtualMachine() == null) {
					
					retDrive.remove(dC);
				}
				else if(!dC.getVirtualMachine().equals(checked2) && dC.getNameOrg().equals(organizat)) {
					retDrive.remove(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
		else {
			for(Drive dC: new ArrayList<Drive>(retDrive)) {
				if(checked2 !=null && dC.getVirtualMachine() == null) {
					
					retDrive.remove(dC);
				}
				else if(!dC.getVirtualMachine().equals(checked2)) {
					retDrive.remove(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
	}

	private static int CheckCheckVMDrive(String checked2, String organizat) {
		if(organizat!=null) {
			for(Drive dC: r.drives.values()) {
				if(dC.getVirtualMachine()!=null && dC.getVirtualMachine().equals(checked2) && 
						dC.getNameOrg().equals(organizat)) {
					retDrive.add(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
		else {
			for(Drive dC: r.drives.values()) {
				if(dC.getVirtualMachine()!=null && dC.getVirtualMachine().equals(checked2)) {
					retDrive.add(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
	}

	private static int CheckCheckTypeNotEmptyDrive(String checked1, String organizat) {
		if(organizat!=null) {
			for(Drive dC: new ArrayList<Drive>(retDrive)) {
				if(!dC.getDriveType().toString().equals(checked1) && dC.getNameOrg().equals(organizat)) {
					retDrive.remove(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
		else {
			for(Drive dC: new ArrayList<Drive>(retDrive)) {
				if(!dC.getDriveType().toString().equals(checked1)) {
					retDrive.remove(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
	}

	private static int CheckCheckTypeDrive(String checked1, String organizat) {
		if(organizat!=null) {
			for(Drive dC: r.drives.values()) {
				if(dC.getDriveType().toString().equals(checked1) && dC.getNameOrg().equals(organizat)) {
					retDrive.add(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
		else {
			for(Drive dC: r.drives.values()) {
				if(dC.getDriveType().toString().equals(checked1)) {
					retDrive.add(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
	}

	private static int CheckNotEmptyTooDrive(int too, String organizat) {
		if(organizat!=null) {
			for(Drive dC: new ArrayList<Drive>(retDrive)) {
				if(dC.getCapacity()>=too && dC.getNameOrg().equals(organizat)) {
					retDrive.remove(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
		else {
			for(Drive dC: new ArrayList<Drive>(retDrive)) {
				if(dC.getCapacity()>=too) {
					retDrive.remove(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
	}

	private static int CheckTooDrive(int too, String organizat) {
		if(organizat!=null) {
			for(Drive dC: r.drives.values()) {
				if(dC.getCapacity()<=too && dC.getNameOrg().equals(organizat)) {
					retDrive.add(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
		else {
			for(Drive dC: r.drives.values()) {
				if(dC.getCapacity()<=too) {
					retDrive.add(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
	}

	private static int CheckNameFromDrive(int fromm, String organizat) {
		if(organizat!=null) {
			for(Drive dC: new ArrayList<Drive>(retDrive)) {
				if(dC.getCapacity()<=fromm && dC.getNameOrg().equals(organizat)) {
					retDrive.remove(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
		else {
			for(Drive dC: new ArrayList<Drive>(retDrive)) {
				if(dC.getCapacity()<=fromm) {
					retDrive.remove(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
		}
	}

	private static int CheckFromDrive(int fromm, String organizat) {
		if(organizat!=null) {
			for(Drive dC: r.drives.values()) {
				if(dC.getCapacity()>=fromm && dC.getNameOrg().equals(organizat)) {
					retDrive.add(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}	
		}
		else {
			for(Drive dC: r.drives.values()) {
				if(dC.getCapacity()>=fromm) {
					retDrive.add(dC);
				}
			}		
			if(retDrive.isEmpty())
			     return 2;  //nema odgovarajucih
			else {
				return 1;
			}
			
			
		}
	}
	private static int CheckNameDrive(String name, String organizat) {
        if(organizat!=null) {
        	for(Drive dC: r.drives.values()) {
    			if(dC.getName().equalsIgnoreCase(name) && dC.getNameOrg().equals(organizat)) {
    				retDrive.add(dC);
    				return 1;  //moze zato sto je ime unique
    			}
    		}		
    		return 2;  //nije pronasao ime
        }else {
        	for(Drive dC: r.drives.values()) {
    			if(dC.getName().equalsIgnoreCase(name)) {
    				retDrive.add(dC);
    				return 1;  //moze zato sto je ime unique
    			}
    		}		
    		return 2;  //nije pronasao ime
        }
		
	}

	
	private static void manageListsUser(User loggedIn, Boolean remove) {
		if (remove) {
			//ako nesto ne radi ovde staviti da brise samo key
			r.users.remove(loggedIn.getEmail(), loggedIn);
			return;
		}
		r.users.put(loggedIn.getEmail(), loggedIn);
		
	}
	private static void manageListsOrg(Organization o, Boolean remove) {
		if (remove) {
			r.organizations.remove(o.getName(), o);
			return;
		}
		r.organizations.put(o.getName(), o);
		
	}
	

	private static void manageListsVM(VirtualMachine v, Boolean remove)  {
		if (remove) {
			r.virtMachines.remove(v.getName());	
			return;
		}
		r.virtMachines.put(v.getName(),v);
	}

	
	private static void changeUser(User change) {
		User oldOne = r.users.get(change.getEmail());
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
		if(drive.getName()==null) {
			return null;
		}
		for(Drive dd: r.drives.values()) {
			if(dd.getName().equalsIgnoreCase(drive.getName()))
				return null;
		}
		for(VirtualMachine dd: r.virtMachines.values()) {
			if(dd.getName().equalsIgnoreCase(drive.getName()))
				return null;
		}
		if(drive.getCapacity() < 0) {
			return null;
		}
		if(drive.getNameOrg() ==null) {
			return null;
		}
		if(drive.getDriveType() ==null) {
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
				if(drive.getVirtualMachine()!=null && drive.getVirtualMachine().equals("delete")) {
					driv.setVirtualMachine(null); //ako otkacim od diska vm
				}
				else {
					driv.setVirtualMachine(drive.getVirtualMachine());
				}
				
			}
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
		
	}

	private static void refreshDiscVM(Drive drive) {
		
		if(d.getVirtualMachine()==null) {//drive nije imoa do sada referencu 
			for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getName().equalsIgnoreCase(drive.getVirtualMachine())) {
					vm.getDrives().add(drive.getName());
				}
				
			}
		}else {// u dr naziv menja
			for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getDrives().contains(d.getName())) 
				{
					vm.getDrives().remove(d.getName());//obrisi stari, ko zna hoce sta on hoce sa novim
					
				}
				if(vm.getName().equalsIgnoreCase(drive.getVirtualMachine())) {
					vm.getDrives().add(drive.getName());//setujem samo naziv 
				}
				
			}
		}
		
		//ako hoce da otkaci vm od diska
		
		if(drive.getVirtualMachine()!=null && drive.getVirtualMachine().equals("delete")) {
			for(VirtualMachine vm : r.virtMachines.values()) 
			{
				if(vm.getName().equalsIgnoreCase(d.getVirtualMachine())) {
					vm.getDrives().remove(d.getName());
				}
				
			}
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
	}
	
			
	private static Category checkCategory(Category cat) {
		for(Category check : r.categories.values()) {
		
			if(check.getName().equalsIgnoreCase(cat.getName()) && !cat.getName().equalsIgnoreCase(c.getName())) {
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
	private static Category checkCategory1(Category cat) {
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
		for (Organization or : r.organizations.values()) {
			if (or.getUsers().contains(user.getEmail())) {
				or.getUsers().remove(user.getEmail());
			}
		}
	}
	private static void deleteUserData() throws IOException {
		r.organizations.get(u.getOrganization()).getUsers().remove(u.getEmail());
		removeOrgUser(u);
		r.users.remove(u.getEmail(),u);
		refreshFiles();
	}
	
	private static void deleteOrgData() throws IOException {
		manageListsOrg(o, true);
		for (User user : r.users.values()) {
			if (user.getOrganization()!=null) {
				if (user.getOrganization().equals(o.getName())) { //ako user ima tu org moramo da je obrisemo
					user.setOrganization(null);
				}
			}
		}
	}
	
	private static void refreshOrgData(Organization org) throws IOException {
		manageListsOrg(o, true);
		manageListsOrg(org, false);
		for (User user : r.users.values()) {
			if  (user.getOrganization() != null) {
				if (user.getOrganization().equals(o.getName())) {
					user.setOrganization(org.getName());
				}
			}
		}
	}
	
	private static ArrayList<User> takeUsers(User user) {
		ArrayList<User> userL = new ArrayList<User>();
		if (user!=null) {
			if  (user.getRole().toString().equals("superAdmin")){
				for (User u: r.users.values()) {
					userL.add(u); 
				}
				return userL;
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
	 
	private static ArrayList<Organization> takeOrganizations(User user) {
		ArrayList<Organization> organizationL = new ArrayList<Organization>();
		if (user!=null) {
		if  (user.getRole().toString().equals("superAdmin")){
			for (Organization o: r.organizations.values()) {
				organizationL.add(o);
			}
			return organizationL;
		}
		else {
			String org = user.getOrganization();
			if (r.organizations.get(org)!=null) {
				organizationL.add(r.organizations.get(org));
			}}
		}
		return organizationL;
	}
	
	private static ArrayList<VirtualMachine> loadVMOUser(User user) {
		ArrayList<VirtualMachine> machines = new ArrayList<VirtualMachine>();
		String organization = user.getOrganization();
		for (String resource : r.organizations.get(organization).getResources()) {
			for (VirtualMachine vm : r.virtMachines.values()) {
				if (vm.getName().equals(resource)) {
					machines.add(vm);
				}
			}
		}
		return machines;
	}
	
	private static Boolean validateDate(Date d, Activity a) {
		int index = 0;		
		for (Activity activity : v.getActivityLog()) {
			if (!activity.equals(a)) {
			if (d.before(activity.getStart()) || d.after(activity.getEnd())) {
				//mora da vidi sa ostalima iz liste pa ide continue ovde
			}
			else {
				index += 1;
			}
		}
		}
		if (index == v.getActivityLog().size()-1) {
			return false;
		}
		if (d.after(new Date())) {
			return false; 
		}
		return true;
	}
}
