/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Linn Hein Htet
 */
public class cartItem {
    private String foodname;
    private double price;
    private int qty;

    public cartItem(String foodname, int qty) {
        this.foodname = foodname;
        this.qty = qty;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public cartItem(String foodname, double price, int qty) {
        this.foodname = foodname;
        this.price = price;
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }
    
    
    
}


