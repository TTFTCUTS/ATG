package ttftcuts.atg.configuration;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ttftcuts.atg.ATG;

import javax.annotation.Nullable;
import java.io.File;

public class ConfigHandler {

    public Configuration config;

    public ConfigHandler(File configFile) {

        this.config = new Configuration(configFile);
        this.config.load();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void load() {


        this.saveIfDirty();
    }

    public void saveIfDirty() {
        if (config.hasChanged()) {
            config.save();
        }
    }

    @SubscribeEvent
    public void onChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ATG.MODID)) {
            load();
        }
    }
}
