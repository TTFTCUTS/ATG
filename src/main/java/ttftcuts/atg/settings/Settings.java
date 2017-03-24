package ttftcuts.atg.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import ttftcuts.atg.ATG;

import java.util.List;

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

    // ##### saveable interface #####

    public interface IJsonable {
        JsonObject toJson();
        void fromJson(JsonObject o);

        static <T extends IJsonable> void readJsonableList(JsonObject o, List<T> list, String tagname, Class<T> clazz) {
            list.clear();
            if (o.has(tagname)) {
                JsonArray g = o.getAsJsonArray(tagname);
                for (JsonElement element : g) {
                    T def = T.create(clazz);
                    def.fromJson(element.getAsJsonObject());
                    list.add(def);
                }
            }

        }

        static <T extends IJsonable> void writeJsonableList(JsonObject o, List<T> list, String tagname) {
            if (!list.isEmpty()) {
                JsonArray g = new JsonArray();
                for (T def : list) {
                    g.add(def.toJson());
                }
                o.add(tagname, g);
            }
        }

        static <T extends IJsonable> T create(Class<T> clazz) {
            try {
                return clazz.getConstructor().newInstance();
            } catch (Exception e) {
                return null;
            }
        }
    }
}
