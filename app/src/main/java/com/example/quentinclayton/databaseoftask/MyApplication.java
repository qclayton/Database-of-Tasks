package com.example.quentinclayton.databaseoftask;

public class MyApplication {

    private static boolean sActivityVisible;

    public static boolean isActivityVisible(){
        return sActivityVisible;
    }

    public static void activityResumed(){
        sActivityVisible = true;
    }

    public static void activityPaused(){
        sActivityVisible = false;
    }
}
