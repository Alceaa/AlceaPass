package com.alcea.models;

public class Service {
    private int id;
    private String name;
    private Integer logoResId;
    private String password;
    private String timestamp;
    private String extraData;


    public Service(int id, String name, Integer logoResId, String password, String timestamp, String extraData) {
        this.id = id;
        this.name = name;
        this.logoResId = logoResId;
        this.password = password;
        this.timestamp = timestamp;
        this.extraData = extraData;

    }
    public Service(){

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getLogoResId() {
        return logoResId;
    }

    public String getPassword() {
        return password;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLogoResId(Integer logoResId) {
        this.logoResId = logoResId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}
