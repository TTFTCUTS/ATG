package ttftcuts.atg.generator.biome;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import ttftcuts.atg.ATG;
import ttftcuts.atg.ATGBiomes;
import ttftcuts.atg.generator.CoreNoise;
import ttftcuts.atg.settings.BiomeSettings;
import ttftcuts.atg.util.MathUtil;

import java.util.*;
import java.util.stream.Collectors;

public class BiomeRegistry {

    public Map<EnumBiomeCategory, Map<String, BiomeGroup>> biomeGroups;
    public Map<Biome, Map<Biome, Double>> subBiomes;
    public Map<Biome, Double> subWeightTotals;
    public Map<Biome, LinkedHashMap<Biome, Double>> hillBiomes; // sub lists are assumed to be ordered lowest to highest!
    public Map<Biome, HeightModRegistryEntry> heightMods;

    public BiomeRegistry() {
        this.biomeGroups = new HashMap<EnumBiomeCategory, Map<String, BiomeGroup>>();
        this.subBiomes = new HashMap<Biome, Map<Biome, Double>>();
        this.subWeightTotals = new HashMap<Biome, Double>();
        this.hillBiomes = new HashMap<Biome, LinkedHashMap<Biome, Double>>();
        this.heightMods = new HashMap<Biome, HeightModRegistryEntry>();

        for (EnumBiomeCategory category : EnumBiomeCategory.values()) {
            this.biomeGroups.put(category, new HashMap<String, BiomeGroup>());
        }
    }

