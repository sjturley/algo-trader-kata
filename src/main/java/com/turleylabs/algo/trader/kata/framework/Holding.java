package com.turleylabs.algo.trader.kata.framework;

public class Holding {
    public static Holding Default = new Holding(0, 0);
    private final int quantity;
    private double averagePrice;

    public Holding(double averagePrice, int quantity) {
        this.averagePrice = averagePrice;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getAveragePrice() {
        return averagePrice;
    }
}
