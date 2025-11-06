package adsexcs;
/*
 *   SKYLINE Test Framework
 *
 *   The SKYLINE problem is a classic algorithmic problem, where a
 *   divide-and-conquer algorithm is hard to get 100% correct.
 *
 *   In the course you are asked to make a theory handin on the problem.
 *
 *   If you would like more confidence in your theoretical considerations,
 *   you might consider implementing the algorithm.
 *
 *   NOTE THIS IS NOT PART OF THE HANDIN.
 *
 *   The below test framework runs your skyline algorithm on randomly
 *   generated inputs. If your algorithm stops or returns an incorrect skyline,
 *   the input list of buildings and the computed skyline is printed.
 *
 *   Furthermore, if your algorithm returns an incorrect skyline, it also
 *   tries to find a small input where your algorithm fails, by recursively
 *   removing buildings from the input.
 *
 *   2023-11-21 Gerth Stølting Brodal
 */


import java.util.ArrayList;
import java.util.List;


class Building {
    public int left, height, right;

    public Building(int left, int height, int right) {
        this.left = left;
        this.height = height;
        this.right = right;
    }

    public String toString() {
        return "Building(" + left + ", " + height + ", " + right + ")";
    }
}


class Skyline {
    public static ArrayList<Integer> skyline(ArrayList<Building> buildings) {
        var skyline = skyline_aux(buildings);
        if (!skyline.isEmpty() && skyline.getLast() == 0) {
            skyline.removeLast();
        }
        return skyline;
    }

    private static ArrayList<Integer> skyline_aux(List<Building> buildings) {
        if (buildings.size() == 1) {
            ArrayList<Integer> skyline = new ArrayList<Integer>();
            Building b = buildings.get(0);
            skyline.add(b.left);
            skyline.add(b.height);
            skyline.add(b.right);
            skyline.add(0);
            return skyline;
        } else if (buildings.size() == 0) {
            return new ArrayList<Integer>();
        }
        int mid = buildings.size() / 2;
        ArrayList<Integer> leftSkyline = skyline_aux(buildings.subList(0, mid));
        ArrayList<Integer> rightSkyline = skyline_aux(buildings.subList(mid, buildings.size()));
        ArrayList<Integer> skyline = mergeSkylines(leftSkyline, rightSkyline);
        return skyline;
    }

    private static ArrayList<Integer> mergeSkylines(ArrayList<Integer> leftSkyline, ArrayList<Integer> rightSkyline) {
        ArrayList<Integer> mergedSkyline = new ArrayList<Integer>();
        int h1 = 0, h2 = 0;
        int i = 0, j = 0;
        while (i < leftSkyline.size() && j < rightSkyline.size()) {
            int x1 = leftSkyline.get(i);
            int x2 = rightSkyline.get(j);
            if (x1 < x2) {
                // bump h1 to the next height
                h1 = leftSkyline.get(i + 1);
                // max height between current h1 and h2
                int maxH = Math.max(h1, h2);
                // if maxH is different from last height in mergedSkyline, add (x1, maxH)
                if (mergedSkyline.isEmpty() || mergedSkyline.get(mergedSkyline.size() - 1) != maxH) {
                    mergedSkyline.add(x1);
                    mergedSkyline.add(maxH);
                }
                // move to next point in leftSkyline
                i += 2;
            } else if (x1 > x2) {
                // bump h2 to the next height
                h2 = rightSkyline.get(j + 1);
                // max height between current h1 and h2
                int maxH = Math.max(h1, h2);
                // if maxH is different from last height in mergedSkyline, add (x2, maxH)
                if (mergedSkyline.isEmpty() || mergedSkyline.get(mergedSkyline.size() - 1) != maxH) {
                    mergedSkyline.add(x2);
                    mergedSkyline.add(maxH);
                }
                // move to next point in rightSkyline
                j += 2;
            } else {
                // if x1 == x2, bump both h1 and h2
                h1 = leftSkyline.get(i + 1);
                h2 = rightSkyline.get(j + 1);
                // compute max height
                int maxH = Math.max(h1, h2);
                // if maxH is different from last height in mergedSkyline, add (x1, maxH)
                if (mergedSkyline.isEmpty() || mergedSkyline.get(mergedSkyline.size() - 1) != maxH) {
                    mergedSkyline.add(x1);
                    mergedSkyline.add(maxH);
                }
                // move to next point in both skylines
                i += 2;
                j += 2;
            }
        }
        // drain remaining points from skylines
        while (i < leftSkyline.size()) {
            mergedSkyline.add(leftSkyline.get(i));
            mergedSkyline.add(leftSkyline.get(i + 1));
            i += 2;
        }
        while (j < rightSkyline.size()) {
            mergedSkyline.add(rightSkyline.get(j));
            mergedSkyline.add(rightSkyline.get(j + 1));
            j += 2;
        }
        return mergedSkyline;
    }
}


class SkylineTest {
    public static ArrayList<Integer> safe_skyline(ArrayList<Building> buildings) {
        /* Wrapper that creates a copy of buildings before calling skyline. */

        ArrayList<Building> buildings_copy = new ArrayList<Building>();
        for (Building building : buildings)
            buildings_copy.add(new Building(building.left, building.height, building.right));

        return Skyline.skyline(buildings_copy);
    }

