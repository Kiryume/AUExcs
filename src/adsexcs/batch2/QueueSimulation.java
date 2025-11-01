package adsexcs.batch2;
// Version: 20200917
// Handin done by:
//   202506625 Ansa Ithal
//   202507620 Kirsten Pleskot
// Contributions:
//   All members contributed equally to all tasks

import java.io.*;
import java.util.*;

public class QueueSimulation {
    public int simulate(ArrayList<Integer> input) {
        Queue<Integer> q = new ArrayDeque<Integer>(input);

        while (q.size() > 1) {
            q.poll();
            q.offer(q.poll());
        }
        int n = input.size();
        int p = Integer.highestOneBit(n);
        int l = n - p;
        int pos = (l == 0) ? n : 2 * l;

        assert  input.get(pos - 1).equals(q.peek());
        return input.get(pos - 1);
    }

    public static void testAll() {
        clearTerminal();
        test1();
        test2();
        test3();
        test1024();
        test513();
        test100();
    }

    public static void test1() {
        int[] input = { 1, 2, 3, 4, 5 };
        int correctAnswer = 2;

        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i : input) list.add(i);

        int output;
        try {
            output = new QueueSimulation().simulate(list);
        } catch (Exception e) {
            outputFail("test1",
                    "Exception: " + e);
            return;
        }

        if (output != correctAnswer)
            outputFail("test1",
                    "Expected output " + correctAnswer +
                            " but got " + output);
        else
            outputPass("test1");
    }

    public static void test2() {
        int[] input = { 123 };
        int correctAnswer = 123;

        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i : input) list.add(i);

        int output;
        try {
            output = new QueueSimulation().simulate(list);
        } catch (Exception e) {
            outputFail("test2",
                    "Exception: " + e);
            return;
        }

        if (output != correctAnswer)
            outputFail("test2",
                    "Expected output " + correctAnswer +
                            " but got " + output);
        else
            outputPass("test2");
    }

    public static void test3() {
        int[] input = { 4, 3, 2, 1 };
        int correctAnswer = 1;

        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i : input) list.add(i);

        int output;
        try {
            output = new QueueSimulation().simulate(list);
        } catch (Exception e) {
            outputFail("test3",
                    "Exception: " + e);
            return;
        }

        if (output != correctAnswer)
            outputFail("test3",
                    "Expected output " + correctAnswer +
                            " but got " + output);
        else
            outputPass("test3");
    }

    public static void test1024() {
        int N = 1024;
        int correctAnswer = 1024;

        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= N; i++) list.add(i);

        int output;
        try {
            output = new QueueSimulation().simulate(list);
        } catch (Exception e) {
            outputFail("test1024",
                    "Exception: " + e);
            return;
        }

        if (output != correctAnswer)
            outputFail("test1024",
                    "Expected output " + correctAnswer +
                            " but got " + output);
        else
            outputPass("test1024");
    }

    public static void test513() {
        int N = 513;
        int correctAnswer = 2;

        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= N; i++) list.add(i);

        int output;
        try {
            output = new QueueSimulation().simulate(list);
        } catch (Exception e) {
            outputFail("test513",
                    "Exception: " + e);
            return;
        }

        if (output != correctAnswer)
            outputFail("test513",
                    "Expected output " + correctAnswer +
                            " but got " + output);
        else
            outputPass("test513");
    }

    public static void test100() {
        int N = 100;
        int correctAnswer = 72;

        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= N; i++) list.add(i);

        int output;
        try {
            output = new QueueSimulation().simulate(list);
        } catch (Exception e) {
            outputFail("test100",
                    "Exception: " + e);
            return;
        }

        if (output != correctAnswer)
            outputFail("test100",
                    "Expected output " + correctAnswer +
                            " but got " + output);
        else
            outputPass("test100");
    }

    private static void clearTerminal() {
        System.out.print('\u000C');
    }

    private static void outputPass(String testName) {
        System.out.println("[Pass " + testName + "]");
    }

    private static void outputFail(String testName, String message) {
        System.out.println("[FAIL " + testName + "] " + message);
    }

    public static void main(String[] args) {
        testAll();
        Scanner sc = new Scanner(System.in);
        int testcases = sc.nextInt();
        for (int t = 0; t < testcases; ++t) {
            int n = sc.nextInt();
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < n; ++i) list.add(sc.nextInt());
            System.out.println(new QueueSimulation().simulate(list));
        }
    }
}