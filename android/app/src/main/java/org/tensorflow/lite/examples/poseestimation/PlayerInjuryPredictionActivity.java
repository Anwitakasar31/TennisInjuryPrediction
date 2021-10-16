package org.tensorflow.lite.examples.poseestimation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.InjuryPredictionResult;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.InjuryPredictionResultDBHelper;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.Message;

import java.util.ArrayList;

public class PlayerInjuryPredictionActivity extends AppCompatActivity {
    int playerID;
    String playerName;
    ArrayList<String> dataList;
    InjuryPredictionResultDBHelper injuryPredictionResultDBHelper;
    TextView txtPlayerID, txtPlayerName, txtWMA, txtPredictionScore, txtPrediction;
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
            playerID = Integer.parseInt(playerID1);
            playerName = dataList.get(1);
            Log.i(ProjectConstants.TAG, "PlayerID in TennisServeRecord from Array Value - " + playerID1);
            Log.i(ProjectConstants.TAG, "PlayerName in TennisServeRecord from Array Value - " + playerName);
        }

        injuryPredictionResultDBHelper = new InjuryPredictionResultDBHelper(this);

        txtPlayerName= (TextView) findViewById(R.id.textPlayerName);
        txtPlayerName.setText(playerName);
        txtWMA= (TextView) findViewById(R.id.textWMA);
        txtPredictionScore= (TextView) findViewById(R.id.textPredictionScore);
        txtPrediction= (TextView) findViewById(R.id.textPrediction);

        InjuryPredictionResult injuryPredictionResult = injuryPredictionResultDBHelper.getInjuryPredictionResult(playerID);
        if(injuryPredictionResult == null)
        {
            Log.i(ProjectConstants.TAG, "Injury Prediction Result not available yet");
            Message.message(getApplicationContext(),"Injury Prediction Result not available yet");
        }
        else
        {
            txtWMA.setText(String.valueOf(injuryPredictionResult.GetWMA()));
            double predictionScore = injuryPredictionResult.GetPredictionScore();
            txtPredictionScore.setText(String.valueOf(injuryPredictionResult.GetPredictionScore()));

            if(predictionScore == 1)
            {
                txtPrediction.setText("You have low chances of shoulder injury");
            }
            else if(predictionScore == 2)
            {
                txtPrediction.setText("You have moderate chances of shoulder injury");
            }
            else if(predictionScore == 3)
            {
                txtPrediction.setText("You have high chances of shoulder injury");
            }
            else
            {
                txtPrediction.setText("You have no chances of shoulder injury");
            }
        }

    }
}