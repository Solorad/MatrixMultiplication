package com.presenso.test;

import java.util.concurrent.RecursiveAction;


/**
 * Works with only quadratic matrices n*n, where n = 2^x.
 * Given: two matrices a, b
 * To compute: c = axb
 * Algorithm:
 * <p>
 * Split a, b, c into quadrants of equal sizes
 * a = |a11  a12|    b = |b11  b12|    c = |c11  c12|
 * |a21  a22|        |b21  b22|        |c21  c22|
 * <p>
 * Hence   c11 = mult(a11,b11) + mult(a12,b21)
 * c12 = mult(a11,b12) + mult(a12,b22)
 * c21 = mult(a21,b11) + mult(a22,b21)
 * c22 = mult(a21,b12) + mult(a22,b22)
 */
public class MatrixMultiplyRecursiveTask extends RecursiveAction {
    private static final int THRESHOLD = 64;

    private final double[][] a;
    private final int aRow;
    private final int aCol;

    private final double[][] b;
    private final int bRow;
    private final int bCol;

    private final double[][] c;
    private final int cRow;
    private final int cCol;
    private final int size;

    MatrixMultiplyRecursiveTask(double[][] A, int aRow, int aCol, double[][] B,
                                int bRow, int bCol, double[][] C, int cRow, int cCol, int size) {
        this.a = A;
        this.aRow = aRow;
        this.aCol = aCol;
        this.b = B;
        this.bRow = bRow;
        this.bCol = bCol;
        this.c = C;
        this.cRow = cRow;
        this.cCol = cCol;
        this.size = size;
    }

    @Override
    protected void compute() {
        if (size <= THRESHOLD) {
            // base case
            multiplyBase();
            return;
        }
        int h = size / 2;
        MatrixMultiplyRecursiveTask
                a11b11 = new MatrixMultiplyRecursiveTask(a, aRow, aCol, b, bRow, bCol, c, cRow, cCol, h);
        MatrixMultiplyRecursiveTask
                a12b21 = new MatrixMultiplyRecursiveTask(a, aRow, aCol + h, b, bRow + h, bCol, c, cRow, cCol, h);

        MatrixMultiplyRecursiveTask
                a11b12 = new MatrixMultiplyRecursiveTask(a, aRow, aCol, b, bRow, bCol + h, c, cRow, cCol + h, h);
        MatrixMultiplyRecursiveTask
                a12b22 = new MatrixMultiplyRecursiveTask(a, aRow, aCol + h, b, bRow + h, bCol + h, c, cRow, cCol + h, h);

        MatrixMultiplyRecursiveTask
                a21b11 = new MatrixMultiplyRecursiveTask(a, aRow + h, aCol, b, bRow, bCol, c, cRow + h, cCol, h);
        MatrixMultiplyRecursiveTask
                a22b21 = new MatrixMultiplyRecursiveTask(a, aRow + h, aCol + h, b, bRow + h, bCol, c, cRow + h, cCol, h);


        MatrixMultiplyRecursiveTask
                a21b12 = new MatrixMultiplyRecursiveTask(a, aRow + h, aCol, b, bRow, bCol + h, c, cRow + h, cCol + h, h);
        MatrixMultiplyRecursiveTask
                a22b22 = new MatrixMultiplyRecursiveTask(a, aRow + h, aCol + h, b, bRow + h, bCol + h, c, cRow + h, cCol + h, h);
        invokeAll(a11b11, a12b21, a11b12, a12b22, a21b11, a22b21, a21b12, a22b22);
    }

    /**
     * Plain old 3-for loop for matrix multiplication.
     * Lock striping was used on C row to protect from Race conditions
     */
    synchronized void multiplyBase() {
        for (int i = 0; i < size; ++i) {
            synchronized (c[cRow + i]) {
                for (int j = 0; j < size; ++j) {
                    for (int k = 0; k < size; ++k) {
                        c[cRow + i][cCol + j] += a[aRow + i][aCol + k] * b[bRow + k][bCol + j];
                    }
                }
            }
        }
    }
}
