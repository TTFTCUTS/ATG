package ttftcuts.atg.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import ttftcuts.atg.ATG;
import ttftcuts.atg.generator.biome.BiomeModParameter;
import ttftcuts.atg.generator.biome.BiomeRegistry;
import ttftcuts.atg.generator.biome.BiomeRegistry.EnumBiomeCategory;
import ttftcuts.atg.generator.biome.IBiomeHeightModifier;
import ttftcuts.atg.util.JsonUtil;

import java.util.*;

public class BiomeSettings extends Settings implements Comparable<BiomeSettings> {

    public LinkedHashMap<String, GroupDefinition> groups = new LinkedHashMap<>();
    public LinkedHashMap<String, GroupEntry> groupRemovals = new LinkedHashMap<>();

    public LinkedHashMap<String, BiomeReplacement> replacements = new LinkedHashMap<>();

    public LinkedHashMap<String, BiomeDefinition> biomes = new LinkedHashMap<>();
    public LinkedHashMap<String, GroupedBiomeEntry> removals = new LinkedHashMap<>();

    public LinkedHashMap<String, SubBiomeEntry> subBiomes = new LinkedHashMap<>();
    public LinkedHashMap<String, ParentBiomeEntry> subRemovals = new LinkedHashMap<>();

    public LinkedHashMap<String, HillBiomeEntry> hillBiomes = new LinkedHashMap<>();
    public LinkedHashMap<String, ParentBiomeEntry> hillRemovals = new LinkedHashMap<>();

    public LinkedHashMap<String, HeightModEntry> heightMods = new LinkedHashMap<>();
    public LinkedHashMap<String, BiomeEntry> heightModRemovals = new LinkedHashMap<>();

