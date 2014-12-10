/**
 * Tri Doan
 * file PCAapply.java
 * Utilize Principal Components Analysis with weka
 * to transform original dataset into 5 column data.
 * DateL Oct 8, 2014
 * 
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.PrincipalComponents;
import weka.attributeSelection.Ranker;

/**
 * This class use the weka libary to implement Principal Components Analysis.
 */
public class PCAapply {

  private static final String USAGE = String
      .format("This program performs principal components analysis on the given"
      + "dataset.\nIt will transform the data onto its principal components, "
      + "optionally performing\ndimensionality reduction by ignoring the "
      + "principal components with the\nsmallest eigenvalues.\n\n"
      + "Required options:\n"
      + "-i [string]     Input dataset to perform PCA on.\n\n"
      + "Options:\n\n"
      + "-d [int]    Desired dimensionality of output dataset. If -1,\n"
      + "            no dimensionality reduction is performed.\n"
      + "            Default value -1.\n"
      + "-s          If set, the data will be scaled before running\n"
      + "            PCA, such that the variance of each feature is 1.");

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

  
  public static void main(String args[]) {
   // Timers timer = new Timers();
    try {
      // Get the data set path.
      //String dataset = Utils.getOption('i', args);
     
        String inPath  = "c:\\data\\tmp";
        String outPath;
        List<String> results = getList(inPath);
        String dataset;
        int dimension=4;  // determining the number of attributes for target
        for (int i=0; i< results.size();i++) {
    		dataset=results.get(i);
    		String inStore = inPath+"\\"+ dataset;
    		System.out.println("Dataset : "+inStore);
 	 
        //}


	  //dataset ="C:\\data\\uciTest\\kr-vs-kp.arff";
     	 
      if (dataset.length() == 0)
        throw new IllegalArgumentException();
   

     // timer.StartTimer("total_time");
     // timer.StartTimer("loading_data");

      // Load input dataset.
      dimension = 5; // default number of attributes for PCA
      DataSource source = new DataSource(inStore);
      Instances data = source.getDataSet();
      System.out.println(" Number of attributes before PCA = "+String.format("%d",data.numAttributes()));
      for (int j=0;j < data.numAttributes();j++)     
         System.out.print(data.attribute(j).name()+" ");
    
      
      //timer.StopTimer("loading_data");

        
      //Find out what dimension we want.
     
    /*  String dimension = Utils.getOption('d', args);
     if (dimension.length() == 0) {
        k = data.numAttributes();
      } else {
        k = Integer.parseInt(dimension);
        // Validate the parameter.
        if (k > data.numAttributes()) {
          System.out.printf("[Fatal] New dimensionality (%d) cannot be greater"
              + "than existing dimensionality (%d)!'\n", k, data.numAttributes());
          
         System.exit(-1); 
        } 
      } */
      
      
      if (dimension > data.numAttributes()) {
    	  dimension = data.numAttributes()-1;
    	  outPath =  "c:\\data\\PCA_less\\pca_"+dataset;
      }
      else { 
    	  dimension = 4;
    	  outPath =  "c:\\data\\PCA_large\\pca_"+dataset;
      }
    // test
      if (data.classIndex() == -1)
	         data.setClassIndex(data.numAttributes() - 1);
      
      
      // Performs a principal components analysis.
      PrincipalComponents pcaEvaluator = new PrincipalComponents();

      // Sets the amount of variance to account for when retaining principal 
      // components.
      pcaEvaluator.setVarianceCovered(1.0);
      // Sets maximum number of attributes to include in transformed attribute 
      // names.
      pcaEvaluator.setMaximumAttributeNames(-1);

      // Scaled X such that the variance of each feature is 1.
      String scale = Utils.getOption('s', args);
      if (scale.length() == 0) {
        pcaEvaluator.setCenterData(true);
      } else {
        pcaEvaluator.setCenterData(false);
      }

      // Ranking the attributes.
      Ranker ranker = new Ranker();
      // Specify the number of attributes to select from the ranked list.
      ranker.setNumToSelect(dimension);

      AttributeSelection selector = new AttributeSelection();
      selector.setSearch(ranker);
      selector.setEvaluator(pcaEvaluator);
      selector.SelectAttributes(data);

      // Transform data into eigenvector basis.
      Instances transformedData = selector.reduceDimensionality(data);
      System.out.println(" \nNumber of attributes after PCA = "+String.format("%d",transformedData.numAttributes()));
      BufferedWriter writer = new BufferedWriter(new FileWriter(outPath));
	 
	  
     
	  writer.write(transformedData.toString());
	  //System.out.print(transformedData.classAttribute());
	  writer.flush();
	  writer.close();      
      
     // timer.StopTimer("total_time");

      //timer.PrintTimer("loading_data");
     // timer.PrintTimer("total_time");
        }
    } catch (IllegalArgumentException e) {
      System.err.println(USAGE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
