package week5;
// name: Kirsten Pleskot


public class RandomCircles {
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        double black_prob = Double.parseDouble(args[1]);
        double min_radius = Double.parseDouble(args[2]);
        double max_radius = Double.parseDouble(args[3]);
        for (int i = 0; i < n; i++) {
            // get random x y coordinates. Since stddraw operates on unit square we don't need to do any manipulation
            double x = Math.random();
            double y = Math.random();
            // get radius between the set bounds
            double r = min_radius + (max_radius - min_radius) * Math.random();
            if (Math.random() < black_prob)
                stdlib.StdDraw.setPenColor(stdlib.StdDraw.BLACK);
            else
                stdlib.StdDraw.setPenColor(stdlib.StdDraw.WHITE);
            stdlib.StdDraw.filledCircle(x, y, r);
        }
    }
}
