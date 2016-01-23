package ttftcuts.atg.world;

import ttftcuts.atg.ATG;
import ttftcuts.atg.gui.GuiATGCustomiseWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ATGWorldType extends WorldType {

	public ATGWorldType() {
		super("ATG");
	}

	@Override
	@SideOnly(Side.CLIENT)
    public String getTranslateName()
    {
        return "generator.ATG";
    }
	
	@Override
	public WorldChunkManager getChunkManager(World world)
    {
        return new ATGChunkManager(world);
    }

	@Override
    public IChunkProvider getChunkGenerator(World world, String generatorOptions)
    {
        return new ATGChunkProvider(world, generatorOptions);
    }
	
	@Override
	public boolean isCustomizable()
    {
        return true;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void onCustomizeButton(Minecraft mc, GuiCreateWorld guiCreateWorld)
    {
		mc.displayGuiScreen(new GuiATGCustomiseWorld(guiCreateWorld, guiCreateWorld.chunkProviderSettingsJson));
    }

	@Override
    public float getCloudHeight()
    {
        return 192.0F;
    }
}
