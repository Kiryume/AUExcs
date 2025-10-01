package week3;
// name: Kirsten Pleskot
// 1.3.5

import java.util.Random;

public class RollLoadedDie {
    public static void main(String[] args) {
        // acquire random number between 1 and 8 (random nextInt gives value between 0 inclusive and bound exclusive)
        int r = new Random().nextInt(8) + 1;

        // if the number is smaller or equal then five return that number
        if (r <= 5) {
            System.out.printf("Rolled %d\n", r);
        } else {
            // for the cases 6, 7 and 8 print "Rolled 6"
            System.out.println("Rolled 6");
        }

    }
}
