package week11;
// name: Kirsten Pleskot

import stdlib.StdDraw;
import java.awt.Color;


public class RandomWalkers {
    static int getSize(int n) {
        int base = 1000;
        if (n <= base) return n * (base / n);
        else return base;
    }

    private static class StepResult {
        final int deltaVisited;
        final int newMaxVisits;

        StepResult(int deltaVisited, int newMaxVisits) {
            this.deltaVisited = deltaVisited;
            this.newMaxVisits = newMaxVisits;
        }
    }

    public static class Walker {
        int x;
        int y;

        Walker(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static void initWalkersAtCenter(int n, Walker[] walkers, int cx, int cy) {
        for (int i = 0; i < n; i++) {
            walkers[i] = new Walker(cx, cy);
        }
    }


    private static StepResult stepAllWalkers(int n, Walker[] walkers, int[][] visits, int currentMax) {
        int newlyVisited = 0;
        int maxVisits = currentMax;

        for (int i = 0; i < n; i++) {
            int x = walkers[i].x;
            int y = walkers[i].y;

            int[] dx = new int[4];
            int[] dy = new int[4];
            int m = 0;
            if (x + 1 < n) { dx[m] = 1; dy[m] = 0; m++; }
            if (x - 1 >= 0) { dx[m] = -1; dy[m] = 0; m++; }
            if (y + 1 < n) { dx[m] = 0; dy[m] = 1; m++; }
            if (y - 1 >= 0) { dx[m] = 0; dy[m] = -1; m++; }

            int choice = (int) (Math.random() * m);
            x += dx[choice];
            y += dy[choice];

            walkers[i].x = x;
            walkers[i].y = y;

            if (visits[x][y] == 0) newlyVisited++;
            visits[x][y]++;
            if (visits[x][y] > maxVisits) maxVisits = visits[x][y];
        }
        return new StepResult(newlyVisited, maxVisits);
    }

    // Draw the grid of visit intensities
    private static void drawGrid(int n, int[][] visits, int maxVisits) {
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                int v = visits[x][y];
                if (v == 0) {
                    StdDraw.setPenColor(32, 32, 48); // dark unvisited
                } else {
                    float intensity = (float) v / (float) maxVisits; // 0..1
                    float hue = 0.66f * (1.0f - intensity);
                    float sat = 0.6f + 0.4f * intensity;
                    float bri = 0.3f + 0.7f * intensity;
                    StdDraw.setPenColor(Color.getHSBColor(hue, sat, bri));
                }
                StdDraw.filledSquare(x + 0.5, y + 0.5, 0.5);
            }
        }
    }

    private static void drawWalkersIfLargeEnough(int n, int size, Walker[] walkers) {
        if (n * 3 >= size) return;
        for (int i = 0; i < n; i++) {
            StdDraw.setPenColor(Color.WHITE);
            // These values just work
            double r = 0.5 / Math.sqrt(n);
            StdDraw.filledCircle(walkers[i].x + 0.5, walkers[i].y + 0.5, r);
        }
    }

    private static void renderFrame(int n, int size, int[][] visits, int maxVisits, int visitedCells,
                                    Walker[] walkers, int delay) {
        StdDraw.clear();
        drawGrid(n, visits, maxVisits);
        drawWalkersIfLargeEnough(n, size, walkers);
        StdDraw.show();
        StdDraw.pause(delay);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java RandomWalkers <n>");
            return;
        }
        int n = Integer.parseInt(args[0]);
        if (n <= 0) {
            System.out.println("n must be a positive integer.");
            return;
        }


        int size = getSize(n);
        StdDraw.setCanvasSize(size, size);
        StdDraw.setXscale(0, n);
        StdDraw.setYscale(0, n);
        StdDraw.enableDoubleBuffering();

        int[][] visits = new int[n][n];
        int visitedCells = 0;

        int cx = n / 2;
        int cy = n / 2;
        Walker[] walkers = new Walker[n];
        initWalkersAtCenter(n, walkers, cx, cy);
        visits[cx][cy] = n;
        visitedCells = 1;

        long steps = 0;
        int maxVisits = visits[cx][cy];

        int drawEvery = (n <= 100) ? 1 : (int)Math.clamp(Math.pow(2, n/100), 64, 1000);
        int delay = (size <= 800) ? 1000 / 60 : 1000 / 30;

        while (visitedCells < n * n) {
            StepResult res = stepAllWalkers(n, walkers, visits, maxVisits);
            visitedCells += res.deltaVisited;
            maxVisits = res.newMaxVisits;
            steps++;

            if (steps % drawEvery == 0 || visitedCells == n * n) {
                renderFrame(n, size, visits, maxVisits, visitedCells, walkers, delay);
            }
        }

        System.out.printf("Completed coverage of %dx%d grid with %d walkers in %d steps.%n", n, n, n, steps);
    }
}
