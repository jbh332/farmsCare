package com.example.hong.boaaproject.mainActivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GraphDBHelper extends SQLiteOpenHelper {
    public GraphDBHelper(Context context){
        super(context, "graph5", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE graph (_id INTEGER PRIMARY KEY AUTOINCREMENT, userID VARCHAR(10), userDataSleep INTEGER, userDataCalorie INTEGER, userDataWalk INTEGER, userDataWater INTEGER, userDateMonth VARCHAR(10), userDateDay VARCHAR(10), userDateYear VARCHAR(10));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS graph");
        onCreate(db);
    }

}
