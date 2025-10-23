package week7;

import stdlib.StdDraw;

public class RecursiveSquares {
    private static final double RATIO = 2.2;

    public static void drawShadedSquare(double x, double y, double halfLen) {
        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        StdDraw.filledSquare(x, y, halfLen);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.square(x, y, halfLen);
    }

    public static void drawPattern1(int n, double x, double y, double halfLen) {
        if (n == 0) {
            return;
        }
        double childHalfLen = halfLen / RATIO;
        drawPattern1(n - 1, x - halfLen, y - halfLen, childHalfLen);
        drawPattern1(n - 1, x - halfLen, y + halfLen, childHalfLen);
        drawPattern1(n - 1, x + halfLen, y - halfLen, childHalfLen);
        drawPattern1(n - 1, x + halfLen, y + halfLen, childHalfLen);
        drawShadedSquare(x, y, halfLen);
    }

    public static void drawPattern2(int n, double x, double y, double halfLen) {
        if (n == 0) {
            return;
        }
        double childHalfLen = halfLen / RATIO;
        drawPattern2(n - 1, x - halfLen, y + halfLen, childHalfLen);
        drawPattern2(n - 1, x - halfLen, y - halfLen, childHalfLen);
        drawPattern2(n - 1, x + halfLen, y + halfLen, childHalfLen);
        drawShadedSquare(x, y, halfLen);
        drawPattern2(n - 1, x + halfLen, y - halfLen, childHalfLen);
    }

    public static void drawPattern3(int n, double x, double y, double halfLen) {
        if (n == 0) {
            return;
        }
        double childHalfLen = halfLen / RATIO;
        drawShadedSquare(x, y, halfLen);
        drawPattern3(n - 1, x - halfLen, y - halfLen, childHalfLen);
        drawPattern3(n - 1, x - halfLen, y + halfLen, childHalfLen);
        drawPattern3(n - 1, x + halfLen, y - halfLen, childHalfLen);
        drawPattern3(n - 1, x + halfLen, y + halfLen, childHalfLen);
    }

    public static void drawPattern4(int n, double x, double y, double halfLen) {
        if (n == 0) {
            return;
        }
        double childHalfLen = halfLen / RATIO;
        drawPattern4(n - 1, x - halfLen, y + halfLen, childHalfLen);
        drawPattern4(n - 1, x + halfLen, y + halfLen, childHalfLen);
        drawShadedSquare(x, y, halfLen);
        drawPattern4(n - 1, x - halfLen, y - halfLen, childHalfLen);
        drawPattern4(n - 1, x + halfLen, y - halfLen, childHalfLen);
    }

    public static void main(String[] args) {

        StdDraw.setCanvasSize(800, 200);
        StdDraw.setXscale(0, 4);
        StdDraw.setYscale(0, 1);
        StdDraw.enableDoubleBuffering();

        double size = 0.25;
        drawPattern1(4, 0.5, 0.5, size);
        drawPattern2(4, 1.5, 0.5, size);
        drawPattern3(4, 2.5, 0.5, size);
        drawPattern4(4, 3.5, 0.5, size);

        StdDraw.show();
    }
}
