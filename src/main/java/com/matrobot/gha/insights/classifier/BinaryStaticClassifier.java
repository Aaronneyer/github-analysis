package com.matrobot.gha.insights.classifier;

/**
 * Just assume that all are negative examples
 */
public class BinaryStaticClassifier implements IBinaryClassifier {

	@Override
	public double classify(double[] featureVector) {
		return 0;
	}

}
