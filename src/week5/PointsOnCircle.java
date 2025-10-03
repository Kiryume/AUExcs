package week5;

import java.util.Arrays;

public class PointsOnCircle {
    public static void main(String[] args) throws InterruptedException {
        int n = Integer.parseInt(args[0]);
        double line_chance = Double.parseDouble(args[1]);
        boolean animate = args.length > 2 && args[2].equals("sleep");
        // use radians to draw the points evenly spaced out on a circle
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = 0.5 + 0.45 * Math.cos(angle);
            double y = 0.5 + 0.45 * Math.sin(angle);
            stdlib.StdDraw.filledCircle(x, y, 0.01);
        }
        // I liked how the drawing looked so I added this code
        int[] order = new int[n];
        for (int i = 0; i < n; i++)
            order[i] = i;
        if (animate) {
            // divide into 4 segments and interleave them
            int seg = n / 4;
            int[] new_order = new int[n];
            for (int i = 0; i < seg; i++) {
                new_order[4 * i] = order[i];
                new_order[4 * i + 1] = order[i + seg];
                new_order[4 * i + 2] = order[i + 2 * seg];
                new_order[4 * i + 3] = order[i + 3 * seg];
            }
            if (n % 4 >= 1) new_order[n - 1] = order[n - 1];
            if (n % 4 >= 2) new_order[n - 2] = order[n - 2];
            if (n % 4 == 3) new_order[n - 3] = order[n - 3];
            order = new_order;
        }
        // this would normally look like for (int i = 0; i < n; i++)
        for (int i : order) {
            // draw only between points after current point so we don't overdraw
            for (int j = i + 1; j < n; j++) {
                if (Math.random() < line_chance) {
                    double angle1 = 2 * Math.PI * i / n;
                    double x1 = 0.5 + 0.45 * Math.cos(angle1);
                    double y1 = 0.5 + 0.45 * Math.sin(angle1);
                    double angle2 = 2 * Math.PI * j / n;
                    double x2 = 0.5 + 0.45 * Math.cos(angle2);
                    double y2 = 0.5 + 0.45 * Math.sin(angle2);
                    if (animate) Thread.sleep(600/n);
                    stdlib.StdDraw.line(x1, y1, x2, y2);
                }
            }
        }
    }
}
