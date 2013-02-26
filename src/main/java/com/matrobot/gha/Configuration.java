package com.matrobot.gha;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;


public class Configuration {

	private String command;
	private String dataPath;
	private List<String> repositories = new ArrayList<String>();
	private String actor;
	private String startDate;
	private String endDate;
	private String dateResolution;
	private String outputFilename;
	private String orderBy;
	private int minActivity = 0;
	private PrintStream outputStream;
	private List<String> eventTypes = new ArrayList<String>();
	
	
	public Configuration(InputStream is){
		
		load(is);
	}
	
	public Configuration(String filename) throws FileNotFoundException{
		
		load(new FileInputStream(filename));
	}
	
	private void setDatapath(String path) {
		
		dataPath = path;
		if(!dataPath.endsWith("/")){
			dataPath += '/';
		}
	}

	
	@SuppressWarnings("unchecked")
	private void load(InputStream inputStream) {

		Yaml yaml = new Yaml();
		Map<String, Object> config = (Map<String, Object>) yaml.load(inputStream);
		command = config.get("command").toString();
		setDatapath(config.get("datapath").toString());
		loadRepoFilter(config.get("repository"));
		loadEventFilter(config.get("event_type"));
		if(config.get("actor") != null){
			actor = config.get("actor").toString();
		}
		if(config.get("order_by") != null){
			orderBy = config.get("order_by").toString();
		}
		if(config.get("min_activity") != null){
			minActivity = Integer.parseInt(config.get("min_activity").toString());
		}
		outputFilename = config.get("output").toString();
		parseDate((Map<String, String>) config.get("date"));
	}

	
	@SuppressWarnings("unchecked")
	private void loadRepoFilter(Object repositoryKey) {
		
		if(repositoryKey instanceof List<?>){
			List<String> repos = (List<String>) repositoryKey;
			for(String repo : repos){
				repositories.add(repo);
			}
		}
		else if(repositoryKey != null){
			repositories.add(repositoryKey.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadEventFilter(Object eventKey) {
		
		if(eventKey instanceof List<?>){
			List<String> events = (List<String>) eventKey;
			for(String event : events){
				eventTypes.add(event);
			}
		}
		else if(eventKey != null){
			eventTypes.add(eventKey.toString());
		}
	}
	
	private void parseDate(Map<String, String> date) {
		startDate = date.get("from");
		endDate = date.get("to");
		if(date.get("resolution") != null){
			dateResolution = date.get("resolution");
		}
	}

	/**
	 * @return command
	 */
	public String getCommand(){
		return command;
	}
	
	/**
	 * @return -data= parameter
	 */
	public String getDataPath(){
		return dataPath;
	}
	
	
	/**
	 * @return -from=
	 */
	public String getStartDate(){
		return startDate;
	}
	
	
	/**
	 * @return -to=
	 */
	public String getEndDate(){
		return endDate;
	}
	
	
	/**
	 * @return Full path folders based on date range
	 */
	public List<String> getMonthFolders(){
		
		List<String> folders = new ArrayList<String>();
		
		String[] tokens;
		int month;
		int year;
		int endMonth;
		int endYear;
		
		tokens = startDate.split("-");
		if(tokens.length == 2){
		
			year = Integer.parseInt(tokens[0]);
			month = Integer.parseInt(tokens[1]);
			
			tokens = endDate.split("-");
			if(tokens.length == 2){
			
				endYear = Integer.parseInt(tokens[0]);
				endMonth = Integer.parseInt(tokens[1]);
				int end = endYear*100+endMonth;
		
				while(year*100+month <= end){
					folders.add(dataPath + year + "-" + month);
					
					month ++;
					if(month > 12){
						year ++;
						month = 1;
					}
				}
			}
		}
		
		return folders;
	}

	
	/**
	 * @return -repo=
	 */
	public List<String> getRepositories() {
		return repositories;
	}
	
	public List<String> getEventTypes() {
		return eventTypes;
	}
	
	
	public PrintStream getOutputStream(){
		
		try {
			if(outputStream == null){
				if(outputFilename != null){
					FileOutputStream fos;
						fos = new FileOutputStream(outputFilename, false);
					outputStream = new PrintStream(fos);
				}
				else{
					outputStream = System.out;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			outputStream = System.out;
		}
		
		return outputStream;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public int getMinActivity() {
		return minActivity;
	}

	public String getActor() {
		return actor;
	}

	//Test
	public String getDateResolution() {
		return dateResolution;
	}
}
