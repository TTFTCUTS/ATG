package ttftcuts.atg.biome.heightmods;

import ttftcuts.atg.generator.biome.BiomeModParameter;
import ttftcuts.atg.generator.biome.IBiomeHeightModifier;
import ttftcuts.atg.util.MathUtil;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class HeightModOffset extends ParamHeightMod {

    public HeightModOffset() {
        parameters.put("height", new BiomeModParameter.IntParameter(0, 0, 255));
        parameters.put("tapered", new BiomeModParameter.BooleanParameter(false));
        parameters.put("taperstart", new BiomeModParameter.IntParameter(0,0,255));
        parameters.put("taperend", new BiomeModParameter.IntParameter(0,0,255));
        parameters.put("taperheight", new BiomeModParameter.IntParameter(0,0,255));
    }
    
    @Override
    public double getModifiedHeight(int x, int z, double height, @Nullable Map<String, Object> args) {
        int offset = BiomeModParameter.getManual("height", args, 0);
        boolean tapered = BiomeModParameter.getManual("tapered", args, false);

        if (tapered) {
            int taperstart = BiomeModParameter.getManual("taperstart", args, 0);
            int taperend = BiomeModParameter.getManual("taperend", args, 0);;
            int taperheight = BiomeModParameter.getManual("taperheight", args, 0);;

            if (taperstart == taperend) {
                return height + (offset / 255.0);
            }

            double dtaperstart = taperstart / 255.0;
            double dtaperend = taperend / 255.0;
            double dtaperheight = taperheight / 255.0;
            double doffset = offset / 255.0;

            if (height < dtaperstart) {
                return height + doffset;
            } else if (height > dtaperend) {
                return height + dtaperheight;
            } else {
                double fraction = MathUtil.smoothstep((height - dtaperstart) / (dtaperend - dtaperstart));
                return height + doffset * (1.0-fraction) + dtaperheight * fraction;
            }
        }

        return height + (offset / 255.0);
    }
}
