package ttftcuts.atg.compat.builtin;

import ttftcuts.atg.ATG;
import ttftcuts.atg.compat.BiomeModule;
import ttftcuts.atg.settings.BiomeSettings;

public class ProvidedBiomeModule extends BiomeModule {
    public final String modid;

    public ProvidedBiomeModule(String name, String modid) {
        super(name, ATG.MODID, new BiomeSettings(), true);
        this.modid = modid;

        ATG.modCompat.builtInBiomeModules.add(this);
    }
}
