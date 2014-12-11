/*
 * Tri Doan
 * convertCSV.java will convert synthetic data in csv format into arff format with nominal class
 * These synhetic datasets are created by createData.py using scikit 
 * 
 * Oct 11
 * */
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
//import  weka.classifiers.trees.M5P; 


import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class convertCSV_arff {
  /**
   * takes 2 arguments:
   * - CSV input file
   * - ARFF output file
   * Tri Doan
   * Date Oct 11
   */
	
	public static List<String> getList(String path) {
		   List<String> results = new ArrayList<String>();

	         File[] files = new File(path).listFiles();
	        //If this pathname does not denote a directory, then listFiles() returns null. 
	   
	        for (File file : files) {
	            if (file.isFile() && file.getName().endsWith((".csv"))) {
	                  results.add(file.getName());
	                   }
	               }
		   return results;
	  }
	
  public static void main(String[] args) throws Exception {
    /*if (args.length != 2) {
      System.out.println("\nUsage: CSV2Arff <input.csv> <output.arff>\n");
      System.exit(1);
    }
    */

    String fileName;
	String inPath  = "c:\\AlgoSelecMeta\\finalData";
    List<String> results = getList(inPath);
    String dataset;
    
    for (int i=0; i< results.size();i++) {
		dataset=results.get(i);
		String inStore = inPath+"\\"+ dataset;
		System.out.println("Dataset : "+inStore);
	 
    
    // load CSV
    CSVLoader loader = new CSVLoader();
   // loader.setSource(new File(args[0]));
    loader.setSource(new File(inStore));
    Instances data = loader.getDataSet();
   
    // Convert class into numeric
    
    NumericToNominal convert= new NumericToNominal();
    String[] options= new String[2];
    options[0]="-R";
    options[1]=String.format("%d",data.numAttributes());  //Set class to nominal
    
    convert.setOptions(options);
    convert.setInputFormat(data);

    Instances newData=Filter.useFilter(data, convert);


    // save ARFF
    ArffSaver saver = new ArffSaver();
    saver.setInstances(newData);
    // saver.setFile(new File(args[1]));
    fileName= dataset.split("\\.(?=[^\\.]+$)")[0];
    saver.setFile(new File("c:\\data\\cleanedData\\"+fileName+".arff"));
    //saver.setDestination(new File(args[1]));
    saver.writeBatch(); 
  } }
}