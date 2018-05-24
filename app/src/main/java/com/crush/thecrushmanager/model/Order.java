package com.crush.thecrushmanager.model;

import java.io.Serializable;

/**
 * Created by TuanDat on 5/23/2018.
 */

public class Order implements Serializable {
    private String name;
    private String imageURL;

    public Order() {

    }

    public Order(String name, String imageURL) {
        this.setName(name);
        this.setImageURL(imageURL);
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
