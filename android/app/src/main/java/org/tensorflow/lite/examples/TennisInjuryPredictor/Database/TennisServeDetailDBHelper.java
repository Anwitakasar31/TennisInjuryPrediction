package org.tensorflow.lite.examples.TennisInjuryPredictor.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
import org.tensorflow.lite.examples.TennisInjuryPredictor.*;
import org.tensorflow.lite.examples.poseestimation.ProjectConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TennisServeDetailDBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "TennisInjuryPredictor.db";
    // Contacts table name
    public static final String TABLE_TennisServeDetailS = "TennisServeDetail";
    // Musics Table Columns names
    public static final String RECORD_ID = "RecordID";
    public static final String PLAYER_ID = "PlayerID";
    public static final String RECORD_DATE = "RecordDate";
    public static final String SERVE_ANGLE = "ServeAngle";

    public TennisServeDetailDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public TennisServeDetailDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TENNISSERVEDETAIL_TABLE = "CREATE TABLE " + TABLE_TennisServeDetailS + "("
                + RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PLAYER_ID + " INTEGER,"
                + RECORD_DATE + " TEXT," +  SERVE_ANGLE + " REAL" + ")";
        db.execSQL(CREATE_TENNISSERVEDETAIL_TABLE);
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
        String CREATE_TENNISSERVEDETAIL_TABLE = "CREATE TABLE " + TABLE_TennisServeDetailS + "("
                + RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PLAYER_ID + " INTEGER,"
                + RECORD_DATE + " TEXT," +  SERVE_ANGLE + " REAL" + ")";
        db.execSQL(CREATE_TENNISSERVEDETAIL_TABLE);
    }

    public void addTennisServeDetail(TennisServeDetail tennisServeDetail) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put(PLAYER_ID, tennisServeDetail.GetPlayerID()); // tennisServeDetail code
            //values.put(RECORD_DATE, tennisServeDetail.GetRecordDate()); // tennisServeDetail desc
            values.put(RECORD_DATE, tennisServeDetail.GetRecordDate().toString());
            values.put(SERVE_ANGLE, tennisServeDetail.GetServeAngle());

            db.insert(TABLE_TennisServeDetailS, null, values);
            db.close(); // Closing database connection
        }
        catch(Exception ex)
        {
            throw ex;
        }
    }

    public void addAllTennisServeDetail(List<TennisServeDetail> TennisServeDetailList) {
        try {
            for(TennisServeDetail tennisServeDetailRec : TennisServeDetailList)
            {
                addTennisServeDetail(tennisServeDetailRec);
            }
        }
        catch(Exception ex)
        {
            throw ex;
        }
    }
    // Getting one tennisServeDetail
    public TennisServeDetail getTennisServeDetail(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TennisServeDetailS, new String[] { RECORD_ID,
                        PLAYER_ID, RECORD_DATE, SERVE_ANGLE }, PLAYER_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        TennisServeDetail tennisServeDetail = new TennisServeDetail();
        tennisServeDetail.SetRecordID(cursor.getInt(0));
        tennisServeDetail.SetPlayerID(cursor.getInt(1));
        tennisServeDetail.SetRecordDate(new Date(cursor.getString(2)));
        tennisServeDetail.SetServeAngle(cursor.getDouble(3));
// return tennisServeDetail
        return tennisServeDetail;
    }

    // Getting All tennisServeDetails
    public List<TennisServeDetail> getAllTennisServeDetails(int playerId, int expectedCount) {


        List<TennisServeDetail> tennisServeDetailList = new ArrayList<TennisServeDetail>();
// Select All Query
        String selectQuery = "SELECT RecordID, PlayerID, RecordDate, ServeAngle FROM " + TABLE_TennisServeDetailS
                //+ " Inner Join tennisServeDetail on TennisServeDetail.tennisServeDetailID = tennisServeDetail.tennisServeDetailID "
                + " Where PlayerID = " + playerId + " ORDER BY datetime(RecordDate) DESC " + "LIMIT  " + expectedCount;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TennisServeDetail tennisServeDetail = new TennisServeDetail();
                tennisServeDetail.SetRecordID(cursor.getInt(0));
                tennisServeDetail.SetPlayerID(cursor.getInt(1));
                tennisServeDetail.SetRecordDate(new Date(cursor.getString(2)));
                tennisServeDetail.SetServeAngle(cursor.getDouble(3));
                tennisServeDetailList.add(tennisServeDetail);
            } while (cursor.moveToNext());
        }

        return tennisServeDetailList;
    }

    // Getting All tennisServeDetails
    public List<Double> getRecentTennisServeAnglesDetails(int playerId, int expectedCount) {


        List<Double> tennisServeDetailList = new ArrayList<Double>();
// Select All Query
        String selectQuery = "SELECT RecordDate, ServeAngle FROM " + TABLE_TennisServeDetailS
                //+ " Inner Join tennisServeDetail on TennisServeDetail.tennisServeDetailID = tennisServeDetail.tennisServeDetailID "
                + " Where PlayerID = " + playerId + " ORDER BY datetime(RecordDate) DESC ";
                //+ "LIMIT " + expectedCount;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                double serverAngle = cursor.getDouble(1);
                tennisServeDetailList.add(serverAngle);
            } while (cursor.moveToNext());
        }
        Log.i(ProjectConstants.TAG, "Array values in TennisServeDetailDBHelper - " + Arrays.toString(tennisServeDetailList.toArray()) );
        return tennisServeDetailList;
    }

    public List<String> getAllTennisServeDetails1(int playerId) {


        List<String> tennisServeDetailList = new ArrayList<String>();
// Select All Query
        String selectQuery = "SELECT RecordID, PlayerID, RecordDate, ServeAngle FROM " + TABLE_TennisServeDetailS
                + " Where PlayerID = " + playerId  + " order by datetime(RecordDate) DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TennisServeDetail tennisServeDetail = new TennisServeDetail();
                tennisServeDetail.SetRecordID(cursor.getInt(0));
                tennisServeDetail.SetPlayerID(cursor.getInt(1));
                tennisServeDetail.SetRecordDate(new Date(cursor.getString(2)));
                tennisServeDetail.SetServeAngle(cursor.getDouble(3));
                String val =tennisServeDetail.GetRecordID() + " - " + tennisServeDetail.GetRecordDate() + " - " + tennisServeDetail.GetServeAngle();
                tennisServeDetailList.add(val);
            } while (cursor.moveToNext());
        }

        return tennisServeDetailList;
    }

    // Getting tennisServeDetails Count
    public int getTennisServeDetailsCount(int playerID) {
        String countQuery = "SELECT * FROM " + TABLE_TennisServeDetailS + " Where PlayerID=" + playerID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();
// return count
        return cursor.getCount();
    }

    // Updating a tennisServeDetail
    public int updateTennisServeDetail(TennisServeDetail tennisServeDetail) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RECORD_DATE, tennisServeDetail.GetRecordDate().toString());
        values.put(SERVE_ANGLE, tennisServeDetail.GetServeAngle());

// updating row
        return db.update(TABLE_TennisServeDetailS, values, RECORD_ID + " = ?",
                new String[]{String.valueOf(tennisServeDetail.GetRecordID())});
    }

    // Deleting a tennisServeDetail
    public void deleteTennisServeDetail(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TennisServeDetailS, RECORD_ID + " = ?",
                new String[] {Integer.toString(id)});
        db.close();
    }
}
