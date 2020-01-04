package rest;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.cloudprovider.Reader;

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
		staticFiles.externalLocation(new File("./static").getCanonicalPath()); 		
	}
}
	

