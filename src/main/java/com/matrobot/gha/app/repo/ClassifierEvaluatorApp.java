package com.matrobot.gha.app.repo;

import java.io.IOException;

import com.matrobot.gha.app.Settings;
import com.matrobot.gha.classifier.BinaryStaticClassifier;
import com.matrobot.gha.classifier.IBinaryClassifier;
import com.matrobot.gha.classifier.LogisticRegressionClassifier;
import com.matrobot.gha.filter.ClassifyRepositoryFilter;
import com.matrobot.gha.ml.Dataset;
import com.matrobot.gha.ml.EvaluationMetrics;
import com.matrobot.gha.ml.Sample;

public class ClassifierEvaluatorApp {

	private Dataset dataset;
	private int counter;
	private EvaluationMetrics metrics;
	
	
	protected ClassifierEvaluatorApp(String firstPath, String secondPath, String thirdPath) throws IOException{
		
		ClassifyRepositoryFilter filter = new ClassifyRepositoryFilter(firstPath, secondPath, thirdPath);
		dataset = filter.getDataset();
		dataset.normalize();
	}
	
	private double evaluate(IBinaryClassifier classifier, Dataset dataset) {

		metrics = new EvaluationMetrics();
		counter = 0;
		double sum = 0;
		for(Sample sample : dataset.getData()){
				
			double confidence = classifier.classify(dataset.normalize(sample.features));
			double error = Math.pow(sample.output-confidence, 2); 
			sum += error;
			if(error > 0.25){
				if(sample.output == 1){
					metrics.addFalseNegative();
				}
				else{
					metrics.addFalsePositive();
				}
			}
			else{
				if(sample.output == 1){
					metrics.addTruePositive();
				}
			}
			counter += 1;
		}

		return Math.sqrt(sum/counter);
	}

	
	/**
	 * Feature vector:
	 *  feature[0] = currentActivity in log10 scale
	 *  feature[1] = current activity rating (from previous month)
	 */
	public static void main(String[] args) throws IOException {

		ClassifierEvaluatorApp app = new ClassifierEvaluatorApp(
				Settings.DATASET_PATH+"2012-1/", 
				Settings.DATASET_PATH+"2012-10/",
				Settings.DATASET_PATH+"2012-11/");
		Dataset dataset = app.dataset;
		dataset.saveAsCSV(Settings.DATASET_PATH+"weka.csv");
		
		// Static classifier
		System.out.println("Static: ");
		app.evaluate(new BinaryStaticClassifier(), dataset);
		app.metrics.print();
		System.out.println();

		// Logistic regression
		LogisticRegressionClassifier classifier = new LogisticRegressionClassifier();
		System.out.println("Train");
		classifier.train(dataset);
		System.out.println("Evaluate");
		app.evaluate(classifier, dataset);
		System.out.println("Logistic regression: ");
		app.metrics.print();
		System.out.println();
	}
	
}
