package brotherdetjr.gama;

import java.util.Random;

public final class Utils {

    private Utils() {
        throw new AssertionError();
    }

    public static long nextLong(Random rng, long n) {
        long bits, val;
        do {
            bits = (rng.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0L);
        return val;
    }
}
