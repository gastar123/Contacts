package com.example.contacts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "phoneDB", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table phones ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "phone text UNIQUE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("drop table phones;");
            db.execSQL("create table phones ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "phone text UNIQUE);");
        }
    }
}
