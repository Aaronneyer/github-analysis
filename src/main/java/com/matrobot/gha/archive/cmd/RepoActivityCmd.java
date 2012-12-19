package com.matrobot.gha.archive.cmd;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.matrobot.gha.Configuration;
import com.matrobot.gha.ICommand;
import com.matrobot.gha.archive.event.EventReader;
import com.matrobot.gha.archive.repo.FilteredRepoReader;
import com.matrobot.gha.archive.repo.IRepositoryReader;
import com.matrobot.gha.archive.repo.OrderedRepoReader;
import com.matrobot.gha.archive.repo.RepositoryReader;
import com.matrobot.gha.archive.repo.RepositoryRecord;

/**
 * Parse archive and create reports with repository info by months
 * 
 * @author Krzysztof Langner
 */
public class RepoActivityCmd implements ICommand{

	private IRepositoryReader reader;
	private HashMap<String, String> staticCSVFields = new HashMap<String, String>();
	
	
	@Override
	public void run(Configuration params) throws IOException {

		EventReader eventReader = new EventReader(params.getMonthFolders());
		RepositoryReader repoReader = new RepositoryReader(eventReader);
		reader = repoReader;
		if(params.getMinActivity() > 0){
			FilteredRepoReader filteredReader = new FilteredRepoReader(reader);
			filteredReader.setMinActivity(params.getMinActivity());
			reader = filteredReader;
		}
		
		if(params.getOrderBy() != null){
			OrderedRepoReader orderedReader = new OrderedRepoReader(reader);
			orderedReader.addField(OrderedRepoReader.SORT_BY_EVENTS);
			reader = orderedReader;
		}
		
		saveAsCSV(params.getOutputStream());
	}

	private void saveAsCSV(PrintStream printStream) throws UnsupportedEncodingException, IOException {

		for(String key : staticCSVFields.keySet()){
			printStream.print(key + ", ");
		}
		printStream.print(RepositoryRecord.getCSVHeaders());
		RepositoryRecord record;
		while((record = reader.next()) != null){
			for(String value : staticCSVFields.values()){
				printStream.print(value + ", ");
			}
			printStream.print(record.toCSV());
		}
	}

	
	/**
	 * Export data for matrobot.com website
	 */
	public static void main(String[] args) throws IOException {

		RepoActivityCmd app = new RepoActivityCmd();
		Configuration params = new Configuration("configs/export_repos.yaml");
		
		String[] date = params.getStartDate().split("-");
		if(date.length == 2){
			app.staticCSVFields.put("year", date[0]);
			app.staticCSVFields.put("month", date[1]);
		}
		app.run(params);
	}
	
}
