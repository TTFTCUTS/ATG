package ttftcuts.atg.biome.heightmods;

import ttftcuts.atg.generator.biome.BiomeModParameter;
import ttftcuts.atg.generator.biome.IBiomeHeightModifier;
import ttftcuts.atg.noise.Noise;
import ttftcuts.atg.noise.OctaveNoise;
import ttftcuts.atg.util.MathUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HeightModIsland extends ParamHeightMod {
    protected Noise landNoise;
    protected Noise cliffNoise;
    protected Noise hillNoise;

    public HeightModIsland() {
        parameters.put("heightoffset", new BiomeModParameter.IntParameter(0, -255, 255));
        parameters.put("hilliness", new BiomeModParameter.DoubleParameter(1.0, 0.0, 5.0));

        Random rand = new Random(2893742398423L);

        this.landNoise = new OctaveNoise(rand, 50, 4);
        this.hillNoise = new OctaveNoise(rand, 100, 4);
        this.cliffNoise = new OctaveNoise(rand, 50, 3);
    }

    @Override
    public double getModifiedHeight(int x, int z, double height, Map<String,Object> args) {
        int heightoffset = this.parameter("heightoffset", args);
        double hilliness = this.parameter("hilliness", args);

        double sealevel = 0.25 + heightoffset / 255.0;

        double land = this.landNoise.getValue(x,z);
        double hill = this.hillNoise.getValue(x,z);
        double cliff = this.cliffNoise.getValue(x,z);

        double value = sealevel + (land * 4 + 0.5)/255.0;

        double hillmask = MathUtil.clamp((land+0.5) * 1.25, 5/255.0, 1.0);

        value += Math.max(0, hill * hilliness * 0.1 * MathUtil.smoothstep(hillmask));

        double th = 0.4;
        if (cliff >= th) {
            double fraction = MathUtil.smoothstep((cliff - th) / (1 - th));

            int seaint = (int)Math.floor(sealevel * 255);
            int cliff1 = seaint + 15;
            int cliff2 = seaint + 50;
            int range = 15;

            double cliffheight = MathUtil.plateau(value, cliff1 - range, cliff1, cliff1 + range, 4, false);
            cliffheight = MathUtil.plateau(cliffheight, cliff2 - range, cliff2, cliff2 + range, 4, false);

            value = value * (1-fraction) + cliffheight * fraction;
        }

        return value;
    }
}
