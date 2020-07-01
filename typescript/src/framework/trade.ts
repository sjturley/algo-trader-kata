export class Trade {
  constructor(
    readonly symbol: string,
    readonly date: Date,
    readonly shares: number,
    readonly averagePrice: number,
    readonly direction: number
  ) {}
}
