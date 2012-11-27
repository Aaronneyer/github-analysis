package com.matrobot.gha.dataset;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;


public class FileDatasetReader implements Iterator<EventRecord>{

	private List<EventRecord> records;
	private Iterator<EventRecord> iterator;
	private String filename;
	
	
	/**
	 * Init reader from file path
	 * @param filePath
	 * @throws IOException 
	 */
	public FileDatasetReader(String filePath) throws IOException{
	
		filename = filePath;
		initContent(new FileInputStream(filePath));
	}
	
	
	/**
	 * Init reader from stream
	 * @param inputStream
	 * @throws IOException 
	 */
	public FileDatasetReader(InputStream inputStream) throws IOException{

		initContent(inputStream);
	}


	private void initContent(InputStream inputStream) throws IOException{

		Gson gson = new Gson();
		InputStream gzipStream = new GZIPInputStream(inputStream);
		JsonReader reader = new JsonReader(new InputStreamReader(gzipStream, "UTF-8"));
		reader.setLenient(true);
	    records = new ArrayList<EventRecord>();
	    try{
		    while (reader.hasNext() && reader.peek() != JsonToken.END_DOCUMENT) {
		    	EventRecord record = gson.fromJson(reader, EventRecord.class);
		        records.add(record);
		    }
	    }
	    catch(JsonSyntaxException e){
	    	System.out.println("File: " + filename);
	    	int index = records.size()-1;
	    	if(index < 0){
	    		System.out.println("first record problem");
	    	}
	    	else{
	    		System.out.println(records.get(records.size()-1));
	    	}
	    	System.err.println(e);
	    }
	    
	    reader.close();
	    iterator = records.iterator();
	}


	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}


	@Override
	public EventRecord next() {
		if(iterator.hasNext()){
			return iterator.next();
		}
		else{
			return null;
		}
	}


	@Override
	public void remove() {
	}

}


