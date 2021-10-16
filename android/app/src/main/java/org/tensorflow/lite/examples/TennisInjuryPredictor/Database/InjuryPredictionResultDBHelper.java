package org.tensorflow.lite.examples.TennisInjuryPredictor.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class InjuryPredictionResultDBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "TennisInjuryPredictor.db";
    // Contacts table name
    public static final String TABLE_InjuryPrediction_DetailS = "InjuryPredictionResult";
    // Musics Table Columns names
    public static final String RECORD_ID = "RecordID";
    public static final String PLAYER_ID = "PlayerID";
    public static final String WEIGHTED_MOVING_AVERAGE = "WeightedMovingAverage";
    public static final String SCORE = "Score";

    public InjuryPredictionResultDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public InjuryPredictionResultDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
        onCreate(db);
    }

    public void DropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_InjuryPrediction_DetailS);

    }
    public void CreateTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_PREDICTION_TABLE = "CREATE TABLE " + TABLE_InjuryPrediction_DetailS + "("
                + RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PLAYER_ID + " INTEGER,"
                + WEIGHTED_MOVING_AVERAGE + " REAL," +  SCORE + " REAL" + ")";
        db.execSQL(CREATE_PREDICTION_TABLE);
    }

    public void addInjuryPredictionResult(InjuryPredictionResult injuryPredictionResult) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put(PLAYER_ID, injuryPredictionResult.GetPlayerID()); // InjuryPredictionResult code
            values.put(WEIGHTED_MOVING_AVERAGE, injuryPredictionResult.GetWMA());
            values.put(SCORE, injuryPredictionResult.GetPredictionScore());

            db.insert(TABLE_InjuryPrediction_DetailS, null, values);
            db.close(); // Closing database connection
        }
        catch(Exception ex)
        {
            throw ex;
        }
    }

    public int getInjuryPredictionResultCount(int playerID) {
        String countQuery = "SELECT RecordID, PlayerID, WeightedMovingAverage, Score FROM " + TABLE_InjuryPrediction_DetailS + " Where PlayerID=" + playerID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();
// return count
        return cursor.getCount();
    }

    public InjuryPredictionResult getInjuryPredictionResult(int playerId) {
        InjuryPredictionResult injuryPredictionResult = null;
        String selectQuery = "SELECT RecordID, PlayerID, WeightedMovingAverage, Score FROM " + TABLE_InjuryPrediction_DetailS
                + " Where PlayerID = " + playerId ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            injuryPredictionResult = new InjuryPredictionResult();
            injuryPredictionResult.SetRecordID(cursor.getInt(0));
            injuryPredictionResult.SetPlayerID(cursor.getInt(1));
            injuryPredictionResult.SetWMA(cursor.getDouble(2));
            injuryPredictionResult.SetPredictionScore(cursor.getDouble(3));
        }

        return injuryPredictionResult;
    }

    // Updating a InjuryPredictionResult
    public int updateInjuryPredictionResult(InjuryPredictionResult injuryPredictionResult) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WEIGHTED_MOVING_AVERAGE, injuryPredictionResult.GetWMA());
        values.put(SCORE, injuryPredictionResult.GetPredictionScore());

// updating row
        return db.update(TABLE_InjuryPrediction_DetailS, values, RECORD_ID + " = ?",
                new String[]{String.valueOf(injuryPredictionResult.GetRecordID())});
    }
}

