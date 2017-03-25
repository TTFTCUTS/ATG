package ttftcuts.atg.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import ttftcuts.atg.ATG;
import ttftcuts.atg.generator.biome.BiomeRegistry;
import ttftcuts.atg.generator.biome.BiomeRegistry.EnumBiomeCategory;
import ttftcuts.atg.util.JsonUtil;

import java.util.*;

public class BiomeSettings extends Settings {

    public LinkedHashMap<String, GroupDefinition> groups = new LinkedHashMap<>();
    public LinkedHashMap<String, GroupEntry> groupRemovals = new LinkedHashMap<>();

    public LinkedHashMap<String, BiomeReplacement> replacements = new LinkedHashMap<>();

    public LinkedHashMap<String, BiomeDefinition> biomes = new LinkedHashMap<>();
    public LinkedHashMap<String, GroupedBiomeEntry> removals = new LinkedHashMap<>();

    public LinkedHashMap<String, SubBiomeEntry> subBiomes = new LinkedHashMap<>();
    public LinkedHashMap<String, BiomeEntry> subRemovals = new LinkedHashMap<>();

    public LinkedHashMap<String, HillBiomeEntry> hillBiomes = new LinkedHashMap<>();
    public LinkedHashMap<String, BiomeEntry> hillRemovals = new LinkedHashMap<>();

    public LinkedHashMap<String, HeightModEntry> heightMods = new LinkedHashMap<>();
    public LinkedHashMap<String, BiomeEntry> heightModRemovals = new LinkedHashMap<>();

    public void apply(BiomeSettings toApply) {

        // replacement... the hardest part
        ATG.logger.info("replacing");

        for (BiomeReplacement rep : toApply.replacements.values()) {
            ATG.logger.info("Replacing " + rep.replace + " with " + rep.name);

            // biomes
            {
                List<BiomeDefinition> toReAdd = new ArrayList<>();
                for (Iterator<Map.Entry<String, BiomeDefinition>> iter = this.biomes.entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry<String, BiomeDefinition> entry = iter.next();
                    BiomeDefinition def = entry.getValue();
                    if (def.name == rep.replace) {
                        def.name = rep.name;
                        toReAdd.add(def);
                        iter.remove();
                    }
                }
                for (BiomeDefinition def : toReAdd) {
                    this.biomes.put(def.getMapKey(), def);
                }
            }

            // sub-biomes
            {
                List<SubBiomeEntry> toReAdd = new ArrayList<>();
                for (Iterator<Map.Entry<String, SubBiomeEntry>> iter = this.subBiomes.entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry<String, SubBiomeEntry> entry = iter.next();
                    SubBiomeEntry def = entry.getValue();
                    if (def.name == rep.replace || def.parentBiome == rep.replace) {
                        if (def.name == rep.replace) {
                            def.name = rep.name;
                        }
                        if (def.parentBiome == rep.replace) {
                            def.parentBiome = rep.name;
                        }
                        toReAdd.add(def);
                        iter.remove();
                    }
                }
                for (SubBiomeEntry def : toReAdd) {
                    this.subBiomes.put(def.getMapKey(), def);
                }
            }

            // hill biomes
            {
                List<HillBiomeEntry> toReAdd = new ArrayList<>();
                for (Iterator<Map.Entry<String, HillBiomeEntry>> iter = this.hillBiomes.entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry<String, HillBiomeEntry> entry = iter.next();
                    HillBiomeEntry def = entry.getValue();
                    if (def.name == rep.replace || def.parentBiome == rep.replace) {
                        if (def.name == rep.replace) {
                            def.name = rep.name;
                        }
                        if (def.parentBiome == rep.replace) {
                            def.parentBiome = rep.name;
                        }
                        toReAdd.add(def);
                        iter.remove();
                    }
                }
                for (HillBiomeEntry def : toReAdd) {
                    this.hillBiomes.put(def.getMapKey(), def);
                }
            }

            // height mods
            {
                List<HeightModEntry> toReAdd = new ArrayList<>();
                for (Iterator<Map.Entry<String, HeightModEntry>> iter = this.heightMods.entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry<String, HeightModEntry> entry = iter.next();
                    HeightModEntry def = entry.getValue();
                    if (def.name == rep.replace) {
                        def.name = rep.name;
                        toReAdd.add(def);
                        iter.remove();
                    }
                }
                for (HeightModEntry def : toReAdd) {
                    this.heightMods.put(def.getMapKey(), def);
                }
            }
        }

        // removals
        for (Map.Entry<String, GroupEntry> entry : toApply.groupRemovals.entrySet()) {
            if (this.groups.containsKey(entry.getKey())) {
                this.groups.remove(entry.getKey());
            }
        }
        for (Map.Entry<String, GroupedBiomeEntry> entry : toApply.removals.entrySet()) {
            if (this.biomes.containsKey(entry.getKey())) {
                this.biomes.remove(entry.getKey());
            }
        }
        for (Map.Entry<String, BiomeEntry> entry : toApply.subRemovals.entrySet()) {
            if (this.subBiomes.containsKey(entry.getKey())) {
                this.subBiomes.remove(entry.getKey());
            }
        }
        for (Map.Entry<String, BiomeEntry> entry : toApply.hillRemovals.entrySet()) {
            if (this.hillBiomes.containsKey(entry.getKey())) {
                this.hillBiomes.remove(entry.getKey());
            }
        }
        for (Map.Entry<String, BiomeEntry> entry : toApply.heightModRemovals.entrySet()) {
            if (this.heightMods.containsKey(entry.getKey())) {
                this.heightMods.remove(entry.getKey());
            }
        }

        // additions
        this.groups.putAll(toApply.groups); // overwrite groups in lower entries

        for (Map.Entry<String, BiomeDefinition> entry : toApply.biomes.entrySet()) {
            String key = entry.getKey();
            if (this.biomes.containsKey(key)) {
                this.biomes.get(key).weight += toApply.biomes.get(key).weight;
            } else {
                this.biomes.put(key, entry.getValue());
            }
        }

        for (Map.Entry<String, SubBiomeEntry> entry : toApply.subBiomes.entrySet()) {
            String key = entry.getKey();
            if (this.subBiomes.containsKey(key)) {
                this.subBiomes.get(key).weight += toApply.subBiomes.get(key).weight;
            } else {
                this.subBiomes.put(key, entry.getValue());
            }
        }

        this.hillBiomes.putAll(toApply.hillBiomes); // overwrite hill biome heights too
        this.heightMods.putAll(toApply.heightMods); // and the height mods
    }

