/* ClassifiersCost.java is in companion with algorithm.java (without Misclassifier Cost)  
 * 
 * Tri Doan
 * Implement different classifiers in order to get statistical summaries   
 * Date: Sept 25
 * */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.rules.*;
import weka.classifiers.lazy.*;
import weka.classifiers.functions.*;
import weka.classifiers.meta.*;

//import weka.classifiers.trees.DecisionStump;
import weka.classifiers.bayes.NaiveBayes;
import libsvm.*;
//http://www.wikihow.com/Add-JARs-to-Project-Build-Paths-in-Eclipse-%28Java%29
import weka.classifiers.trees.*;
import weka.core.FastVector;
import weka.core.Instances;

 
public class ClassifiersCost {
	
	public static List<String> getList(String path) {
		   List<String> results = new ArrayList<String>();

	          File[] files = new File(path).listFiles();
	         //If this pathname does not denote a directory, then listFiles() returns null. 
	    
	         for (File file : files) {
	             if (file.isFile() && file.getName().endsWith((".arff"))) {
	                   results.add(file.getName());
	                    }
	                }
		   return results;
	   }
	
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
 
	public static int[][] create(int size) {
    	int penalty = 2;  // It depends on domain
    	
    	int[][] matrix = new int[size][size];
    	for(int i = 0; i < size; i++)
    		for(int j = 0; j < size; j++)
    			matrix[i][j] = (i == j) ? 0 : (i>j) ? penalty: 1;
    	return matrix;
   }
	
	public static  CostMatrix createCostM(int size) {
    	double penalty = 2.0;
    	
    	CostMatrix matrix = new CostMatrix(size);
    	for(int i = 0; i < size; i++)
    		for(int j = 0; j < size; j++)
    			matrix.setCell(i, j, (i == j) ? 0.0 : (i>j) ? (double) penalty: (double) 1.0);
    			//matrix[i][j] = (i == j) ? 0 : (i>j) ? penalty: 1;
    	return matrix;
   }
	