    public static void check(boolean condition) {
        /* Throw an AssertionError exception if condition is false. */

        if (!condition) {
            throw new AssertionError();
        }
    }

    public static void validate_building(Building building) {
        /* Assert that building has non-zero width and height. */

        check(building.left < building.right);  // Non-zero width
        check(building.height > 0);  // Non-zero height
    }

    public static void validate_skyline(ArrayList<Integer> skyline) {
        /* Assert skyline has valid format. */

        int n = skyline.size();
        check(n == 0 || (n >= 3 && n % 2 == 1));  // Correct length
        if (n > 0) {
            check(skyline.get(1) > 0);  // First height is positive
            check(skyline.get(n - 2) > 0);  // Last height is positive
            for (int i = 1; i < n; i += 2) {
                check(skyline.get(i) >= 0);  // All heights are non-negative
                check(skyline.get(i - 1) < skyline.get(i + 1));  // x coordinates are strictly increasing
            }
            for (int i = 1; i + 2 < n; i += 2) {
                check(skyline.get(i) != skyline.get(i + 2));  // Adjacent heights are different
            }
        }
    }

    public static int height(Building building, int x) {
        /* Compute the height at x given a single building. */

        return x < building.left || x > building.right ? 0 : building.height;
    }

    public static int height(ArrayList<Integer> skyline, int x) {
        /* Return the height of the skyline at x. */

        int height = 0;
        for (int i = 1; i < skyline.size(); i += 2)
            if (x >= skyline.get(i - 1) && x <= skyline.get(i + 1))
                height = Math.max(height, skyline.get(i));

        return height;
    }

    public static boolean correct_heights(ArrayList<Building> buildings, ArrayList<Integer> skyline) {
        /* Check if the skyline height matches the maximum heights of
           the buildings for all possible integer x coordinates.
           Returns true if correct, false otherwise.
        */

        if (skyline.size() == 0 && buildings.size() == 0)
            return true;
        if (skyline.size() == 0 || buildings.size() == 0)
            return false;

        int x_min = skyline.get(0);
        int x_max = skyline.get(skyline.size() - 1);
        for (Building building : buildings) {
            x_min = Math.min(building.left, x_min);
            x_max = Math.max(building.right, x_max);
        }
        for (int x = x_min - 1; x <= x_max + 1; x++) {
            int h = 0;
            for (Building building : buildings)
                h = Math.max(h, height(building, x));
            if (h != height(skyline, x))
                return false;
        }

        return true;
    }

    public static ArrayList<Building> random_buildings(int n) {
        /* Generate a list of n random buildings. */

        ArrayList<Building> buildings = new ArrayList<Building>();

        for (int i = 0; i < n; i++) {
            int left = (int) (Math.random() * 100);
            int right = left + 1 + (int) (Math.random() * 20);
            int height = 1 + (int) (Math.random() * 100);
            buildings.add(new Building(left, height, right));
        }

        return buildings;
    }

    public static ArrayList<Building> simplify_bad_input(ArrayList<Building> buildings) {
        /* Try to simplify the input by recursively removing buildings
           where the error persists to exist.
        */

        for (int i = 0; i < buildings.size(); i++) {
            ArrayList<Building> simplified = new ArrayList<Building>(buildings);
            simplified.remove(i);
            if (!correct_heights(simplified, safe_skyline(simplified)))
                return simplify_bad_input(simplified);
        }

        return buildings;
    }

    public static void random_skyline_tests(int number_of_buildings, int iterations) {
        /* Test the skyline method iterations times on number_of_buildings
           random buildings. On failure, print the buildings and the skyline.
         */
        System.out.print(number_of_buildings + " buildings");
        for (int i = 0; i < iterations; i++) {
            System.out.print(".");
            ArrayList<Building> buildings = random_buildings(number_of_buildings);
            ArrayList<Integer> skyline = null;
            try {
                for (Building building : buildings)
                    validate_building(building);
                skyline = safe_skyline(buildings);
                validate_skyline(skyline);
            } catch (Throwable e) {
                System.out.println();
                System.out.println("Buildings: " + buildings);
                System.out.println("Skyline: " + skyline);
                throw e;
            }
            if (!correct_heights(buildings, skyline)) {
                System.out.println();
                System.out.println("Incorrect skyline:");
                System.out.println("Buildings: " + buildings);
                System.out.println("Skyline: " + skyline);
                System.out.println();
                System.out.println("Trying to simplify bad input...");
                buildings = simplify_bad_input(buildings);
                System.out.println("Buildings: " + buildings);
                System.out.println("Skyline: " + safe_skyline(buildings));
                throw new AssertionError();
            }
        }
    }

    public static void main(String[] args) {
        /* Test the skyline method on an increasing number of buildings. */

        for (int buildings = 0; buildings <= 10; buildings++)
            random_skyline_tests(buildings, 100);
        for (int buildings = 100, iterations = 10000; iterations > 0; buildings *= 10, iterations /= 10)
            random_skyline_tests(buildings, iterations);
        System.out.println("\npassed all tests");
    }
}