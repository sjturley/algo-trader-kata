import { readFileSync } from 'fs';
import { join } from 'path';
import { CBOE } from './cboe';
import { Bar } from './bar';

export class Slice {
  constructor(readonly date: Date, private symbol: string) {
  }

  containsKey(symbol: String): boolean {
    return symbol === this.symbol;
  }

  getCBOE(symbol: string): CBOE {
    if (!this.containsKey(symbol)) {
      return null;
    }
    return this.findCBOE();
  }

  get(symbol: string): Bar {
    if (!this.containsKey(symbol)) {
      return null;
    }
    return this.findBar(symbol);
  }

  private findCBOE(): CBOE {
    try {
      const fileContents = readFileSync(join(__dirname, '..', '..', 'data', 'VIX.csv'), { encoding: 'utf8'});
      const lines = fileContents.split('\n');
      lines.splice(0, 1);
      for (const line of lines) {
        const values = line.split(',');
        const date = new Date(Date.parse(values[0]));
        if (
          date.toISOString().split('T')[0] ===
          this.date.toISOString().split('T')[0]
        ) {
          return new CBOE(parseFloat(values[5]));
        }
      }
    } catch (e) {
      console.error(e);
    }
  
    return null;
  }

  private findBar(symbol: string): Bar {
    try {
      const fileContents = readFileSync(join(__dirname, '..', '..', 'data', `${symbol}.csv`), { encoding: 'utf8'});
      const lines = fileContents.split('\n');
      lines.splice(0, 1);
      for (const line of lines) {
        const values = line.split(',');
        const date = new Date(Date.parse(values[0]));
        if (
          date.toISOString().split('T')[0] === 
          this.date.toISOString().split('T')[0]
        ) {
          return new Bar(parseFloat(values[5]));
        }
      }
    } catch (e) {
      console.error(e);
    }
    return null;
  }
}