package com.matrobot.gha.archive.repotimeseries;

import java.util.ArrayList;
import java.util.List;


public class RepoTimeSeries {

	class DataPoint{
		String label;
		int value = 0;
	}
	
	private String repoName;
	private List<DataPoint> dataPoints = new ArrayList<DataPoint>();
	private DataPoint lastDataPoint = null;
	

	public RepoTimeSeries(String repoName){
		
		this.repoName = repoName;
	}
	
	
	public String getRepoName(){
		return repoName;
	}
	
	
	/**
	 * Add value to the given data point. 
	 * If data point is not found then new one with value 0 is created.
	 */
	public void addDataPoint(String label){
	
		DataPoint data = new DataPoint();
		data.label = label;
		dataPoints.add(data);
		lastDataPoint = data;
	}
	
	
	public void incrementLastPointValue(){
		
		if(lastDataPoint != null){
			lastDataPoint.value++;
		}
	}
	
	
	public int[] getDataPoints(){
		
		int[] values = null;
		
		if(dataPoints.size() > 0){
			values = new int[dataPoints.size()];
			for(int i = 0; i < dataPoints.size(); i++){
				values[i] = dataPoints.get(i).value;
			}
		}
		
		return values;
	}
}
