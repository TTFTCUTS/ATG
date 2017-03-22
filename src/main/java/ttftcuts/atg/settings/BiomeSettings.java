package ttftcuts.atg.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import ttftcuts.atg.generator.biome.BiomeRegistry.EnumBiomeCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BiomeSettings extends Settings {

    public List<GroupDefinition> groups = new ArrayList<>();
    public List<BiomeDefinition> biomes = new ArrayList<>();
    public List<BiomeReplacement> replacements = new ArrayList<>();

    public void apply(BiomeSettings toApply) {

    }

    @Override
    public void readData(JsonObject json) {
        this.groups.clear();
        this.biomes.clear();
        this.replacements.clear();

        if (json.has("groups")) {
            JsonArray g = json.getAsJsonArray("groups");
            for (JsonElement o : g) {
                GroupDefinition def = new GroupDefinition();
                def.fromJson(o.getAsJsonObject());
                this.groups.add(def);
            }
        }

        if (json.has("biomes")) {
            JsonObject b = json.getAsJsonObject("biomes");
            for(Map.Entry<String, JsonElement> entry : b.entrySet()) {
                EnumBiomeCategory category = EnumBiomeCategory.valueOf(entry.getKey());
                JsonObject cgroups = entry.getValue().getAsJsonObject();
                for (Map.Entry<String, JsonElement> groupentry : cgroups.entrySet()) {
                    String group = groupentry.getKey();
                    JsonArray gbiomes = groupentry.getValue().getAsJsonArray();

                    for (JsonElement biome : gbiomes) {
                        BiomeDefinition bd = new BiomeDefinition();
                        bd.fromJson(biome.getAsJsonObject());
                        bd.category = category;
                        bd.group = group;
                        this.biomes.add(bd);
                    }
                }
            }
        }

        if (json.has("replace")) {
            JsonObject b = json.getAsJsonObject("replace");
            for(Map.Entry<String, JsonElement> entry : b.entrySet()) {
                EnumBiomeCategory category = EnumBiomeCategory.valueOf(entry.getKey());
                JsonObject cgroups = entry.getValue().getAsJsonObject();
                for (Map.Entry<String, JsonElement> groupentry : cgroups.entrySet()) {
                    String group = groupentry.getKey();
                    JsonArray gbiomes = groupentry.getValue().getAsJsonArray();

                    for (JsonElement biome : gbiomes) {
                        BiomeReplacement bd = new BiomeReplacement();
                        bd.fromJson(biome.getAsJsonObject());
                        bd.category = category;
                        bd.group = group;
                        this.replacements.add(bd);
                    }
                }
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

        if (!this.biomes.isEmpty()) {
            JsonObject categories = new JsonObject();

            for (BiomeDefinition biome : this.biomes) {
                String catname = biome.category.toString();
                if (!categories.has(catname)) {
                    categories.add(catname, new JsonObject());
                }
                JsonObject catobject = categories.getAsJsonObject(catname);

                if (!catobject.has(biome.group)) {
                    catobject.add(biome.group, new JsonArray());
                }
                JsonArray groupobject = catobject.getAsJsonArray(biome.group);

                groupobject.add(biome.toJson());
            }

            json.add("biomes", categories);
        }

        if (!this.replacements.isEmpty()) {
            JsonObject categories = new JsonObject();

            for (BiomeReplacement biome : this.replacements) {
                String catname = biome.category.toString();
                if (!categories.has(catname)) {
                    categories.add(catname, new JsonObject());
                }
                JsonObject catobject = categories.getAsJsonObject(catname);

                if (!catobject.has(biome.group)) {
                    catobject.add(biome.group, new JsonArray());
                }
                JsonArray groupobject = catobject.getAsJsonArray(biome.group);

                groupobject.add(biome.toJson());
            }

            json.add("replace", categories);
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

    public static class BiomeEntry {
        public static final ResourceLocation DEFAULT_BIOME_NAME = Biomes.PLAINS.getRegistryName();

        EnumBiomeCategory category = EnumBiomeCategory.UNKNOWN;
        String group = "";
        ResourceLocation name = DEFAULT_BIOME_NAME;

        public JsonObject toJson() {
            JsonObject o = new JsonObject();
            /*o.addProperty("type", category.toString());
            o.addProperty("group", group);*/
            o.addProperty("name", name.toString());
            return o;
        }

        public void fromJson(JsonObject o) {
            /*if (o.has("type")) {
                category = EnumBiomeCategory.valueOf(o.get("type").getAsString());
            }
            if (o.has("group")) {
                group = o.get("group").getAsString();
            }*/
            if (o.has("name")) {
                name = new ResourceLocation(o.get("name").getAsString());
            }
        }
    }

    public static class BiomeDefinition extends BiomeEntry {
        double weight = 1.0;

        public JsonObject toJson() {
            JsonObject o = super.toJson();
            o.addProperty("weight", weight);
            return o;
        }

        public void fromJson(JsonObject o) {
            super.fromJson(o);
            if (o.has("weight")) {
                weight = o.get("weight").getAsDouble();
            }
        }
    }

    public static class BiomeReplacement extends BiomeEntry {
        public static final ResourceLocation DEFAULT_BIOME_TARGET = Biomes.VOID.getRegistryName();

        ResourceLocation replace = DEFAULT_BIOME_TARGET;

        public JsonObject toJson() {
            JsonObject o = super.toJson();
            o.addProperty("replace", replace.toString());
            return o;
        }

        public void fromJson(JsonObject o) {
            super.fromJson(o);
            if (o.has("replace")) {
                replace = new ResourceLocation(o.get("replace").getAsString());
            }
        }
    }
}
