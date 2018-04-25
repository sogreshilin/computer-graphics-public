package wireframe;

public class Utils {
    public static double truncate(double value, double lo, double hi) {
        System.out.printf("truncate(%f, %f, %f)\n", value, lo, hi);
        if (value < lo) {
            return lo;
        }
        if (value > hi) {
            return hi;
        }
        return value;
    }

    public static int truncate(int value, int lo, int hi) {
        if (value < lo) {
            return lo;
        }
        if (value > hi) {
            return hi;
        }
        return value;
    }
}
