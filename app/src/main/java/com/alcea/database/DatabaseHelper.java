package com.alcea.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DATABASE_PATH;
    private static final String DATABASE_NAME = "alceadb.db";
    private static final int DATABASE_VERSION = 1;
    private final Context alceaContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DATABASE_PATH =context.getFilesDir().getPath() + DATABASE_NAME;
        alceaContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) { }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    void createdDb() {
        File file = new File(DATABASE_PATH);
        try{
            InputStream input = alceaContext.getAssets().open(DATABASE_NAME);
            OutputStream output = new FileOutputStream(DATABASE_PATH);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
        } catch (IOException e) {
            Log.d("DatabaseHelper", e.getMessage());
        }
    }
    public SQLiteDatabase open()throws SQLException {

        return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

}