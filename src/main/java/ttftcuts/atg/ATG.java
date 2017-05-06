package ttftcuts.atg;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ttftcuts.atg.compat.ModCompat;
import ttftcuts.atg.configuration.ConfigHandler;
import ttftcuts.atg.generator.GlobalRegistry;
import ttftcuts.atg.generator.biome.VillageBlocks;
import ttftcuts.atg.generator.structure.WoodlandMansionATG;
import ttftcuts.atg.proxy.CommonProxy;
import ttftcuts.atg.tweaks.GrassColours;

@Mod(modid = ATG.MODID, version = ATG.VERSION)
public class ATG
{
    public static final String MODID = "atg";
    public static final String VERSION = "2";

    public static final Logger logger = LogManager.getLogger(MODID);

    public static WorldTypeATG worldType;

    public static GlobalRegistry globalRegistry = new GlobalRegistry();;
    public static ModCompat modCompat = new ModCompat();
    public static ConfigHandler config;

    @Mod.Instance(MODID)
    public static ATG instance;

    @SidedProxy(clientSide = "ttftcuts.atg.proxy.ClientProxy", serverSide = "ttftcuts.atg.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = new ConfigHandler(event.getSuggestedConfigurationFile());

        ATGBiomes.init();

        MapGenStructureIO.registerStructure(WoodlandMansionATG.Start.class, "ATGMansion");
        worldType = new WorldTypeATG("atg");

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }

    @Mod.EventHandler
    public void handleIMC(FMLInterModComms.IMCEvent event) {
        modCompat.processIMC(event.getMessages());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        proxy.serverStopped(event);
    }
}
