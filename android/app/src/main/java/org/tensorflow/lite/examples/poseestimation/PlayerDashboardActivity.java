package org.tensorflow.lite.examples.poseestimation;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.tensorflow.lite.examples.TennisInjuryPredictor.Algorithms.TennisInjuryPredictor;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Algorithms.WeightedMovingAverageCalculator;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.InjuryPredictionResult;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.Message;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.Player;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.PlayerDBHelper;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.TennisServeDetail;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.TennisServeDetailDBHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlayerDashboardActivity extends AppCompatActivity {
    TennisServeDetailDBHelper tennisServeDetailDBHelper;
    TextView txtPlayerID, txtPlayerName;
    int playerID;
    String playerName;
    int expectedRecordCount = 10;
    ArrayList<String> dataList;
    private static final String TAG = "TennisInjuryPredictor";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle b = getIntent().getExtras();
        //Anwita comment
        String playerID1 = b.getString("id");
        playerName = b.getString("name");
        dataList = (ArrayList<String>) getIntent().getSerializableExtra("data");

        Log.i(ProjectConstants.TAG, "DataList size in Dashboard Value - " + dataList.size());
        Log.i(ProjectConstants.TAG, "PlayerID in Dashboard Value - " + playerID);
        Log.i(ProjectConstants.TAG, "PlayerName in Dashboard Value - " + playerName);

        if(dataList.size() > 1)
        {
            playerID1 = dataList.get(0);
            playerName = dataList.get(1);
            Log.i(ProjectConstants.TAG, "PlayerID in Dashboard from Array Value - " + playerID1);
            Log.i(ProjectConstants.TAG, "PlayerName in Dashboard from Array Value - " + playerName);
        }
        txtPlayerID= (TextView) findViewById(R.id.textPlayerID);
        txtPlayerName= (TextView) findViewById(R.id.textPlayerName);
        txtPlayerID.setText(playerID1);
        txtPlayerName.setText(playerName);


        //playerID = Integer.parseInt(txtPlayerID.getText().toString());
        tennisServeDetailDBHelper = new TennisServeDetailDBHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent( view.getContext(), MainActivity.class);
                playerID = Integer.parseInt(txtPlayerID.getText().toString());
                myIntent.putExtra("id", playerID);
                myIntent.putExtra("name", playerName);
                myIntent.putExtra("data", dataList);
                startActivityForResult(myIntent, 0);
            }
        });
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent( view.getContext(), PlayerInjuryPredictionActivity.class);
                playerID = Integer.parseInt(txtPlayerID.getText().toString());
                myIntent.putExtra("id", playerID);
                myIntent.putExtra("name", playerName);
                myIntent.putExtra("data", dataList);
                startActivityForResult(myIntent, 0);
            }
        });
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent( view.getContext(), PlayerTennisServeRecordActivity.class);
                playerID = Integer.parseInt(txtPlayerID.getText().toString());
                myIntent.putExtra("id", playerID);
                myIntent.putExtra("name", playerName);
                myIntent.putExtra("data", dataList);
                startActivityForResult(myIntent, 0);
            }
        });
        //Process record
        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Anwita - new class for TennisInjury predictor
                if(tennisServeDetailDBHelper == null)
                {
                   tennisServeDetailDBHelper = new TennisServeDetailDBHelper(PlayerDashboardActivity.this);
                }
                playerID = Integer.parseInt(txtPlayerID.getText().toString());
                int recordCount = tennisServeDetailDBHelper.getTennisServeDetailsCount(playerID);
                if(recordCount < expectedRecordCount)
                {
                    Message.message(getApplicationContext(),"Not enough data to process. Please collect 30 days data");
                }
                else
                {
                    Log.i(ProjectConstants.TAG, "Processing Data.");
                    TennisInjuryPredictor tennisInjuryPredictor = new TennisInjuryPredictor(PlayerDashboardActivity.this,playerID,expectedRecordCount);
                    InjuryPredictionResult injuryPredictionResult = tennisInjuryPredictor.ProcessData();
                    if(injuryPredictionResult.GetErrorMessage() != null)
                    {
                        Message.message(getApplicationContext(),injuryPredictionResult.GetErrorMessage());
                    }
                    else
                    {
                        Message.message(getApplicationContext(),"Data processed successfully");
                    }
                }

            }
        });
    }

    public void Capture(View view)
    {
        //Finish these
       // Message.message(this,data.GetPlayerName());
    }

    public void ViewResults(View view)
    {
        //Finish these
        //Message.message(this,data.GetPlayerName());
    }

    public void ViewTennisServeTracking(View view)
    {
        //Finish these
        //Message.message(this,data.GetPlayerName());
    }

    public void ProcessTennisServeDetails(View view)
    {
        TennisInjuryPredictor tennisInjuryPredictor = new TennisInjuryPredictor();

        //Complete this
        tennisInjuryPredictor.ProcessData();
    }

    public void addTennisServeDetail(View view)
    {

        if(txtPlayerID == null)
        {
            Message.message(getApplicationContext(),"Player ID is null");
            Log.i(ProjectConstants.TAG, "Object name is null");
            //LOGGER.log(Level.WARNING, "Object name is null");
        }

        playerID = Integer.parseInt(txtPlayerID.getText().toString());
        if(playerID == 0)
        {
            Message.message(getApplicationContext(),"PlayerID missing");
        }
        else
        {
            Date dateNow = Calendar.getInstance().getTime();
            TennisServeDetail tennisServeDetail = new TennisServeDetail();
            tennisServeDetail.SetPlayerID(playerID);
            tennisServeDetail.SetRecordDate(dateNow);
            tennisServeDetail.SetServeAngle(261.4);
            tennisServeDetailDBHelper.addTennisServeDetail(tennisServeDetail);
            Message.message(getApplicationContext(),"Insertion Unsuccessful");
        }
    }
}