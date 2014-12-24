/**
 *  FeatureSelection.java performs attribute selection using CfsSubsetEval and GreedyStepwise
 * (backwards) and write back to arff file. All files keeps original name stored in c:/data/uciSelec folder 
 *  
 *  alternative method is to use meta-classifier in weka api
 *  Principal component analysis does not  guarantee small set of features and improve performance.
 *   Eg: diabtes, creditg,wisconsin and balance-scale datasets
 * 
 * Tri Doan
 * Sept 29
 */

import weka.attributeSelection.*;
import weka.core.*;
import weka.core.converters.ConverterUtils.*;
import weka.classifiers.*;
import weka.classifiers.meta.*;
import weka.classifiers.trees.*;
import weka.filters.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.*;

public class FeatureSelection {

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

	/**
   * uses the meta-classifier
   */
  protected static void useClassifier(Instances data) throws Exception {
    System.out.println("\n1. Meta-classfier");
    AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
    CfsSubsetEval eval = new CfsSubsetEval();
    GreedyStepwise search = new GreedyStepwise();
    search.setSearchBackwards(true);
    J48 base = new J48();
    classifier.setClassifier(base);
    classifier.setEvaluator(eval);
    classifier.setSearch(search);
    Evaluation evaluation = new Evaluation(data);
    
    evaluation.crossValidateModel(classifier, data, 10, new Random(1));
    System.out.println(evaluation.toSummaryString());
  }

  /**
   * uses the filter
   */
 
  protected static void useFilter(String filename ) throws Exception {
	    String inPath  = "c:\\data\\uciTest\\tmp";
	    //System.out.println(" in file "+ inPath);
	    
	    //System.out.println(inPath+"\\"+filename);
	    DataSource source = new DataSource(inPath+"\\"+filename);
        Instances data = source.getDataSet();
        System.out.println(" Class attribute "+data.classAttribute());
	
	    System.out.println("\n2. Filter");
	    weka.filters.supervised.attribute.AttributeSelection filter = new weka.filters.supervised.attribute.AttributeSelection();

	    
	    CfsSubsetEval eval = new CfsSubsetEval();
	    GreedyStepwise search = new GreedyStepwise();
	    search.setSearchBackwards(true); 
	    filter.setEvaluator(eval);
	    filter.setSearch(search);
	    filter.setInputFormat(data);
	    Instances newData = Filter.useFilter(data, filter);
	    BufferedWriter writer = new BufferedWriter(new FileWriter("c:\\data\\uciTest\\p_"+filename));
	    writer.write(newData.toString());
	    writer.flush();
	    writer.close();
	    
	    //System.out.println(newData);
	  }

  /**
   * uses the low level approach
   */
  protected static void useLowLevel(Instances data) throws Exception {
    System.out.println("\n3. Low-level");
    AttributeSelection attsel = new AttributeSelection();
    CfsSubsetEval eval = new CfsSubsetEval();
    GreedyStepwise search = new GreedyStepwise();
    search.setSearchBackwards(true);
    attsel.setEvaluator(eval);
    attsel.setSearch(search);
    attsel.SelectAttributes(data);
    int[] indices = attsel.selectedAttributes();
    
    System.out.println("selected attribute indices (starting with 0):\n" + Utils.arrayToString(indices));
  }

  
  /**
   * takes a dataset as first argument
   *
   * @param args    the commandline arguments
   * @throws Exception  if something goes wrong
   */
  public static void main(String[] args) throws Exception {
    // load data
    System.out.println("\n0. Loading data");
    //DataSource source = new DataSource(args[0]);
    String inPath  = "c:\\data\\uciTest\\tmp";
    List<String> results = getList(inPath);
    String inFile;
    for (int k=0; k< results.size();k++) {
		inFile=results.get(k);
		String inStore = inPath+"\\"+ results.get(k);
		
    // 1. meta-classifier
       // useClassifier(data);

    // 2. filter
		System.out.println(inStore+" ");
        useFilter(inFile);
    // 3. low-level
        //useLowLevel(data);
    }
  }
}
