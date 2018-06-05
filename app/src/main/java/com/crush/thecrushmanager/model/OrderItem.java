package com.crush.thecrushmanager.model;

import java.io.Serializable;
import java.util.List;

public class OrderItem implements Serializable {
    private String drinkName;
    private long drinkPrice;
    private long quantity;
    private long price;
    private List<Topping> toppings;

    public OrderItem() {
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public long getDrinkPrice() {
        return drinkPrice;
    }

    public void setDrinkPrice(long drinkPrice) {
        this.drinkPrice = drinkPrice;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public List<Topping> getToppings() {
        return toppings;
    }

    public void setToppings(List<Topping> toppings) {
        this.toppings = toppings;
    }
}
