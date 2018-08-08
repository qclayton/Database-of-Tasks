package com.example.quentinclayton.databaseoftask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHandler db = new DBHandler(this);
//Inserting Task/Rows
        Log.d("Insert" ,"Inserting..");
        db.addTask(new Task("Take a Shower"));
        db.addTask(new Task("Brush your teeth"));
        db.addTask(new Task("Put clothes on"));
        db.addTask(new Task("Go to School"));


        //Reading all Tasks
        Log.d("Reading ", "Reading all task after adding..");
        List<Task> tasks = db.getAllTasks();

        for (Task task : tasks) {
            String log = "Id: " + task.getId() + " ,Name: " + task.getName() + " ,Address: " + task.getAddress();
            // Writing tasks to log
            Log.d("Task: ", log);
        }
        //Deleting all shops
        for(int i = 1; i <=4; i++) db.deleteTask(db.getAllTask(i));

        //Reading all tasks
        Log.d("Reading: ", "reading all tasks again after deleting..");
        tasks = db.getAllTasks();

        for(Task task : tasks){
            String log = "Id: " + task.getId() + " ,Name: " + task.getName() + " ,Address: " + task.getAddress();
            //Writing tasks to log
            Log.d("Task: : ", log);
        }
    }
}
