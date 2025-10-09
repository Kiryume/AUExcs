package week6;
// name: Kisten Pleskot

import java.util.Arrays;

public class PokerAnalysis {

    private static final int RANKS = 13;
    private static final int SUITS = 4;
    private static final int HAND_SIZE = 5;

    public static void main(String[] args) {
        if (args.length < 1) {
            stdlib.StdOut.println("Usage: java PokerAnalysis <num_trials>");
            return;
        }

        int n = Integer.parseInt(args[0]);

        int onePairCount = 0;
        int twoPairCount = 0;
        int threeOfAKindCount = 0;
        int straightCount = 0;
        int flushCount = 0;
        int fullHouseCount = 0;
        int straightFlushCount = 0;

        // Create a deck of cards
        int[] deck = new int[RANKS * SUITS];
        for (int i = 0; i < deck.length; i++) {
            deck[i] = i;
        }

        for (int i = 0; i < n; i++) {
            stdlib.StdRandom.shuffle(deck);
            int[] hand = new int[HAND_SIZE];
            for (int j = 0; j < HAND_SIZE; j++) {
                hand[j] = deck[j];
            }

            if (isStraightFlush(hand)) straightFlushCount++;
            else if (isFullHouse(hand)) fullHouseCount++;
            else if (isFlush(hand)) flushCount++;
            else if (isStraight(hand)) straightCount++;
            else if (isThreeOfAKind(hand)) threeOfAKindCount++;
            else if (isTwoPair(hand)) twoPairCount++;
            else if (isOnePair(hand)) onePairCount++;
        }

        String[] names = {
                "One Pair", "Two Pair", "Three of a Kind", "Straight",
                "Flush", "Full House", "Straight Flush"
        };

        // get the probabilities by dividing the counts by n
        double[] probabilities = {
                (double) onePairCount / n,
                (double) twoPairCount / n,
                (double) threeOfAKindCount / n,
                (double) straightCount / n,
                (double) flushCount / n,
                (double) fullHouseCount / n,
                (double) straightFlushCount / n
        };

        for (int i = 0; i < names.length; i++) {
            stdlib.StdOut.printf("%-18s: %07.4f%%\n", names[i], probabilities[i] * 100);
        }

        plotResults(names, probabilities);
    }

    private static boolean isStraightFlush(int[] hand) {
        // straight flush is both a straight and a flush
        return isStraight(hand) && isFlush(hand);
    }

    private static boolean isFullHouse(int[] hand) {
        // full house is three of a kind and a pair
        return isThreeOfAKind(hand) && isOnePair(hand);
    }

    private static boolean isFlush(int[] hand) {
        int firstSuit = hand[0] / RANKS;
        for (int i = 1; i < hand.length; i++) {
            // all suits are the same when using integer division by RANKS
            if (hand[i] / RANKS != firstSuit) {
                return false;
            }
        }
        return true;
    }

    private static boolean isStraight(int[] hand) {
        int[] ranks = new int[HAND_SIZE];
        boolean[] hasRank = new boolean[RANKS];
        // checks whether all cards are unique ranks
        for (int i = 0; i < hand.length; i++) {
            int rank = hand[i] % RANKS;
            if (hasRank[rank]) return false;
            hasRank[rank] = true;
            ranks[i] = rank;
        }
        // sort ranks to check for consecutive values
        Arrays.sort(ranks);

        // check if the values of the last and first card differ by 4, it's a straight
        boolean isNormalStraight = (ranks[4] - ranks[0] == 4);
        // special case: A, 2, 3, 4, 5
        boolean isAceLowStraight = ranks[0] == 0 && ranks[1] == 1 && ranks[2] == 2 && ranks[3] == 3 && ranks[4] == 12;

        return isNormalStraight || isAceLowStraight;
    }

    private static boolean isThreeOfAKind(int[] hand) {
        int[] rankCounts = getRankCounts(hand);
        for (int count : rankCounts) {
            if (count == 3) return true;
        }
        return false;
    }

    private static boolean isTwoPair(int[] hand) {
        int[] rankCounts = getRankCounts(hand);
        int pairCount = 0;
        for (int count : rankCounts) {
            if (count == 2) pairCount++;
        }
        return pairCount == 2;
    }

    private static boolean isOnePair(int[] hand) {
        int[] rankCounts = getRankCounts(hand);
        int pairCount = 0;
        for (int count : rankCounts) {
            if (count == 2) pairCount++;
        }
        return pairCount == 1;
    }

    // This returns the amount of each rank in the hand
    private static int[] getRankCounts(int[] hand) {
        int[] counts = new int[RANKS];
        for (int card : hand) {
            counts[card % RANKS]++;
        }
        return counts;
    }

    // Fancy plotting function using StdDraw
    public static void plotResults(String[] names, double[] probs) {
        double maxProb = 0;
        for (double p : probs) {
            if (p > maxProb) maxProb = p;
        }

        // make canvas larger so things aren't squished
        stdlib.StdDraw.setCanvasSize(1000, 600);
        stdlib.StdDraw.setYscale(-0.25 * maxProb, 1.2 * maxProb);
        stdlib.StdDraw.setPenColor(stdlib.StdDraw.PINK);

        stdlib.StdStats.plotBars(probs);

        stdlib.StdDraw.setPenColor(stdlib.StdDraw.BLACK);
        for (int i = 0; i < names.length; i++) {
            // label each bar with the hand name and probability
            stdlib.StdDraw.text(i, -0.05 * maxProb, names[i]);
            stdlib.StdDraw.text(i, probs[i] + 0.05 * maxProb, String.format("%.4f%%", probs[i] * 100));
        }
    }
}