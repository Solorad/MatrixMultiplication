package com.presenso.test;

import java.util.concurrent.Callable;

/**
 * @author Evgenii Morenkov
 */
public class MatrixMultiplyTask implements Callable<Double> {
    final double[][] a;
    final double[][] b;
    final int row;
    final int column;

    public MatrixMultiplyTask(double[][] a, double[][] b, int row, int column) {
        this.a = a;
        this.b = b;
        this.row = row;
        this.column = column;
    }

    @Override
    public Double call() throws Exception {
        double product = 0.;
        for (int i = 0; i < a.length; i++) {
            product += a[row][i] * b[i][column];
        }
        return product;
    }
}
