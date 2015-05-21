/* performance.java is in companion with ClassifiersCost.java (with Misclassifier Cost)
 * weka 1.6 
 * Tri Doan
 * Implement different classifiers in order to get statistical summaries   
 * Date: Sept 25. Last update: Feb 13, 2015
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
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.rules.*;
import weka.classifiers.lazy.*;
import weka.classifiers.functions.*;
import weka.classifiers.meta.*;
import libsvm.*;
//import weka.classifiers.trees.DecisionStump;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.*;
import weka.core.FastVector;
import weka.core.Instances;
import weka.experiment.ClassifierSplitEvaluator; // Evaluate CPU time with weka API

import java.lang.management.*;  // Evaluate CPU time with java



public class performances {
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

	/** Get CPU time in nanoseconds. */
	public static long getCpuTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	        bean.getCurrentThreadCpuTime( ) : 0L;
	}
	 
	/** Get user time in nanoseconds. */
	public static long getUserTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	        bean.getCurrentThreadUserTime( ) : 0L;
	}

	/** Get system time in nanoseconds. */
	public static long getSystemTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	      (bean.getCurrentThreadCpuTime( ) - bean.getCurrentThreadUserTime( )) : 0L;
	}
	
	public static void main(String[] args) throws Exception {
		String outFile = "PerformanceNoCost.csv";
		String outPath = "C:\\data\\runtime";
		
		
		// For test purpose only
		
		String inPath  = "C:\\data\\tmp";
		List<String> results = getList(inPath);
		
		String inFile;
		String outStore= outPath +"\\"+outFile;
		ClassifierSplitEvaluator se  = new ClassifierSplitEvaluator();
		
		PrintWriter out =new PrintWriter(new FileWriter(outStore,true));
		float avgAcc, avgRMSE,avgKappa,avgFscore ,avgPRC,avgAUC ,avgErr,avgCost;
		// For each training-testing split pair, train and test the classifier
        long startTrain,endTrain,startTest,endTest, totalTrain=0,totalTest=0 ;
        
        
     // Use a set of classifiers
     		Classifier[] models = { 
                    //new BayesNet();
                    
                    
     				new J48(), // a decision trees
     			//	new DecisionStump(), //one-level decision tree
     			 //   new LibSVM(),
     				/*new RandomForest(),
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
     				
     				//new Vote(),
     				
     				new Logistic(),
     				new MultilayerPerceptron(),
     				new SimpleLogistic(), // linear logistic regression models. 
     				new SMO()//     				new SMOreg(), //SMOreg implements the support vector machine for regression.
 				
 */
     		};

     		
     		// Get list of all data file's name
     	
     		long startSystemTimeNano =  getSystemTime( );
            long startUserTimeNano   = getUserTime( );
            
            
            // running here
            
           
            	
		for (int k=0; k< results.size();k++) {
			inFile=results.get(k);
			String inStore = inPath+"\\"+ results.get(k);
			System.out.println(inStore);
		
		    BufferedReader datafile = readDataFile(inPath+"\\"+inFile);
		
		    //PrintWriter out = new PrintWriter(new FileWriter("c:\\AlgoSelecMeta\\output.csv"));
		
		   Instances data = new Instances(datafile);
		   data.setClassIndex(data.numAttributes() - 1);

		  // Do 10-split cross validation
		  Instances[][] split = crossValidationSplit(data, 10);
 
		// Separate split into training and testing arrays
		   Instances[] trainingSplits = split[0];
		   Instances[] testingSplits = split[1];
 
		 
        // Print header
		   
		   out.println("Accuracy,RMSE,Fscore,Kappa,PRC,AUC,SAR,Dataset,nInstances,nClasses,nAttributes,Algorithm,trainTime,testTime");
		   //out.close();
		// Run for each model
		for (int j = 0; j < models.length; j++) {
              
			// Collect every group of predictions for current model in a FastVector
			FastVector predictions = new FastVector();
            
            avgAcc=0; avgRMSE=0; avgKappa=0;avgFscore =0 ;avgPRC=0 ;avgAUC=0 ; avgErr=0 ;avgCost=0; 
            totalTrain=0; totalTest=0; 
			// For each training-testing split pair, train and test the classifier
            
            long taskUserTimeNano   ;
            long taskSystemTimeNano ;
            
            System.out.printf(" Number of split %d",trainingSplits.length);
			for (int i = 0; i < trainingSplits.length; i++) {
				startTrain = System.currentTimeMillis();
				Evaluation validation = classify(models[j], trainingSplits[i], testingSplits[i]);
				totalTrain = totalTrain + System.currentTimeMillis()- startTrain;
				startTest  = System.currentTimeMillis() ;
				predictions.appendElements(validation.predictions());
				totalTest  = totalTest+ System.currentTimeMillis() - startTest;
				avgAcc += validation.pctCorrect();
					
	             avgRMSE   += validation.rootMeanSquaredError();
	             avgKappa  += validation.kappa();
	             avgErr    += validation.errorRate(); 
	             avgCost   += validation.avgCost(); //  total cost of misclassifications (incorrect plus unclassified) over the total number of instances.
	                           
	             avgPRC += validation.weightedAreaUnderPRC();
	             avgAUC += validation.weightedAreaUnderROC();
	             avgFscore += validation.weightedFMeasure();
	               
			} // End for loop - running for one model 
			
			//final long duration = System.currentTimeMillis() - startTime;
			
			System.out.println(" RMSE = "+String.format("%.2f", avgRMSE/trainingSplits.length));
			System.out.println(" Fscore = "+String.format("%.2f", avgFscore/trainingSplits.length));
			System.out.println(" kappa = "+String.format("%.2f", avgKappa/trainingSplits.length));
			System.out.println(" avgPRC = "+String.format("%.2f",(float) avgPRC/trainingSplits.length));
			System.out.println(" avgAUC = "+String.format("%.2f",(float) avgAUC/trainingSplits.length));
			
			System.out.println(" running time = "+String.format("%d",(long) totalTrain));
			System.out.println(" CPU training time = "+String.format("%d",(long) totalTest));
			//System.out.println(" ratio of run time over training time = "+String.format("%f",(float)  totalTrainingTime/ duration ));
			// Calculate overall accuracy of current classifier on all splits
			//double accuracy = calculateAccuracy(predictions);
			avgAcc   = avgAcc/trainingSplits.length;
			avgRMSE   = avgRMSE/trainingSplits.length;
			avgFscore = avgFscore/trainingSplits.length;
			avgKappa  = avgKappa/trainingSplits.length;
			avgPRC    = avgPRC/trainingSplits.length;
			avgAUC    = avgAUC/trainingSplits.length;
			double avgSAR = (avgAcc+avgAUC+(1-avgRMSE))/3;	
			
			
			taskUserTimeNano    = getUserTime( ) - startUserTimeNano;
            taskSystemTimeNano  = getSystemTime( ) - startSystemTimeNano;
            
			
			out.print(String.format("%.2f",avgAcc)+","+String.format("%.2f",avgRMSE)+","+String.format("%.2f",avgFscore)+",");
			out.print(String.format("%.2f",avgKappa)+","+String.format("%.2f",avgPRC)+","+String.format("%.2f",avgAUC)+",");
			out.print(String.format("%.2f",avgSAR)+","+ inFile.split("\\.")[0]+","+String.format("%d", data.numInstances())+",");
			out.print(String.format("%d",data.numClasses())+","+String.format("%d", data.numAttributes()-1)+",");
		    out.print(models[j].getClass().getSimpleName()+","+String.format("%d",totalTrain)+",");
		    out.println(String.format("%d",taskSystemTimeNano));
			
		    
		    // Print current classifier's name and accuracy in a complicated,
			
		    System.out.println("User Time = "+ models[j].getClass().getSimpleName() + ": " 
            		+ String.format("%d", taskUserTimeNano));

			System.out.println("Accuracy of " + models[j].getClass().getSimpleName() + ": "
					+ String.format("%.2f%%", avgAcc)
            	+ "\n---------------------------------");

			
            
		}  // End for loop - running for all models 
		
	
		System.out.println(" Number of Classes = "+String.format("%d",data.numClasses()));
		System.out.printf(" Number of Attributes = "+String.format("%d", data.numAttributes()-1));
		
		}
		out.close();
	}
}

