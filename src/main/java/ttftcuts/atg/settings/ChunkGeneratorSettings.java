package ttftcuts.atg.settings;

import com.google.gson.JsonObject;

public class ChunkGeneratorSettings extends Settings {
    @Override
    public void readData(JsonObject json) {

    }

    @Override
    public void writeData(JsonObject json) {

    }

    @Override
    public ChunkGeneratorSettings copy() {
        ChunkGeneratorSettings copy = new ChunkGeneratorSettings();

        return copy;
    }
}
