package com.crush.thecrushmanager.model;

import java.io.Serializable;

/**
 * Created by tuand on 5/19/2018.
 */

public class Status implements Serializable {


    public static final String KEY_STATUS_NAME = "name";
    private String id;
    private String name;


    public Status() {
    }

    public Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}