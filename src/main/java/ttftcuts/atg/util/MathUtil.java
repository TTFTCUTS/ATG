package ttftcuts.atg.util;

import java.util.Random;

public abstract class MathUtil {

    /**
     * Returns num, clamped between upper and lower limits. Math.max(min, Math.min(max, num)).
     * @param num Valume to clamp
     * @param min Lower limit
     * @param max Upper limit
     * @return The clamped value
     */
    public static double clamp(double num, double min, double max) {
        return Math.max(min, Math.min(max, num));
    }
    public static int clamp(int num, int min, int max)  {
        return Math.max(min, Math.min(max, num));
    }

    /**
     * Polynomial smoothed maximum of a and b. Equivalent to Math.max until the difference between the two values is less than div, where it has a smooth transition between the values.
     * @param a First input
     * @param b Second input
     * @param div Smoothing threshold in absolute value
     * @return The maximum value out of a and b, smoothed to avoid discontinuities
     */
    public static double polymax(double a, double b, double div) {
        double h = clamp(0.5 + 0.5 * (b-a) / div, 0.0,1.0);
        return (b * h + a * (1.0-h)) + div * h * (1.0 - h);
    }

    public static double plateau(double height, int minHeight, int plateauHeight, int maxHeight, double exponent, boolean limit) {
        double out = height;
        height = height * 255;
        if (height >= minHeight && height <= maxHeight) {
            int iheight = (int)height;

            if (height >= minHeight && height <= plateauHeight) {
                int range = plateauHeight - minHeight;
                int diff = plateauHeight - iheight;
                double rdiff = plateauHeight - height;

                double factor = diff / range;
                factor = Math.pow(factor, exponent);

                double rfactor = rdiff / range;
                rfactor = Math.pow(rfactor, exponent);

                double ffactor = Math.min(1.0, factor * 0.25 + rfactor * 0.75);

                out = (plateauHeight - ffactor * range) / 255.0;
            } else if (height <= maxHeight && height > plateauHeight) {
                int range = maxHeight - plateauHeight;
                int diff = iheight - plateauHeight;
                double rdiff = height - plateauHeight;

                double factor = diff / range;
                factor = Math.pow(factor, exponent);

                double rfactor = rdiff / range;
                rfactor = Math.pow(rfactor, exponent);

                double ffactor = Math.min(1.0, factor * 0.25 + rfactor * 0.75);

                out = (plateauHeight + ffactor * range) / 255.0;
            }
        }

        if (limit) {
            out = clamp(out, minHeight/255.0, maxHeight/255.0);
        }

        return out;
    }

    public static final long xorShift64(long a) {
        a ^= (a << 21);
        a ^= (a >> 35);
        a ^= (a << 4);
        return a;
    }

    public static final long coordRandom(long x, long z, long seed) {
        return ( xorShift64( xorShift64(x) + Long.rotateLeft(xorShift64(z), 32) ) + seed );
    }

    public static final long coordSeed(long x, long z, long seed) {
        return seed ^ Long.rotateLeft(xorShift64(x), 16) ^ Long.rotateLeft(xorShift64(z), 48);
    }

    public static double smoothstep(double n) {
        return n*n*(3 - 2*n);
    }

    public static double smoothrange(double n, double min, double max) {
        n = clamp(((n - min)/(max - min)), 0.0, 1.0);
        return smoothstep(n);
    }

    public static double spreadRange(double n, double fulcrum, double mult, double offset) {
        return spreadRange(n,fulcrum,mult,offset,0.0,1.0);
    }

    public static double spreadRange(double n, double fulcrum, double mult, double offset, double min, double max) {
        return Math.max(min, Math.min(max, (n-fulcrum)*mult + fulcrum*mult + offset ));
    }

    private static Random fuzz = new Random();
    public static double getFuzz(int x, int z, int salt) {
        double out = 0.0D;

        int ox = 3847234;
        int oz = 8362482;

        long xm = x*ox;
        long zm = z*oz;

        fuzz.setSeed((xm^zm)+salt);
        out += fuzz.nextDouble();

        fuzz.setSeed(((xm+ox)^zm)+salt);
        out += fuzz.nextDouble();

        fuzz.setSeed(((xm-ox)^zm)+salt);
        out += fuzz.nextDouble();

        fuzz.setSeed((xm^(zm+oz))+salt);
        out += fuzz.nextDouble();

        fuzz.setSeed((xm^(zm-oz))+salt);
        out += fuzz.nextDouble();

        return (out*0.2 - 0.5);
    }

    public static int coordHash(int x, int z) {
        int hash = 31;
        hash = ((hash + x) << 13) - (hash + x);
        hash = ((hash + z) << 13) - (hash + z);
        return hash;
    }
}
