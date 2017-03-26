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

public class HeightModPlateaus implements IBiomeHeightModifier {
    protected Noise plateaunoise;
    protected Noise roughness;
    protected Noise rifts;

    protected static final Map<String, BiomeModParameter> PARAMETERS = new HashMap<>();
    static {
        PARAMETERS.put("variant", new BiomeModParameter.IntParameter(0, 0, 1));
    }

    public HeightModPlateaus() {
        Random rand = new Random(37813873749245L);

        this.plateaunoise = new TailoredNoise(rand, 200, 0.5, 50, 0.2, 20, 0.1, 8, 0.01);
        this.roughness = new OctaveNoise(rand, 30, 3);
        this.rifts = new RidgeNoise(rand, 150, 4);
    }

    @Override
    public Map<String, BiomeModParameter> getSettings() {
        return PARAMETERS;
    }

    @Override
    public double getModifiedHeight(int x, int z, double height, @Nullable Map<String, Object> args) {

        /**
         * Variants:
         * 0: moderate plateaus
         * 1: LOLHUEG plateaus
         */

        int variant = 0;

        if(args != null && args.containsKey("variant")) {
            variant = (int)args.get("variant");
        }

        int stepsize = 16;
        double magnitude = 0.045;
        double riftdepth = 0.05;

        if (variant == 1) {
            stepsize = 36;
            magnitude = 0.2;
            riftdepth = 0.3;
        }

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
