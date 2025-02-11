package com.alcea.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alcea.models.Profile;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    public void open() {
        database = dbHelper.open();
    }

    public void close() {
        dbHelper.close();
    }

    private Cursor getAllEntries(String table, String[] columns){
        return database.query(table, columns, null, null, null, null, null);
    }

    public List<Profile> getProfiles(){
        ArrayList<Profile> profiles = new ArrayList<>();
        Cursor cursor = getAllEntries("profiles", new String[]{"id", "name", "master", "salt"});
        while (cursor.moveToNext()){
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String master = cursor.getString(cursor.getColumnIndex("master"));
            @SuppressLint("Range") String salt = cursor.getString(cursor.getColumnIndex("salt"));
            profiles.add(new Profile(id, name, master, salt));
        }
        return profiles;
    }

    public Profile getProfile(String nameAsked){
        Profile profile = null;
        String query = String.format("SELECT * FROM %s WHERE %s = ?", "profiles", "name");
        Cursor cursor = database.rawQuery(query, new String[]{nameAsked});
        if(cursor.moveToFirst()){
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String master = cursor.getString(cursor.getColumnIndex("master"));
            @SuppressLint("Range") String salt = cursor.getString(cursor.getColumnIndex("salt"));
            profile = new Profile(id, name, master, salt);
        }
        cursor.close();
        return profile;
    }

    public Profile createProfile(String name, String master, String salt){
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("master", master);
        cv.put("salt", salt);
        database.insert("profiles", null, cv);
        return getProfile(name);
    }

    public long deleteProfile(Profile profile){
        String whereClause = "id = " + profile.getId();
        return database.delete("profiles", whereClause, null);
    }

    public long updateProfile(Profile profile){
        ContentValues cv = new ContentValues();
        String whereClause = "id = " + profile.getId();
        cv.put("name", profile.getName());
        return database.update("profiles", cv, whereClause, null);
    }
}
