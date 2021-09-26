package org.tensorflow.lite.examples.TennisInjuryPredictor.Database;

public class Player {
    private int playerID;
    private String playerName;
    private int age;
    private String level;

    public void SetPlayerID(int playerID)
    {
        this.playerID = playerID;
    }

    public int GetPlayerID()
    {
        return this.playerID;
    }

    //Anwita - add other methods
    //Name
    public void SetPlayerName(String playerName)
    {
        this.playerName = playerName;
    }
    public String GetPlayerName()
    {
        return this.playerName;
    }
    //Age
    public void SetPlayerAge(int age)
    {
        this.age = age;
    }
    public int GetPlayerAge()
    {
        return this.age;
    }
    //Level
    public void SetPlayerLevel(String level)
    {
        this.level = level;
    }
    public String GetPlayerLevel()
    {
        return this.level;
    }
}

