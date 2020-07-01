import { BaseAlgorithm } from './framework/base_algorithm';
import { SimpleMovingAverage } from './framework/simple_moving_average';
import { CBOE } from './framework/cboe';
import { Slice } from './framework/slice';
import { Holding } from './framework/holding';

function round4(n: number): string { 
  return n.toFixed(4);
}

export class RefactorMeAlgorithm extends BaseAlgorithm {
  symbol: string = 'TQQQ';
  movingAverage200: SimpleMovingAverage;
  movingAverage50: SimpleMovingAverage;
  movingAverage21: SimpleMovingAverage;
  movingAverage10: SimpleMovingAverage;
  previousMovingAverage50: number = 0;
  previousMovingAverage21: number = 0;
  previousMovingAverage10: number = 0;
  previousPrice: number = 0;
  previous: Date;
  lastVix: CBOE;
  boughtBelow50: boolean = false;
  tookProfits: boolean = false;

  initialize() {
    this.setStartDate(2010, 3, 23);
    this.setEndDate(2020, 3, 6);

    this.setCash(100000);

    this.movingAverage200 = this.SMA(this.symbol, 200);
    this.movingAverage50 = this.SMA(this.symbol, 50);
    this.movingAverage21 = this.SMA(this.symbol, 21);
    this.movingAverage10 = this.SMA(this.symbol, 10);
  }

  protected onData(data: Slice) {
    if (data.getCBOE('VIX') != null) {
      this.lastVix = data.getCBOE('VIX');
    }
    if (
      (this.previous && this.previous.toISOString().split('T')[0]) ===
      this.getDate().toISOString().split('T')[0]
    ) {
      return;
    }
    if (!this.movingAverage200.isReady) {
      return;
    }

    if (data.get(this.symbol) == null) {
      return;
    }

    if (this.tookProfits) {
      if (data.get(this.symbol).price < this.movingAverage10.value) {
        this.tookProfits = false;
      }
    } else if ((this.portfolio[this.symbol] || Holding.Default).quantity === 0) {
      if (
        data.get(this.symbol).price > this.movingAverage10.value &&
        this.movingAverage10.value > this.movingAverage21.value &&
        this.movingAverage10.value > this.previousMovingAverage10 &&
        this.movingAverage21.value > this.previousMovingAverage21 &&
        this.lastVix.close < 19.0 &&
        !(data.get(this.symbol).price >= (this.movingAverage50.value * 1.15) && data.get(this.symbol).price >= (this.movingAverage200.value * 1.40)) &&
        (data.get(this.symbol).price - this.movingAverage10.value) / this.movingAverage10.value < 0.07
      ) {
        this.log(`Buy ${this.symbol} Vix ${round4(this.lastVix.close)}. above 10 MA ${round4((data.get(this.symbol).price - this.movingAverage10.value) / this.movingAverage10.value)}`);
        const amount = 1.0;
        this.setHoldings(this.symbol, amount);
        this.boughtBelow50 = data.get(this.symbol).price < this.movingAverage50.value;
      }
    } else {
      const change = (data.get(this.symbol).price - this.portfolio[this.symbol].averagePrice) / this.portfolio[this.symbol].averagePrice;

      if (data.get(this.symbol).price < (this.movingAverage50.value * 0.93) && !this.boughtBelow50) {
        this.log(`Sell ${this.symbol} loss of 50 day. Gain ${round4(change)}. Vix ${round4(this.lastVix.close)}`);
        this.liquidate(this.symbol);
      } else {
        if (this.lastVix.close > 22.0) {
          this.log(`Sell ${this.symbol} high volatility. Gain ${round4(change)}. Vix ${round4(this.lastVix.close)}`);
          this.liquidate(this.symbol);
        } else {
          if (this.movingAverage10.value < 0.97 * this.movingAverage21.value) {
            this.log(`Sell ${this.symbol} 10 day below 21 day. Gain ${round4(change)}. Vix ${round4(this.lastVix.close)}`);
            this.liquidate(this.symbol);
          } else {
            if (data.get(this.symbol).price >= (this.movingAverage50.value * 1.15) && data.get(this.symbol).price >= (this.movingAverage200.value * 1.40)) {
              this.log(`Sell ${this.symbol} taking profits. Gain ${round4(change)}. Vix ${round4(this.lastVix.close)}`);
              this.liquidate(this.symbol);
              this.tookProfits = true;
            }
          }
        }
      }
    }

    this.previous = this.getDate();
    this.previousMovingAverage50 = this.movingAverage50.value;
    this.previousMovingAverage21 = this.movingAverage21.value;
    this.previousMovingAverage10 = this.movingAverage10.value;
    this.previousPrice = data.get(this.symbol).price;
  }
}