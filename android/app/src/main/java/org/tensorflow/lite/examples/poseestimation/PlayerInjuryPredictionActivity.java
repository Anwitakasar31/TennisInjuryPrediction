package org.tensorflow.lite.examples.poseestimation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.InjuryPredictionResult;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.InjuryPredictionResultDBHelper;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.Message;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.Player;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.PlayerDBHelper;

import java.util.ArrayList;

public class PlayerInjuryPredictionActivity extends AppCompatActivity {
    int playerID;
    String playerName;
    ArrayList<String> dataList;
    PlayerDBHelper playerDBHelper;
    InjuryPredictionResultDBHelper injuryPredictionResultDBHelper;
    TextView txtPlayerID, txtPlayerName, txtPlayerAge, txtPlayerLevel, txtWMA, txtPredictionScore, txtPrediction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_injury_prediction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle b = getIntent().getExtras();
        playerID = b.getInt("id");
        dataList = (ArrayList<String>) getIntent().getSerializableExtra("data");

        Log.i(ProjectConstants.TAG, "DataList size in Dashboard Value - " + dataList.size());
        Log.i(ProjectConstants.TAG, "Player ID in InjuryPredictionActivity -" +  playerID);
        if(dataList.size() > 1)
        {
            String playerID1 = dataList.get(0);
            playerID = Integer.parseInt(playerID1);
            playerName = dataList.get(1);
            Log.i(ProjectConstants.TAG, "PlayerID in TennisServeRecord from Array Value - " + playerID1);
            Log.i(ProjectConstants.TAG, "PlayerName in TennisServeRecord from Array Value - " + playerName);
        }

        playerDBHelper = new PlayerDBHelper(this);
        injuryPredictionResultDBHelper = new InjuryPredictionResultDBHelper(this);

        //Get Player details
        Player player = playerDBHelper.getPlayer(playerID);

        txtPlayerName= (TextView) findViewById(R.id.textPlayerName);
        txtPlayerName.setText("Name - " +playerName);
        txtPlayerAge= (TextView) findViewById(R.id.txtPlayerAge);
        txtPlayerAge.setText("Age - " + player.GetPlayerAge());
        txtPlayerLevel= (TextView) findViewById(R.id.txtPlayerLevel);

        if( player.GetPlayerLevel() != null)
            txtPlayerLevel.setText("Experience - " + player.GetPlayerLevel());
        else
            txtPlayerLevel.setText("Experience - " + "");

        txtWMA= (TextView) findViewById(R.id.txtWMA);
        txtPredictionScore= (TextView) findViewById(R.id.txtPredictionScore);
        txtPrediction= (TextView) findViewById(R.id.txtPrediction);

        InjuryPredictionResult injuryPredictionResult = injuryPredictionResultDBHelper.getInjuryPredictionResult(playerID);
        if(injuryPredictionResult == null)
        {
            Log.i(ProjectConstants.TAG, "Injury Prediction Result not available yet");
            Message.message(getApplicationContext(),"Injury Prediction Result not available yet");
        }
        else
        {
            txtWMA.setText(String.valueOf(String.format("%.2f",injuryPredictionResult.GetWMA())));
            double predictionScore = injuryPredictionResult.GetPredictionScore();
            txtPredictionScore.setText(String.valueOf(String.format("%.1f",injuryPredictionResult.GetPredictionScore())));

            if(predictionScore == 1)
            {
                txtPrediction.setText("You have low chances of shoulder injury");
                txtPrediction.setTextColor(Color.GREEN);
            }
            else if(predictionScore == 2)
            {
                txtPrediction.setText("You have moderate chances of shoulder injury");
                txtPrediction.setTextColor(Color.RED);
            }
            else if(predictionScore == 3)
            {
                txtPrediction.setText("You have high chances of shoulder injury");
                txtPrediction.setTextColor(Color.RED);
            }
            else
            {
                txtPrediction.setText("You have no chances or low chances of shoulder injury");
                txtPrediction.setTextColor(Color.GREEN);
            }
        }

    }

    public void back(View view) {
        Intent myIntent = new Intent(PlayerInjuryPredictionActivity.this, PlayerDashboardActivity.class);
        myIntent.putExtra("id", playerID);
        myIntent.putExtra("name", playerName);
        myIntent.putExtra("data", dataList);
        startActivityForResult(myIntent, 0);
    }
}