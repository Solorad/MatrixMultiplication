package com.presenso.test;


import java.util.Random;
import java.util.concurrent.ForkJoinPool;


/**
 * In this task I see no reason for dedicated Matrix class. Plain array of arrays is enough and looks much clearer.
 */
public class MatrixService {
    public static double[][] getRandDoubleMatrix(int n, int m) {
        final Random random = new Random(Runtime.getRuntime().freeMemory());
        double[][] a = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                a[i][j] = random.nextDouble() * 100;
            }
        }
        return a;
    }

    /**
     * In this task I see no reason for dedicated Matrix class. Plain array of arrays is enough and looks much clearer.
     */
    public static double[][] getSquare1Matrix(int n) {
        double[][] a = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = 1;
            }
        }
        return a;
    }

    public static double[][] multiplySquareMatrixes(double[][] a, double[][] b) {
        if (a == null || b == null || a.length == 0 || b.length == 0
                || a.length != a[0].length || b.length != b[0].length || a.length != b.length) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        int size = a.length;
        double[][] c = new double[size][size];
        MatrixMultiplyTask mainTask = new MatrixMultiplyTask(a, 0, 0, b, 0, 0, c, 0, 0, size);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(mainTask);  // blocking operation.
        return c;
    }
}
