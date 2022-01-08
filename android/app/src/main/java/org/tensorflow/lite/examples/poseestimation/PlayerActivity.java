package org.tensorflow.lite.examples.poseestimation;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.PlayerDBHelper;
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerActivity extends AppCompatActivity {
    EditText name, age , experience, updateold, updatenew, delete;
    PlayerDBHelper playerDBHelper;
    //private static final Logger LOGGER = new Logger("PlayerActivity", "TennisInjuryPredictor");
    private static final String TAG = "TennisInjuryPredictor";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name= (EditText) findViewById(R.id.editName);
        age= (EditText) findViewById(R.id.editAge);
        experience= (EditText) findViewById(R.id.editExperience);
        playerDBHelper = new PlayerDBHelper(this);

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
        List<Player> players =  playerDBHelper.getAllPlayers();
        if(players !=null && players.size() != 0)
        {
            Log.i(TAG, "Player count -" +  players.size());
        }

        if(name == null)
        {
            Message.message(getApplicationContext(),"object Name is null");
            Log.i(TAG, "Object name is null");
            //LOGGER.log(Level.WARNING, "Object name is null");
        }
        String t1 = name.getText().toString();
        int t2 = Integer.parseInt(age.getText().toString());
        String t3 = experience.getText().toString();
        if(t1.isEmpty() || t2 == 0)
        {
            Message.message(getApplicationContext(),"Enter Both Name and Age");
        }
        else
        {
            Player player = new Player();

            player.SetPlayerName(t1);
            player.SetPlayerAge(t2);
            player.SetPlayerLevel(t3);
            long id = playerDBHelper.addPlayer(player);
            if(id<=0)
            {
                Message.message(getApplicationContext(),"Insertion Unsuccessful");
                name.setText("");
                age.setText("");
            } else
            {
                Message.message(getApplicationContext(),"Insertion Successful");
                name.setText("");
                age.setText("");
            }
        }
    }

    public void viewdata(View view)
    {
        //Finish these
        Player data = playerDBHelper.getPlayer(1);
        Message.message(this,data.GetPlayerName());

        Intent myIntent = new Intent( view.getContext(), PlayerListActivity.class);
        startActivityForResult(myIntent, 0);    }

    public void update( View view)
    {
        String u1 = updateold.getText().toString();
        int u2 =  Integer.parseInt(updatenew.getText().toString());
        if(u1.isEmpty() || u2 == 0)
        {
            Message.message(getApplicationContext(),"Enter Data");
        }
        else
        {
            Player player = new Player();
            player.SetPlayerName(u1);
            player.SetPlayerAge(u2);
            int a= playerDBHelper.updatePlayer( player);
            if(a<=0)
            {
                Message.message(getApplicationContext(),"Unsuccessful");
                updateold.setText("");
                updatenew.setText("");
            } else {
                Message.message(getApplicationContext(),"Updated");
                updateold.setText("");
                updatenew.setText("");
            }
        }

    }
    public void Cancel( View view)
    {
        Intent myIntent = new Intent(PlayerActivity.this, PlayerListActivity.class);
        startActivityForResult(myIntent, 0);
    }

    public void delete( View view)
    {
        String uname = delete.getText().toString();
        if(uname.isEmpty())
        {
            Message.message(getApplicationContext(),"Enter Data");
        }
        else{
            //Anwita - Finish this

            int id = Integer.parseInt(uname);
            int a= playerDBHelper.deletePlayer(id);
            if(a<=0)
            {
                Message.message(getApplicationContext(),"Unsuccessful");
                delete.setText("");
            }
            else
            {
                Message.message(this, "DELETED");
                delete.setText("");
            }
        }
    }
}