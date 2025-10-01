package week5;

public class RandomCircles {
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        double black_prob = Double.parseDouble(args[1]);
        double min_radius = Double.parseDouble(args[2]);
        double max_radius = Double.parseDouble(args[3]);
        // Draw
        for (int i = 0; i < n; i++) {
            double x = Math.random();
            double y = Math.random();
            double r = min_radius + (max_radius - min_radius) * Math.random();
            if (Math.random() < black_prob)
                stdlib.StdDraw.setPenColor(stdlib.StdDraw.BLACK);
            else
                stdlib.StdDraw.setPenColor(stdlib.StdDraw.WHITE);
            stdlib.StdDraw.filledCircle(x, y, r);
        }
    }
}
