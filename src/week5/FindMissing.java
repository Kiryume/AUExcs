package week5;


import java.util.Arrays;

public class FindMissing {
    public static void main(String[] args) {
        int count = Integer.parseInt(args[0]);
        // if count is smaller than 2 it does not make sense to search for missing number
        if (count < 2) {
            System.out.println("Count must be at least 2");
            return;
        }
        // we allocate array one size smaller since one number will be missing
        int[] arr = new int[count-1];
        for (int i = 0; i < count - 1; i++)
            arr[i] = stdlib.StdIn.readInt();
        // sort the array
        Arrays.sort(arr);
        int missing = -1;
        // the missing number will be in a gap between two numbers
        for (int i = 0; i < count - 1; i++) {
            if (arr[i] != i + 1) {
                missing = i + 1;
                break;
            }
        }
        // if missing number has not been found it's the last one
        if (missing == -1) missing = count;
        System.out.printf("The missing number is %s\n", missing);
    }
}
