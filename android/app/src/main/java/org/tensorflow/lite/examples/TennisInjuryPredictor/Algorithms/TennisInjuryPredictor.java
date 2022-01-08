package org.tensorflow.lite.examples.TennisInjuryPredictor.Algorithms;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.InjuryPredictionResult;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.InjuryPredictionResultDBHelper;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.Player;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.PlayerDBHelper;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.TennisServeDetail;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.TennisServeDetailDBHelper;
import org.tensorflow.lite.examples.poseestimation.ProjectConstants;

import java.util.Arrays;
import java.util.List;

public class TennisInjuryPredictor {
    Context context;
    int playerID;
    int expectedRecordCount;
    PlayerDBHelper playerDBHelper;
    TennisServeDetailDBHelper tennisServeDetailDBHelper;
    InjuryPredictionResultDBHelper injuryPredictionResultDBHelper;
    Player player;
    int playerAge;
    //Player age range
    // range 1- 15 -30
    //range 2 = 30 - 45
    //range 3 - 45 +
    double maxServeAngle = 170;
    double minServeAngle = 60;
    double range1thresholdLevel1ServeAngle = 130;
    double range1thresholdLeve2ServeAngle = 140;
    double range1thresholdLeve3ServeAngle = 150;

    double range2thresholdLevel1ServeAngle = 125;
    double range2thresholdLeve2ServeAngle = 130;
    double range2thresholdLeve3ServeAngle = 135;

    double range3thresholdLevel1ServeAngle = 120;
    double range3thresholdLeve2ServeAngle = 125;
    double range3thresholdLeve3ServeAngle = 130;

    public TennisInjuryPredictor()
    {
        playerDBHelper = new PlayerDBHelper(context);
        tennisServeDetailDBHelper = new TennisServeDetailDBHelper(context);
    }
    public TennisInjuryPredictor(Context context, int playerID, int expectedRecordCount)
    {
        Log.i(ProjectConstants.TAG, "Coming in TennisInjuryPredictor constructor");
        this.context = context;
        this.playerID = playerID;
        this.expectedRecordCount = expectedRecordCount;
        playerDBHelper = new PlayerDBHelper(context);
        tennisServeDetailDBHelper = new TennisServeDetailDBHelper(context);
        injuryPredictionResultDBHelper = new InjuryPredictionResultDBHelper(context);

        player = playerDBHelper.getPlayer(playerID);
        if(player != null)
        {
            playerAge =  player.GetPlayerAge();
            Log.i(ProjectConstants.TAG, "Player age is " + playerAge);
        }
    }
    public InjuryPredictionResult ProcessData()
    {
        boolean recordExists = false;
        InjuryPredictionResult injuryPredictionResult;
        int predictionResultRecordCount = injuryPredictionResultDBHelper.getInjuryPredictionResultCount(playerID);
        injuryPredictionResult = injuryPredictionResultDBHelper.getInjuryPredictionResult(playerID);
        Log.i(ProjectConstants.TAG, "InjuryPredictionResult Record Count = " + predictionResultRecordCount);

        if(predictionResultRecordCount == 0) {
            recordExists = false;
            Log.i(ProjectConstants.TAG, "InjuryPredictionResult does not exists in database");
            injuryPredictionResult = new InjuryPredictionResult();
            injuryPredictionResult.SetPlayerID(playerID);
        }
        else
        {
            recordExists = true;
        }
        try{
            //Get All Tennis serve detail
            int recordCount = tennisServeDetailDBHelper.getTennisServeDetailsCount(playerID);
            if(recordCount < expectedRecordCount)
            {
                //Process data here
                injuryPredictionResult.SetErrorMessage("Not enough data to process prediction");
                return injuryPredictionResult;
            }
            else
            {
                // process
                //Calculate WEighted moving average
                //Save in Tennis prediction
                double predictionScore = 0.0;
                double weightedMovingAverage = 0.0;
                double[] playerServerAngles;
                List<Double> tennisServeDetails =   tennisServeDetailDBHelper.getRecentTennisServeAnglesDetails(playerID, expectedRecordCount);
                Log.i(ProjectConstants.TAG, "tennisServeDetail list size - " + tennisServeDetails.size());

                if (tennisServeDetails !=null && tennisServeDetails.size() >= expectedRecordCount)
                {
                    //Anwita - Arrange these from old to latest
                    playerServerAngles = getArray (tennisServeDetails, expectedRecordCount);
                    weightedMovingAverage= WeightedMovingAverage.CalculateWeightedMovingAverage(playerServerAngles);

                    Log.i(ProjectConstants.TAG, "Weighted Moving Average - " + weightedMovingAverage);
                    injuryPredictionResult.SetWMA(weightedMovingAverage);

                    //Calculate Prediction SCore
                    double predScoreReturned = calculatePredictionScore(playerAge, weightedMovingAverage);
                    Log.i(ProjectConstants.TAG, "Prediction score returned from function- " + predScoreReturned);
                    predictionScore = predScoreReturned;
                    Log.i(ProjectConstants.TAG, "Prediction score calculated- " + predictionScore);
                }
                injuryPredictionResult.SetPredictionScore(predictionScore);
                Log.i(ProjectConstants.TAG, "Prediction score calculated- " + predictionScore);
                //Anwita - Add or update record in database
                if(recordExists)
                {
                    Log.i(ProjectConstants.TAG, "Record exists - Updating");
                    //Update record
                    injuryPredictionResultDBHelper.updateInjuryPredictionResult(injuryPredictionResult);
                }
                else
                {
                    Log.i(ProjectConstants.TAG, "Record does not exists - inserting");
                    //Add new record
                    injuryPredictionResultDBHelper.addInjuryPredictionResult(injuryPredictionResult);
                }
                return injuryPredictionResult;
            }

        }
        catch(Exception ex)
        {
            injuryPredictionResult.SetErrorMessage(ex.getMessage());
            return injuryPredictionResult;
        }

    }


