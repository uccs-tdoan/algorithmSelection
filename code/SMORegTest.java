/* ClassifiersCost.java is in companion with performance.java (without Misclassifier Cost)  
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
//import weka.classifiers.meta
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.rules.*;
import weka.classifiers.lazy.*;
import weka.classifiers.functions.*;
import weka.classifiers.meta.*;
import weka.classifiers.functions.SMOreg;
//import weka.classifiers.trees.DecisionStump;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.*;
import weka.core.FastVector;
import weka.core.Instances;

 
public class SMORegTest {
	
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
		String outFile = "ClassifierCostOuput1.csv";
		String outPath = "c:\\AlgoSelecMeta";
		
		
		// For test purpose only
		
		//List<String> results = new ArrayList<String>() ;
		//List<String> results = Arrays.asList("zoo.arff","vehicle.arff"); //4.2 no
		String inPath  = "c:\\data\\PCA\\run0";
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
			/*	Classifier[] models = { 
					  /*new J48(), // a decision trees
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
						
						new AdaBoostM1(), 
						new Bagging(), 
						new Stacking(), 
						new LogitBoost(), 
						new RandomCommittee(),   
				
						new Vote(),
						
						new Logistic(), 
						new MultilayerPerceptron(), 
						new SimpleLogistic(),  // linear logistic regression models. 
					    new SMO() 
					new SMOreg() //SMOreg implements the support vector machine for regression.				
				}; */
		 

	/*	for (int k=0; k< results.size();k++) {
			inFile=results.get(k);
			String inStore = inPath+"\\"+ results.get(k); */
			inPath ="c:\\data\\numeric\\auto93.arff";
            BufferedReader datafile = readDataFile(inPath);
			Instances data = new Instances(datafile);
			data.setClassIndex(data.numAttributes() - 1);
			//matrix= createCostM(data.numClasses());
        
    //    System.out.println(matrix);
		// Do 10-split cross validation
	//		Instances[][] split = crossValidationSplit(data, 10);

		// Separate split into training and testing arrays
		//	Instances[] trainingSplits = split[0];
		//	Instances[] testingSplits = split[1];
 

            
			SMOreg model = new weka.classifiers.functions.SMOreg();
			String svmOptions = "-C 1.0 -N 2 -I weka.classifiers.functions.supportVector.RegSMOImproved -L 0.0010 -W 1 -P 1.0E-12 -T 0.0010 -V -K weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0";
			String saveName = "output.txt";
			svmOptions += " -d " + saveName;

			model.setOptions(weka.core.Utils.splitOptions(svmOptions));

			model.buildClassifier(data);

			Evaluation eval = new Evaluation(data);
			eval.evaluateModel(model, data);
			
			System.out.println(eval.toSummaryString("\nResults\n======\n", false));

      
		out.close();
	
	}
}

