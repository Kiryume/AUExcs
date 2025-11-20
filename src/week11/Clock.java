package week11;
// name: Kirsten Pleskot

import stdlib.StdDraw;
import java.time.LocalTime;

public class Clock {
    public static void main(String[] args) {
        StdDraw.setScale(-1, 1);
        StdDraw.enableDoubleBuffering();

        while (true) {
            StdDraw.clear();
            drawClockFace();
            drawHands();
            StdDraw.show();
            // run roughly 60 times per second
            StdDraw.pause(1000 / 60);
        }
    }

    public static void drawClockFace() {
        StdDraw.setPenRadius(0.005);
        StdDraw.circle(0, 0, 1.0);

        // draw hours
        StdDraw.setPenRadius(0.03);
        for (int h = 0; h < 12; h++) {
            double angle = Math.toRadians(90 - h * 30);
            double x = Math.cos(angle);
            double y = Math.sin(angle);
            StdDraw.point(x * 0.85, y * 0.85);
        }
        StdDraw.setPenRadius();

        // draw minutes
        for (int m = 0; m < 60; m++) {
            if (m % 5 == 0) continue;
            double angle = Math.toRadians(90 - m * 6);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double r1 = 0.92;
            double r2 = 0.88;
            StdDraw.line(cos * r1, sin * r1, cos * r2, sin * r2);
        }

        // draw numbers
        for (int h = 1; h <= 12; h++) {
            double angle = Math.toRadians(90 - h * 30);
            double r = 0.72;
            double x = Math.cos(angle) * r;
            double y = Math.sin(angle) * r;
            StdDraw.text(x, y, Integer.toString(h));
        }
    }

    public static void drawHands() {
        // get current time
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        // nano for smooth second hand movement
        int nano = now.getNano();

        double secondAngleDeg = 90 - (second + nano / 1_000_000_000.0) * 6.0;
        double minuteAngleDeg = 90 - (minute + second / 60.0) * 6.0;
        double hourAngleDeg = 90 - ((hour % 12) + minute / 60.0 + second / 3600.0) * 30.0;

        StdDraw.setPenRadius(0.02);
        drawHand(hourAngleDeg, 0.5);

        StdDraw.setPenRadius(0.01);
        drawHand(minuteAngleDeg, 0.75);

        StdDraw.setPenRadius(0.005);
        drawHand(secondAngleDeg, 0.9);
    }

    private static void drawHand(double angleDeg, double length) {
        double angleRad = Math.toRadians(angleDeg);
        double x = Math.cos(angleRad) * length;
        double y = Math.sin(angleRad) * length;
        StdDraw.line(0, 0, x, y);
    }
}