    @Override
    public BiomeSettings readFromJson(String input) {
        return (BiomeSettings)super.readFromJson(input);
    }

    @Override
    public void readData(JsonObject json) {
        IJsonMappable.readJsonableMap(json, this.groups, "groups", GroupDefinition.class);
        IJsonMappable.readJsonableMap(json, this.groupRemovals, "groupremove", GroupEntry.class);

        IJsonMappable.readJsonableMap(json, this.replacements, "replace", BiomeReplacement.class);

        GroupedBiomeEntry.readGroupedEntryMap(json, this.biomes, "biomes", BiomeDefinition.class);
        GroupedBiomeEntry.readGroupedEntryMap(json, this.removals, "biomeremove", GroupedBiomeEntry.class);

        IJsonMappable.readJsonableMap(json, this.subBiomes, "subbiomes", SubBiomeEntry.class);
        IJsonMappable.readJsonableMap(json, this.subRemovals, "subremove", BiomeEntry.class);

        IJsonMappable.readJsonableMap(json, this.hillBiomes, "hillbiomes", HillBiomeEntry.class);
        IJsonMappable.readJsonableMap(json, this.hillRemovals, "hillremove", BiomeEntry.class);

        IJsonMappable.readJsonableMap(json, this.heightMods, "heightmods", HeightModEntry.class);
        IJsonMappable.readJsonableMap(json, this.heightModRemovals, "heightmodremoval", BiomeEntry.class);
    }

    @Override
    public void writeData(JsonObject json) {
        IJsonMappable.writeJsonableMap(json, this.groups, "groups");
        IJsonMappable.writeJsonableMap(json, this.groupRemovals, "groupremove");

        IJsonMappable.writeJsonableMap(json, this.replacements, "replace");

        GroupedBiomeEntry.writeGroupedEntryMap(json, this.biomes, "biomes");
        GroupedBiomeEntry.writeGroupedEntryMap(json, this.removals, "biomeremove");

        IJsonMappable.writeJsonableMap(json, this.subBiomes, "subbiomes");
        IJsonMappable.writeJsonableMap(json, this.subRemovals, "subremove");

        IJsonMappable.writeJsonableMap(json, this.hillBiomes, "hillbiomes");
        IJsonMappable.writeJsonableMap(json, this.hillRemovals, "hillremove");

        IJsonMappable.writeJsonableMap(json, this.heightMods, "heightmods");
        IJsonMappable.writeJsonableMap(json, this.heightModRemovals, "heightmodremove");
    }

    // Subclasses and handling

    // ##### Group Entries #####

    public static class GroupEntry implements IJsonMappable {
        public EnumBiomeCategory category = EnumBiomeCategory.UNKNOWN;
        public String name = "New Group";

        @Override
        public JsonObject toJson() {
            JsonObject o = new JsonObject();
            o.addProperty("type", category.toString());
            o.addProperty("name", name);
            return o;
        }

        @Override
        public void fromJson(JsonObject o) {
            if (o.has("type")) {
                category = EnumBiomeCategory.valueOf(JsonUtil.get(o, "type", "UNKNOWN"));
            }
            if (o.has("name")) {
                name = JsonUtil.get(o, "name", "Invalid Group");
            }
        }

        @Override
        public String getMapKey() {
            return this.category +"_"+ this.name;
        }
    }

    public static class GroupDefinition extends GroupEntry {
        public EnumBiomeCategory category = EnumBiomeCategory.UNKNOWN;
        public String name = "New Group";
        public double height = 0.25;
        public double temperature = 0.5;
        public double moisture = 0.5;

