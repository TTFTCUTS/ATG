package ttftcuts.atg;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ttftcuts.atg.compat.ModCompat;
import ttftcuts.atg.configuration.ConfigHandler;
import ttftcuts.atg.generator.GlobalRegistry;
import ttftcuts.atg.generator.biome.VillageBlocks;

@Mod(modid = ATG.MODID, version = ATG.VERSION)
public class ATG
{
    public static final String MODID = "atg";
    public static final String VERSION = "2";

    public static final Logger logger = LogManager.getLogger(MODID);

    public static GlobalRegistry globalRegistry = new GlobalRegistry();;
    public static ModCompat modCompat = new ModCompat();
    public static ConfigHandler config;

    @Mod.Instance(MODID)
    public static ATG instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = new ConfigHandler(event.getSuggestedConfigurationFile());

        new WorldTypeATG("atg");

        ATGBiomes.init();
        modCompat.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.TERRAIN_GEN_BUS.register(new VillageBlocks());
        modCompat.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        modCompat.postInit();
    }

    // IMC stuff
    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        modCompat.processIMC(FMLInterModComms.fetchRuntimeMessages(this));
        modCompat.registerBuiltInModules();
    }

    @Mod.EventHandler
    public void handleIMC(FMLInterModComms.IMCEvent event) {
        modCompat.processIMC(event.getMessages());
    }
}
