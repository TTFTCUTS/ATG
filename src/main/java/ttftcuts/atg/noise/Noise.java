package ttftcuts.atg.noise;

public abstract class Noise {
    public abstract double getValue(int x, int z);

    public double[] getRegion(int x, int z, int w, int h, double[] list) {
        if (list == null) {
            list = new double[w*h];
        }

        for (int ix = 0; ix<w; ix++) {
            for (int iz = 0; iz<h; iz++) {
                list[iz*w+ix] = this.getValue(x+ix,z+iz);
            }
        }

        return list;
    }
}
