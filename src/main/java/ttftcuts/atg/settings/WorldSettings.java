package ttftcuts.atg.settings;

import com.google.gson.JsonObject;
import ttftcuts.atg.util.JsonUtil;

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

    public static WorldSettings loadWorldSettings(String generatorSettings) {
        if (generatorSettings.isEmpty()) {
            return new DefaultWorldSettings();
        }

        WorldSettings settings = new WorldSettings();

        try {
            settings.readFromJson(generatorSettings);
        } catch (Exception e) {
            return new DefaultWorldSettings();
        }

        return settings;
    }
}
