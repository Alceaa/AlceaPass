package com.alcea.models;

public class Profile {
    private final int id;
    private String name;
    private String master;
    private String salt;

    public Profile(int id, String name, String master, String salt){
        this.id = id;
        this.name = name;
        this.master = master;
        this.salt = salt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getMaster(){
        return master;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getSalt() {
        return salt;
    }
}
