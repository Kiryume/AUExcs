package week2;
// name: Kirsten Pleskot

public class WindChill {
    public static void main(String[] args) {
        // retrieve the command line arguments and parse them into doubles using the static method of class Double
        var T = Double.parseDouble(args[0]);
        var v = Double.parseDouble(args[1]);
        // calculate the wind chill temperature using provided formula
        var wc = 35.74d + 0.6215*T + (0.4275*T - 35.75) * Math.pow(v, 0.16);
        // print the temp out
        System.out.printf("Wind chill: %f\n", wc);
    }
}
