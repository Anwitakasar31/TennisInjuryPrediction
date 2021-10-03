package org.tensorflow.lite.examples.TennisInjuryPredictor.Algorithms;

//Anwita - Wrote from online algorithm
public class WeightedMovingAverage {

    public static void main(String[] args) {
        double[] array = {50, 45, 60};

        double WMA = CalculateWeightedMovingAverage(array);
        System.out.println("Final Weighted Moving Average - " + WMA);

    }

    public static double CalculateWeightedMovingAverage(double[] array)
    {
        int sumPeriod  = 0;
        double movingSum = 0;
        double WMA = 0.0;

        for(int i=0; i<array.length; i++) {
            sumPeriod = sumPeriod + (i+1);
        }
        System.out.println("sumPeriod - " + sumPeriod);

        for(int i=0; i<array.length; i++) {
            double sum = movingAvarage(i+1, array[i]);
            movingSum = movingSum + sum;
            System.out.println("Total Moving Sum - " + movingSum);
        }
        WMA = movingSum / sumPeriod;
        System.out.println("Final Weighted Moving Average - " + WMA);
        return WMA;
    }

    private static double movingAvarage(int period, double value) {
        double sum = 0.0;
        System.out.println("period - " + period);
        System.out.println("Data value - " + value);

        sum = period * value;
        System.out.println("Moving Sum (Perriod * Value)- " + sum);
        return sum;
    }
}
