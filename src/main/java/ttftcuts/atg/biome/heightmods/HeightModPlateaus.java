package ttftcuts.atg.biome.heightmods;

import ttftcuts.atg.generator.biome.BiomeModParameter;
import ttftcuts.atg.generator.biome.IBiomeHeightModifier;
import ttftcuts.atg.noise.Noise;
import ttftcuts.atg.noise.OctaveNoise;
import ttftcuts.atg.noise.RidgeNoise;
import ttftcuts.atg.noise.TailoredNoise;
import ttftcuts.atg.util.MathUtil;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HeightModPlateaus extends ParamHeightMod {
    protected Noise plateaunoise;
    protected Noise roughness;
    protected Noise rifts;

    public HeightModPlateaus() {
        parameters.put("stepsize", new BiomeModParameter.IntParameter(16, 1, 255));
        parameters.put("magnitude", new BiomeModParameter.DoubleParameter(0.045, 0.0, 1.0));
        parameters.put("riftdepth", new BiomeModParameter.DoubleParameter(0.05, 0.0, 1.0));

        Random rand = new Random(37813873749245L);

        this.plateaunoise = new TailoredNoise(rand, 200, 0.5, 50, 0.2, 20, 0.1, 8, 0.01);
        this.roughness = new OctaveNoise(rand, 30, 3);
        this.rifts = new RidgeNoise(rand, 150, 4);
    }

    @Override
    public double getModifiedHeight(int x, int z, double height, @Nullable Map<String, Object> args) {

        /**
         * Parameter Values:
         * normal (default): 16, 0.045, 0.05
         * savanna M craziness: 36, 0.2, 0.3
         */

        int stepsize = this.parameter("stepsize", args);
        double magnitude = this.parameter("magnitude", args);
        double riftdepth = this.parameter("riftdepth", args);

        double plateau = this.plateaunoise.getValue(x,z);
        double rough = this.roughness.getValue(x,z);
        double rift = this.rifts.getValue(x,z);

        double n = height + (plateau + 0.6) * 0.65 * magnitude;

        int halfstep = stepsize/2;
        int steps = Math.floorDiv(255, stepsize);

        double sharp = n;
        for (int i=0; i<steps; i++) {
            int h = i * stepsize + halfstep + (int)Math.round(rough * 1.5);

            sharp = MathUtil.plateau(sharp, h - halfstep, h, h + halfstep, 5.0 - rough, false);
        }

        double mix = MathUtil.smoothstep((rough + 0.6) * 1.2);

        n = mix * sharp + (1-mix) * n;

        double r = rift * rift * rift;
        r = Math.max(0.0, r - 0.8);

        n -= r * riftdepth;

        n += rough * 0.01;

        return MathUtil.polymax(n, height, 0.5);
    }
}
