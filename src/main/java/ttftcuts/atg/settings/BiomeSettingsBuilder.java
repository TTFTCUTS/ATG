package ttftcuts.atg.settings;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import ttftcuts.atg.generator.biome.BiomeRegistry.EnumBiomeCategory;

public class BiomeSettingsBuilder {
    public final BiomeSettings settings;

    public BiomeSettingsBuilder(BiomeSettings settings) {
        this.settings = settings;
    }

    // Getting groups
    public GroupDetails getGroup(EnumBiomeCategory category, String name) {
        return new GroupDetails(category, name);
    }

    // Adding groups
    public GroupDetails addGroup(EnumBiomeCategory category, String name, double temperature, double moisture, double height, double minHeight, double maxHeight) {
        GroupDetails details = new GroupDetails(category, name);

        BiomeSettings.GroupDefinition group = new BiomeSettings.GroupDefinition();
        group.category = category;
        group.name = name;
        group.temperature = temperature;
        group.moisture = moisture;
        group.height = height;
        group.minHeight = minHeight;
        group.maxHeight = maxHeight;
        settings.groups.put(group.getMapKey(), group);

        return details;
    }
    public GroupDetails addGroup(EnumBiomeCategory category, String name, double temperature, double moisture, double height) {
        return this.addGroup(category, name, temperature, moisture, height, 0.0, 1.0);
    }

    // Biome replacement
    public void replaceBiome(String toReplace, String replaceWith) {
        BiomeSettings.BiomeReplacement def = new BiomeSettings.BiomeReplacement();
        def.name = new ResourceLocation(replaceWith);
        def.replace = new ResourceLocation(toReplace);

        this.settings.replacements.put(def.getMapKey(),def);
    }
    public void replaceBiome(Biome toReplace, Biome replaceWith) {
        this.replaceBiome(toReplace.getRegistryName().toString(), replaceWith.getRegistryName().toString());
    }

    // Adding sub biomes
    public void addSubBiome(String parent, String biome, double weight) {
        BiomeSettings.SubBiomeEntry def = new BiomeSettings.SubBiomeEntry();
        def.name = new ResourceLocation(biome);
        def.parentBiome = new ResourceLocation(parent);
        def.weight = weight;

        if (this.settings.subBiomes.containsKey(def.getMapKey())) {
            this.settings.subBiomes.get(def.getMapKey()).weight += weight;
        } else {
            this.settings.subBiomes.put(def.getMapKey(), def);
        }
    }
    public void addSubBiome(String parent, String biome) {
        this.addSubBiome(parent, biome, 1.0);
    }
    public void addSubBiome(Biome parent, Biome biome, double weight) {
        this.addSubBiome(parent.getRegistryName().toString(), biome.getRegistryName().toString(), weight);
    }
    public void addSubBiome(Biome parent, Biome biome) {
        this.addSubBiome(parent, biome, 1.0);
    }

    // Removing sub biomes
    public void removeSubBiome(String parent, String biome) {
        BiomeSettings.ParentBiomeEntry def = new BiomeSettings.ParentBiomeEntry();
        def.name = new ResourceLocation(biome);
        def.parentBiome = new ResourceLocation(parent);

        this.settings.subRemovals.put(def.getMapKey(), def);
    }
    public void removeSubBiome(Biome parent, Biome biome) {
        this.removeSubBiome(parent.getRegistryName().toString(), biome.getRegistryName().toString());
    }

    // Adding hill biomes
    public void addHillBiome(String parent, String biome, double height) {
        BiomeSettings.HillBiomeEntry def = new BiomeSettings.HillBiomeEntry();
        def.name = new ResourceLocation(biome);
        def.parentBiome = new ResourceLocation(parent);
        def.height = height;

        this.settings.hillBiomes.put(def.getMapKey(), def);
    }
    public void addHillBiome(Biome parent, Biome biome, double height) {
        this.addHillBiome(parent.getRegistryName().toString(), biome.getRegistryName().toString(), height);
    }

