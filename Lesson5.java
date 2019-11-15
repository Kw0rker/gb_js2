package ru.gb.jtwo.lesson5;

import ru.gb.jtwo.lone.online.Main;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Lesson5 implements Runnable{
   static double []array=new double[10000];
   final static int h=array.length/2;

    @Override
    public void run() {
        for (int i = 0; i < h; i++) {
            array[i]=(float)array[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2);

        }
    }

    public static void main(String[] args) {
        Arrays.fill(array,1);
        edit_by_one_thread();
        Arrays.fill(array,1);
        new Lesson5().edit_by_2_threads();
    }

    private static void edit_by_one_thread() {
        Long a=System.currentTimeMillis();
        for (int i = 0; i < array.length; i++) {
            array[i] = (float)(array[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        System.out.println("Time for first method is:"+(System.currentTimeMillis()-a));
    }

    public void edit_by_2_threads(){
        Long a=System.currentTimeMillis();
       new Thread(this){
       };
       double []arr=new double[h];
        for (int i = 0; i <h ; i++) {
            arr[i]=(float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));

        }
        System.arraycopy(arr,0,array,h,h);
        System.out.println("Time for second method is:"+(System.currentTimeMillis()-a));
    }
}