    public void populate(BiomeSettings settings) {

        // Define groups
        for (BiomeSettings.GroupDefinition def : settings.groups.values()) {
            if (def.category != EnumBiomeCategory.UNKNOWN) {
                this.addGroup(def.category, def.name, def.temperature, def.moisture, def.height, def.minHeight, def.maxHeight)
                    .setBlobSizeModifier(def.blobsize)
                    .setSubBlobSizeModifier(def.subblobsize);
            }
        }

        // Add biomes to groups
        for (BiomeSettings.BiomeDefinition def : settings.biomes.values()) {
            if (def.category == EnumBiomeCategory.UNKNOWN) { continue; }

            if (!Biome.REGISTRY.containsKey(def.name)) {
                continue;
            }

            Biome biome = Biome.REGISTRY.getObject(def.name);

            if (this.biomeGroups.get(def.category).containsKey(def.group)) {
                BiomeGroup group = this.biomeGroups.get(def.category).get(def.group);

                group.addBiome(biome, def.weight);
            }
        }

        // Sub-biomes
        for (BiomeSettings.SubBiomeEntry def : settings.subBiomes.values()) {
            if (!Biome.REGISTRY.containsKey(def.name) || !Biome.REGISTRY.containsKey(def.parentBiome)) {
                continue;
            }

            Biome biome = Biome.REGISTRY.getObject(def.name);
            Biome parent = Biome.REGISTRY.getObject(def.parentBiome);

            this.addSubBiome(parent, biome, def.weight);
        }

        // Hill biomes
        for (BiomeSettings.HillBiomeEntry def : settings.hillBiomes.values()) {
            if (!Biome.REGISTRY.containsKey(def.name) || !Biome.REGISTRY.containsKey(def.parentBiome)) {
                continue;
            }

            Biome biome = Biome.REGISTRY.getObject(def.name);
            Biome parent = Biome.REGISTRY.getObject(def.parentBiome);

            this.addHillBiome(parent, biome, def.height);
        }

        // Scary eldritch hill biome sub-map sorting call. Feed it a blood sacrifice every full moon for best effect.
        this.hillBiomes.replaceAll((k, v) -> v.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1,v2)->v1, LinkedHashMap::new)));

        // Biome height modifiers
        for (BiomeSettings.HeightModEntry def : settings.heightMods.values()) {
            if (!Biome.REGISTRY.containsKey(def.name)) {
                continue;
            }
            IBiomeHeightModifier mod = ATG.globalRegistry.getHeightModifier(def.heightMod);
            if (mod == null) {
                continue;
            }
            Biome biome = Biome.REGISTRY.getObject(def.name);

            this.addHeightModifier(biome, mod, def.parameters);
        }
    }

    //------ Adding and getting methods ---------------------------------------------------------

    public BiomeGroup addGroup(EnumBiomeCategory category, String name, double temperature, double moisture, double height, double minHeight, double maxHeight) {
        BiomeGroup biomeGroup = new BiomeGroup(name, temperature, moisture, height, minHeight, maxHeight);

        this.biomeGroups.get(category).put(name, biomeGroup);

        return biomeGroup;
    }

    public BiomeGroup addGroup(EnumBiomeCategory category, String name, double temperature, double moisture, double height) {
        return this.addGroup(category, name, temperature, moisture, height, 0.0, 1.0);
    }

    public void addSubBiome(Biome parent, Biome subBiome, double weight) {
        if (!this.subBiomes.containsKey(parent)) {
            this.subBiomes.put(parent, new LinkedHashMap<Biome, Double>());
            this.subWeightTotals.put(parent, 0.0);
        }

        Map<Biome,Double> subs = this.subBiomes.get(parent);
        this.subWeightTotals.put(parent, this.subWeightTotals.get(parent) + weight);

        if (!subs.containsKey(subBiome)) {
            subs.put(subBiome, weight);
        } else {
            subs.put(subBiome, subs.get(subBiome) + weight);
        }

        //ATG.logger.info("Sub biomes for "+parent.getBiomeName()+": (total weight: "+this.subWeightTotals.get(parent)+") "+subs);
    }

    public Biome getSubBiome(Biome parent, double value) {
        if (!this.subBiomes.containsKey(parent)) {
            return parent;
        }

        double weight = MathUtil.clamp(value, 0.0, 1.0) * (this.subWeightTotals.get(parent) + 1.0);

        if (weight <= 1.0) {
            return parent;
        }

        weight -= 1.0;

        Map<Biome, Double> weights = this.subBiomes.get(parent);

        double total = 0.0;
        for (Map.Entry<Biome, Double> entry : weights.entrySet()) {
            total += entry.getValue();
            if (total >= weight) {
                return entry.getKey();
            }
        }

        return null; // shouldn't happen!
    }

    public void addHillBiome(Biome parent, Biome hillBiome, double height) {
        if (!this.hillBiomes.containsKey(parent)) {
            this.hillBiomes.put(parent, new LinkedHashMap<Biome, Double>());
        }

        Map<Biome, Double> hills = this.hillBiomes.get(parent);
        hills.put(hillBiome, height);
    }

    public Biome getHillBiome(Biome parent, CoreNoise noise, int x, int z) {
        if (!this.hillBiomes.containsKey(parent)) {
            return parent;
        }

        Map<Biome, Double> hills = this.hillBiomes.get(parent);

        double height = noise.getHeight(x,z) + noise.getRoughness(x,z) * 0.1;

        Biome biome = parent;

        for (Map.Entry<Biome,Double> e : hills.entrySet()) {
            if (height > e.getValue()) {
                biome = e.getKey();
            } else {
                return biome;
            }
        }

        return biome;
    }

    public void addHeightModifier(Biome biome, IBiomeHeightModifier mod, Map<String,Object> args) {
        this.heightMods.put(biome, new HeightModRegistryEntry(mod, args));
    }

    public void addHeightModifier(Biome biome, IBiomeHeightModifier mod) {
        this.addHeightModifier(biome, mod, null);
    }

    public void addHeightModifier(Biome biome, IBiomeHeightModifier mod, int variant) {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("variant", variant);
        this.addHeightModifier(biome, mod, args);
    }

    public HeightModRegistryEntry getHeightModifier(Biome biome) {
        return this.heightMods.get(biome);
    }

    //------ BiomeGroup type enum ---------------------------------------------------------

    public enum EnumBiomeCategory {
        UNKNOWN(Biomes.PLAINS),
        LAND(Biomes.PLAINS),
        OCEAN(Biomes.OCEAN),
        BEACH(Biomes.BEACH),
        SWAMP(Biomes.SWAMPLAND),
        ;

        public final BiomeGroup fallback;

        EnumBiomeCategory(Biome fallback) {
            this.fallback = new BiomeGroup(this.name()+"_fallback", 0.5,0.5,0.25);
            this.fallback.addBiome(fallback);
        }

        @Override
        public String toString() {
            return this.name().toLowerCase(Locale.ENGLISH);
        }

        public static EnumBiomeCategory get(String name) {
            name = name.toUpperCase(Locale.ENGLISH);
            EnumBiomeCategory category;
            try {
                category = EnumBiomeCategory.valueOf(name);
            } catch (Exception e) {
                return null;
            }
            return category;
        }
    }

    //------ BiomeGroup Class ---------------------------------------------------------

    public static class BiomeGroup {
        public String name;
        public double temperature;
        public double moisture;
        public double height;
        public double minHeight;
        public double maxHeight;
        public int blobSizeModifier = 0;
        public int subBlobSizeModfier = 0;

        public long salt;
        public int offsetx;
        public int offsetz;

        public Map<Biome, Double> biomes;
        public double totalweight = 0.0;

        public BiomeGroup(String name, double temperature, double moisture, double height, double minHeight, double maxHeight) {
            this.name = name;
            this.temperature = temperature;
            this.moisture = moisture;
            this.height = height;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
            this.salt = name.hashCode();

            Random rand = new Random(this.salt);

            this.offsetx = rand.nextInt();
            this.offsetz = rand.nextInt();

            this.biomes = new LinkedHashMap<Biome, Double>();
        }

        public BiomeGroup(String name, double temperature, double moisture, double height) {
            this(name, temperature, moisture, height, 0.0, 1.0);
        }

        public BiomeGroup(String name, double temperature, double moisture) {
            this(name, temperature, moisture, 0.5);
        }

        public BiomeGroup addBiome(Biome biome, double weight) {
            if (biome != null) {
                if (!this.biomes.containsKey(biome)) {
                    this.biomes.put(biome, weight);
                } else {
                    this.biomes.put(biome, this.biomes.get(biome) + weight);
                }
                this.totalweight += weight;
            }
            return this;
        }

        public BiomeGroup addBiome(Biome biome) {
            return this.addBiome(biome, 1.0);
        }

        public Biome getBiome(double value) {
            double weight = MathUtil.clamp(value,0.0,1.0) * this.totalweight;

            double total = 0.0;
            for (Map.Entry<Biome, Double> entry : this.biomes.entrySet()) {
                total += entry.getValue();
                if (total >= weight) {
                    return entry.getKey();
                }
            }

            return null; // shouldn't happen!
        }

        public double getClassificationScore(Biome biome) {
            return 0.0;
        }

        public BiomeGroup setBlobSizeModifier(int size) {
            this.blobSizeModifier = size;
            return this;
        }

        public BiomeGroup setSubBlobSizeModifier(int size) {
            this.subBlobSizeModfier = size;
            return this;
        }
    }

    //------ HeightModRegistryEntry Class ---------------------------------------------------------

    public static class HeightModRegistryEntry {
        public final IBiomeHeightModifier modifier;
        public final Map<String,Object> arguments;

        public HeightModRegistryEntry(IBiomeHeightModifier modifier, Map<String,Object> args) {
            this.modifier = modifier;
            this.arguments = args;
        }
    }
}
