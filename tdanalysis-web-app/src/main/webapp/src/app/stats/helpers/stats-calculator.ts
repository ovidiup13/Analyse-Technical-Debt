export class StatsCalculator {
  static getMean(data: number[]): number {
    return data.reduce((n1, n2) => n1 + n2, 0) / data.length;
  }

  static getStandardDeviation(mean: number, data: number[]): number {
    const variance = data.reduce(
      (previous, current) => Math.pow(mean - current, 2) + previous,
      0
    );
    return Math.sqrt(variance / data.length);
  }
}
