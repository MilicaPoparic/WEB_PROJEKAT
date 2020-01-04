package rest;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.cloudprovider.Activity;
import beans.cloudprovider.Category;
import beans.cloudprovider.Drive;
import beans.cloudprovider.DriveType;
import beans.cloudprovider.Organization;
import beans.cloudprovider.Reader;
import beans.cloudprovider.Role;
import beans.cloudprovider.User;
import beans.cloudprovider.UserToLog;
import beans.cloudprovider.VirtualMachine;
import ws.WsHandler;
import spark.Session;

public class Main {
    
	private static Gson g = new Gson();
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static Reader r = new Reader(); 

	private static void writeToFiles(ArrayList<Object> listForWrite, String string) throws IOException {
		String json = gson.toJson(listForWrite);
		FileWriter file = new FileWriter(string);
		file.write(json);
		file.close();
	}
	
	public static void main(String[] args) throws IOException {
		port(8080);
		webSocket("/ws", WsHandler.class);		
		User superadmin  = new User("admin", "admin");
		r.users.put(superadmin.getEmail(), superadmin);
	
		
		staticFiles.externalLocation(new File("./static").getCanonicalPath()); 

	
		get("/test", (req, res) -> {
			return "Works";
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
		
				
		post("/rest/login", (req, res) -> {
			res.type("application/json");
			String payload = req.body(); 
			UserToLog u = g.fromJson(payload, UserToLog.class);
			Session ss = req.session(true);
			User user = ss.attribute("user");
			if (user == null) {
				user = testLogin(u);
				ss.attribute("user",user);
			}
			if (user != null) {
				res.status(200);
			}
			else {
				res.status(400);
			}
			return ("OK");
		});	
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

}
