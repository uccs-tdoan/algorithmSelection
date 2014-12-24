/* Tri Doan
 * algorithms.java 
 * Implement different classifiers in order to get statistical summaries   
 * 
 * */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.rules.*;
import weka.classifiers.lazy.*;
import weka.classifiers.functions.*;
import weka.classifiers.meta.*;
//import weka.classifiers.trees.DecisionStump;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.*;
import weka.core.FastVector;
import weka.core.Instances;
//import libsvm.*;
 
public class algorithms {
	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
 
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
 
		return inputReader;
	}
 
	public static Evaluation classify(Classifier model,
			Instances trainingSet, Instances testingSet) throws Exception {
		Evaluation evaluation = new Evaluation(trainingSet);
 
		model.buildClassifier(trainingSet);
		evaluation.evaluateModel(model, testingSet);
 
		return evaluation;
	}
 
	public static double calculateAccuracy(FastVector predictions) {
		double correct = 0;
 
		for (int i = 0; i < predictions.size(); i++) {
			NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
			if (np.predicted() == np.actual()) {
				correct++;
			}
		}
 
		return 100 * correct / predictions.size();
	}
 
	public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {
		Instances[][] split = new Instances[2][numberOfFolds];
 
		for (int i = 0; i < numberOfFolds; i++) {
			split[0][i] = data.trainCV(numberOfFolds, i);
			split[1][i] = data.testCV(numberOfFolds, i);
		}
 
		return split;
	}
 
	public static void main(String[] args) throws Exception {
		String outFile = "algorithmOutput.csv";
		String outPath = "c:\\data";
		
		String inFile  = "auto93.arff";
		String inPath  = "c:\\data\\numeric";
		
		String inStore = inPath+"\\"+ inFile;
		String outStore= outPath +"\\"+outFile;
		
		//System.out.println(outStore);
		BufferedReader datafile = readDataFile(inStore);
		PrintWriter out = new PrintWriter(new FileWriter(outStore));
		//PrintWriter out = new PrintWriter(new FileWriter("c:\\AlgoSelecMeta\\output.csv"));
		
		Instances data = new Instances(datafile);
		data.setClassIndex(data.numAttributes() - 1);

		// Do 10-split cross validation
		Instances[][] split = crossValidationSplit(data, 10);
 
		// Separate split into training and testing arrays
		Instances[] trainingSplits = split[0];
		Instances[] testingSplits = split[1];
 
		// Use a set of classifiers
		Classifier[] models = { 
				new J48(), // a decision trees
				new DecisionStump(), //one-level decision tree
				new RandomForest(),
				new PART(), 
				new DecisionTable(),//decision table majority classifier
				new JRip(),
				new ZeroR(),
				
				new IBk(), // instance based classifier used K nearest neighbor
				new KStar(),  // instance based classifier using entropy based distance 
				
				new NaiveBayes(),
				
				new AdaBoostM1(),
				new Bagging(),
				new Stacking(),
				new LogitBoost(),
				new RandomCommittee(),
				

				new Logistic(),
				new MultilayerPerceptron(),
				new SimpleLogistic(), // linear logistic regression models. 
				new SMO(),
				new SMOreg(), //SMOreg implements the support vector machine for regression.				
		};
 
        // Print header
		 out.println("Accuracy,RMSE,Fscore,Kappa,PRC,AUC,Dataset,Algorithm");
		//System.out.println(" Number of instances  = "+String.format("%.2f", data.size()));
		// Run for each model
		for (int j = 0; j < models.length; j++) {
 
			// Collect every group of predictions for current model in a FastVector
			FastVector predictions = new FastVector();
            float avgRMSE=0,avgKappa=0,avgFscore =0,avgPRC=0,avgAUC=0;
            float avgAcc=0;
			// For each training-testing split pair, train and test the classifier
			for (int i = 0; i < trainingSplits.length; i++) {
				Evaluation validation = classify(models[j], trainingSplits[i], testingSplits[i]);
 
				predictions.appendElements(validation.predictions());
				//avgFscore += validation.weightedFMeasure();
                avgRMSE   += validation.rootMeanSquaredError();
                System.out.println(validation.toClassDetailsString());
                //avgKappa  += validation.kappa();
                //avgPRC    += validation.weightedAreaUnderPRC();;
                //avgAUC    += validation.weightedAreaUnderROC();
                //avgAcc  +=validation.pctCorrect();
				// Uncomment to see the summary for each training-testing pair.
			//	System.out.println(models[j].toString()); 
			//	System.out.println(" RMSE = "+String.format("%.2f", validation.rootMeanSquaredError()));
			//	System.out.println(" AUROC = "+String.format("%.2f", validation.areaUnderROC(j)));
			//	System.out.println(" kappa = "+String.format("%.2f", validation.kappa()));
			
			//  System.out.println(" F score = "+String.format("%.2f", validation.fMeasure(0));
			} 
			
			System.out.println(" RMSE = "+String.format("%.2f", avgRMSE/trainingSplits.length));
			//System.out.println(" Fscore = "+String.format("%.2f", avgFscore/trainingSplits.length));
			//System.out.println(" kappa = "+String.format("%.2f", avgKappa/trainingSplits.length));
			//System.out.println(" avgPRC = "+String.format("%.2f",(float) avgPRC/trainingSplits.length));
			//System.out.println(" avgAUC = "+String.format("%.2f",(float) avgAUC/trainingSplits.length));
			// Calculate overall accuracy of current classifier on all splits
			double accuracy = calculateAccuracy(predictions);
			avgRMSE   = avgRMSE/trainingSplits.length;
			avgFscore = avgFscore/trainingSplits.length;
			avgKappa  = avgKappa/trainingSplits.length;
			avgPRC    = avgPRC/trainingSplits.length;
			avgAUC    = avgAUC/trainingSplits.length;
			avgAcc   =  avgAcc/trainingSplits.length;
			//out.println(accuracy,avgRMSE,avgFscore,avgKappa,avgPRC,avgAUC);
           // out.printf("%.2f",accuracy); out.print(avgRMSE);out.print(avgFscore); out.print(avgKappa);
           // out.print(avgPRC); out.println(avgAUC);
			out.print(String.format("%.2f",accuracy)+","+String.format("%.2f",avgRMSE)+","+String.format("%.2f",avgFscore)+",");
			out.print(String.format("%.2f",avgKappa)+","+String.format("%.2f",avgPRC)+","+String.format("%.2f",avgAUC)+",");
			out.println(inFile+","+models[j].getClass().getSimpleName());
			
			// Print current classifier's name and accuracy in a complicated,
			
			System.out.println(j);
			System.out.println(" copy "+String.format("%.2f%%",avgAcc));
			System.out.println("Accuracy of " + models[j].getClass().getSimpleName() + ": "
					+ String.format("%.2f%%", accuracy)
            	+ "\n---------------------------------");
			//System.out.println("RMSE = "+ String.format("%.2f%%",  );
			 //evaluation.evaluateModel(scheme, testInstances);
			//out.println();
			
		} 
		out.close();
		System.out.println(" Number of Classes = "+String.format("%d",data.numClasses()));
		System.out.printf(" Number of Attributes = "+String.format("%d", data.numAttributes()-1));
 
	}
}

