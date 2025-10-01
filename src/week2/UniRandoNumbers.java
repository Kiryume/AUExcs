package week2;
// name: Kirsten Pleskot

import java.util.ArrayList;

public class UniRandoNumbers {
    public static void main(String[] args) {
        // initiate new instance of class ArrayList (Java's dynamic array) with the generic argument of class Double
        // (since I can't use value types for generic arguments in Java)
        var randNums = new ArrayList<Double>();
        // have a loop of five iterations and push one pseudorandomly generated number on each iteration to the list
        for (int i = 0; i < 5; i++) {
            randNums.add(Math.random());
        }
        // print out the generated numbers
        System.out.printf("The numbers %s\n", randNums);
        // initiate accumulator for proceeding calculations
        double acc = 0;
        // loop over all elements of the ranNums list and sum them to the acc variable
        for (Double element : randNums) {
            acc += element;
        }
        // print the average by dividing the acc variable by the amount of randomly generated numbers
        System.out.printf("The average %s\n", acc / randNums.size());
        // set acc to number outside the generated range (this is not necessary since the accumulation of all elements will
        // be more or equal to the minimum element (in case of full zeroes) so the result of the operation would be the same)
        acc = 2;
        // loop over all elements and comparing acc to every element and reassigning acc with the lower of the two
        // until we loop over all elements
        for (Double element : randNums) {
            acc = Math.min(acc, element);
        }
        // print out the minimum element
        System.out.printf("The minimum %s\n", acc);
        // since acc holds the value of the element with the lowest value mean acc either holds value lower or equal to
        // the highest element guaranteeing we find the highest element
        // then we do the same process as we did to find min value but for max value
        for  (Double element : randNums) {
            acc = Math.max(acc, element);
        }
        // print out the max value
        System.out.printf("The maximum %s\n", acc);
    }
}
