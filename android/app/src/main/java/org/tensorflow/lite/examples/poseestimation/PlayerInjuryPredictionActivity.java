package org.tensorflow.lite.examples.poseestimation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class PlayerInjuryPredictionActivity extends AppCompatActivity {
    int playerID;
    ArrayList<String> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_injury_prediction);

        Bundle b = getIntent().getExtras();
        playerID = b.getInt("id");
        dataList = (ArrayList<String>) getIntent().getSerializableExtra("data");

        Log.i(ProjectConstants.TAG, "DataList size in Dashboard Value - " + dataList.size());
        Log.i(ProjectConstants.TAG, "Player ID in InjuryPredictionActivity -" +  playerID);
        if(dataList.size() > 1)
        {
            String playerID1 = dataList.get(0);
            String playerName = dataList.get(1);
            Log.i(ProjectConstants.TAG, "PlayerID in TennisServeRecord from Array Value - " + playerID1);
            Log.i(ProjectConstants.TAG, "PlayerName in TennisServeRecord from Array Value - " + playerName);
        }
    }
}