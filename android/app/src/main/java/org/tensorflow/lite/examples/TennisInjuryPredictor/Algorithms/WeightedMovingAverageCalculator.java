package org.tensorflow.lite.examples.TennisInjuryPredictor.Algorithms;

import java.util.List;

public class WeightedMovingAverageCalculator {
    
    public static double CalculateWeightedMovingAverage(List<Double> tennisServeDetails)
    {
        double weightedMoivingAverage = 0;
        return weightedMoivingAverage;
    }

    public static double CalculateWeightedMovingAverage1(List<Double> tennisServeDetails)
    {
        double weightedMoivingAverage = 0;
        int peroid = 30;
        SimpleMovingAverage sma = new SimpleMovingAverage(30);
        return weightedMoivingAverage;
    }
}