	public static void main(String[] args) throws Exception {
		String outFile = "ClassifierCostSynthetic.csv";
		String outPath = "c:\\AlgoSelecMeta";
		
		
		// For test purpose only
		
		//List<String> results = new ArrayList<String>() ;
		//List<String> results = Arrays.asList("zoo.arff","vehicle.arff"); //4.2 no
		String inPath  = "c:\\data\\tmp";
		List<String> results = getList(inPath);
		//String inFile  = "zoo.arff";
		String inFile;
		String outStore= outPath +"\\"+outFile;
		PrintWriter out =new PrintWriter(new FileWriter(outStore));
		
		CostMatrix matrix;
		CostSensitiveClassifier metaCost= new CostSensitiveClassifier();
	    float avgAcc=0, avgRMSE=0,avgKappa=0,avgFscore =0,avgPRC=0,avgAUC=0 ,avgErr=0,avgMissClassCost=0; 
        float newEndTime=0,avgTotalCost=0;
        long startTime , duration ;
        
		// Use a set of classifiers
				Classifier[] models = { 
				        new J48(), // a decision trees
						new DecisionStump(), //one-level decision tree
						new RandomForest(),
						new PART(), 
						new DecisionTable(),//decision table majority classifier
						new JRip(),
						new OneR(),
						new ZeroR(),
					
						new IBk(), // instance based classifier used K nearest neighbor
						new KStar(),  // instance based classifier using entropy based distance 
						new LWL(), // Locally weighted learning used KNN
						
						new NaiveBayes(), 
					    //(Classifier) new svm(),	
						new AdaBoostM1(), 
						new Bagging(), 
						new Stacking(), 
						new LogitBoost(), 
						new RandomCommittee(),   
				
						new Vote(),  
						
						new Logistic() , 
						new MultilayerPerceptron(), 
						new SimpleLogistic(),  // linear logistic regression models. 
				    	new SMOreg(), //SMOreg implements the support vector machine for regression.				
				
				};
		 

		for (int k=0; k< results.size();k++) {
			inFile=results.get(k);
			String inStore = inPath+"\\"+ results.get(k);
			BufferedReader datafile = readDataFile(inPath+"\\"+inFile);
			Instances data = new Instances(datafile);
			data.setClassIndex(data.numAttributes() - 1);
			matrix= createCostM(data.numClasses());
            //    System.out.println(matrix);
		// Do 10-split cross validation
			Instances[][] split = crossValidationSplit(data, 10);

		// Separate split into training and testing arrays
			Instances[] trainingSplits = split[0];
			Instances[] testingSplits = split[1];
 
	  // Print header
		 //out.println("Accuracy,RMSE,Fscore,Kappa,PRC,AUC,SAR,Dataset,nInstances,nClasses,nAttributes,Algorithm,RunTime,avgMissCost,avgTotalCost");
		
		// Run for each model
		
		for (int j = 0; j < models.length; j++) {
 
			
			// Collect every group of predictions for current model in a FastVector
			FastVector predictions = new FastVector();
        	// For each training-testing split pair, train and test the classifier
            startTime = System.currentTimeMillis();
            avgAcc=0; avgRMSE=0;avgKappa=0;avgFscore =0;avgPRC=0;avgAUC=0 ;avgErr=0;
            avgMissClassCost=0;avgTotalCost=0;  
            
            System.out.printf(" Number of split %d",trainingSplits.length);
			for (int i = 0; i < trainingSplits.length; i++) {
				metaCost.setClassifier(models[j]);
				
				metaCost.setCostMatrix(matrix);
				metaCost.buildClassifier(trainingSplits[i]); 
				
				Evaluation validation = new Evaluation(trainingSplits[i],matrix); 
				validation.evaluateModel(models[j],testingSplits[i]);
				//Evaluation validation = classify(models[j], trainingSplits[i], testingSplits[i]);
				//predictions.appendElements(validation.predictions());
				
			    avgAcc += validation.pctCorrect();
				
                avgRMSE   += validation.rootMeanSquaredError();
                avgKappa  += validation.kappa();
                avgErr    += validation.errorRate(); 
                avgMissClassCost += validation.avgCost(); //  total cost of misclassifications (incorrect plus unclassified) over the total number of instances.
                avgTotalCost	 += validation.totalCost(); // total cost of each prediction times the weight of the instance, summed over all instance          
                avgPRC += validation.weightedAreaUnderPRC();
                avgAUC += validation.weightedAreaUnderROC();
                avgFscore += validation.weightedFMeasure();
               
			} // End for loop - running for one model 
			
			duration = System.currentTimeMillis() - startTime;
			
			avgAcc   = avgAcc/10;
			avgRMSE   = avgRMSE/10;
			avgFscore = avgFscore/10;
			avgKappa  = avgKappa/10;
			avgPRC    = avgPRC/10;
			avgAUC    = avgAUC/10;
			avgErr	  = avgErr /10;
	        avgMissClassCost   = avgMissClassCost/10;
	        avgTotalCost  = avgTotalCost/10;
	        
			double avgSAR = ((avgAcc/100)+avgAUC+(1-avgRMSE))/3;
				
			System.out.println(" RMSE = "+String.format("%.2f", avgRMSE));
			System.out.println(" Fscore = "+String.format("%.2f", avgFscore));
			System.out.println(" kappa = "+String.format("%.2f", avgKappa));
			System.out.println(" avgPRC = "+String.format("%.2f",(float) avgPRC));
			System.out.println(" avgAUC = "+String.format("%.2f",(float) avgAUC));
			System.out.println(" avgMissCost = "+String.format("%.2f",(float) avgMissClassCost));
			System.out.println(" avgTotalCost = "+String.format("%.2f",(float) avgTotalCost));
			System.out.println(" running time = "+String.format("%d",(long) duration));
			//System.out.println(" ratio of run time over training time = "+String.format("%f",(float)  totalTrainingTime/ duration ));
			// Calculate overall accuracy of current classifier on all splits
			//double accuracy = calculateAccuracy(predictions);
			//out.print(String.format("%.2f",accuracy)+","+String.format("%.2f",avgRMSE)+","+String.format("%.2f",avgFscore)+",");
			out.print(String.format("%.2f",avgAcc)+","+String.format("%.2f",avgRMSE)+","+String.format("%.2f",avgFscore)+",");
			out.print(String.format("%.2f",avgKappa)+","+String.format("%.2f",avgPRC)+","+String.format("%.2f",avgAUC)+",");
			out.print(String.format("%.2f",avgSAR)+",");
			out.print(inFile.split("\\.")[0]+","+String.format("%d", data.numInstances())+",");
			out.print(String.format("%d",data.numClasses())+","+String.format("%d", data.numAttributes()-1)+",");
		    out.print(models[j].getClass().getSimpleName()+","+String.format("%d",duration)+","+String.format("%.2f",avgMissClassCost));
		    out.println(","+String.format("%.2f",avgTotalCost));
			// Print current classifier's name and accuracy in a complicated,
			
			System.out.println(j);
			System.out.println("Accuracy of " + models[j].getClass().getSimpleName() + ": "
					+ String.format("%.2f%%", avgAcc)+" on dataset "+inFile
            	+ "\n---------------------------------");
			
		}  // End for loop - running for all models 
		
	
		System.out.println(" Number of Classes = "+String.format("%d",data.numClasses()));
		System.out.printf(" Number of Attributes = "+String.format("%d", data.numAttributes()-1));
		System.out.println();
		
		}
		out.close();
		
	}
}

