package com.turleylabs.algo.trader.kata.framework;

import java.time.LocalDate;

public class Trade {
    private String symbol;
    private final LocalDate currentDate;
    private final int shares;
    private final double averagePrice;
    private final int direction;

    public Trade(String symbol, LocalDate date, int shares, double averagePrice, int direction) {
        this.symbol = symbol;
        this.currentDate = date;
        this.shares = shares;
        this.averagePrice = averagePrice;
        this.direction = direction;
    }

    public LocalDate getDate() {
        return currentDate;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public int getNumberOfShares() {
        return shares;
    }

    public String getSymbol() {
        return symbol;
    }
}
