package ttftcuts.atg.biome.heightmods;

import ttftcuts.atg.generator.biome.BiomeModParameter;
import ttftcuts.atg.generator.biome.IBiomeHeightModifier;

import java.util.HashMap;
import java.util.Map;

public abstract class ParamHeightMod implements IBiomeHeightModifier {
    public final Map<String, BiomeModParameter> parameters = new HashMap<>();

    @Override
    public Map<String, BiomeModParameter> getSettings() {
        return parameters;
    }

    public <T> T parameter(String name, Map<String, Object> args) {
        return BiomeModParameter.get(name, args, this.getSettings());
    }
}
