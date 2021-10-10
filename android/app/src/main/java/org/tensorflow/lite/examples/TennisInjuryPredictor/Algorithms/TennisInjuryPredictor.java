package org.tensorflow.lite.examples.TennisInjuryPredictor.Algorithms;

import android.content.Context;

import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.InjuryPredictionResult;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.InjuryPredictionResultDBHelper;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.PlayerDBHelper;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.TennisServeDetail;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.TennisServeDetailDBHelper;

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
        injuryPredictionResult = injuryPredictionResultDBHelper.getInjuryPredictionResult(playerID);

        if(injuryPredictionResult == null) {
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
                double[] playerServerAngles = new double[expectedRecordCount];
                List<TennisServeDetail> tennisServeDetails =   tennisServeDetailDBHelper.getAllTennisServeDetails(playerID, expectedRecordCount);
                if (tennisServeDetails !=null && tennisServeDetails.size() == expectedRecordCount)
                {

                    weightedMovingAverage= WeightedMovingAverageCalculator.CalculateWeightedMovingAverage(tennisServeDetails,expectedRecordCount);
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
                //Anwita - Add or update record in database
                if(recordExists)
                {
                    //Update record
                    injuryPredictionResultDBHelper.updateInjuryPredictionResult(injuryPredictionResult);
                }
                else
                {
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
}
