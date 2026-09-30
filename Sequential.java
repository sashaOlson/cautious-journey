public class Sequential {
    // Helper method for approximate equality of doubles
    // Use in place of "=="
    public static boolean approxEquals(double a, double b) {
        final double EPSILON = 1e-5;
        return Math.abs(a - b) < EPSILON;
    }

    public static double dotProduct(double[] a, double[] b) {
        double answer = 0;
        for (int i = 0; i < a.length; i++) {
            answer += a[i] * b[i];
        }
        return answer;
    }

    private static double dotProduct(double[][] a, double[][] b, int row, int col) {
        double answer = 0;
        for (int i = 0; i < a.length; i++) {
            answer += a[row][i] * b[i][col];
        }
        return answer;
    }

    public static double[][] multiply(double[][] a, double[][] b) {
        double[][] product = new double[a.length][a.length];
        for (int row = 0; row < a.length; row++) {
            for (int col = 0; col < a.length; col++) {
                product[row][col] = dotProduct(a, b, row, col);
            }
        }
        return product;
    }

    public static double[][] takeSlice(double[][] arr, double[] point, double[] normal) {
        double product = dotProduct(point, normal);

        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (approxEquals(dotProduct(arr[i], normal), product)) {
                count++;
            }
        }

        double[][] filtered = new double[count][arr[0].length];

        int index = 0;

        for (int i = 0; i < arr.length; i++) {
            if (approxEquals(dotProduct(arr[i], normal), product)) {
                filtered[index] = arr[i];
                index++;
            }
        }
        return filtered;
    }
}
