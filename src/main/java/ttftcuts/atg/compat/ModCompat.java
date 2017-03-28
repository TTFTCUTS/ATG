package ttftcuts.atg.compat;

import net.minecraftforge.fml.common.event.FMLInterModComms.*;

import java.util.*;

public class ModCompat {
    private Map<String, Set<String>> messageSenders = new HashMap<>();

    public ModCompat() {

    }

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
