package org.tensorflow.lite.examples.TennisInjuryPredictor.Database;

import java.util.Date;

public class TennisServeDetail {
    private int recordID;
    private int playerID;
    private double serveAngle;
    private Date recordDate;

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

    //Serve Angle
    public void SetServeAngle(double serveAngle)
    {
        this.serveAngle = serveAngle;
    }
    public double GetServeAngle()
    {
        return this.serveAngle;
    }

    //Date Record
    public void SetRecordDate(Date recordDate)
    {
        this.recordDate = recordDate;
    }
    public Date GetRecordDate()
    {
        return this.recordDate;
    }
}