        @Override
        public JsonObject toJson() {
            JsonObject o = super.toJson();
            o.addProperty("height", height);
            o.addProperty("temp", temperature);
            o.addProperty("moisture", moisture);
            return o;
        }

        @Override
        public void fromJson(JsonObject o) {
            super.fromJson(o);
            if (o.has("height")) {
                height = JsonUtil.get(o, "height", 0.25);
            }
            if (o.has("temp")) {
                temperature = JsonUtil.get(o, "temp", 0.5);
            }
            if (o.has("moisture")) {
                moisture = JsonUtil.get(o, "moisture", 0.5);
            }
        }
    }

    // ##### Biome Entries #####

    public static class BiomeEntry implements IJsonMappable {
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
                name = new ResourceLocation(JsonUtil.get(o, "biome", "invalid"));
            }
        }

        @Override
        public String getMapKey() {
            return this.name.toString();
        }
    }

    public static class GroupedBiomeEntry extends BiomeEntry {
        public EnumBiomeCategory category = EnumBiomeCategory.UNKNOWN;
        public String group = "";

        protected static <T extends GroupedBiomeEntry> void readGroupedEntryMap(JsonObject readFrom, LinkedHashMap<String, T> list, String tagname, Class<T> clazz) {
            list.clear();
            if (readFrom.has(tagname)) {
                JsonObject b = JsonUtil.getAsObject(readFrom, tagname);
                readGroupedEntryMap(b, list, clazz);
            }
        }

        protected static <T extends GroupedBiomeEntry> void readGroupedEntryMap(JsonObject o, LinkedHashMap<String, T> list, Class<T> clazz) {
            for(Map.Entry<String, JsonElement> entry : o.entrySet()) {
                EnumBiomeCategory category = EnumBiomeCategory.valueOf(entry.getKey());
                JsonObject cgroups = JsonUtil.asObject(entry.getValue());

                for (Map.Entry<String, JsonElement> groupentry : cgroups.entrySet()) {
                    String group = groupentry.getKey();
                    JsonArray gbiomes = JsonUtil.asArray(groupentry.getValue());

                    for (JsonElement biome : gbiomes) {
                        T bd = IJsonable.create(clazz);
                        if(bd == null) { return; }
                        bd.fromJson(JsonUtil.asObject(biome));
                        bd.category = category;
                        bd.group = group;
                        list.put(bd.getMapKey(), bd);
                    }
                }
            }
        }

        protected static <T extends GroupedBiomeEntry> void writeGroupedEntryMap(JsonObject writeTo, LinkedHashMap<String, T> list, String tagname) {
            if (!list.isEmpty()) {
                JsonObject categories = new JsonObject();
                writeGroupedEntryMap(categories, list);
                writeTo.add(tagname, categories);
            }
        }

        protected static <T extends GroupedBiomeEntry> void writeGroupedEntryMap(JsonObject o, LinkedHashMap<String, T> list) {
            for (T biome : list.values()) {
                String catname = biome.category.toString();
                if (!o.has(catname)) {
                    o.add(catname, new JsonObject());
                }
                JsonObject catobject = JsonUtil.getAsObject(o, catname);

                if (!catobject.has(biome.group)) {
                    catobject.add(biome.group, new JsonArray());
                }
                JsonArray groupobject = JsonUtil.getAsArray(catobject, biome.group);

                groupobject.add(biome.toJson());
            }
        }

        @Override
        public String getMapKey() {
            return this.category +"_"+ this.group +"_"+ this.name.toString();
        }
    }

    public static class BiomeDefinition extends GroupedBiomeEntry {
        public double weight = 1.0;

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
                weight = JsonUtil.get(o, "weight", 1.0);
            }
        }

    }

    public static class BiomeReplacement extends BiomeEntry {
        public static final ResourceLocation DEFAULT_BIOME_TARGET = Biomes.VOID.getRegistryName();

        public ResourceLocation replace = DEFAULT_BIOME_TARGET;

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
                replace = new ResourceLocation(JsonUtil.get(o, "replace", "invalid"));
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
                parentBiome = new ResourceLocation(JsonUtil.get(o, "parent", "invalid"));
            }
            if (o.has("weight")) {
                weight = JsonUtil.get(o, "weight", 1.0);
            }
        }

        @Override
        public String getMapKey() {
            return this.name + "_" + this.parentBiome;
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
                parentBiome = new ResourceLocation(JsonUtil.get(o, "parent", "invalid"));
            }
            if (o.has("height")) {
                height = JsonUtil.get(o, "height", 0.25);
            }
        }

        @Override
        public String getMapKey() {
            return this.name + "_" + this.parentBiome;
        }
    }

    public static class HeightModEntry extends BiomeEntry {
        public String heightMod = "";

        @Override
        public JsonObject toJson() {
            JsonObject o = super.toJson();
            o.addProperty("mod", heightMod);
            return o;
        }

        @Override
        public void fromJson(JsonObject o) {
            super.fromJson(o);
            if (o.has("mod")) {
                heightMod = JsonUtil.get(o, "mod", "");
            }
        }
    }
}
