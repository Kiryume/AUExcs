package week3;
// name: Kirsten Pleskot
// 1.3.25

public class Gambler {
    public static void main(String[] args) { // Run trials experiments that start with
// $stake and terminate on $0 or $goal.
        int stake = Integer.parseInt(args[0]);
        int goal = Integer.parseInt(args[1]);
        int trials = Integer.parseInt(args[2]);
        double chance = Double.parseDouble(args[3]);
        int bets = 0;
        int wins = 0;
        for (int t = 0; t < trials; t++) { // Run one experiment.
            int cash = stake;
            while (cash > 0 && cash < goal) { // Simulate one bet.
                bets++;
                if (Math.random() < chance) cash++;
                else cash--;
            } // Cash is either 0 (ruin) or $goal (win).
            if (cash == goal) wins++;
        }
        System.out.println(100*(double)wins/trials + "% wins");
        System.out.println("Avg # bets: " + bets/trials);
    }
    // the uniform distribution of generated random numbers guarantees that number smaller than `chance` will be generated
    // only `chance`*100 percent times, since if we set chance to .2 number between 0 and .2 will be generated only 20% time in 0 to 1 range
    // java Gambler.java 50 100 10000 0.5:
    //      this will produce the result of approx 50% winrate and 2500 bets
    // java Gambler.java 50 100 10000 0.52
    //      this will produce the result of approx 98% winrate and 1200 bets
    // java Gambler.java 50 100 10000 0.48
    //      this will produce the result of approx 2% winrate and 1200 bets
    // this is because just the small change changes where the expected value after 50 throws is
    // if we change the stake to 500 and goal to 1000 the probability of loosing or winning enough times is so small the outputted winrate will be 100 and 0 percent respectively

}
