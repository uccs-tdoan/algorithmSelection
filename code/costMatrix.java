# Tri Doan
# Generate Cost Matrix for algorithm selection

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

import weka.classifiers.CostMatrix;
class  costMatrix
{
 
    
	
    
    public static void main(String[] args) throws Exception
    {
       	double penalty = 2.0;
        Scanner input=new Scanner(System.in);
        System.out.println("Enter size of matrix: ");
        int size=input.nextInt();
        PrintWriter out =new PrintWriter(new FileWriter("c:\\wekaAPI\\codes\\costMatrix28.Cost"));
        out.println("% Rows  Columns");
        out.println(String.format("%4d", size)+" "+String.format("%4d", size));
        out.print("% Matrix implements\n");
        for(int i = 0; i < size; i++) {
    		for(int j = 0; j < size; j++)
    			if (i==j)
    			   out.print(String.format("%4.1f",  0.0)); 
    			else if (i>j) 
    				out.print (String.format("%4.1f", (double) penalty));
    			else 
    				 out.print(String.format("%4.1f",(double) 1.0));
    	  out.println();	}
        
       out.close();  
     System.out.println("Done !");
    }
    
}

