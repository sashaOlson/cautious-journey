import java.util.Random;

public class TestClient {

    public static void main(String[] args) {
        testDotProduct();
        testMatrixMultiply();
        testSliceFilter();
    }

    public static void testDotProduct() {
        Random rand = new Random(2024);
        double[] a = new double[100];
        double[] b = new double[100];
        for (int i = 0; i < a.length; i++) {
            a[i] = rand.nextDouble(a.length * 10);
            b[i] = rand.nextDouble(b.length * 10);
        }
        double correct = Sequential.dotProduct(a, b);
        double given = DotProduct.dotProduct(a, b, 1); // make sure it works for any choice of cutoff!
        if (Sequential.approxEquals(given, correct)) {
            System.out.println("Correct!");
        } else {
            System.out.println("Incorrect.");
        }
    }

    public static void testMatrixMultiply() {
        Random rand = new Random(332);
        double[][] a = new double[100][100];
        double[][] b = new double[100][100];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                a[i][j] = rand.nextDouble(a.length * 10);
                b[i][j] = rand.nextDouble(b.length * 10);
            }
        }
        double[][] correct = Sequential.multiply(a, b);
        double[][] given = MatrixMultiply.multiply(a, b, 1); // make sure it works for any choice of cutoff!
        for (int i = 0; i < correct.length; i++) {
            for (int j = 0; j < correct.length; j++) {
                if (!Sequential.approxEquals(correct[i][j], given[i][j])) {
                    System.out.println("Incorrect.");
                    return;
                }
            }
        }
        System.out.println("Correct!");
    }

    public static void testSliceFilter() {
        Random rand = new Random(332);

        int d = 2; // How many dimensions the space has, try some higher ones too!

        // Create normal vector of the plane and a point on the hyperplane.
        double[] normal = new double[d];
        double[] point = new double[d];

        for (int i = 0; i < d; i++) {
            normal[i] = rand.nextInt(1, 5);
            point[i] = rand.nextInt(0, 5);
        }

        // Create array of vectors
        // Some will be on the hyperplane, most will not.
        double[][] arr = new double[10000][d];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < d; j++) {
                arr[i][j] = rand.nextInt(-100, 100);
            }
        }

        double[][] correct = Sequential.takeSlice(arr, point, normal);
        double[][] given = SliceFilter.takeSlice(arr, point, normal, 1000); // make sure it works for any choice of cutoff!

        System.out.println("Correct length: " + correct.length);

        if (correct.length != given.length) {
            System.out.println("incorrect");
            return;
        }

        for (int i = 0; i < correct.length; i++) {
            for (int j = 0; j < d; j++) {
                if (!Sequential.approxEquals(correct[i][j], given[i][j])) {
                    System.out.println("incorrect");
                    return;
                }
            }
        }

        System.out.println("Correct!");
    }
}
