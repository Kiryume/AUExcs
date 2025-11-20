package week11;
// name: Kirsten Pleskot

import stdlib.*;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Iterator;

// r < 1: population goes to 0
// 1 < r < 3: population goes to a fixed point
// 3 < r < ~3.5: population oscillates between finite set of values
// ~3.5 < r <= 4: chaotic behavior
// r > 4: explosion and extinction

public class Chaos {
    static class PopulationSeries {
        private final double r;
        private final Color color;
        private final String label;
        private final ArrayDeque<Double> history;
        private final int historyLen;

        PopulationSeries(double r, Color color, String label, int historyLen, double initial) {
            this.r = r;
            this.color = color;
            this.label = label;
            this.historyLen = historyLen;
            this.history = new ArrayDeque<>(this.historyLen);
            for (int i = 0; i < this.historyLen; i++) {
                history.add(initial);
            }
        }


        void step() {
            double current = history.getLast();
            double next = r * current * (1 - current);
            next = Math.clamp(next, 0.0, 1.0);
            history.addLast(next);
            history.removeFirst();
        }

        void draw() {
            StdDraw.setPenColor(color);
            int n = history.size();
            if (n < 2) return;

            Iterator<Double> it = history.iterator();
            double prevY = it.next();
            int i = 1;
            while (it.hasNext()) {
                double y = it.next();
                double x1 = (double) (i - 1) / (n - 1);
                double x2 = (double) i / (n - 1);
                StdDraw.line(x1, prevY, x2, y);
                prevY = y;
                i++;
            }
        }

        void drawLegendText(double x, double y) {
            StdDraw.setPenColor(color);
            StdDraw.textLeft(x, y, label);
        }
    }

    public static void main(String[] args) {
        final PopulationSeries sExt = new PopulationSeries(0.9, StdDraw.GRAY, "r<1 (extinction) = 0.9", 200, 0.01);
        final PopulationSeries sFix = new PopulationSeries(2.5, StdDraw.GREEN, "1<r<3 (fixed) = 2.5", 200, 0.01);
        final PopulationSeries sPer = new PopulationSeries(3.3, StdDraw.ORANGE, "3<r<~3.5 (periodic) = 3.3", 200, 0.01);
        final PopulationSeries sCh = new PopulationSeries(3.0, StdDraw.RED, ">~3.5 (chaotic) = 3.9", 200, 0.01);

        StdDraw.setScale(0.0, 1.0);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenRadius(0.002);

        while (true) {
            StdDraw.clear();

            // Step and draw all series
            sExt.step();
            sFix.step();
            sPer.step();
            sCh.step();
            sExt.draw();
            sFix.draw();
            sPer.draw();
            sCh.draw();

            // Legend
            double y = 0.97;
            double dy = 0.04;
            sExt.drawLegendText(0.02, y);
            y -= dy;
            sFix.drawLegendText(0.02, y);
            y -= dy;
            sPer.drawLegendText(0.02, y);
            y -= dy;
            sCh.drawLegendText(0.02, y);

            StdDraw.show();
            StdDraw.pause(60);
        }
    }
}
