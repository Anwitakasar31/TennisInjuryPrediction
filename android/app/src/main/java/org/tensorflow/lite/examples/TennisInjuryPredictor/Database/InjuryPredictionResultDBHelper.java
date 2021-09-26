package org.tensorflow.lite.examples.TennisInjuryPredictor.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class InjuryPredictionResultDBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "TennisInjuryPredictor.db";
    // Contacts table name
    public static final String TABLE_TennisServeDetailS = "InjuryPredictionResult";
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
        String CREATE_PLAYER_TABLE = "CREATE TABLE " + TABLE_TennisServeDetailS + "("
                + RECORD_ID + " INTEGER PRIMARY KEY," + PLAYER_ID + " TEXT,"
                + WEIGHTED_MOVING_AVERAGE + " DOUBLE," +  SCORE + " DOUBLE" + ")";
        db.execSQL(CREATE_PLAYER_TABLE);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TennisServeDetailS);

    }
    public void CreateTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_PLAYER_TABLE =  "CREATE TABLE " + TABLE_TennisServeDetailS + "("
                + RECORD_ID + " INTEGER PRIMARY KEY," + PLAYER_ID + " TEXT,"
                + WEIGHTED_MOVING_AVERAGE + " DOUBLE," +  SCORE + " DOUBLE" + ")";
        db.execSQL(CREATE_PLAYER_TABLE);
    }
}