    public void apply(BiomeSettings toApply) {

        // replacement... the hardest part
        for (BiomeReplacement rep : toApply.replacements.values()) {
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
        for (Map.Entry<String, ParentBiomeEntry> entry : toApply.subRemovals.entrySet()) {
            if (this.subBiomes.containsKey(entry.getKey())) {
                this.subBiomes.remove(entry.getKey());
            }
        }
        for (Map.Entry<String, ParentBiomeEntry> entry : toApply.hillRemovals.entrySet()) {
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
        IJsonMappable.readJsonableMap(json, this.subRemovals, "subremove", ParentBiomeEntry.class);

        IJsonMappable.readJsonableMap(json, this.hillBiomes, "hillbiomes", HillBiomeEntry.class);
        IJsonMappable.readJsonableMap(json, this.hillRemovals, "hillremove", ParentBiomeEntry.class);

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

    @Override
    public BiomeSettings copy() {
        BiomeSettings copy = new BiomeSettings();

        IJsonMappable.copyMap(this.groups, copy.groups);
        IJsonMappable.copyMap(this.groupRemovals, copy.groupRemovals);

        IJsonMappable.copyMap(this.replacements, copy.replacements);

        IJsonMappable.copyMap(this.biomes, copy.biomes);
        IJsonMappable.copyMap(this.removals, copy.removals);

        IJsonMappable.copyMap(this.subBiomes, copy.subBiomes);
        IJsonMappable.copyMap(this.subRemovals, copy.subRemovals);

        IJsonMappable.copyMap(this.hillBiomes, copy.hillBiomes);
        IJsonMappable.copyMap(this.hillRemovals, copy.hillRemovals);

        IJsonMappable.copyMap(this.heightMods, copy.heightMods);
        IJsonMappable.copyMap(this.heightModRemovals, copy.heightModRemovals);

        return copy;
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
                category = EnumBiomeCategory.get(JsonUtil.get(o, "type", "UNKNOWN"));
            }
            if (o.has("name")) {
                name = JsonUtil.get(o, "name", "Invalid Group");
            }
        }

        @Override
        public String getMapKey() {
            return this.category +"_"+ this.name;
        }

        @Override
        public GroupEntry copy() {
            GroupEntry copy = new GroupEntry();

            copy.category = this.category;
            copy.name = this.name;

            return copy;
        }
    }

    public static class GroupDefinition extends GroupEntry {
        public double height = 0.25;
        public double temperature = 0.5;
        public double moisture = 0.5;
        public double minHeight = 0.0;
        public double maxHeight = 1.0;
        public int blobsize = 0;
        public int subblobsize = 0;

        @Override
        public JsonObject toJson() {
            JsonObject o = super.toJson();
            o.addProperty("height", height);
            o.addProperty("temp", temperature);
            o.addProperty("moisture", moisture);
            if (minHeight != 0.0) {
                o.addProperty("minheight", minHeight);
            }
            if (maxHeight != 1.0) {
                o.addProperty("maxheight", maxHeight);
            }
            if (blobsize != 0) {
                o.addProperty("blobsize", blobsize);
            }
            if (subblobsize != 0) {
                o.addProperty("subblobsize", subblobsize);
            }
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
            if (o.has("minheight")) {
                minHeight = JsonUtil.get(o, "minheight", 0.0);
            }
            if (o.has("maxheight")) {
                maxHeight = JsonUtil.get(o, "maxheight", 1.0);
            }
            if (o.has("blobsize")) {
                blobsize = JsonUtil.get(o, "blobsize", 0);
            }
            if (o.has("subblobsize")) {
                subblobsize = JsonUtil.get(o, "subblobsize", 0);
            }
        }

        @Override
        public GroupDefinition copy() {
            GroupDefinition copy = new GroupDefinition();

            copy.category = this.category;
            copy.name = this.name;

            copy.height = this.height;
            copy.temperature = this.temperature;
            copy.moisture = this.moisture;
            copy.minHeight = this.minHeight;
            copy.maxHeight = this.maxHeight;
            copy.blobsize = this.blobsize;
            copy.subblobsize = this.subblobsize;

            return copy;
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

        @Override
        public BiomeEntry copy() {
            BiomeEntry copy = new BiomeEntry();

            copy.name = new ResourceLocation(this.name.toString());

            return copy;
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
                EnumBiomeCategory category = EnumBiomeCategory.get(entry.getKey());
                if (category == null) { continue; }
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

        @Override
        public GroupedBiomeEntry copy() {
            GroupedBiomeEntry copy = new GroupedBiomeEntry();

            copy.name = new ResourceLocation(this.name.toString());
            copy.category = this.category;
            copy.group = this.group;

            return copy;
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

        @Override
        public BiomeDefinition copy() {
            BiomeDefinition copy = new BiomeDefinition();

            copy.name = new ResourceLocation(this.name.toString());
            copy.category = this.category;
            copy.group = this.group;
            copy.weight = this.weight;

            return copy;
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

        @Override
        public BiomeReplacement copy() {
            BiomeReplacement copy = new BiomeReplacement();

            copy.name = new ResourceLocation(this.name.toString());
            copy.replace = new ResourceLocation(this.replace.toString());

            return copy;
        }
    }

    public static class ParentBiomeEntry extends BiomeEntry {
        public ResourceLocation parentBiome = null;

        @Override
        public JsonObject toJson() {
            JsonObject o = super.toJson();
            o.addProperty("parent", parentBiome.toString());
            return o;
        }

        @Override
        public void fromJson(JsonObject o) {
            super.fromJson(o);
            if (o.has("parent")) {
                parentBiome = new ResourceLocation(JsonUtil.get(o, "parent", "invalid"));
            }
        }

        @Override
        public String getMapKey() {
            return this.name + "_" + this.parentBiome;
        }

        @Override
        public ParentBiomeEntry copy() {
            ParentBiomeEntry copy = new ParentBiomeEntry();

            copy.name = new ResourceLocation(this.name.toString());
            copy.parentBiome = new ResourceLocation(this.parentBiome.toString());

            return copy;
        }
    }

    public static class SubBiomeEntry extends ParentBiomeEntry {
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

        @Override
        public SubBiomeEntry copy() {
            SubBiomeEntry copy = new SubBiomeEntry();

            copy.name = new ResourceLocation(this.name.toString());
            copy.parentBiome = new ResourceLocation(this.parentBiome.toString());
            copy.weight = this.weight;

            return copy;
        }
    }

    public static class HillBiomeEntry extends ParentBiomeEntry {
        public double height = 1.0;

        @Override
        public JsonObject toJson() {
            JsonObject o = super.toJson();
            o.addProperty("height", height);
            return o;
        }

        @Override
        public void fromJson(JsonObject o) {
            super.fromJson(o);
            if (o.has("height")) {
                height = JsonUtil.get(o, "height", 0.25);
            }
        }

        @Override
        public HillBiomeEntry copy() {
            HillBiomeEntry copy = new HillBiomeEntry();

            copy.name = new ResourceLocation(this.name.toString());
            copy.parentBiome = new ResourceLocation(this.parentBiome.toString());
            copy.height = this.height;

            return copy;
        }

        @Override
        public String getMapKey() {
            return this.name + "_" + this.parentBiome +"_"+ this.height;
        }
    }

    public static class HeightModEntry extends BiomeEntry {
        public String heightMod = "";
        public Map<String, Object> parameters = new HashMap<>();

        @Override
        public JsonObject toJson() {
            JsonObject o = super.toJson();
            o.addProperty("mod", heightMod);

            JsonObject po = new JsonObject();
            IBiomeHeightModifier mod = ATG.globalRegistry.getHeightModifier(heightMod);
            if (mod != null) {
                Map<String, BiomeModParameter> params = mod.getSettings();
                if (params != null) {
                    for (Map.Entry<String, BiomeModParameter> e : params.entrySet()) {
                        String key = e.getKey();
                        BiomeModParameter param = e.getValue();
                        if (this.parameters.containsKey(key)) {
                            Object val = this.parameters.get(key);
                            if (!val.equals(param.defaultValue)) {
                                param.writeToJson(po, key, val);
                            }
                        }
                    }
                }
            }
            if (!po.entrySet().isEmpty()) {
                o.add("params", po);
            }

            return o;
        }

        @Override
        public void fromJson(JsonObject o) {
            super.fromJson(o);
            if (o.has("mod")) {
                heightMod = JsonUtil.get(o, "mod", "");
            }
            if (o.has("params")) {
                JsonObject po = JsonUtil.getAsObject(o, "params");
                IBiomeHeightModifier mod = ATG.globalRegistry.getHeightModifier(heightMod);
                if (mod != null) {
                    Map<String, BiomeModParameter> params = mod.getSettings();
                    if (params != null) {
                        for (Map.Entry<String, BiomeModParameter> e : params.entrySet()) {
                            String key = e.getKey();
                            BiomeModParameter param = e.getValue();
                            if (po.has(key)) {
                                this.parameters.put(key, param.readFromJson(po, key));
                            }
                        }
                    }
                }
            }
        }

        @Override
        public HeightModEntry copy() {
            HeightModEntry copy = new HeightModEntry();

            copy.name = new ResourceLocation(this.name.toString());
            copy.heightMod = this.heightMod;
            copy.parameters.putAll(this.parameters);

            return copy;
        }
    }

    // ##### Sorting #####

    // TODO: Get these sorted in a sensible manner for the module list

    @Override
    public int compareTo(BiomeSettings o) {
        return 0;
    }
}
