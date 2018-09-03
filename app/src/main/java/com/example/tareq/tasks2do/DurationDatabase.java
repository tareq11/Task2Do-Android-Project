package com.example.tareq.tasks2do;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DurationDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "duration.db";
    public static final String TABLE_NAME = "duration_table";
    public static final String ID_COL = "ID";
    public static final String STATUS_COL = "STATUS";
    public static final String LAST_START_COL = "LAST_START";
    public static final String OVERALL_DURATION_COL = "OVERALL_DURATION";


    public DurationDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_TASKS = "CREATE TABLE " + TABLE_NAME  + "("
                + ID_COL + " INTEGER PRIMARY KEY ,"
                + STATUS_COL + " TEXT, "
                + LAST_START_COL + " TEXT,"
                + OVERALL_DURATION_COL + " INTEGER" + ")";


        db.execSQL(CREATE_TABLE_TASKS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean addNewDuration(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_COL,id);
        contentValues.put(STATUS_COL, isProcessed(false));
        contentValues.put(LAST_START_COL,0);
        contentValues.put(OVERALL_DURATION_COL,0);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean updateData(String id,Boolean isProceed,String lastStart,long overAll) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_COL,id);
        contentValues.put(STATUS_COL, isProcessed(isProceed));
        contentValues.put(LAST_START_COL,lastStart);
        contentValues.put(OVERALL_DURATION_COL,overAll);

        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }

    public void updateDuration(String id,Boolean isProceed,String lastStart,int overAll) {
        String strSQL = "UPDATE "+TABLE_NAME+" SET  ("+ID_COL+", "+STATUS_COL+", "+LAST_START_COL+","+OVERALL_DURATION_COL+")= " +
                "("+id+",'"+ isProcessed(isProceed)+"','"+lastStart+"',"+overAll+")" +
                " WHERE id="+id;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(strSQL);
    }

    public String isProcessed(boolean isProcessed){
        if (isProcessed)
            return "processed";
        return "paused";
    }
    public String[] getDurationDetailsByID(String taskID) {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME+ " WHERE "+ID_COL+"=\""+taskID+"\"";;
        String []durationDetails = new String[4];
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            String id = cursor.getString(0);
            String status = cursor.getString(1);
            String lastStart = cursor.getString(2);
            String overAll = cursor.getString(3);
             durationDetails = new String[]{id, status,lastStart,overAll};
        }

        return durationDetails;
    }

}