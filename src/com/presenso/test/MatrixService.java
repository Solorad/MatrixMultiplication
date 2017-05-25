package com.presenso.test;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.IntStream;


/**
 * In this task I see no reason for dedicated Matrix class. Plain array of arrays is enough and looks much clearer.
 */
public class MatrixService {
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

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
        if ((size & (size - 1)) == 0) { // if size is power of 2.
            multiplyPower2Matrices(a, b, c, size);
        } else {
            multiplyMatrices(a, b, c, size);
        }
        return c;
    }

    private static void multiplyPower2Matrices(double[][] a, double[][] b, double[][] c, int size) {
        MatrixMultiplyRecursiveTask mainTask = new MatrixMultiplyRecursiveTask(a, 0, 0, b, 0, 0, c, 0, 0, size);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(mainTask);  // blocking operation.
    }

    private static void multiplyMatrices(double[][] a, double[][] b, double[][] c, int size) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        try {
            List<MatrixMultiplyTask> matrixTaskList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrixTaskList.add(new MatrixMultiplyTask(a, b, i, j));
                }
            }
            List<Future<Double>> results = executor.invokeAll(matrixTaskList);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    c[i][j] = results.get(i * size + j).get();
                }
            }
        } catch (Exception e) {
            System.out.println("Exception occurred.");
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}
