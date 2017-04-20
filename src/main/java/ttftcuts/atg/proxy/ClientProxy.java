package ttftcuts.atg.proxy;

import net.minecraftforge.fml.common.event.*;
import ttftcuts.atg.tweaks.GrassColours;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public void loadComplete(FMLLoadCompleteEvent event) {
        super.loadComplete(event);
        GrassColours.init();
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        GrassColours.clearCache();
    }

    @Override
    public void serverStopped(FMLServerStoppedEvent event) {
        GrassColours.clearCache();
    }
}
