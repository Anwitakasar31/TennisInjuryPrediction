package org.tensorflow.lite.examples.TennisInjuryPredictor.Algorithms;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.InjuryPredictionResult;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.InjuryPredictionResultDBHelper;
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
    double thresholdLevelServeAngle = 230;
    double thresholdLeve2ServeAngle = 250;
    double thresholdLeve3ServeAngle = 270;
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
                double predictionScore = 0;
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
                    if (weightedMovingAverage > thresholdLevelServeAngle &&  weightedMovingAverage <=thresholdLeve2ServeAngle)
                    {
                        predictionScore = 1;
                    }
                    else if (weightedMovingAverage > thresholdLeve2ServeAngle &&  weightedMovingAverage <=thresholdLeve3ServeAngle)
                    {
                        predictionScore = 2;
                    }
                    if (weightedMovingAverage > thresholdLeve3ServeAngle)
                    {
                        predictionScore = 3;
                    }
                    else
                    {
                        predictionScore = 0;
                    }
                }
                injuryPredictionResult.SetPredictionScore(predictionScore);
                Log.i(ProjectConstants.TAG, "Prediction Score - " + predictionScore);
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
            Log.i(ProjectConstants.TAG, "inside getArray - " + tennisServeDetails.size());
            int indexToStart = 0;
            int recordCount = tennisServeDetails.size();
            Log.i(ProjectConstants.TAG, "tennisServeDetail list size in getArray - " + tennisServeDetails.size());
            if (recordCount >= expectedCount) {
                indexToStart = recordCount - expectedCount;
                Log.i(ProjectConstants.TAG, "Index to Start - " + indexToStart);
            }
            double[] playerServerAngles = new double[expectedCount];
            int i = 0;
            int totalCount = tennisServeDetails.size();
            for (int x = indexToStart - 1; x < recordCount -1; x++) {
                Double tennisServeDetail = tennisServeDetails.get(x);
                if (tennisServeDetail == null) {
                    Log.i(ProjectConstants.TAG, "tennisServeDetail record not found");
                } else {
                    Log.i(ProjectConstants.TAG, "Adding value -" + tennisServeDetail + " in the array");
                    playerServerAngles[i] = tennisServeDetail;
                    i++;
                }
            }
            Log.i(ProjectConstants.TAG, "playerServerAngles array size - " + playerServerAngles.length);
            Log.i(ProjectConstants.TAG, "Array values - " + Arrays.toString(playerServerAngles));
            return playerServerAngles;
        }
        catch(Exception ex)
        {
            Log.e(ProjectConstants.TAG, "Error when creating array - " + ex.getMessage());
            return null;
        }
    }
}
