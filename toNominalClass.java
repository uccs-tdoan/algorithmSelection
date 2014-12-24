/*
 * toNominalClass.java is pivot program to convert last column (class) into nominal from numeric
 * The core will be used in convertCSV.java 
 * Tri Doan
 * Sept 27, 2014
 * */
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class toNominalClass {
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


    public static void main(String[] args) throws Exception
    {


        String fileName;
    	String inPath  = "c:\\algoSelecMeta\\syntheticData";
        List<String> results = getList(inPath);
        String dataset;

        for (int i=0; i< results.size();i++) {
    		dataset=results.get(i);
    		String inStore = inPath+"\\"+ dataset;
    		System.out.println("Dataset : "+inStore);
    	 

    	CSVLoader loader = new CSVLoader();
        // loader.setSource(new File(args[0]));
         loader.setSource(new File(inStore));
         Instances data = loader.getDataSet();
      
        NumericToNominal convert= new NumericToNominal();
        String[] options= new String[2];
        options[0]="-R";
        options[1]=String.format("%d",data.numAttributes());  //range of variables to make numeric
        
        convert.setOptions(options);
        convert.setInputFormat(data);

        Instances newData=Filter.useFilter(data, convert);

        System.out.println("Before");
        for(int j=0; j<data.numAttributes(); j=j+1)
        {
            System.out.println("Nominal? "+data.attribute(j).isNominal());
        }

        System.out.println("After");
        for(int j=0; j<data.numAttributes(); j=j+1)
        {
            System.out.println("Nominal? "+newData.attribute(j).isNominal());
        }

    }
    }
} 