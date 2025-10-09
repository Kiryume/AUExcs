package week6;
import java.util.Arrays;

public class Histogram {
    public static int[] histogram(int[] a, int m) {
        // generate array which for which every value of a[] is valid index
        int[] frequencies = new int[m];
        for (int value : a) {
            if (value >= 0 && value < m) {
                frequencies[value]++;
            } else {
                throw new IllegalArgumentException("Value out of range: " + value);
            }
        }
        return frequencies;
    }

    public static void main(String[] args) {
        int n = 100000;
        int m = 50;   
        double mean = m / 2.0; 
        // select standard deviation for which the graph looks pretty and doesn't generate too many outliers
        // too many outliers would cause to big bars on the sides
        double stdDev = m / 8.0;

        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            // generate numbers with gaussian distribution
            int value = (int) Math.round(stdlib.StdRandom.gaussian(mean, stdDev));
            // clamp outliers
            value = Math.clamp(value, 0, m - 1);
            data[i] = value;
        }
        int[] histData = histogram(data, m);

        int sum = Arrays.stream(histData).sum();
        System.out.println("Sum of histogram frequencies: " + sum + " (should be " + n + ")");
        // crying my eyes out as a rust programmer
        double[] plotData = Arrays.stream(histData).asDoubleStream().toArray();


        
        // set the Y scale to fit the whole histogram
        stdlib.StdDraw.setYscale(0, Arrays.stream(histData).max().orElse(1) * 1.1);
        stdlib.StdStats.plotBars(plotData);
    }
}
