package com.example.feelsave;

public class LocationModel {

    private String adress;
    private String time;
    private String key;



    public LocationModel(String adress, String time, String key){
        this.adress = adress;
        this.time = time;
        this.key = key;
    }
    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
