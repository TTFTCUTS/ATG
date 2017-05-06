package ttftcuts.atg.settings;

import ttftcuts.atg.ATG;
import ttftcuts.atg.compat.BiomeModule;

import java.util.ArrayList;
import java.util.List;

public class DefaultWorldSettings extends WorldSettings {

    public DefaultWorldSettings() {
        this.biomeSettings = new DefaultBiomeSettings();
    }

    public void applyDefaultModuleStack() {
        List<BiomeModule> modules = new ArrayList<>();

        for (BiomeModule module : ATG.globalRegistry.biomeModules) {
            if (module.active) {
                modules.add(module);
            }
        }

        modules.sort(null); // natural ordering since they implement Comparable

        for (int i=modules.size()-1; i>=0; i--) {
            BiomeModule module = modules.get(i);
            ATG.logger.info("Applying Biome Module: {} from {}", module.name, module.owner);
            this.biomeSettings.apply(module.settings);
        }
    }
}
