package com.matrobot.gha.app.parser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matrobot.gha.dataset.ActivityRecord;
import com.matrobot.gha.dataset.DataRecord;
import com.matrobot.gha.dataset.FolderDatasetReader;
import com.matrobot.gha.dataset.IDatasetReader;

public class ActivityParserApp {

	private static final String DATASET_PATH = "/home/klangner/datasets/github/2012/3/";
	HashMap<String, ActivityRecord> repos = new HashMap<String, ActivityRecord>();
	List<String> newRepositoriesThisMonth = new ArrayList<String>();
	private int eventsFound = 0;
	
	
	public void saveAsJson(String path) {
	
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		try{
			FileWriter writer = new FileWriter(path+"activity.json");
			String json = gson.toJson(repos.values());
			writer.write(json);
			writer.close();
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}

	private void parseFolder(String folder) throws IOException{
		
		IDatasetReader datasetReader = new FolderDatasetReader(folder);
		DataRecord	recordData;
		
		eventsFound = 0;
		while((recordData = datasetReader.readNextRecord()) != null){
			
			String url = recordData.getRepositoryId();
			if(url != null){
				
				if(recordData.type.equals("CreateEvent")){
					newRepositoriesThisMonth.add(url);
				}
				else if(recordData.type.equals("PushEvent")){
					ActivityRecord data = repos.get(url);
					if(data == null){
						data = new ActivityRecord();
						data.repository = url;
					}
					
					data.activity += 1;
					repos.put(url, data);
				}
				eventsFound ++;
			}
		}
	}

	private void removeNewRepos() {

		for(String repositoryName : newRepositoriesThisMonth){
			repos.remove(repositoryName);
		}
	}

	public ActivityParserApp(String folder) throws IOException{
		
		parseFolder(folder);
		removeNewRepos();
	}

	public static void main(String[] args) throws IOException {

		long time = System.currentTimeMillis();
		ActivityParserApp app = new ActivityParserApp(DATASET_PATH);
		app.saveAsJson(DATASET_PATH);
		time = (System.currentTimeMillis()-time)/1000;
		System.out.println("Create events: " + app.newRepositoriesThisMonth.size() + " Repos: " + app.repos.size());
		System.out.println("Events: " + app.eventsFound + " parse time: " + time + "sec.");
	}
}
