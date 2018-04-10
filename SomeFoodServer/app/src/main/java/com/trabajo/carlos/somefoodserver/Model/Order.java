package com.trabajo.carlos.somefoodserver.Model;

/**
 * Created by Carlos Prieto on 05/10/2017.
 */

public class Order {

    private String ProductId, ProductName, Quantity, Price, Discount;

    public Order() {

    }

    public Order(String ProductId, String ProductName, String Quantity, String Price, String Discount) {

        this.ProductId = ProductId;
        this.ProductName = ProductName;
        this.Quantity = Quantity;
        this.Price = Price;
        this.Discount = Discount;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String ProductId) {
        this.ProductId = ProductId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String ProductName) {
        this.ProductName = ProductName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        this.Quantity = Quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        this.Price = Price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        this.Discount = Discount;
    }

}