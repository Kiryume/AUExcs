package week7;

import stdlib.StdDraw;

public class Heart {

    public static void main(String[] args) {
        StdDraw.setXscale(-20, 20);
        StdDraw.setYscale(-20, 15);

        StdDraw.setPenColor(StdDraw.RED);

        int N = 200;
        double[] x = new double[N + 1];
        double[] y = new double[N + 1];

        for (int i = 0; i <= N; i++) {
            double t = -Math.PI + i * (2 * Math.PI / N);

            x[i] = 16 * Math.pow(Math.sin(t), 3);
            y[i] = 13 * Math.cos(t) - 5 * Math.cos(2 * t) - 2 * Math.cos(3 * t) - Math.cos(4 * t);
        }

        StdDraw.filledPolygon(x, y);
    }
}