export class SimpleMovingAverage {
  private buffer: number[] = [];
  constructor(readonly symbol: string, private length: number) {
  }

  get isReady(): boolean {
    return this.buffer.length === this.length;
  }

  get value(): number {
    const sum = this.buffer.reduce((a, b) => a + b, 0);
    const numElements = this.buffer.length;
    if (numElements === 0) {
      throw new Error('Average of empty list is undefined');
    }
    return sum / numElements;
  }

  addData(data: number) {
    this.buffer.push(data);
    if (this.buffer.length > this.length) {
      this.buffer.splice(0, 1);
    }
  }
}