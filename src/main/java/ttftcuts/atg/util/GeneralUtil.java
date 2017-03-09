package ttftcuts.atg.util;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeProvider;
import ttftcuts.atg.generator.BiomeProviderATG;

public abstract class GeneralUtil {

    public static BiomeProviderATG getATGBiomeProvider(World world) {
        BiomeProvider provider = world.getBiomeProvider();
        if (provider instanceof BiomeProviderATG) {
            return (BiomeProviderATG)provider;
        }

        //todo: if deemed necessary, some form of recursive field search in case the biome provider is wrapped

        return null;
    }
}
