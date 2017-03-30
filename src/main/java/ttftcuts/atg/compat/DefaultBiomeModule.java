package ttftcuts.atg.compat;

import ttftcuts.atg.settings.BiomeSettings;

public class DefaultBiomeModule extends BiomeModule {

    public DefaultBiomeModule(String name, String owner, BiomeSettings settings, boolean startsEnabled) {
        super(name, owner, settings, startsEnabled);
    }

    @Override
    public boolean getConfigState() {
        return false;
    }
}
