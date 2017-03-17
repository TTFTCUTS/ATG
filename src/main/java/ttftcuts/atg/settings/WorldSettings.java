package ttftcuts.atg.settings;

import com.google.gson.JsonObject;

public class WorldSettings extends Settings {

    public BiomeSettings biomeSettings;

    public WorldSettings() {
        this.biomeSettings = new BiomeSettings();
    }

    @Override
    public void readData(JsonObject json) {


        // biome stuff
        if (json.has("biomes")) {
            biomeSettings.readData(json.getAsJsonObject("biomes"));
        }
    }

    @Override
    public void writeData(JsonObject json) {


        // biome stuff
        JsonObject biomes = new JsonObject();
        this.biomeSettings.writeData(biomes);
        if (!biomes.entrySet().isEmpty()) {
            json.add("biomes", biomes);
        }
    }
}
