package ttftcuts.atg.compat.builtin;

import ttftcuts.atg.ATGBiomes;
import ttftcuts.atg.settings.BiomeSettingsBuilder;

public class BOPModule extends ProvidedBiomeModule {

    public BOPModule() {
        super("Biomes'O'Plenty Integration", "BiomesOPlenty");

        BiomeSettingsBuilder b = new BiomeSettingsBuilder(this.settings, "biomesoplenty");

        b.replaceBiome(ATGBiomes.SHRUBLAND, "shrubland");
        b.replaceBiome(ATGBiomes.WOODLAND, "woodland");
        b.replaceBiome(ATGBiomes.GRAVEL_BEACH, "gravel_beach");
        b.replaceBiome(ATGBiomes.TUNDRA, "tundra");
    }
}
