package week11;

import stdlib.*;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Chaos {
    public static void main(String[] args) {
        final Double R = 3.2;
        ArrayDeque<Double> populations = new ArrayDeque<>();
        for (int i = 0; i < 100; i++) {
            populations.add(0.01);
        }
        StdDraw.enableDoubleBuffering();
        while (true) {
            StdDraw.clear();
            var currentPopulation = populations.getLast();
            var nextPopulation = R * currentPopulation * (1 - currentPopulation);
            nextPopulation = Math.clamp(nextPopulation, 0, 1);
            populations.addLast(nextPopulation);
            populations.removeFirst();
            double last = -1.0;
            for (int i = 0; i < populations.size(); i++) {
                double p = (double) populations.toArray()[i];
                if (last != -1.0) {
                    StdDraw.line((double) (i - 1) /100, last, (double) i /100, p);
                }
                last = p;
            }
            StdDraw.show();
            StdDraw.pause(200);
        }


    }
}
