package com.alcea.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alcea.models.Profile;
import com.alcea.models.Service;

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

    public Profile createProfile(Profile profile){
        ContentValues cv = new ContentValues();
        cv.put("name", profile.getName());
        cv.put("master", profile.getMaster());
        cv.put("salt", profile.getSalt());
        database.insert("profiles", null, cv);
        return getProfile(profile.getName());
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

    public List<Service> getServices(){
        ArrayList<Service> services = new ArrayList<>();
        Cursor cursor = getAllEntries("services", new String[]{"id", "service", "logoResId", "password", "timestamp", "extra"});
        while (cursor.moveToNext()){
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String service = cursor.getString(cursor.getColumnIndex("service"));
            @SuppressLint("Range") int logoResId = cursor.getInt(cursor.getColumnIndex("logoResId"));
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password"));
            @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
            @SuppressLint("Range") String extraData = cursor.getString(cursor.getColumnIndex("extra"));
            services.add(new Service(id, service, logoResId, password, timestamp, extraData));
        }
        return services;
    }
    public Service getService(String nameAsked){
        Service service = null;
        String query = String.format("SELECT * FROM %s WHERE %s = ?", "services", "service");
        Cursor cursor = database.rawQuery(query, new String[]{nameAsked});
        if(cursor.moveToFirst()){
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("service"));
            @SuppressLint("Range") int logoResId = cursor.getInt(cursor.getColumnIndex("logoResId"));
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password"));
            @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
            @SuppressLint("Range") String extraData = cursor.getString(cursor.getColumnIndex("extra"));
            service = new Service(id, name, logoResId, password, timestamp, extraData);
        }
        cursor.close();
        return service;
    }

    public Service createService(Service service){
        ContentValues cv = new ContentValues();
        cv.put("service", service.getName());
        cv.put("logoResId", service.getLogoResId());
        cv.put("password", service.getPassword());
        cv.put("timestamp", service.getTimestamp());
        cv.put("extra", service.getExtraData());
        database.insert("services", null, cv);
        return getService(service.getName());
    }

    public Service updateService(Service service){
        ContentValues cv = new ContentValues();
        cv.put("service", service.getName());
        cv.put("logoResId", service.getLogoResId());
        cv.put("password", service.getPassword());
        cv.put("timestamp", service.getTimestamp());
        cv.put("extra", service.getExtraData());
        String whereClause = "id = ?";
        database.update("services", cv, whereClause, new String[]{String.valueOf(service.getId())});
        return getService(service.getName());
    }
}
