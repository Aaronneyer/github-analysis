package com.matrobot.gha.archive.app;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matrobot.gha.archive.EventRecord;
import com.matrobot.gha.archive.FolderArchiveReader;
import com.matrobot.gha.archive.SummaryRecord;
import com.matrobot.gha.archive.repo.RepositoryRecord;
import com.matrobot.gha.archive.user.UserRecord;

public class ArchiveParserApp {

	private static final int REPO_MIN_ACTIVITY = 5;
	private static final int USER_MIN_ACTIVITY = 5;
	private String datasetPath;
	HashMap<String, RepositoryRecord> repos = new HashMap<String, RepositoryRecord>();
	HashMap<String, UserRecord> users = new HashMap<String, UserRecord>();
	private SummaryRecord info = new SummaryRecord();
	private int year;
	private int month;
	
	
	public ArchiveParserApp(int year, int month) throws IOException{
		
		this.year = year;
		this.month = month;
//		datasetPath = prop.getProperty("data_path") + year + "-" + month; 
		parseFolder();
	}

	private void parseFolder() throws IOException{
		
		FolderArchiveReader datasetReader = new FolderArchiveReader(datasetPath);
		EventRecord	recordData;
		
		info.eventCount = 0;
		info.newRepositoryCount = 0;
		while((recordData = datasetReader.readNextRecord()) != null){
			
			updateRepositoryData(recordData);
			updateUserData(recordData);
			info.eventCount ++;
		}
		
		info.repositoryCount = repos.size();
	}

	
	private void updateRepositoryData(EventRecord event) {
	
		String url = event.getRepositoryId();
		if(url != null){
			
			RepositoryRecord record = repos.get(url);
			if(record == null){
				record = new RepositoryRecord();
				record.repository = url;
			}

			if(event.isCreateRepository()){
				record.isNew = true;
				info.newRepositoryCount += 1;
			}
			else if(event.type.equals("PushEvent")){
				addPushEventToRepository(event, record);
			}
			else if(event.type.equals("IssuesEvent")){
				record.issueOpenEventCount += 1;
			}
			

			record.eventCount += 1;
			repos.put(url, record);
			
		}
	}

	private void addPushEventToRepository(EventRecord event, RepositoryRecord record) {
		
		if(event.payload.size > 0){
			record.pushEventCount += 1;
			for(String committer : event.getCommitters()){
				record.committers.add(committer);
			}
		}
	}

	
	private void updateUserData(EventRecord event) {

		if(event.type.equals("PushEvent")){
			
			for(String name : event.getCommitters()){ 
				
				if(name != null && name.length() > 1){
					UserRecord user = users.get(name);
					if(user == null){
						user = new UserRecord();
						user.name = name;
					}
					
					user.pushEventCount += 1;
					
					user.eventCount += 1;
					users.put(user.name, user);
				}
			}
		}
	}

	
	public void saveAsJson() {
	
		FileWriter writer;
		String json;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		try{
			writer = new FileWriter(datasetPath+"/repositories.json", false);
			json = gson.toJson(repos.values());
			writer.write(json);
			writer.close();
			
			writer = new FileWriter(datasetPath+"/users.json");
			json = gson.toJson(users.values());
			writer.write(json);
			writer.close();
			
			writer = new FileWriter(datasetPath+"/summary.json");
			json = gson.toJson(info);
			writer.write(json);
			writer.close();
			
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}

	
	public void saveAsCSV() {
		
		try{
			saveRepositoriesAsCSV();
			saveCommittersAsCSV();
			
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}

	private void saveRepositoriesAsCSV() throws FileNotFoundException, UnsupportedEncodingException, IOException {
		
//		String filename = "repositories-" + year + "-" + month + ".csv";
//		FileOutputStream fos = new FileOutputStream(prop.getProperty("data_path") + filename, false);
//		Writer writer = new OutputStreamWriter(fos, "UTF-8");
//		writer.write("name,year,month,push_count,committer_count\n");
//		for(RepositoryRecord record : repos.values()){
//			if(record.pushEventCount >= REPO_MIN_ACTIVITY){
//				String line = record.repository + "," +
//								year + "," + month  + "," +
//								record.pushEventCount + ", " + record.committers.size() + "\n"; 
//				writer.write(line);
//			}
//		}
//		writer.close();
	}
	

	private void saveCommittersAsCSV() throws FileNotFoundException, UnsupportedEncodingException, IOException {
		
//		String filename = "committers-" + year + "-" + month + ".csv";
//		FileOutputStream fos = new FileOutputStream(prop.getProperty("data_path") + filename, false);
//		Writer writer = new OutputStreamWriter(fos, "UTF-8");
//		writer.write("name,year,month,commit_count\n");
//		for(UserRecord record : users.values()){
//			if(record.pushEventCount >= USER_MIN_ACTIVITY){
//				String line = "\"" + record.name + "\"," +
//								year + "," + month  + "," +
//								record.pushEventCount + "\n"; 
//				writer.write(line);
//			}
//		}
//		writer.close();
	}
	

	public static void main(String[] args) throws IOException {

		parseMonth(2011, 11);
		
		// Parse 2012
		for(int i = 1; i < 11; i++){
//			parseMonth(2012, i);
		}
		
		// Parse 2011
		for(int i = 3; i <= 12; i++){
//			parseMonth(2011, i);
		}

	}

	
	private static void parseMonth(int year, int month) throws IOException {
		
		long time = System.currentTimeMillis();

		System.out.println("Dataset: " + year + "-" + month);
		ArchiveParserApp app = new ArchiveParserApp(year, month);
		app.saveAsJson();
		app.saveAsCSV();
		time = (System.currentTimeMillis()-time)/1000;
		System.out.println(	"Repos: " + app.repos.size() + 
							" Users: " + app.users.size() + 
							" Events: " + app.info.eventCount);
		
		System.out.println("Parse time: " + time + "sec.");
		System.out.println();
	}
}
