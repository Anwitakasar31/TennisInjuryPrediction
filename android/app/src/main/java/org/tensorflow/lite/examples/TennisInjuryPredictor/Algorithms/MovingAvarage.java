package org.tensorflow.lite.examples.TennisInjuryPredictor.Algorithms;

//Anwita - This is simple and better
public class MovingAvarage {

    public static void main(String[] args) {
        double[] array = {1.2, 3.4, 4.5, 4.5, 4.5};

        double St = 0D;
        for(int i=0; i<array.length; i++) {
            St = movingAvarage(St, array[i]);
        }
        System.out.println(St);

    }

    public static double CalculateMovingAverage(double[] array)
    {
        double St = 0D;
        for(int i=0; i<array.length; i++) {
            St = movingAvarage(St, array[i]);
        }
        System.out.println(St);
        return St;
    }

    private static double movingAvarage(double St, double Yt) {
        double alpha = 0.01, oneMinusAlpha = 0.99;
        if(St <= 0D) {
            St = Yt;
        } else {
            St = alpha*Yt + oneMinusAlpha*St;
        }
        return St;
    }

}