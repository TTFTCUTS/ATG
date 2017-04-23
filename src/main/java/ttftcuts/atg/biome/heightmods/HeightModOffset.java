package ttftcuts.atg.biome.heightmods;

import ttftcuts.atg.ATG;
import ttftcuts.atg.generator.biome.BiomeModParameter;
import ttftcuts.atg.generator.biome.IBiomeHeightModifier;
import ttftcuts.atg.util.MathUtil;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class HeightModOffset implements IBiomeHeightModifier {

    protected static final Map<String, BiomeModParameter> PARAMETERS = new HashMap<>();
    static {
        PARAMETERS.put("height", new BiomeModParameter.IntParameter(0, 0, 255));
        PARAMETERS.put("tapered", new BiomeModParameter.BooleanParameter(false));
        PARAMETERS.put("taperstart", new BiomeModParameter.IntParameter(0,0,255));
        PARAMETERS.put("taperend", new BiomeModParameter.IntParameter(0,0,255));
        PARAMETERS.put("taperheight", new BiomeModParameter.IntParameter(0,0,255));
    }

    @Override
    public double getModifiedHeight(int x, int z, double height, @Nullable Map<String, Object> args) {
        int offset = 0;
        boolean tapered = false;
        int taperstart = 0;
        int taperend = 0;
        int taperheight = 0;

        if(args != null && args.containsKey("height")) {
            offset = (int)args.get("height");
        }
        if(args != null && args.containsKey("tapered")) {
            tapered = (boolean)args.get("tapered");
        }

        if (tapered) {

            if (args != null && args.containsKey("taperstart")) {
                taperstart = (int) args.get("taperstart");
            }
            if (args != null && args.containsKey("taperend")) {
                taperend = (int) args.get("taperend");
            }
            if (args != null && args.containsKey("taperheight")) {
                taperheight = (int) args.get("taperheight");
            }

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

    @Override
    public Map<String, BiomeModParameter> getSettings() {
        return PARAMETERS;
    }
}
