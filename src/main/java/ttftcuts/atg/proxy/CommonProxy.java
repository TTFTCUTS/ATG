package ttftcuts.atg.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import ttftcuts.atg.ATG;
import ttftcuts.atg.ATGBiomes;
import ttftcuts.atg.generator.biome.VillageBlocks;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        ATGBiomes.init();
        ATG.modCompat.preInit();
    }

    public void init(FMLInitializationEvent event) {
        MinecraftForge.TERRAIN_GEN_BUS.register(new VillageBlocks());
        ATG.modCompat.init();
    }

    public void postInit(FMLPostInitializationEvent event) {
        ATG.modCompat.postInit();
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
        ATG.modCompat.processIMC(FMLInterModComms.fetchRuntimeMessages(this));
        ATG.modCompat.registerBuiltInModules();
    }

    public void serverStarting(FMLServerStartingEvent event) {

    }

    public void serverStopped(FMLServerStoppedEvent event) {

    }
}
