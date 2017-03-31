package ttftcuts.atg.generator;

import ttftcuts.atg.ATG;
import ttftcuts.atg.compat.BiomeModule;
import ttftcuts.atg.compat.builtin.ProvidedBiomeModule;
import ttftcuts.atg.generator.biome.IBiomeHeightModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalRegistry {
    public Map<String, IBiomeHeightModifier> heightMods = new HashMap<>();
    public List<BiomeModule> biomeModules = new ArrayList<>();

    public IBiomeHeightModifier registerHeightModifier(IBiomeHeightModifier mod, String name) {
        if (this.heightMods.containsKey(name)) {
            ATG.logger.error("A biome height modifier is already registered under the name '"+name+"'");
            return mod;
        }
        this.heightMods.put(name, mod);
        return mod;
    }

    public IBiomeHeightModifier getHeightModifier(String name) {
        if (heightMods.containsKey(name)) {
            return heightMods.get(name);
        }
        return null;
    }
}
