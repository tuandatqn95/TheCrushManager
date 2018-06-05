package com.crush.thecrushmanager.model;

import java.io.Serializable;

/**
 * Created by tuand on 5/19/2018.
 */

public class Status implements Serializable {


    public static final String KEY_STATUS_NAME = "name";

    private String name;
    private String color;

    public Status() {
    }

    public Status(String name, String color) {

        this.name = name;
        this.color = color;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}