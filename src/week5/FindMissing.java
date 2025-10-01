package week5;


import java.util.Arrays;

public class FindMissing {
    public static void main(String[] args) {
        int count = Integer.parseInt(args[0]);
        if (count < 2) {
            System.out.println("Count must be at least 2");
            return;
        }
        int[] arr = new int[count-1];
        for (int i = 0; i < count - 1; i++)
            arr[i] = stdlib.StdIn.readInt();
        Arrays.sort(arr);
        int missing = -1;
        for (int i = 0; i < count - 1; i++) {
            if (arr[i] != i + 1) {
                missing = i + 1;
                break;
            }
        }
        if (missing == -1) missing = count;
        System.out.printf("The missing number is %s\n", missing);
    }
}
