package com.matrobot.gha.insights.regression;


public class CustomLinearRegression implements IRegression{

	private double slope;
	private double intercept;
	
	public CustomLinearRegression(double slope, double intercept){
	
		this.slope = slope;
		this.intercept = intercept;
	}
	
	
	@Override
	public double predict(double[] input) {
		
		double value = slope*input[0]+intercept;
		return Math.max(value, 0);
	}


	public static CustomLinearRegression train(double[][] inputs, double[] outputs){
		
		double slope = 0;
		double intercept = 0;
		double tempSlope;
		double tempIntercept;
		double gradientSlope = 10;
		double gradientIntercept = 10;
		int m = inputs.length;
		double alpha = 0.0001;
		double sum;
		
		while(Math.abs(gradientSlope) > .001 || Math.abs(gradientIntercept) > .001){

			sum = 0;
			for(int i = 0; i < m; i++){
				sum += inputs[i][0]*slope+intercept-outputs[i];
			}
			gradientIntercept = sum/m;
			tempIntercept = intercept - alpha*gradientIntercept; 

			sum = 0;
			for(int i = 0; i < m; i++){
				sum += (inputs[i][0]*slope+intercept-outputs[i])*inputs[i][0];
			}
			gradientSlope = sum/m;
			tempSlope = slope - alpha*gradientSlope;
			
			intercept = tempIntercept;
			slope = tempSlope;
			
		}
		
		return new CustomLinearRegression(slope, intercept);
	}


	@Override
	public void printModel() {
		// TODO Auto-generated method stub
		
	}
	
}
