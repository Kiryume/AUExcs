package week7;
// name: Kisten Pleskot


import stdlib.StdDraw;

public class RecursiveSquares {
    private static final double RATIO = 2.2;

    // draw the square according to the specified style
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
        // draw the parent square on top of the children
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
        // draw the parent square below the bottom right child and on top of the rest
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
        // draw the parent square below all children
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
        // draw the parent square between the top and bottom children
        drawPattern4(n - 1, x - halfLen, y + halfLen, childHalfLen);
        drawPattern4(n - 1, x + halfLen, y + halfLen, childHalfLen);
        drawShadedSquare(x, y, halfLen);
        drawPattern4(n - 1, x - halfLen, y - halfLen, childHalfLen);
        drawPattern4(n - 1, x + halfLen, y - halfLen, childHalfLen);
    }

    public static void main(String[] args) {
        // setup canvas with nice values to draw all four patterns side by side
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
