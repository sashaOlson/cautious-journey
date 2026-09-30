import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class SliceFilter {
    static ForkJoinPool POOL = new ForkJoinPool();
    static int CUTOFF;

    // Behavior should match Sequential.takeSlice.
    // Ignoring the initialization of arrays, your implementation must have linear
    // work and log(n) span
    public static double[][] takeSlice(double[][] arr, double[] point, double[] normal, int cutoff) {
        SliceFilter.CUTOFF = cutoff;
        // arr = array of points, point = a point, normal = normal vector
        // Task: see which points from arr are on the hyperplane created from point and normal

        // TODO: Implement to match the filter/pack procedure discussed in class.
        // Reminder: the main steps are:
        // 0) dot product of the point and the normal vector
        double normDotPt = dotProduct(point, normal); 

        // 1) do a map on the arr of "vectors" (points) (with normal)
        int[] ifPlanar = new int[arr.length];
        POOL.invoke(new SliceFilterAction(ifPlanar, arr, normal, normDotPt, 0, arr.length));

        // 2) do prefix sum on the map result (implementation provided for you in
        // ParallelPrefix.java)
        int[] prefixSumResult = ParallelPrefix.prefixSum(ifPlanar, cutoff);

        // 3) initialize an array whose length matches the last value in the prefix sum
        // result
        int length = prefixSumResult[prefixSumResult.length - 1];
        double[][] coords = new double[length][point.length];

        // 4) do a map to populate that new array.
        POOL.invoke(new SliceFilterAction2(coords, arr, ifPlanar, prefixSumResult, 0, arr.length));

        return coords;
    }

    private static class SliceFilterAction2 extends RecursiveAction {
        private final double[][] coords, arr;
        private final int[] ifPlanar, prefixSumResult;
        private final int lo, hi;

        public SliceFilterAction2 (double[][] coords, double[][] arr, int[] ifPlanar, int[] prefixSumResult, int lo, int hi) {
            this.arr = arr;
            this.ifPlanar = ifPlanar;
            this.coords = coords;
            this.lo = lo;
            this.hi = hi;
            this.prefixSumResult = prefixSumResult;
        }

        @Override
        public void compute() {
            if (hi - lo <= CUTOFF) {
                SequentialSliceFilter2(coords, arr, ifPlanar, prefixSumResult, lo, hi);
            } else {
                int mid = lo + (hi - lo) / 2;
                SliceFilterAction2 left = new SliceFilterAction2(coords, arr, ifPlanar, prefixSumResult, lo, mid);
                SliceFilterAction2 right = new SliceFilterAction2(coords, arr, ifPlanar, prefixSumResult, mid, hi);

                left.fork();
                right.compute();
                left.join();
            }
        }
    }

    private static void SequentialSliceFilter2(double[][] coords, double[][] arr, int[] ifPlanar, int[] prefixSumResult, int lo, int hi) {
        for (int i = lo; i < hi; i++) {
            if (ifPlanar[i] == 1) {   // 1 if on plane, 0 if not? or is this supposed to equal normDotPt
                coords[prefixSumResult[i]- 1] = arr[i];
            }
        }
    }

    private static class SliceFilterAction extends RecursiveAction {
        private final double[][] arr;
        private final int[] ifPlanar;
        private final double[] normal;
        private final int lo, hi;
        private final double ptNormDot;

        public SliceFilterAction (int[] ifPlanar, double[][] arr, double[] normal, double ptNormDot, int lo, int hi) {
            this.arr = arr;
            this.ifPlanar = ifPlanar;
            this.normal = normal;
            this.lo = lo;
            this.hi = hi;
            this.ptNormDot = ptNormDot;
        }

        @Override
        public void compute() {
            if (hi - lo <= CUTOFF) {
                SequentialSliceFilter(ifPlanar, arr, normal, ptNormDot, lo, hi);
            } else {
                int mid = lo + (hi - lo) / 2;
                SliceFilterAction left = new SliceFilterAction(ifPlanar, arr, normal, ptNormDot, lo, mid);
                SliceFilterAction right = new SliceFilterAction(ifPlanar, arr, normal, ptNormDot, mid, hi);

                left.fork();
                right.compute();
                left.join();
            }
        }
    }

    private static void SequentialSliceFilter(int[] ifPlanar, double[][] arr, double[] normal, double ptNormDot, int lo, int hi) {
        for (int i = lo; i < hi; i++) {
            if (Sequential.approxEquals(dotProduct(arr[i], normal), ptNormDot)) {
                ifPlanar[i] = 1;   // if dot products equal, ie point on surface
            } else {
                ifPlanar[i] = 0;
            }
        }
    }

    private static double dotProduct(double[] a, double[] b) {
        return DotProduct.dotProduct(a, b, SliceFilter.CUTOFF);
    }
}
