package week3;
// name: Kirsten Pleskot
// 1.3.24

public class GamblerPlot {
    public static void main(String[] args) { // Run trials experiments that start with
// $stake and terminate on $0 or $goal.
        int stake = Integer.parseInt(args[0]);
        int goal = Integer.parseInt(args[1]);
        int trials = Integer.parseInt(args[2]);
        int bets = 0;
        int wins = 0;
        for (int t = 0; t < trials; t++) { // Run one experiment.
            int cash = stake;
            System.out.printf("Trial %d\n", t);
            while (cash > 0 && cash < goal) { // Simulate one bet.
                bets++;
                if (Math.random() < 0.5) cash++;
                else cash--;
                // print out * cash times after each bet
                System.out.println("*".repeat(cash));
            } // Cash is either 0 (ruin) or $goal (win).
            if (cash == goal) wins++;
        }
        System.out.println(100*wins/trials + "% wins");
        System.out.println("Avg # bets: " + bets/trials);
    }
}