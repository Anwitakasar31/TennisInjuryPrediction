package org.tensorflow.lite.examples.poseestimation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.PlayerDBHelper;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.TennisServeDetailDBHelper;

import java.util.ArrayList;
import java.util.List;

public class PlayerTennisServeRecordActivity extends AppCompatActivity {
    ListView playerListView;
    TennisServeDetailDBHelper tennisServeDetailDBHelper;
    int playerID;
    String playerName;
    ArrayList<String> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_tennis_serve_record);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle b = getIntent().getExtras();
        playerID = b.getInt("id");
        dataList = (ArrayList<String>) getIntent().getSerializableExtra("data");

        Log.i(ProjectConstants.TAG, "DataList size in Dashboard Value - " + dataList.size());
        Log.i(ProjectConstants.TAG, "Player ID in TennisServeRecordActivity -" +  playerID);
        if(dataList.size() > 1)
        {
            String playerID1 = dataList.get(0);
            playerName = dataList.get(1);
            Log.i(ProjectConstants.TAG, "PlayerID in TennisServeRecord from Array Value - " + playerID1);
            Log.i(ProjectConstants.TAG, "PlayerName in TennisServeRecord from Array Value - " + playerName);
        }
        playerListView = (ListView) findViewById(R.id.playerTennisServeRecordList);
        tennisServeDetailDBHelper = new TennisServeDetailDBHelper(this);
        List<String> players = tennisServeDetailDBHelper.getAllTennisServeDetails1(playerID);
        if(players !=null && players.size() != 0)
        {
            Log.i(ProjectConstants.TAG, "Player count -" +  players.size());
        }
        ArrayAdapter<String> playersAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, players);
        playerListView.setAdapter(playersAdapter);
    }

    public void back(View view) {
        Intent myIntent = new Intent(PlayerTennisServeRecordActivity.this, PlayerDashboardActivity.class);
        myIntent.putExtra("id", playerID);
        myIntent.putExtra("name", playerName);
        myIntent.putExtra("data", dataList);
        startActivityForResult(myIntent, 0);
    }
}