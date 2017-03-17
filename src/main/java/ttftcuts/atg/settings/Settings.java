package ttftcuts.atg.settings;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ttftcuts.atg.ATG;

public abstract class Settings {

    public String writeToJson() {
        JsonObject json = new JsonObject();

        this.writeData(json);

        return json.toString();
    }

    public static <T extends Settings> T readFromJson(String input, Class<T> clazz) {
        try {
            T settings = clazz.getConstructor().newInstance();

            settings.readData((JsonObject) new JsonParser().parse(input));

            return settings;
        } catch (Exception e) {
            ATG.logger.error(e); // shouldn't happen
        }
        return null;
    }

    public abstract void readData(JsonObject json);

    public abstract void writeData(JsonObject json);
}
