package com.example.quentinclayton.databaseoftask.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.quentinclayton.databaseoftask.model.ModelTask;

public class AlarmHelper {

    private static AlarmHelper sInstance;
    private Context mContext;
    private AlarmManager mAlarmManager;

    public static AlarmHelper getInstance(){
        if (sInstance == null){
            sInstance = new AlarmHelper();
        }
        return sInstance;
    }

    public void init(Context context){
        this.mContext = context;
        mAlarmManager = (AlarmManager) context.getApplicationContext().
                getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm(ModelTask task){
        Intent intent = new Intent(mContext, AlarmReciver.class);
        intent.putExtra("title", task.getTitle());
        intent.putExtra("time_stamp", task.getTimeStamp());
        intent.putExtra("color", task.getPriorityColor());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(),
                (int) task.getTimeStamp(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, task.getDate(), pendingIntent);
    }

    public void removeAlarm(long taskTimeStump){
        Intent intent = new Intent(mContext, AlarmReciver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, (int) taskTimeStump,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.cancel(pendingIntent);
    }
}
