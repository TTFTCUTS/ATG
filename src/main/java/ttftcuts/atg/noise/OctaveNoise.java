package ttftcuts.atg.noise;

import java.util.Random;

public class OctaveNoise extends Noise {
    protected OpenSimplexNoise generator;
    protected double scale;

    protected int octaves;
    protected double lacunarity;
    protected double gain;

    protected double offset = 0.0;

    public OctaveNoise(Random rand, double scale, int octaves, double lacunarity, double gain) {
        this.generator = new OpenSimplexNoise(rand.nextLong());
        this.scale = scale;
        this.octaves = octaves;
        this.lacunarity = lacunarity;
        this.gain = gain;
    }

    public OctaveNoise(Random rand, double scale, int octaves) {
        this(rand, scale, octaves, 2.0, 0.5);
    }

    @Override
    public double getValue(int x, int z) {
        double sum = 0.0;
        double size = 1/scale;
        double amp = 1.0;

        double n;

        for (int i=0; i<octaves; i++) {
            n = this.generator.eval(x * size, z * size, this.offset + 0.5 + i * 31.0);
            sum += n * amp;
            size *= lacunarity;
            amp *= gain;
        }

        return sum;
    }
}
