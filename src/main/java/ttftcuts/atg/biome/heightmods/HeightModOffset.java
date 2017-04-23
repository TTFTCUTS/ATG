package ttftcuts.atg.biome.heightmods;

import ttftcuts.atg.ATG;
import ttftcuts.atg.generator.biome.BiomeModParameter;
import ttftcuts.atg.generator.biome.IBiomeHeightModifier;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class HeightModOffset implements IBiomeHeightModifier {

    protected static final Map<String, BiomeModParameter> PARAMETERS = new HashMap<>();
    static {
        PARAMETERS.put("height", new BiomeModParameter.IntParameter(0, 0, 255));
    }

    @Override
    public double getModifiedHeight(int x, int z, double height, @Nullable Map<String, Object> args) {
        int offset = 0;

        if(args != null && args.containsKey("height")) {
            offset = (int)args.get("height");
        }

        return height + (offset / 255.0);
    }

    @Override
    public Map<String, BiomeModParameter> getSettings() {
        return PARAMETERS;
    }
}
