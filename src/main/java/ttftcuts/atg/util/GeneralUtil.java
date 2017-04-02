package ttftcuts.atg.util;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import ttftcuts.atg.ATG;
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

    public static boolean isWorldATG(World world) {
        return world.getWorldType() == ATG.worldType;
    }

    public static String padString(String in, char pad, int toLength, boolean clip) {
        if (in.length() < toLength) {
            for (int i=in.length(); i< toLength; i++) {
                in += pad;
            }
        } else if (clip && in.length() > toLength) {
            return in.substring(0, toLength-1);
        }
        return in;
    }
    public static String padString(String in, char pad, int toLength) {
        return padString(in, pad, toLength, false);
    }

    public static void printBiomeInformation() {
        ATG.logger.info("-------------------------------------------------------------------------------------------------------------");
        ATG.logger.info("Biome Information");
        ATG.logger.info("-------------------------------------------------------------------------------------------------------------");
        ATG.logger.info("ID  | Location                                           | Name");

        Biome biome;
        String output, name, prettyName;

        for (int id = 0; id<256; id++) {
            biome = Biome.getBiome(id);
            name = "-";
            prettyName = "-";
            if (biome != null) {
                name = biome.getRegistryName().toString();
                prettyName = biome.getBiomeName();
            }

            output = "" + id;
            output = padString(output, ' ', 3);
            output += " | ";
            output += padString(prettyName, ' ', 50);
            output += " | ";
            output += padString(name, ' ', 50);

            ATG.logger.info(output);
        }

        ATG.logger.info("-------------------------------------------------------------------------------------------------------------");
    }
}
