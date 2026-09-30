import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class MatrixMultiply {
    static ForkJoinPool POOL = new ForkJoinPool();
    static int CUTOFF;

    // Behavior should match Sequential.multiply.
    // Ignoring the initialization of arrays, your implementation should have n^3
    // work and log(n) span
    public static double[][] multiply(double[][] a, double[][] b, int cutoff) {
        MatrixMultiply.CUTOFF = cutoff;
        double[][] product = new double[a.length][b[0].length];
        POOL.invoke(new MatrixMultiplyAction(product, a, b, 0, a.length, 0, b[0].length)); // TODO: add parameters to match your constructor
        return product;
    }

    // Behavior should match the 2d version of Sequential.dotProduct.
    // Your implementation must have linear wo  rk and log(n) span
    public static double dotProduct(double[][] a, double[][] b, int row, int col, int cutoff) {
        MatrixMultiply.CUTOFF = cutoff;
        return POOL.invoke(new DotProductTask(a, b, row, col, 0, a.length)); // TODO: add parameters to match your constructor
    }

    private static void sequentialMatrixMult(double[][] product, double[][] a, double[][] b, int left, int right, int top, int bot) {
        // layered for loops through 2D matrix
        // get dotProduct for each product[i][j]
        for (int i = left; i < right; i++) {
            for (int j = top; j < bot; j++) {
                product[i][j] = dotProduct(a, b, i, j, CUTOFF);
            }
        }
    }

    private static class MatrixMultiplyAction extends RecursiveAction {
        // TODO: select fields
        private final double[][] product, a, b;   // all nxn size
        private final int left, right, top, bot;   // left = top, right = bot, always?

        public MatrixMultiplyAction(double[][] product, double[][] a, double[][] b, int left, int right, int top, int bot) {
            // TODO: implement constructor
            this.product = product;
            this.a = a;
            this.b = b;
            this.left = left;
            this.right = right;
            this.top = top;
            this.bot = bot;
        }

        @Override
        public void compute() {
            // TODO: implement
            if ((bot - top <= CUTOFF) || (right - left <= CUTOFF)) {
                sequentialMatrixMult(product, a, b, left, right, top, bot);
            } else {
                int mid = left + (right - left) / 2;   
                int center = top + (bot - top) / 2;

                MatrixMultiplyAction topLeft = new MatrixMultiplyAction(product, a, b, left, mid, top, center);
                MatrixMultiplyAction topRight = new MatrixMultiplyAction(product, a, b, mid, right, top, center);
                MatrixMultiplyAction botLeft = new MatrixMultiplyAction(product, a, b, left, mid, center, bot);
                MatrixMultiplyAction botRight = new MatrixMultiplyAction(product, a, b, mid, right, center, bot);

                topLeft.fork();
                topRight.fork();
                botLeft.fork();
                botRight.compute();
                topLeft.join();
                topRight.join();
                botLeft.join();
            }
        }
    }

    private static Double sequentialDotProduct(double[][] a, double[][] b, int row, int col, int lo, int hi) {
        double answer = 0;
        for (int i = lo; i < hi; i++) {
            answer += a[row][i] * b[i][col];
        }
        return answer;
    }

    private static class DotProductTask extends RecursiveTask<Double> {
        // TODO: select fields
        private final double[][] a, b;
        private final int row, col, lo, hi;

        public DotProductTask(double[][] a, double[][] b, int row, int col, int lo, int hi){
            // TODO: implement constructor
            this.a = a;
            this.b = b;
            this.row = row;
            this.col = col;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        public Double compute() {
            // TODO: implement
            if (hi - lo <= MatrixMultiply.CUTOFF) {
                return sequentialDotProduct(a, b, row, col, lo, hi);
            } else {
                int mid = lo + (hi - lo) / 2;
                DotProductTask left = new DotProductTask(a, b, row, col, lo, mid);
                DotProductTask right = new DotProductTask(a, b, row, col, mid, hi);
                left.fork();
                double rightRes = right.compute();
                double leftRes = left.join();
                return rightRes + leftRes;
            }
        }
    }
}
