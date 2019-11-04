package ru.gb.jtwo.lone.online.lesson2;

public class main  {
    public static void main(String[] args) {
        try {
            int matrix[][]=new Matrix().from_str("10 3 1 2\n2 3 2 2\n5 6 7 1\n300 3 1 0");
        }
        catch (Exception e){e.printStackTrace();}

    }
}
