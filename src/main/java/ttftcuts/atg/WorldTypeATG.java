package ttftcuts.atg;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.layer.GenLayer;
import ttftcuts.atg.generator.BiomeProviderATG;
import ttftcuts.atg.generator.ChunkProviderATG;
import ttftcuts.atg.generator.ChunkProviderBasic;
import ttftcuts.atg.settings.BiomeSettings;
import ttftcuts.atg.settings.DefaultBiomeSettings;
import ttftcuts.atg.util.GeneralUtil;

public class WorldTypeATG extends WorldType {
    public WorldTypeATG(String name) {
        super(name);
    }

    @Override
    public float getCloudHeight() {
        return 192F;
    }

    @Override
    public boolean isCustomizable() {
        return true;
    }

    @Override
    public void onCustomizeButton(Minecraft mc, GuiCreateWorld guiCreateWorld) {
        // testing time
        ATG.logger.info("wheee");

        /*BiomeSettings testsettings = new DefaultBiomeSettings();

        BiomeSettings testammendment = new BiomeSettings();
        BiomeSettings.BiomeReplacement testreplace = new BiomeSettings.BiomeReplacement();
        testreplace.replace = Biomes.MUSHROOM_ISLAND.getRegistryName();
        testreplace.name = Biomes.MESA.getRegistryName();
        testammendment.replacements.put(testreplace.getMapKey(), testreplace);

        testsettings.apply(testammendment);

        String json = testsettings.writeToJson();
        ATG.logger.info("Json 1: "+json);

        BiomeSettings test2 = new BiomeSettings().readFromJson(json);
        String json2 = test2.writeToJson();
        ATG.logger.info("Json 2: "+json2);*/

        GeneralUtil.printBiomeInformation();
    }

    @Override
    public BiomeProvider getBiomeProvider(World world) {
        return new BiomeProviderATG(world);
    }

    @Override
    public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
        return new ChunkProviderATG(world);
    }
}