    // Removing hill biomes
    public void removeHillBiome(String parent, String biome) {
        BiomeSettings.ParentBiomeEntry def = new BiomeSettings.ParentBiomeEntry();
        def.name = new ResourceLocation(biome);
        def.parentBiome = new ResourceLocation(parent);

        this.settings.hillRemovals.put(def.getMapKey(), def);
    }
    public void removeHillBiome(Biome parent, Biome biome) {
        this.removeHillBiome(parent.getRegistryName().toString(), biome.getRegistryName().toString());
    }

    // Adding biome height mods
    public HeightModDetails addHeightModifier(String biome, String mod) {
        BiomeSettings.HeightModEntry def = new BiomeSettings.HeightModEntry();
        def.name = new ResourceLocation(biome);
        def.heightMod = mod;

        this.settings.heightMods.put(def.getMapKey(), def);

        return new HeightModDetails(def.getMapKey(), mod);
    }
    public HeightModDetails addHeightModifier(Biome biome, String mod) {
        return this.addHeightModifier(biome.getRegistryName().toString(), mod);
    }

    // Removing biome height mods
    public void removeHeightModifier(String biome) {
        BiomeSettings.BiomeEntry def = new BiomeSettings.BiomeEntry();
        def.name = new ResourceLocation(biome);

        this.settings.heightModRemovals.put(def.getMapKey(), def);
    }
    public void removeHeightModifier(Biome biome) {
        this.removeHeightModifier(biome.getRegistryName().toString());
    }

    // ########## group class ##########

    public class GroupDetails {
        public final EnumBiomeCategory category;
        public final String group;

        public GroupDetails(EnumBiomeCategory category, String group) {
            this.category = category;
            this.group = group;
        }

        // Adding biomes
        public GroupDetails addBiome(String biome, double weight) {
            BiomeSettings.BiomeDefinition def = new BiomeSettings.BiomeDefinition();
            def.category = this.category;
            def.group = this.group;
            def.name = new ResourceLocation(biome);
            def.weight = weight;

            BiomeSettings settings = BiomeSettingsBuilder.this.settings;
            if (settings.biomes.containsKey(def.getMapKey())) {
                settings.biomes.get(def.getMapKey()).weight += weight;
            } else {
                settings.biomes.put(def.getMapKey(), def);
            }

            return this;
        }
        public GroupDetails addBiome(String biome) {
            return this.addBiome(biome, 1.0);
        }
        public GroupDetails addBiome(Biome biome, double weight) {
            return this.addBiome(biome.getRegistryName().toString(), weight);
        }
        public GroupDetails addBiome(Biome biome) {
            return this.addBiome(biome.getRegistryName().toString());
        }

        // Removing biomes
        public GroupDetails removeBiome(String biome) {
            BiomeSettings.GroupedBiomeEntry def = new BiomeSettings.GroupedBiomeEntry();
            def.category = this.category;
            def.group = this.group;
            def.name = new ResourceLocation(biome);

            BiomeSettingsBuilder.this.settings.removals.put(def.getMapKey(), def);

            return this;
        }
        public GroupDetails removeBiome(Biome biome) {
            return this.removeBiome(biome.getRegistryName().toString());
        }

        // Blobs
        public GroupDetails setBlobSizeModifier(int scale) {
            BiomeSettings settings = BiomeSettingsBuilder.this.settings;
            String key = this.getKey();

            if (settings.groups.containsKey(key)) {
                settings.groups.get(key).blobsize = scale;
            }

            return this;
        }
        public GroupDetails setSubBlobSizeModifier(int scale) {
            BiomeSettings settings = BiomeSettingsBuilder.this.settings;
            String key = this.getKey();

            if (settings.groups.containsKey(key)) {
                settings.groups.get(key).subblobsize = scale;
            }

            return this;
        }

        private String getKey() {
            return this.category +"_"+ this.group;
        }
    }

    // ########## height mod class ##########

    public class HeightModDetails {
        public final String biome;
        public final String mod;

        public HeightModDetails(String biome, String mod) {
            this.biome = biome;
            this.mod = mod;
        }

        public HeightModDetails setParameter(String key, Object value) {
            BiomeSettings settings = BiomeSettingsBuilder.this.settings;

            if (settings.heightMods.containsKey(biome)) {
                BiomeSettings.HeightModEntry entry = settings.heightMods.get(biome);
                if (entry.heightMod.equals(mod)) {
                    entry.parameters.put(key, value);
                }
            }

            return this;
        }
    }
}
