package com.presenso.test;

public class Main {

    public static void main(String[] args) {
        int n = 300;
        double a[][] = MatrixService.getSquare1Matrix(n);
        double b[][] = MatrixService.getSquare1Matrix(n);
        double[][] square = MatrixService.multiplySquareMatrixes(a, b);
        check(square, n);
    }

    static void check(double[][] c, int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (c[i][j] != n) {
                    //throw new Error("Check Failed at [" + i + "][" + j + "]: " + c[i][j]);
                    System.out.println("Check Failed at [" + i + "][" + j + "]: " + c[i][j]);
                }
            }
        }
    }
}
