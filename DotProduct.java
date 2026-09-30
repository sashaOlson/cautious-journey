import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class DotProduct {
    static ForkJoinPool POOL = new ForkJoinPool();
    static int CUTOFF;

    // Behavior should match Sequential.dotProduct
    // Your implementation must have linear work and log(n) span!!!! CHECK LATER
    public static double dotProduct(double[] a, double[]b, int cutoff){
        DotProduct.CUTOFF = cutoff;
        return POOL.invoke(new DotProductTask(a, b, 0, a.length)); // TODO: add parameters to match your constructor
    }

    private static Double sequentialDotProduct(double[] a, double[] b, int lo, int hi) {
        double answer = 0;
        for (int i = lo; i < hi; i++) {
            answer += a[i] * b[i];
        }
        return answer;
    }

    private static class DotProductTask extends RecursiveTask<Double>{
        // TODO: select fields
        private final double[] a, b;
        private final int lo, hi;

        public DotProductTask(double[] a, double[] b, int lo, int hi){
            // TODO: implement constructor
            this.a = a;
            this.b = b;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        public Double compute(){
            //TODO: Implement.
            if (hi - lo <= DotProduct.CUTOFF) {
                return sequentialDotProduct(a, b, lo, hi);
            } else {
                int mid = lo + (hi - lo) / 2;
                DotProductTask left = new DotProductTask(a, b, lo, mid);
                DotProductTask right = new DotProductTask(a, b, mid, hi);
                left.fork();
                double rightRes = right.compute();
                double leftRes = left.join();
                return rightRes + leftRes;
            }
        }
    }
}
