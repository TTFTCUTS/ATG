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

public class HeightModMesa implements IBiomeHeightModifier {
    protected Noise mesanoise;
    protected Noise roughness;
    protected Noise rifts;
    protected Noise spires;

    protected static final Map<String, BiomeModParameter> PARAMETERS = new HashMap<>();
    static {
        PARAMETERS.put("variant", new BiomeModParameter.IntParameter(0, 0, 2));
    }

    public HeightModMesa() {
        Random rand = new Random(329047298523L);
        this.mesanoise = new TailoredNoise(rand, 100, 0.1, 50, 0.5, 20, 0.1, 8, 0.01);
        this.roughness = new OctaveNoise(rand, 30, 3);
        this.rifts = new RidgeNoise(rand, 150, 4);
        this.spires = new OctaveNoise(rand, 10, 3, 0.5, 0.6);
    }

    @Override
    public Map<String, BiomeModParameter> getSettings() {
        return PARAMETERS;
    }

    @Override
    public double getModifiedHeight(int x, int z, double height, @Nullable Map<String, Object> args) {

        /**
         * Variants
         * 0: normal mesa
         * 1: double size + some height (plateau)
         * 2: bryce style sprires
         */

        int variant = 0;

        if(args != null && args.containsKey("variant")) {
            variant = (int)args.get("variant");
        }

        int nx = variant == 1 ? x / 2 + 37492 : x;
        int nz = variant == 1 ? z / 2 + 85477 : z;

        double n = height + (this.mesanoise.getValue(nx,nz) - 0.35) * 0.5;

        double spire = 0.0;
        if (variant == 2) {
            spire = spires.getValue(x,z);
            spire *= spire * spire;// * spire;
            spire = Math.max(0,spire) * 0.2;
            spire += height;
        }

        if (variant == 1) {
            n += 14/255.0;
        }

        for (int i=0; i<15; i++) {
            int h = i * 16 + 8;

            n = MathUtil.plateau(n, h - 8, h, h + 8, 5.0, false);
            if (variant == 2) {
                spire = MathUtil.plateau(spire, h - 8, h, h + 8, 5.0, false);
            }
        }

        double mix = 0.992;
        n = n * mix + height * (1-mix);

        n += roughness.getValue(x,z) * 0.01;

        n += 3/255.0;

        double r = rifts.getValue(x,z);
        r *= r * r;
        r = Math.max(0,r - 0.75);
        //r *= r;

        n = Math.max(height-0.01, n- r);

        if (variant == 2) {
            n = MathUtil.polymax(n, spire, 8/255.0);
        }

        return MathUtil.polymax(n, height, 12/255.0);
    }
}
