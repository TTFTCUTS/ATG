package ttftcuts.atg.settings;

import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import ttftcuts.atg.ATG;
import ttftcuts.atg.generator.biome.BiomeRegistry.EnumBiomeCategory;

public class DefaultBiomeSettings extends BiomeSettings {
    public DefaultBiomeSettings() {
        super();

        addGroup(EnumBiomeCategory.LAND, "test group", 1.0, 0.2, 0.75);

        addBiome(EnumBiomeCategory.LAND, "Plains", Biomes.MUSHROOM_ISLAND, 0.5);
        addBiome(EnumBiomeCategory.LAND, "Forest", Biomes.MUSHROOM_ISLAND, 0.25);
        addBiome(EnumBiomeCategory.OCEAN, "Ocean", Biomes.SWAMPLAND, 0.5);

        addReplacement(EnumBiomeCategory.OCEAN, "Ocean", Biomes.OCEAN, Biomes.FOREST);
    }

    public void addGroup(EnumBiomeCategory category, String name, double height, double temperature, double moisture) {
        GroupDefinition def = new GroupDefinition();
        def.category = category;
        def.name = name;
        def.height = height;
        def.temperature = temperature;
        def.moisture = moisture;
        this.groups.add(def);
    }

    public void addBiome(EnumBiomeCategory category, String group, ResourceLocation name, double weight) {
        BiomeDefinition def = new BiomeDefinition();
        def.category = category;
        def.group = group;
        def.name = name;
        def.weight = weight;
        this.biomes.add(def);
    }

    public void addBiome(EnumBiomeCategory category, String group, String name, double weight) {
        this.addBiome(category, group, new ResourceLocation(name), weight);
    }

    public void addBiome(EnumBiomeCategory category, String group, Biome biome, double weight) {
        this.addBiome(category, group, biome.getRegistryName(), weight);
    }

    public void addReplacement(EnumBiomeCategory category, String group, ResourceLocation toReplace, ResourceLocation name) {
        BiomeReplacement def = new BiomeReplacement();
        def.category = category;
        def.group = group;
        def.name = name;
        def.replace = toReplace;
        this.replacements.add(def);
    }

    public void addReplacement(EnumBiomeCategory category, String group, String toReplace, String name) {
        this.addReplacement(category, group, new ResourceLocation(toReplace), new ResourceLocation(name));
    }

    public void addReplacement(EnumBiomeCategory category, String group, Biome toReplace, Biome biome) {
        this.addReplacement(category, group, toReplace.getRegistryName(), biome.getRegistryName());
    }
}
