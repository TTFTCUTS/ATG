package ttftcuts.atg.compat;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms.*;
import ttftcuts.atg.ATG;
import ttftcuts.atg.compat.builtin.BOPModule;
import ttftcuts.atg.compat.builtin.ProvidedBiomeModule;

import java.util.*;

public class ModCompat {
    private Map<String, Set<String>> messageSenders = new HashMap<>();

    public List<ProvidedBiomeModule> builtInBiomeModules = new ArrayList<>();

    public void preInit() {}

    public void init() {}

    public void postInit() {
        new BOPModule();
    }

    // ########## Registration ##########

    public void registerBuiltInModules() {
        for (ProvidedBiomeModule module : builtInBiomeModules) {
            if (Loader.isModLoaded(module.modid)) {
                if (messageSenders.containsKey(module.modid) && messageSenders.get(module.modid).contains("biomeModule")) {
                    continue;
                }
                ATG.logger.info("Registering built-in biome module: {} for {}", module.name, module.modid);
                ATG.globalRegistry.biomeModules.add(module);
            }
        }
    }

    // ########## IMC ##########

    public void processIMC(Collection<IMCMessage> messages) {
        for (IMCMessage message : messages) {
            this.processIMC(message);
        }
    }

    private void processIMC(IMCMessage message) {
        if (!messageSenders.containsKey(message.getSender())) {
            messageSenders.put(message.getSender(), new HashSet<String>());
        }
        messageSenders.get(message.getSender()).add(message.key);

        switch(message.key) {
            case "biomeModule":
                BiomeModule.fromIMC(message);
                break;
        }
    }
}
