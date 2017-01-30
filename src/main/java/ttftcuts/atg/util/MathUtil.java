package ttftcuts.atg.util;

public abstract class MathUtil {

    public static double clamp(double num, double min, double max) {
        return Math.max(min, Math.min(max, num));
    }

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
}
