package org.tensorflow.lite.examples.poseestimation;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.Player;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.PlayerDBHelper;

import java.util.ArrayList;
import java.util.List;

public class PlayerListActivity extends AppCompatActivity {
    ListView playerListView;
    PlayerDBHelper playerDBHelper;
    private static final String TAG = "TennisInjuryPredictor";
    ArrayList<String> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), PlayerActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        playerListView = (ListView) findViewById(R.id.playerList);
        playerDBHelper = new PlayerDBHelper(this);
        List<String> players = playerDBHelper.getAllPlayerNames();
        if (players != null && players.size() != 0) {
            Log.i(TAG, "Player count -" + players.size());
        }
        ArrayAdapter<String> playersAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, players);
        playerListView.setAdapter(playersAdapter);

        playerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                data.clear();
                //playerName
                String value = (String) adapter.getItemAtPosition(position);
                // assuming string and if you want to get the value on click of list item
                // do what you intend to do on click of listview row
                Log.i(ProjectConstants.TAG, "Selected Value - " + value);
                if(value.contains("-")) {
                    String[] parts = value.split("-");
                    String playerID = parts[0]; // 004
                    String playerName = parts[1]; // 034556
                    Log.i(ProjectConstants.TAG, "PlayerID  in list Value - " + playerID);
                    Log.i(ProjectConstants.TAG, "PlayerName in list Value - " + playerName);
                    Intent myIntent = new Intent(PlayerListActivity.this, PlayerDashboardActivity.class);
                    //playerID = Integer.parseInt(txtPlayerID.getText().toString());
                    data.add(playerID.toString());
                    data.add(playerName);
                    myIntent.putExtra("id", playerID);
                    myIntent.putExtra("name", playerName);
                    myIntent.putExtra("data", data);

                    startActivityForResult(myIntent, 0);
                }
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

     }

    public void addPlayer(View view)
    {
    }

}