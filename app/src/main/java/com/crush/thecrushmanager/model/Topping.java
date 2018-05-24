package com.crush.thecrushmanager.model;

import java.io.Serializable;

/**
 * Created by tuand on 5/20/2018.
 */
public class Topping implements Serializable {
    public static final String KEY_TOPPING_NAME = "name";


    private String name;
    private long price;
    private String imageURL;

    public Topping() {

    }

    public Topping(String name, long price, String imageURL) {

        this.name = name;
        this.price = price;
        this.imageURL = imageURL;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}