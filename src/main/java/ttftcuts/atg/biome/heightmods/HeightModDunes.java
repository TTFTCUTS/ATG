package ttftcuts.atg.biome.heightmods;

import ttftcuts.atg.generator.biome.IBiomeHeightModifier;
import ttftcuts.atg.noise.DuneNoise;
import ttftcuts.atg.noise.Noise;

import java.util.Map;
import java.util.Random;

public class HeightModDunes implements IBiomeHeightModifier {
    protected Noise noise;

    public HeightModDunes() {
        Random rand = new Random(98378923455324L);
        this.noise = new DuneNoise(rand, 80.0, 0.2);
    }

    @Override
    public double getModifiedHeight(int x, int z, double height, Map<String,Object> args) {
        return height + this.noise.getValue(x,z) * 0.05;
    }
}
