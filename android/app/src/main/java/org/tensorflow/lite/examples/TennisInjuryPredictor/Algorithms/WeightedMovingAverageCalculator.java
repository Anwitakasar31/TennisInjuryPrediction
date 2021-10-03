package org.tensorflow.lite.examples.TennisInjuryPredictor.Algorithms;

import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.TennisServeDetail;

import java.util.List;

public class WeightedMovingAverageCalculator {
    
    public static double CalculateWeightedMovingAverage(List<TennisServeDetail> tennisServeDetails, int expectedRecordCount)
    {   double weightedMoivingAverage = 0;
        double[] playerServerAngles = new double[expectedRecordCount];
        try{
            //add them to previous to latest
            for (TennisServeDetail tennisServeDetail:tennisServeDetails) {
                playerServerAngles[expectedRecordCount -1] =tennisServeDetail.GetServeAngle();

            }
            weightedMoivingAverage = WeightedMovingAverage.CalculateWeightedMovingAverage(playerServerAngles);
        }
        catch(Exception ex)
        {}

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
