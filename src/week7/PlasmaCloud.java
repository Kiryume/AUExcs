package week7;
// name: Kisten Pleskot


import stdlib.StdDraw;
import stdlib.StdRandom;

import java.awt.Color;

public class PlasmaCloud {
    private static Color getColor(double v) {
        // clamp v to [0,1]
        v = Math.max(0, Math.min(1, v));
        int c = (int) (v * 255);
        return new Color(c, c, 255);
    }

    public static void plasma(double x, double y, double size, double c00, double c10, double c01, double c11, double var,  double s) {
        if (size <= 0.001) {
            // average the corner colors
            double c = (c00 + c10 + c01 + c11) / 4.0;
            StdDraw.setPenColor(getColor(c));
            StdDraw.filledSquare(x, y, size);
            return;
        }

        // calculate center color with displacement
        double displacement = StdRandom.gaussian(0, Math.sqrt(var));
        double cc = (c00 + c10 + c01 + c11) / 4.0 + displacement;

        // calculate new corner colors
        double c0010 = (c00 + c10) / 2.0;
        double c0111 = (c01 + c11) / 2.0;
        double c0001 = (c00 + c01) / 2.0;
        double c1011 = (c10 + c11) / 2.0;

        size = size / 2.0;
        var = var / s;
        // move square positions and recurse
        plasma(x - size, y + size, size, c00, c0010, c0001, cc, var, s);
        plasma(x + size, y + size, size, c0010, c10, cc, c1011, var, s);
        plasma(x - size, y - size, size, c0001, cc, c01, c0111, var, s);
        plasma(x + size, y - size, size, cc, c1011, c0111, c11, var, s);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java PlasmaCloud <hurst>");
            return;
        }
        // calculate s from hurst
        double hurst = Double.parseDouble(args[0]);
        double s = Math.pow(2, 2 * hurst);

        // Initial corner colors
        double c00 = 0.5;
        double c10 = 0.5;
        double c01 = 0.5;
        double c11 = 0.5;

        // variance which produces pretty good results
        double var = 0.25;

        // make the drawing not comically slow
        StdDraw.enableDoubleBuffering();

        plasma(0.5, 0.5, 0.5, c00, c10, c01, c11, var, s);

        StdDraw.show();
    }
}

