package ttftcuts.atg.settings;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {

    Map<Biome,Biome> biomeReplacements = new HashMap<>();
    List<GroupDefinition> groupDefinitions = new ArrayList<>();

    public Settings() {

    }

    public void apply(Settings toApply) {

    }

    // #### reading and writing ###

    public String writeToJson() {
        JsonObject json = new JsonObject();

        this.writeData(json);

        return json.toString();
    }

    public static Settings readFromJson(String input) {
        Settings settings = new Settings();

        settings.readData((JsonObject)new JsonParser().parse(input));

        return settings;
    }

    public void readData(JsonObject json) {

    }

    public void writeData(JsonObject json) {

    }

    // #### subclasses ####

    public static class GroupDefinition {

    }
}
