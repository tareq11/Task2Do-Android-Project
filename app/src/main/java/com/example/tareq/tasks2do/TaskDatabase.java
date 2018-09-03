package com.example.tareq.tasks2do;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class TaskDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "task.db";
    public static final String TABLE_NAME = "task_table";
    public static final String ID_COL = "ID";
    public static final String NAME_COL = "NAME";
    public static final String CATEGORY_COL = "CATEGORY";
    public static final String DURATION_COL = "DURATION";
    public static final String DATETIME_COL = "DATETIME";
    public static final String DESCRIPTION_COL = "DESCRIPTION";
    SQLiteDatabase db;
    public TaskDatabase(Context context) {
        super(context, DATABASE_NAME, null, 2);
         db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_TASKS = "CREATE TABLE " + TABLE_NAME  + "("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + NAME_COL + " TEXT, "
                + DATETIME_COL + " TEXT,"
                + DURATION_COL + " INTEGER,"
                + CATEGORY_COL + " TEXT,"
                + DESCRIPTION_COL + " TEXT " + ")";

        this.db=db;
        db.execSQL(CREATE_TABLE_TASKS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COL,task.getName());
        contentValues.put(DATETIME_COL,task.getDateTime());
        contentValues.put(DURATION_COL,task.getDuration());
        contentValues.put(CATEGORY_COL,task.getCategory().toString());
        contentValues.put(DESCRIPTION_COL,task.getDescription());

        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String id,Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_COL,id);
        contentValues.put(NAME_COL,task.getName());
        contentValues.put(CATEGORY_COL,task.getCategory().toString());
        contentValues.put(DATETIME_COL,task.getDateTime());
        contentValues.put(DURATION_COL,task.getDuration());
        contentValues.put(DESCRIPTION_COL,task.getDescription());

        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }



    public List<Task> getAllTasksListByCategory(CategoryEnum category) {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        if (!category.equals(CategoryEnum.ALL))
            selectQuery= selectQuery+ " WHERE "+CATEGORY_COL+"=\""+category.toString()+"\"";
        List<Task> taskList = new ArrayList<Task>();

        // Select All Query

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {


                int id = (Integer.parseInt(cursor.getString(0)));
                String name = cursor.getString(1);
                String dateTime = cursor.getString(2);
                int duration =Integer.parseInt( cursor.getString(3));
                CategoryEnum categ = CategoryEnum.valueOf( cursor.getString(4).toUpperCase());
                String description = cursor.getString(5);
                Task task = new Task(id,name,dateTime,duration,categ,description);
                // Adding task to list
                taskList.add(task);

            } while (cursor.moveToNext());
        }

        // return tasks list
        return taskList;
    }

    public Task getTaskByID(String taskID) {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME+ " WHERE "+ID_COL+"=\""+taskID+"\"";;

       Task task=null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

                int id = (Integer.parseInt(cursor.getString(0)));
                String name = cursor.getString(1);
                String dateTime = cursor.getString(2);
                int duration =Integer.parseInt( cursor.getString(3));
                CategoryEnum categ = CategoryEnum.valueOf( cursor.getString(4).toUpperCase());
                String description = cursor.getString(5);
                 task = new Task(id,name,dateTime,duration,categ,description);
        }

        return task;
    }
    public void updateTaskDetails(Task task) {
        String strSQL = "UPDATE "+TABLE_NAME+" SET  ("+ID_COL+", "+NAME_COL+", "+DATETIME_COL+","+DURATION_COL+","+CATEGORY_COL+","+DESCRIPTION_COL+")= " +
                "("+task.getId()+",'"+task.getName()+"','"+task.getDateTime()+"',"+task.getDuration()+",'"+task.getCategory().getCategory().toUpperCase()+"','"+task.getDescription()+"')" +
                " WHERE id="+task.getId()+")";
        db.execSQL(strSQL);
    }

    public Task getTaskByName(String taskName) {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME+ " WHERE "+NAME_COL+"=\""+taskName+"\"";;

        Task task=null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            int id = (Integer.parseInt(cursor.getString(0)));
            String name = cursor.getString(1);
            String dateTime = cursor.getString(2);
            int duration =Integer.parseInt( cursor.getString(3));
            CategoryEnum categ = CategoryEnum.valueOf( cursor.getString(4).toUpperCase());
            String description = cursor.getString(5);
            task = new Task(id,name,dateTime,duration,categ,description);
        }

        return task;
    }
}