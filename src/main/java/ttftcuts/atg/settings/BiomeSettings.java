package ttftcuts.atg.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ttftcuts.atg.generator.biome.BiomeRegistry;
import ttftcuts.atg.generator.biome.BiomeRegistry.EnumBiomeCategory;

import java.util.ArrayList;
import java.util.List;

public class BiomeSettings extends Settings {

    public List<GroupDefinition> groups = new ArrayList<>();


    public void apply(BiomeSettings toApply) {

    }

    @Override
    public void readData(JsonObject json) {
        this.groups.clear();

        if (json.has("groups")) {
            JsonArray g = json.getAsJsonArray("groups");
            for (JsonElement o : g) {
                GroupDefinition def = new GroupDefinition();
                def.fromJson((JsonObject)o);
                this.groups.add(def);
            }
        }
    }

    @Override
    public void writeData(JsonObject json) {
        if (!this.groups.isEmpty()) {
            JsonArray g = new JsonArray();
            for (GroupDefinition def : this.groups) {
                g.add(def.toJson());
            }
            json.add("groups", g);
        }
    }

    // subclasses

    public static class GroupDefinition {
        EnumBiomeCategory category = EnumBiomeCategory.UNKNOWN;
        String name = "New Group";
        double height = 0.25;
        double temperature = 0.5;
        double moisture = 0.5;

        public JsonObject toJson() {
            JsonObject o = new JsonObject();
            o.addProperty("type", category.toString());
            o.addProperty("name", name);
            o.addProperty("height", height);
            o.addProperty("temp", temperature);
            o.addProperty("moisture", moisture);
            return o;
        }

        public void fromJson(JsonObject o) {
            if (o.has("type")) {
                category = EnumBiomeCategory.valueOf(o.get("type").getAsString());
            }
            if (o.has("name")) {
                name = o.get("name").getAsString();
            }
            if (o.has("height")) {
                height = o.get("height").getAsDouble();
            }
            if (o.has("temp")) {
                temperature = o.get("temp").getAsDouble();
            }
            if (o.has("moisture")) {
                moisture = o.get("moisture").getAsDouble();
            }
        }
    }
}
