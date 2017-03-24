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
    public List<GroupedBiomeEntry> removals = new ArrayList<>();
    public List<SubBiomeEntry> subBiomes = new ArrayList<>();
    public List<HillBiomeEntry> hillBiomes = new ArrayList<>();

    public void apply(BiomeSettings toApply) {

    }

    @Override
    public BiomeSettings readFromJson(String input) {
        return (BiomeSettings)super.readFromJson(input);
    }

    @Override
    public void readData(JsonObject json) {
        IJsonable.readJsonableList(json, this.groups, "groups", GroupDefinition.class);
        GroupedBiomeEntry.readGroupedEntryList(json, this.biomes, "biomes", BiomeDefinition.class);
        GroupedBiomeEntry.readGroupedEntryList(json, this.removals, "remove", GroupedBiomeEntry.class);
        IJsonable.readJsonableList(json, this.replacements, "replace", BiomeReplacement.class);
        IJsonable.readJsonableList(json, this.subBiomes, "subbiomes", SubBiomeEntry.class);
        IJsonable.readJsonableList(json, this.hillBiomes, "hillbiomes", HillBiomeEntry.class);
    }

    @Override
    public void writeData(JsonObject json) {
        IJsonable.writeJsonableList(json, this.groups, "groups");
        GroupedBiomeEntry.writeGroupedEntryList(json, this.biomes, "biomes");
        GroupedBiomeEntry.writeGroupedEntryList(json, this.removals, "remove");
        IJsonable.writeJsonableList(json, this.replacements, "replace");
        IJsonable.writeJsonableList(json, this.subBiomes, "subbiomes");
        IJsonable.writeJsonableList(json, this.hillBiomes, "hillbiomes");
    }

    // Subclasses and handling

    // ##### Group Entries #####

    public static class GroupDefinition implements IJsonable {
        EnumBiomeCategory category = EnumBiomeCategory.UNKNOWN;
        String name = "New Group";
        double height = 0.25;
        double temperature = 0.5;
        double moisture = 0.5;

        @Override
        public JsonObject toJson() {
            JsonObject o = new JsonObject();
            o.addProperty("type", category.toString());
            o.addProperty("name", name);
            o.addProperty("height", height);
            o.addProperty("temp", temperature);
            o.addProperty("moisture", moisture);
            return o;
        }

        @Override
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

    // ##### Biome Entries #####


    public static class BiomeEntry implements IJsonable {
        public static final ResourceLocation DEFAULT_BIOME_NAME = Biomes.PLAINS.getRegistryName();
        public ResourceLocation name = DEFAULT_BIOME_NAME;

        @Override
        public JsonObject toJson() {
            JsonObject o = new JsonObject();
            o.addProperty("biome", name.toString());
            return o;
        }

        @Override
        public void fromJson(JsonObject o) {
            if (o.has("biome")) {
                name = new ResourceLocation(o.get("biome").getAsString());
            }
        }
    }

    public static class GroupedBiomeEntry extends BiomeEntry {
        public EnumBiomeCategory category = EnumBiomeCategory.UNKNOWN;
        public String group = "";

        protected static <T extends GroupedBiomeEntry> void readGroupedEntryList(JsonObject readFrom, List<T> list, String tagname, Class<T> clazz) {
            list.clear();
            if (readFrom.has(tagname)) {
                JsonObject b = readFrom.getAsJsonObject(tagname);
                readGroupedEntryList(b, list, clazz);
            }
        }

        protected static <T extends GroupedBiomeEntry> void readGroupedEntryList(JsonObject o, List<T> list, Class<T> clazz) {
            for(Map.Entry<String, JsonElement> entry : o.entrySet()) {
                EnumBiomeCategory category = EnumBiomeCategory.valueOf(entry.getKey());
                JsonObject cgroups = entry.getValue().getAsJsonObject();

                for (Map.Entry<String, JsonElement> groupentry : cgroups.entrySet()) {
                    String group = groupentry.getKey();
                    JsonArray gbiomes = groupentry.getValue().getAsJsonArray();

                    for (JsonElement biome : gbiomes) {
                        T bd = IJsonable.create(clazz);
                        if(bd == null) { return; }
                        bd.fromJson(biome.getAsJsonObject());
                        bd.category = category;
                        bd.group = group;
                        list.add(bd);
                    }
                }
            }
        }

        protected static <T extends GroupedBiomeEntry> void writeGroupedEntryList(JsonObject writeTo, List<T> list, String tagname) {
            if (!list.isEmpty()) {
                JsonObject categories = new JsonObject();
                writeGroupedEntryList(categories, list);
                writeTo.add(tagname, categories);
            }
        }

        protected static <T extends GroupedBiomeEntry> void writeGroupedEntryList(JsonObject o, List<T> list) {
            for (T biome : list) {
                String catname = biome.category.toString();
                if (!o.has(catname)) {
                    o.add(catname, new JsonObject());
                }
                JsonObject catobject = o.getAsJsonObject(catname);

                if (!catobject.has(biome.group)) {
                    catobject.add(biome.group, new JsonArray());
                }
                JsonArray groupobject = catobject.getAsJsonArray(biome.group);

                groupobject.add(biome.toJson());
            }
        }
    }

    public static class BiomeDefinition extends GroupedBiomeEntry {
        double weight = 1.0;

        @Override
        public JsonObject toJson() {
            JsonObject o = super.toJson();
            o.addProperty("weight", weight);
            return o;
        }

        @Override
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

        @Override
        public JsonObject toJson() {
            JsonObject o = super.toJson();
            o.addProperty("replace", replace.toString());
            return o;
        }

        @Override
        public void fromJson(JsonObject o) {
            super.fromJson(o);
            if (o.has("replace")) {
                replace = new ResourceLocation(o.get("replace").getAsString());
            }
        }
    }

    public static class SubBiomeEntry extends BiomeEntry {
        public ResourceLocation parentBiome = null;
        public double weight = 1.0;

        @Override
        public JsonObject toJson() {
            JsonObject o = super.toJson();
            o.addProperty("parent", parentBiome.toString());
            o.addProperty("weight", weight);
            return o;
        }

        @Override
        public void fromJson(JsonObject o) {
            super.fromJson(o);
            if (o.has("parent")) {
                parentBiome = new ResourceLocation(o.get("parent").getAsString());
            }
            if (o.has("weight")) {
                weight = o.get("weight").getAsDouble();
            }
        }
    }

    public static class HillBiomeEntry extends BiomeEntry {
        public ResourceLocation parentBiome = null;
        public double height = 1.0;

        @Override
        public JsonObject toJson() {
            JsonObject o = super.toJson();
            o.addProperty("parent", parentBiome.toString());
            o.addProperty("height", height);
            return o;
        }

        @Override
        public void fromJson(JsonObject o) {
            super.fromJson(o);
            if (o.has("parent")) {
                parentBiome = new ResourceLocation(o.get("parent").getAsString());
            }
            if (o.has("height")) {
                height = o.get("height").getAsDouble();
            }
        }
    }
}
