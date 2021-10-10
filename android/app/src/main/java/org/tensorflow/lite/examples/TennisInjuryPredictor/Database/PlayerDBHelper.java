package org.tensorflow.lite.examples.TennisInjuryPredictor.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
import org.tensorflow.lite.examples.TennisInjuryPredictor.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayerDBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "TennisInjuryPredictor.db";
    // Contacts table name
    public static final String TABLE_PlayerS = "Player";
    // Musics Table Columns names
    public static final String PLAYER_ID = "PlayerID";
    public static final String PLAYER_NAME = "PlayerName";
    public static final String PLAYER_LEVEL = "TennisLevel";
    public static final String PLAYER_AGE = "PlayerAge";
    public static final String TABLE_TennisServeDetailS = "TennisServeDetail";
    // Musics Table Columns names
    public static final String RECORD_ID = "RecordID";
    public static final String RECORD_DATE = "RecordDate";
    public static final String SERVE_ANGLE = "ServeAngle";

    public static final String TABLE_InjuryPrediction_DetailS = "InjuryPredictionResult";

    public static final String WEIGHTED_MOVING_AVERAGE = "WeightedMovingAverage";
    public static final String SCORE = "Score";

    public PlayerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public PlayerDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLAYER_TABLE = "CREATE TABLE " + TABLE_PlayerS + "("
                + PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PLAYER_NAME + " TEXT,"
                + PLAYER_AGE + " INTEGER," +  PLAYER_LEVEL + " TEXT" + ")";
        db.execSQL(CREATE_PLAYER_TABLE);

        String CREATE_TENNISSERVEDETAIL_TABLE = "CREATE TABLE " + TABLE_TennisServeDetailS + "("
                + RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PLAYER_ID + " INTEGER,"
                + RECORD_DATE + " NUMERIC," +  SERVE_ANGLE + " REAL" + ")";
        db.execSQL(CREATE_TENNISSERVEDETAIL_TABLE);

        String CREATE_PREDICTION_TABLE = "CREATE TABLE " + TABLE_InjuryPrediction_DetailS + "("
                + RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PLAYER_ID + " INTEGER,"
                + WEIGHTED_MOVING_AVERAGE + " REAL," +  SCORE + " REAL" + ")";

        db.execSQL(CREATE_PREDICTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_MusicS);
// Creating tables again
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PlayerS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TennisServeDetailS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_InjuryPrediction_DetailS);
        onCreate(db);
    }

    public void DropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PlayerS);

    }
    public void CreateTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_PLAYER_TABLE = "CREATE TABLE " + TABLE_PlayerS + "("
                + PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PLAYER_NAME + " TEXT,"
                + PLAYER_AGE + " INTEGER," +  PLAYER_LEVEL + " TEXT" + ")";
        db.execSQL(CREATE_PLAYER_TABLE);
    }

    public long addPlayer(Player Player) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            if(db == null)
            {
                Log.i("TennisInjuryPredictor", "database is null");
            }
            ContentValues values = new ContentValues();
           // values.put(PLAYER_ID, Player.GetPlayerID()); // Player code
            //values.put(RECORD_DATE, Player.GetRecordDate()); // Player desc
            values.put(PLAYER_NAME, Player.GetPlayerName().toString());
            values.put(PLAYER_AGE, Player.GetPlayerAge());
            values.put(PLAYER_LEVEL, Player.GetPlayerLevel());

            long id =  db.insert(TABLE_PlayerS, null, values);
            db.close(); // Closing database connection
            return id;
        }
        catch(Exception ex)
        {
            throw ex;
        }
    }

    public void addAllPlayer(List<Player> PlayerList) {
        try {
            for(Player PlayerRec : PlayerList)
            {
                addPlayer(PlayerRec);
            }
        }
        catch(Exception ex)
        {
            throw ex;
        }
    }
    // Getting one Player
    public Player getPlayer(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PlayerS, new String[] {
                        PLAYER_ID, PLAYER_NAME, PLAYER_AGE, PLAYER_LEVEL }, PLAYER_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Player Player = new Player();
        Player.SetPlayerID(cursor.getInt(0));
        Player.SetPlayerName(cursor.getString(1));
        Player.SetPlayerAge(cursor.getInt(2));
        Player.SetPlayerLevel(cursor.getString(3));
// return Player
        return Player;
    }

    // Getting All Players
    public List<Player> getAllPlayers(int playerId) {


        List<Player> PlayerList = new ArrayList<Player>();
// Select All Query
        String selectQuery = "SELECT  PlayerID, PlayerName, PlayerAge, TennisLevel FROM " + TABLE_PlayerS
                //+ " Inner Join Player on Player.PlayerID = Player.PlayerID "
                + " Where Player.PersonID = " + playerId ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Player Player = new Player();
                Player.SetPlayerID(cursor.getInt(0));
                Player.SetPlayerName(cursor.getString(1));
                Player.SetPlayerAge(cursor.getInt(2));
                Player.SetPlayerLevel(cursor.getString(3));
                PlayerList.add(Player);
            } while (cursor.moveToNext());
        }

        return PlayerList;
    }

    public List<Player> getAllPlayers() {


        List<Player> PlayerList = new ArrayList<Player>();
// Select All Query
        String selectQuery = "SELECT  PlayerID, PlayerName, PlayerAge, TennisLevel FROM " + TABLE_PlayerS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Player Player = new Player();
                Player.SetPlayerID(cursor.getInt(0));
                Player.SetPlayerName(cursor.getString(1));
                Player.SetPlayerAge(cursor.getInt(2));
                Player.SetPlayerLevel(cursor.getString(3));
                PlayerList.add(Player);
            } while (cursor.moveToNext());
        }

        return PlayerList;
    }

    public List<String> getAllPlayerNames() {


        List<String> PlayerList = new ArrayList<String>();
// Select All Query
        String selectQuery = "SELECT  PlayerID, PlayerName, PlayerAge, TennisLevel FROM " + TABLE_PlayerS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Integer id = cursor.getInt(0);
                String name = cursor.getString(1);
                String val =id + "-" + name;

                PlayerList.add(val);
            } while (cursor.moveToNext());
        }

        return PlayerList;
    }
    // Getting Players Count
    public int getPlayersCount() {
        String countQuery = "SELECT * FROM " + TABLE_PlayerS ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();
// return count
        return cursor.getCount();
    }

    // Updating a Player
    public int updatePlayer(Player Player) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PLAYER_AGE, Player.GetPlayerAge());
        values.put(PLAYER_LEVEL, Player.GetPlayerLevel());

// updating row
        return db.update(TABLE_PlayerS, values, PLAYER_ID + " = ?",
                new String[]{String.valueOf(Player.GetPlayerAge())});
    }

    // Deleting a Player
    public int deletePlayer(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = db.delete(TABLE_PlayerS, PLAYER_ID + " = ?",
                new String[] {Integer.toString(id)});
        db.close();
        return count;
    }
}


