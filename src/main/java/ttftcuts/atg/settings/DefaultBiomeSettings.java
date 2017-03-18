package ttftcuts.atg.settings;

import ttftcuts.atg.ATG;
import ttftcuts.atg.generator.biome.BiomeRegistry;

public class DefaultBiomeSettings extends BiomeSettings {
    public DefaultBiomeSettings() {
        super();

        addGroup(BiomeRegistry.EnumBiomeCategory.LAND, "test group", 1.0, 0.2, 0.75);

        ATG.logger.info(this.writeToJson());
    }

    public void addGroup(BiomeRegistry.EnumBiomeCategory category, String name, double height, double temperature, double moisture) {
        GroupDefinition def = new GroupDefinition();
        def.category = category;
        def.name = name;
        def.height = height;
        def.temperature = temperature;
        def.moisture = moisture;
        this.groups.add(def);
    }
}
