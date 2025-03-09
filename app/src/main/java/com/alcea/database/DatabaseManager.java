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

    public void deleteProfile(Profile profile){
        String servicesWhereClause = "profileId = ?";
        database.delete("services", servicesWhereClause, new String[]{String.valueOf(profile.getId())});
        String profileWhereClause = "id = ?";
        database.delete("profiles", profileWhereClause, new String[]{String.valueOf(profile.getId())});
    }

    public void updateProfile(Profile profile){
        ContentValues cv = new ContentValues();
        String whereClause = "id = ?";
        cv.put("name", profile.getName());
        database.update("profiles", cv, whereClause, new String[]{String.valueOf(profile.getId())});
    }

    public List<Service> getServices(int askedProfileId){
        ArrayList<Service> services = new ArrayList<>();
        String query = String.format("SELECT * FROM %s WHERE %s = ?", "services", "profileId");
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(askedProfileId)});
        while (cursor.moveToNext()){
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String service = cursor.getString(cursor.getColumnIndex("service"));
            @SuppressLint("Range") int logoResId = cursor.getInt(cursor.getColumnIndex("logoResId"));
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password"));
            @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
            @SuppressLint("Range") String extraData = cursor.getString(cursor.getColumnIndex("extra"));
            @SuppressLint("Range") int profileId = cursor.getInt(cursor.getColumnIndex("profileId"));
            services.add(new Service(id, service, logoResId, password, timestamp, extraData, profileId));
        }
        return services;
    }
    public Service getService(String nameAsked, int profileIdAsked){
        Service service = null;
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", "services", "service", "profileId");
        Cursor cursor = database.rawQuery(query, new String[]{nameAsked, String.valueOf(profileIdAsked)});
        if(cursor.moveToFirst()){
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("service"));
            @SuppressLint("Range") int logoResId = cursor.getInt(cursor.getColumnIndex("logoResId"));
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password"));
            @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
            @SuppressLint("Range") String extraData = cursor.getString(cursor.getColumnIndex("extra"));
            @SuppressLint("Range") int profileId = cursor.getInt(cursor.getColumnIndex("profileId"));
            service = new Service(id, name, logoResId, password, timestamp, extraData, profileId);
        }
        cursor.close();
        return service;
    }

    public void deleteService(Service service){
        String whereClause = "id = ?";
        database.delete("services", whereClause, new String[]{String.valueOf(service.getId())});
    }

    public Service createService(Service service){
        ContentValues cv = new ContentValues();
        cv.put("service", service.getName());
        cv.put("logoResId", service.getLogoResId());
        cv.put("password", service.getPassword());
        cv.put("timestamp", service.getTimestamp());
        cv.put("extra", service.getExtraData());
        cv.put("profileId", service.getProfileId());
        database.insert("services", null, cv);
        return getService(service.getName(), service.getProfileId());
    }

    public Service updateService(Service service){
        ContentValues cv = new ContentValues();
        cv.put("service", service.getName());
        cv.put("logoResId", service.getLogoResId());
        cv.put("password", service.getPassword());
        cv.put("timestamp", service.getTimestamp());
        cv.put("extra", service.getExtraData());
        cv.put("profileId", service.getProfileId());
        String whereClause = "id = ? AND profileId = ?";
        database.update("services", cv, whereClause, new String[]{String.valueOf(service.getId()), String.valueOf(service.getProfileId())});
        return getService(service.getName(), service.getProfileId());
    }
}
