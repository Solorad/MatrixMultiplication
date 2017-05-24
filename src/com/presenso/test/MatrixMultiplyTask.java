package com.presenso.test;

import java.util.concurrent.RecursiveAction;


/**
 * Fact, that matrices are squared are used for this task.
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
public class MatrixMultiplyTask extends RecursiveAction {
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

    MatrixMultiplyTask(double[][] A, int aRow, int aCol, double[][] B,
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
            multiplyStride2();
            return;
        }
        int h = size / 2;
        MatrixMultiplyTask a11b11 = new MatrixMultiplyTask(a, aRow, aCol, b, bRow, bCol, c, cRow, cCol, h);
        MatrixMultiplyTask a12b21 = new MatrixMultiplyTask(a, aRow, aCol + h, b, bRow + h, bCol, c, cRow, cCol, h);

        MatrixMultiplyTask a11b12 = new MatrixMultiplyTask(a, aRow, aCol, b, bRow, bCol + h, c, cRow, cCol + h, h);
        MatrixMultiplyTask a12b22 = new MatrixMultiplyTask(a, aRow, aCol + h, b, bRow + h, bCol + h, c, cRow, cCol + h, h);

        MatrixMultiplyTask a21b11 = new MatrixMultiplyTask(a, aRow + h, aCol, b, bRow, bCol, c, cRow + h, cCol, h);
        MatrixMultiplyTask a22b21 = new MatrixMultiplyTask(a, aRow + h, aCol + h, b, bRow + h, bCol, c, cRow + h, cCol, h);


        MatrixMultiplyTask a21b12 = new MatrixMultiplyTask(a, aRow + h, aCol, b, bRow, bCol + h, c, cRow + h, cCol + h, h);
        MatrixMultiplyTask a22b22 = new MatrixMultiplyTask(a, aRow + h, aCol + h, b, bRow + h, bCol + h, c, cRow + h, cCol + h, h);
        invokeAll(a11b11, a12b21, a11b12, a12b22, a21b11, a22b21, a21b12, a22b22);
    }

    void multiplyBase() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                for (int k = 0; k < size; ++k) {
                    c[cRow + i][cCol + j] += a[aRow + i][aCol + k] * b[bRow + k][bCol + j];
                }
            }
        }
    }

    void multiplyStride2() {
        for (int j = 0; j < size; j += 2) {
            for (int i = 0; i < size; i += 2) {

                double[] a0 = a[aRow + i];
                double[] a1 = a[aRow + i + 1];

                double s00 = 0.0;
                double s01 = 0.0;
                double s10 = 0.0;
                double s11 = 0.0;

                for (int k = 0; k < size; k += 2) {
                    double[] b0 = b[bRow + k];
                    s00 += a0[aCol + k] * b0[bCol + j];
                    s10 += a1[aCol + k] * b0[bCol + j];
                    s01 += a0[aCol + k] * b0[bCol + j + 1];
                    s11 += a1[aCol + k] * b0[bCol + j + 1];

                    double[] b1 = b[bRow + k + 1];
                    s00 += a0[aCol + k + 1] * b1[bCol + j];
                    s10 += a1[aCol + k + 1] * b1[bCol + j];
                    s01 += a0[aCol + k + 1] * b1[bCol + j + 1];
                    s11 += a1[aCol + k + 1] * b1[bCol + j + 1];
                }

                synchronized (MatrixMultiplyTask.class) {
                    c[cRow + i][cCol + j] += s00;
                    c[cRow + i][cCol + j + 1] += s01;
                    c[cRow + i + 1][cCol + j] += s10;
                    c[cRow + i + 1][cCol + j + 1] += s11;
                }
            }
        }
    }
}
