package com.example.feelsave;

public class contactModel {

        private String name;
        private String number;
        private String key;



    public contactModel(String name, String number, String key) {
        this.name = name;
        this.number = number;
        this.key = key;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
