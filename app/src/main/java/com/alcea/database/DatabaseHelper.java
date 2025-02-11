package com.alcea.database;

import static android.content.Context.MODE_PRIVATE;

import static com.alcea.utils.Contsants.DATABASE_NAME;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS profiles (" +
                "id INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE," +
                "master TEXT," +
                "salt TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS passwords (" +
                "id INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT," +
                "service TEXT UNIQUE," +
                "value TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public SQLiteDatabase open()throws SQLException {
        return getReadableDatabase();
    }

}