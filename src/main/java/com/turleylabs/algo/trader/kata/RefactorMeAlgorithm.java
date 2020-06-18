package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.framework.*;

import java.time.LocalDate;
import java.util.Map;

public class RefactorMeAlgorithm extends BaseAlgorithm {

    String symbol = "TQQQ";
    SimpleMovingAverage movingAverage200;
    SimpleMovingAverage movingAverage50;
    SimpleMovingAverage movingAverage21;
    SimpleMovingAverage movingAverage10;
    double previousMovingAverage50;
    double previousMovingAverage21;
    double previousMovingAverage10;
    double previousPrice;
    LocalDate previous;
    CBOE lastVix;
    boolean boughtBelow50;
    boolean tookProfits;

    public void initialize() {
        this.setStartDate(2010, 3, 23);  //Set Start Date
        this.setEndDate(2020, 03, 06);

        this.setCash(100000);             //Set Strategy Cash

        movingAverage200 = this.SMA(symbol, 200);
        movingAverage50 = this.SMA(symbol, 50);
        movingAverage21 = this.SMA(symbol, 21);
        movingAverage10 = this.SMA(symbol, 10);

    }

    protected void onData(Slice data) {
        if (data.getCBOE("VIX") != null) {
            lastVix = data.getCBOE("VIX");
        }
        if (previous == getDate()) return;

        if (!movingAverage200.isReady()) return;

        if (data.get(symbol) == null) {
            return;
        }
        if (tookProfits) {
            if (data.get(symbol).getPrice() < movingAverage10.getValue()) {
                tookProfits = false;
            }
        } else if (portfolio.getOrDefault(symbol, Holding.Default).getQuantity() == 0) {

            if (data.get(symbol).getPrice() > movingAverage10.getValue()
                    && movingAverage10.getValue() > movingAverage21.getValue()
                    && movingAverage10.getValue() > previousMovingAverage10
                    && movingAverage21.getValue() > previousMovingAverage21
                    && (double) (lastVix.getClose()) < 19.0
                    && !(data.get(symbol).getPrice() >= (movingAverage50.getValue() * 1.15) && data.get(symbol).getPrice() >= (movingAverage200.getValue() * 1.40))
                    && (data.get(symbol).getPrice() - movingAverage10.getValue()) / movingAverage10.getValue() < 0.07) {
                this.log(String.format("Buy %s Vix %.4f. above 10 MA %.4f", symbol, lastVix.getClose(), (data.get(symbol).getPrice() - movingAverage10.getValue()) / movingAverage10.getValue()));
                double amount = 1.0;
                this.setHoldings(symbol, amount);

                boughtBelow50 = data.get(symbol).getPrice() < movingAverage50.getValue();
            }
        } else {
            double change = (data.get(symbol).getPrice() - portfolio.get(symbol).getAveragePrice()) / portfolio.get(symbol).getAveragePrice();

            if (data.get(symbol).getPrice() < (movingAverage50.getValue() * .93) && !boughtBelow50) {
                this.log(String.format("Sell %s loss of 50 day. Gain %.4f. Vix %.4f", symbol, change, lastVix.getClose()));
                this.liquidate(symbol);
            } else {
                if ((double) (lastVix.getClose()) > 22.0) {
                    this.log(String.format("Sell %s high volatility. Gain %.4f. Vix %.4f", symbol, change, lastVix.getClose()));
                    this.liquidate(symbol);
                } else {
                    if (movingAverage10.getValue() < 0.97 * movingAverage21.getValue()) {
                        this.log(String.format("Sell %s 10 day below 21 day. Gain %.4f. Vix %.4f", symbol, change, lastVix.getClose()));
                        this.liquidate(symbol);
                    } else {
                        if (data.get(symbol).getPrice() >= (movingAverage50.getValue() * 1.15) && data.get(symbol).getPrice() >= (movingAverage200.getValue() * 1.40)) {
                            this.log(String.format("Sell %s taking profits. Gain %.4f. Vix %.4f", symbol, change, lastVix.getClose()));
                            this.liquidate(symbol);
                            tookProfits = true;
                        }
                    }
                }
            }
        }

        previous = getDate();
        previousMovingAverage50 = movingAverage50.getValue();
        previousMovingAverage21 = movingAverage21.getValue();
        previousMovingAverage10 = movingAverage10.getValue();
        previousPrice = data.get(symbol).getPrice();
    }

    @Override
    public String toString() {
        String approveString = portfolioToString();
        approveString += tradesToString();
        return approveString;
    }

    private String tradesToString() {
        return "Trades{\n" +
                trades.stream().map(this::tradeToString).reduce("", (left, right) -> left + right + "\n") +
                "}\n";
    }

    private String tradeToString(Trade trade) {
        return "[Date=" + trade.getDate() + ", Ticker=" + trade.getSymbol() + ", Quantity=" + trade.getNumberOfShares() + ", Price=" + trade.getAveragePrice() + "]";
    }

    public String portfolioToString() {
        return "Portfolio{\n" +
                portfolio.entrySet().stream().map(this::holdingToString).reduce("", (left, right) -> left + right + "\n") +
                "}\n";
    }

    private String holdingToString(Map.Entry<String, Holding> entry) {
        return "[Ticker=" + entry.getKey() + ", Quantity=" + entry.getValue().getQuantity() + ", Price=" +  entry.getValue().getAveragePrice() + "]";
    }

}
