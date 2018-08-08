package com.example.quentinclayton.databaseoftask;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "tasksInfo";
    // Contacts table name
    private static final String TABLE_TASKS = "tasks";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SH_ADDR = "shop_address";


    public DBHandler(Context context){super(context,DATABASE_NAME,null, DATABASE_VERSION);}
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + KEY_ID + "INTEGER PRIMARY KEY," + KEY_NAME;
                db.execSQL(CREATE_CONTACTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Drop older table if it existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        //Creating table again
        onCreate(db);
    }
    //Adding new task
    public void addTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, task.getName());
        //Inserting Row
        db.insert(TABLE_TASKS, null,values);
        db.close();
    }
    //Getting one task
    public Task getAllTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS, new String[]{KEY_ID,
                        KEY_NAME, KEY_SH_ADDR}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Task contact = new Task(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
// return task
        return contact;
    }
    //Getting All Task
    public List<Task> getAllTasks(){
        List<Task> taskList = new ArrayList<Task>();
        //Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_TASKS;

        SQLiteDatabase db = this. getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping through all rows and adding to the list
        if(cursor.moveToFirst()){
            do{
                Task task = new Task();
                task.setId(Integer.parseInt(cursor.getString(0)));
                task.setName(cursor.getString(1));
                task.setAddress(cursor.getString(2));
                //Adding contacts to list
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        //return contact list
        return taskList;
    }
    // Getting tasks Count
    public int getTasksCount(){
        String countQuery = "Select * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        //return count
        return cursor.getCount();
    }
    //updating a task
    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, task.getName());
        values.put(KEY_SH_ADDR,task.getAddress());

        //updating row
        return db.update(TABLE_TASKS, values, KEY_ID + "=?",
                new String [] {String.valueOf(task.getId())});
    }
    //Deleting a task
    public void deleteTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_ID + " =?",
                new String [] {String.valueOf(task.getId()) });
        db.close();
    }
}
