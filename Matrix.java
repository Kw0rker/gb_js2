package ru.gb.jtwo.lone.online.lesson2;
import java.math.*;
import java.util.Arrays;

public class Matrix {
    private String[]first;
     int [][] from_str(String str) throws Exception {
         int [][]result;
         int rows,columns;
         first=str.split("\r?\n");
         rows=first.length;
         result=new int[rows][];
         int row_m=0;
         int previous_columns_l=first[0].split(" ").length;
         for(String s:first){
             String []second=s.split(" ");
             if (second.length!=previous_columns_l){throw new Exception("Its not a matrix");}
             previous_columns_l=second.length;columns=second.length;
             if ((rows!=4)||(columns!=4)){throw new Exception("Its not a 4x4 matrix");}
             result[row_m]=new int[second.length];
             for (int i = 0; i <second.length ; i++) {
                 try {
                     result[row_m][i]=Integer.parseInt(second[i]);
             }catch (NumberFormatException e){throw new Exception("Its not a number matrix");}

             }
             row_m++;
         }
         return result;
     }

}
