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
    PlayerDBHelper playerDBHelper;
    TennisServeDetailDBHelper tennisServeDetailDBHelper;
    InjuryPredictionResultDBHelper injuryPredictionResultDBHelper;

    public TennisInjuryPredictor()
    {
        playerDBHelper = new PlayerDBHelper(context);
        tennisServeDetailDBHelper = new TennisServeDetailDBHelper(context);
    }
    public TennisInjuryPredictor(Context context, int playerID)
    {
        this.context = context;
        this.playerID = playerID;
        playerDBHelper = new PlayerDBHelper(context);
        tennisServeDetailDBHelper = new TennisServeDetailDBHelper(context);
        injuryPredictionResultDBHelper = new InjuryPredictionResultDBHelper(context);
    }
    public InjuryPredictionResult ProcessData()
    {
        InjuryPredictionResult injuryPredictionResult = new InjuryPredictionResult();
        injuryPredictionResult.SetPlayerID(playerID);
        try{
            //Get All Tennis serve detail
            List<TennisServeDetail> tennisServeDetails = tennisServeDetailDBHelper.getAllTennisServeDetails(playerID);
            if(tennisServeDetails != null && tennisServeDetails.size() > 30)
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
