import { SimpleMovingAverage } from './simple_moving_average';
import { Holding } from './holding';
import { Trade } from './trade';
import { Slice } from './slice';

export abstract class BaseAlgorithm {
  private movingAverages: SimpleMovingAverage[] = [];
  private currentDate: Date;
  private cash: number;
  private startDate: Date;
  private endDate: Date;
  protected portfolio: { [key: string]: Holding } = {};
  protected trades: Trade[] = [];
  private currentSlice: Slice;

  protected abstract initialize();

  private processData(data: Slice) {
    this.currentDate = data.date;
    this.currentSlice = data;

    for (const movingAverage of this.movingAverages) {
      if (data.containsKey(movingAverage.symbol)) {
        const bar = data.get(movingAverage.symbol);
        if (bar != null) {
          movingAverage.addData(bar.price);
        }
      }
    }

    this.onData(data);
  }

  protected abstract onData(data: Slice);

  protected setStartDate(i: number, i1: number, i2: number) {
    this.startDate = new Date(Date.UTC(i, i1 - 1, i2));
  }

  protected setEndDate(i: number, i1: number, i2: number) {
    this.endDate = new Date(Date.UTC(i, i1 -1, i2));
  }

  protected setCash(cash: number) {
    this.cash = cash;
  }

  getCash(): number {
    return this.cash;
  }

  protected getDate(): Date {
    return this.currentDate;
  }

  protected SMA(symbol: string, i: number): SimpleMovingAverage {
    const movingAverage = new SimpleMovingAverage(symbol, i);
    this.movingAverages.push(movingAverage);
    return movingAverage;
  }

  protected log(log: string) {
    console.log(`${this.currentDate.toISOString().split('T')[0]} - ${log}`);
  }

  protected setHoldings(symbol: string, amount: number) {
    const averagePrice = this.currentSlice.get(symbol).price;
    const shares = Math.floor(amount * this.getCash() / averagePrice);
    const trade = new Trade(symbol, this.currentDate, shares, averagePrice, 1);
    this.trades.push(trade);
    this.portfolio[symbol] = new Holding(averagePrice, shares);
    this.cash -= (averagePrice * shares);
  }

  protected liquidate(symbol: string) {
    const holding = this.portfolio[symbol];
    const shares = holding.quantity;
    const currentPrice = this.currentSlice.get(symbol).price;
    const trade = new Trade(symbol, this.currentDate, shares, currentPrice, -1);
    this.trades.push(trade);
    delete this.portfolio[symbol];
    this.cash += currentPrice * shares;
  }

  run() {
    this.initialize();
    BaseAlgorithm.datesUntil(this.startDate, this.endDate).forEach(date => {
      this.processData(new Slice(date, 'VIX'));
      this.processData(new Slice(date, 'TQQQ'));
    });
  }
  
  static datesUntil(start: Date, end: Date): Date[] {
    const dates = [];
    let currentDate = new Date(start.getTime());
    while (currentDate.toISOString().split('T')[0] != end.toISOString().split('T')[0]) {
      dates.push(new Date(currentDate.getTime()));
      currentDate.setUTCDate(currentDate.getUTCDate() + 1);
    }
    return dates;
  }
}