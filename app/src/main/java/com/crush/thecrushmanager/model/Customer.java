package com.crush.thecrushmanager.model;

import java.io.Serializable;
import java.util.Date;

public class Customer implements Serializable {
    private String name;
    private String address;
    private String email;
    private String phone;
    private String gender;
    private String imageURL;
    private Date birth;

    public Customer() {
    }

    public Customer(String name, String address, String email, String phone, String gender, String imageURL, Date birth) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.imageURL = imageURL;
        this.birth = birth;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }
}