    private double[] getArray(List<Double> tennisServeDetails, int expectedCount)
    {
        try {
            //Records are ordered by record id decending order
            Log.i(ProjectConstants.TAG, "inside getArray - " + tennisServeDetails.size());
            int indexToStart = 0;
            int recordCount = tennisServeDetails.size();
            Log.i(ProjectConstants.TAG, "tennisServeDetail list size in getArray - " + tennisServeDetails.size());
            if (recordCount >= expectedCount) {
                indexToStart = recordCount - expectedCount;
                Log.i(ProjectConstants.TAG, "Index to Start - " + indexToStart);
            }
            double[] playerServerAngles = new double[expectedCount];
            //int i = 0;
            //for (int x = indexToStart - 1; x < recordCount -1; x++) {
            for (int x = 0; x < expectedCount; x++) {
                Double tennisServeDetail = tennisServeDetails.get(x);
                if (tennisServeDetail == null) {
                    Log.i(ProjectConstants.TAG, "tennisServeDetail record not found");
                } else {
                    Log.i(ProjectConstants.TAG, "Adding value -" + tennisServeDetail + " in the array");
                    playerServerAngles[x] = tennisServeDetail;
                    //i++;
                }
            }
            Log.i(ProjectConstants.TAG, "playerServerAngles array size - " + playerServerAngles.length);
            Log.i(ProjectConstants.TAG, "Array values in getArray (Latest) - " + Arrays.toString(playerServerAngles));
            return playerServerAngles;
        }
        catch(Exception ex)
        {
            Log.e(ProjectConstants.TAG, "Error when creating array - " + ex.getMessage());
            return null;
        }
    }

    private double calculatePredictionScore(int playerAge, double weightedMovingAverage)
    {
        //Player age range
        // range 1- 15 -25
        //range 2 = 25 - 35
        //range 3 - 35 +
        double predScore = 0;
        if(playerAge >= 15 && playerAge <= 30) {
            if (weightedMovingAverage > range1thresholdLevel1ServeAngle && weightedMovingAverage <= range1thresholdLeve2ServeAngle) {
                predScore = 1;
            }
            else if (weightedMovingAverage > range1thresholdLeve2ServeAngle && weightedMovingAverage <= range1thresholdLeve3ServeAngle) {
                predScore = 2;
            }
            else if (weightedMovingAverage > range1thresholdLeve3ServeAngle && weightedMovingAverage <=maxServeAngle) {
                predScore = 3;
            }
            else {
                predScore = 0;
            }
        }
        else if(playerAge > 30 && playerAge <= 45) {
            if (weightedMovingAverage > range2thresholdLevel1ServeAngle && weightedMovingAverage <= range2thresholdLeve2ServeAngle) {
                predScore = 1;
            }
            else if (weightedMovingAverage > range2thresholdLeve2ServeAngle && weightedMovingAverage <= range2thresholdLeve3ServeAngle) {
                predScore = 2;
            }
            else if (weightedMovingAverage > range2thresholdLeve3ServeAngle && weightedMovingAverage <=maxServeAngle) {
                predScore = 3;
            }
            else {
                predScore = 0;
            }
        }
        else if(playerAge > 45 ) {
            Log.i(ProjectConstants.TAG, "Prediction score for above 35");
            if (weightedMovingAverage > range3thresholdLevel1ServeAngle && weightedMovingAverage <= range3thresholdLeve2ServeAngle) {
                Log.i(ProjectConstants.TAG, "WMA is above " + range3thresholdLevel1ServeAngle);
                predScore = 1.0;
                Log.i(ProjectConstants.TAG, "Prediction Score based on age and WMA - " + predScore);
            }
            else if (weightedMovingAverage > range3thresholdLeve2ServeAngle && weightedMovingAverage <= range3thresholdLeve3ServeAngle) {
                predScore = 2.0;
            }
            else if (weightedMovingAverage > range3thresholdLeve3ServeAngle && weightedMovingAverage <=maxServeAngle) {
                predScore = 3.0;
            }
            else {
                predScore = 0;
            }
        }
        else {
            Log.e(ProjectConstants.TAG, "Player is not in the appropriate age group");
        }
        Log.i(ProjectConstants.TAG, "Prediction Score based on age - " + predScore);
        return predScore;
    }
}
