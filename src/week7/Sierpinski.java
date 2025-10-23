package week7;

import stdlib.StdDraw;

public class Sierpinski {
    public static void draw(int n, double x1, double y1, double x2, double y2, double x3, double y3) {
        if (n == 0) {
            return;
        }
        double m12x = (x1 + x2) / 2.0;
        double m12y = (y1 + y2) / 2.0;
        double m23x = (x2 + x3) / 2.0;
        double m23y = (y2 + y3) / 2.0;
        double m31x = (x3 + x1) / 2.0;
        double m31y = (y3 + y1) / 2.0;

        double[] x = {m12x, m23x, m31x};
        double[] y = {m12y, m23y, m31y};
        StdDraw.polygon(x, y);

        draw(n - 1, x1, y1, m12x, m12y, m31x, m31y);
        draw(n - 1, m12x, m12y, x2, y2, m23x, m23y);
        draw(n - 1, m31x, m31y, m23x, m23y, x3, y3);
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Please provide a recursion depth 'n' as a command-line argument.");
            System.out.println("Example: java Sierpinski 5");
            return;
        }
        int n = Integer.parseInt(args[0]);

        StdDraw.enableDoubleBuffering();

        double height = Math.sqrt(3.0) / 2.0;
        double x1 = 0.0, y1 = 0.0;
        double x2 = 1.0, y2 = 0.0;
        double x3 = 0.5, y3 = height;
        double[] x = {x1, x2, x3};
        double[] y = {y1, y2, y3};
        StdDraw.polygon(x, y);
        draw(n, x1, y1, x2, y2, x3, y3);
        StdDraw.show();
    }
}
