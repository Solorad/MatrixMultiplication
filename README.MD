# Matrix multiplication
For the sake of simplicity let they be square matrix.
Implementation of matrix multiplication algorithm using Java concurrency and OOP practices.

## Details
Simple array of arrays was used as a matrix representation. Matrix multiplication logic is placed in `MatrixService`.
For multiplication of power-2 sized square matrices recursive algorithm was chosen. `ForkJoinPool` with `RecursiveAction` is perfectly fit here.
For non power-2 sized matrices, simple Future object was chosen. No shared memory there.

## Algorithm
### Square Matrix with power of 2 size
Split a, b, c into quadrants of equal sizes
```
a = |a11  a12|    b = |b11  b12|    c = |c11  c12|
    |a21  a22|        |b21  b22|        |c21  c22|
```
 Hence
 ```
  c11 = mult(a11,b11) + mult(a12,b21)
  c12 = mult(a11,b12) + mult(a12,b22)
  c21 = mult(a21,b11) + mult(a22,b21)
  c22 = mult(a21,b12) + mult(a22,b22)
```

#### Race Conditions
To guard data from race conditions when multiple threads may attempt to do non-atomic sum, lock striping on C's row is used.


### Non-power of 2 sized matrices.
Plain multiplication for each result value. n*n tasks would be applied to executor.
