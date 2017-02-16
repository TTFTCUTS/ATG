package ttftcuts.atg.util;

public class Kernel {
    public final double[] array;
    public final int radius;
    public final int size;

    public Kernel(int radius, IKernelFormula formula, boolean norm) {
        this.radius = radius;
        this.size = radius * 2 +1;
        this.array = new double[size * size];

        int x,z,index;
        double n;

        double total = 0.0;

        for (int ix=0; ix<this.size; ix++) {
            for (int iz=0; iz<this.size; iz++) {
                index = iz * this.size + ix;

                x = ix - this.radius;
                z = iz - this.radius;

                n = formula.operation(x,z);

                this.array[index] = n;
                total += n;
            }
        }

        if (norm && total != 0.0) {
            for (int i=0; i<this.array.length; i++) {
                this.array[i] /= total;
            }
        }
    }

    public Kernel(int radius, IKernelFormula formula) {
        this(radius, formula, true);
    }

    public double getValue(int x, int z) {
        if (Math.abs(x) > this.radius || Math.abs(z) > this.radius) {
            return 0.0;
        }

        return this.array[(z+this.radius) * this.size + (x+this.radius)];
    }

    public interface IKernelFormula {
        double operation(int x, int z);
    }
}
