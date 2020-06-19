export class Holding {
  static Default: Holding = new Holding(0, 0);
  constructor(readonly averagePrice: number, readonly quantity: number) {}
}
