package com.crush.thecrushmanager.model;

/**
 * Created by tuand on 5/19/2018.
 */

import java.io.Serializable;

public class MainDrink implements Serializable {

    private String name;
    private long price;
    private String imageURL;
    private long rating;


    public MainDrink() {
    }

    public MainDrink(String name, long price, String imageURL, long rating) {

        this.name = name;
        this.price = price;
        this.imageURL = imageURL;
        this.rating = rating;
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

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }
}