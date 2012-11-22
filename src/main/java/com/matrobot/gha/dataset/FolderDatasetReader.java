package com.matrobot.gha.dataset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



public class FolderDatasetReader implements IDatasetReader{

	private List<String> filePaths = new ArrayList<String>();
	private IDatasetReader datasetReader;
			
			
	/**
	 * Init reader from given folder
	 * @param filePath
	 * @throws IOException 
	 */
	public FolderDatasetReader(String folder) throws IOException{
		
		initFileNames(folder);
		nextDatasetReader();
	}
	
	
	private void initFileNames(String folder) {

		File rootFolder = new File(folder);
		List<File> files = Arrays.asList(rootFolder.listFiles());
		Collections.sort(files);
		
		for(File file : files){
			if(file.isFile()){
				filePaths.add(file.getPath());
			}
		}
	}


	private void nextDatasetReader() {

		if(filePaths.size() > 0){
			String path = filePaths.remove(0);
			try {
				datasetReader = new FileDatasetReader(path);
			} catch (IOException e) {
				e.printStackTrace();
				datasetReader = null;
			}
		}
	}


	public DataRecord readNextRecord(){
		
		DataRecord data = null;
		
		if(datasetReader != null){
		
			data = datasetReader.readNextRecord();
			if(data == null){
				nextDatasetReader();
				if(datasetReader != null){
					data = datasetReader.readNextRecord();		
				}
			}
		}
		
		return data;
	}
}