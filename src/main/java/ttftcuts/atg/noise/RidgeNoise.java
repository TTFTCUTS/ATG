package ttftcuts.atg.noise;

import java.util.Random;

public class RidgeNoise extends Noise {
    OpenSimplexNoise generator;
    int layers;
    double scale;

    public RidgeNoise(Random rand, double scale, int layers) {
        this.generator = new OpenSimplexNoise(rand.nextLong());
        this.scale = scale;
        this.layers = layers;
    }

    @Override
    public double getValue(int x, int z) {
        double output = 0.0;

        double mult, layerscale;
        for (int i=0; i<layers; i++) {
            mult = i==0 ? 0.666 : ((output * output) / (i+1));
            layerscale = scale / (i+1);

            output += (1-Math.abs(generator.eval(x/layerscale, z/layerscale, i*773 + 0.5))) * mult;
        }

        return output;
    }
}
