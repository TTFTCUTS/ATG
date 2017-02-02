package ttftcuts.atg.noise;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TailoredNoise extends Noise {
    Map<Double, Double> layers;
    OpenSimplexNoise generator;

    public TailoredNoise(Random rand, double... values) {
        this.generator = new OpenSimplexNoise(rand.nextLong());
        this.layers = new HashMap<Double, Double>();

        for (int i=0; i+1<values.length; i+=2) {
            layers.put(values[i], values[i+1]);
        }
    }

    @Override
    public double getValue(int x, int z) {
        double output = 0.0;

        int i = 0;
        for (Map.Entry<Double, Double> layer : layers.entrySet()) {
            output += (this.generator.eval(x / layer.getKey(), z / layer.getKey(), 0.5 + i * 7) + 1) * 0.5 * layer.getValue();
        }

        return output;
    }
}
