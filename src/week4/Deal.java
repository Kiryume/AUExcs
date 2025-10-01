package week4;
// name: Kirsten Pleskot

import java.util.Random;

public class Deal {
    public static void main(String[] args) {
        String[] vals = {"Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King", "Ace"};
        String[] suits = {"Hearts", "Spades", "Diamonds", "Clubs"};
        String[] deck = new  String[52];
        int hands = Integer.parseInt(args[0]);
        // we cannot deal more than 10 hands as only 52 cards are in the deck
        if (hands > 10) {
            System.out.println("Too many hands");
            return;
        }

        var inx = 0;
        // generate every combination of val and suit
        for (var val: vals) {
            for (var suit: suits) {
                deck[inx++] = val + " of " + suit;
            }
        }
        var rnd = new Random();
        // shuffle all the cards
        for (int i = deck.length - 1; i > 0; i--) {
            // go from the end of the deck and swap the last unshuffled card with any of the unshuffled cards
            var tmp = deck[i];
            var rinx = rnd.nextInt(i);
            deck[i] = deck[rinx];
            deck[rinx] = tmp;
        }
        // take first hands*5 cards from the deck in batches of 5
        for (int i = 0; i < hands; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.println(deck[i*5+j]);
            }
            System.out.println();
        }
    }
}
