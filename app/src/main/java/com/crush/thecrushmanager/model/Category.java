package com.crush.thecrushmanager.model;

/**
 * Created by tuand on 5/19/2018.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {
    public static final String KEY_CATEGORY_NAME = "name";

    private String name;
    private String imageURL;


    public Category() {

    }

    public Category(String name, String imageURL) {

        this.imageURL = imageURL;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


}