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
        ATG.logger.info("wheee");
    }

    @Override
    public BiomeProvider getBiomeProvider(World world) {
        return new BiomeProviderSingle(Biomes.PLAINS);
    }

    @Override
    public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
        return new ChunkProviderATG(world);
    }
}
