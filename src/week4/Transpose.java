package week4;
// name: Kirsten Pleskot

import java.util.Arrays;

public class Transpose {
    public static void main(String[] args) {
        int[][] matrix = {
                {1,2,3},
                {4,5,6},
                {7,8,9}
        };
        int[][] matrix2 = {
                {1,2},
                {4,5},
        };
        int[][] matrix4 = {
                {1, 2, 3, 4},
                {4, 5, 6, 5},
                {7, 8, 9, 10},
                {11,12,13,14}
        };
        // select test case
        var cmatrix = matrix4;
        // use the fact the we know that the matrix is square and do inplace transpose
        // go over all rows except the last one (on the last one the last element would be swapped with itself)
        for (int i = 0; i < cmatrix.length - 1; i++) {
            // go over the last i elements of the ith row
            for (int j = cmatrix.length - 1; j > i; j--) {
                // swap the elements along the diagonal
                var tmp = cmatrix[i][j];
                cmatrix[i][j] = cmatrix[j][i];
                cmatrix[j][i] = tmp;
            }
        }
        System.out.println(Arrays.deepToString(cmatrix));
    }
}
