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

    // Removing sub biomes

    // Adding hill biomes

    // Removing hill biomes

    // Adding biome height mods

    // Removing biome height mods

    // ########## getGroup class ##########

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
    }
}
