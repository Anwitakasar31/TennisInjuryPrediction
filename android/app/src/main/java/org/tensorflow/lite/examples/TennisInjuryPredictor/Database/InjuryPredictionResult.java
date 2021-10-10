package org.tensorflow.lite.examples.TennisInjuryPredictor.Database;

public class InjuryPredictionResult {
    private int recordID;
    private int playerID;
    private double weightedMovingAverage;
    private double predictionScore;
    private String errorMessage;

    public void SetRecordID(int recordID)
    {
        this.recordID = recordID;
    }
    public int GetRecordID()
    {
        return this.recordID;
    }
    //ID
    public void SetPlayerID(int playerID)
    {
        this.playerID = playerID;
    }
    public int GetPlayerID()
    {
        return this.playerID;
    }
    //WMA
    public void SetWMA(double weightedMovingAverage)
    {
        this.weightedMovingAverage = weightedMovingAverage;
    }
    public double GetWMA()
    {
        return this.weightedMovingAverage;
    }

    //Score
    public void SetPredictionScore(double score)
    {
        this.predictionScore = score;
    }
    public double GetPredictionScore()
    {
        return this.predictionScore;
    }

    public void SetErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
    public String GetErrorMessage()
    {
        return this.errorMessage;
    }
}

