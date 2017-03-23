package ttftcuts.atg.settings;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import ttftcuts.atg.ATG;

public abstract class Settings {
    public static final JsonParser PARSER = new JsonParser();


    public String writeToJson() {
        JsonObject json = new JsonObject();

        this.writeData(json);

        return json.toString();
    }

    public Settings readFromJson(String input) {

        this.readData(PARSER.parse(input).getAsJsonObject());

        return this;
    }

    public abstract void readData(JsonObject json);

    public abstract void writeData(JsonObject json);
}
