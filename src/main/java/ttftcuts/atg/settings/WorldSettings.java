package ttftcuts.atg.settings;

import com.google.gson.JsonObject;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import ttftcuts.atg.ATG;
import ttftcuts.atg.compat.BiomeModule;
import ttftcuts.atg.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class WorldSettings extends Settings {

    public BiomeSettings biomeSettings;
    public ChunkGeneratorSettings genSettings;

    public WorldSettings() {
        this.biomeSettings = new BiomeSettings();
        this.genSettings = new ChunkGeneratorSettings();
    }

    @Override
    public void readData(JsonObject json) {

        if (json.has("generator")) {
            genSettings.readData(JsonUtil.getAsObject(json, "generator"));
        }
        if (json.has("biomedata")) {
            biomeSettings.readData(JsonUtil.getAsObject(json, "biomedata"));
        }
    }

    @Override
    public void writeData(JsonObject json) {

        JsonObject generator = new JsonObject();
        this.genSettings.writeData(generator);
        if (!generator.entrySet().isEmpty()) {
            json.add("generator", generator);
        }

        JsonObject biomes = new JsonObject();
        this.biomeSettings.writeData(biomes);
        if (!biomes.entrySet().isEmpty()) {
            json.add("biomedata", biomes);
        }
    }

    @Override
    public WorldSettings copy() {
        WorldSettings copy = new WorldSettings();

        copy.biomeSettings = this.biomeSettings.copy();
        copy.genSettings = this.genSettings.copy();

        return copy;
    }

    public static WorldSettings loadWorldSettings(World world) {
        WorldInfo worldInfo = world.getWorldInfo();
        String generatorSettings = worldInfo.getGeneratorOptions();

        if (generatorSettings.isEmpty()) {
            DefaultWorldSettings settings = new DefaultWorldSettings();

            settings.applyDefaultModuleStack();

            // If the options are empty, force them to the default value to match behaviour with provided settings
            setGeneratorOptions(world, worldInfo, settings);

            return settings;
        }

        WorldSettings settings = new WorldSettings();

        try {
            settings.readFromJson(generatorSettings);
        } catch (Exception e) {
            return new DefaultWorldSettings();
        }

        return settings;
    }

    private static void setGeneratorOptions(World world, WorldInfo worldInfo, WorldSettings settings) {
        if (!world.isRemote) {
            ATG.logger.info("Setting empty generatorOptions to current defaults");
            ObfuscationReflectionHelper.setPrivateValue(WorldInfo.class, worldInfo, settings.writeToJson(), "generatorOptions");
        }
    }
}
