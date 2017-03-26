package ttftcuts.atg.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ttftcuts.atg.util.JsonUtil;

import java.util.LinkedHashMap;
import java.util.Map;

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

    public abstract Settings copy();

    // ##### saveable interface #####

    public interface IJsonable {
        JsonObject toJson();
        void fromJson(JsonObject o);

        IJsonable copy();

        static <T extends IJsonable> T create(Class<T> clazz) {
            try {
                return clazz.getConstructor().newInstance();
            } catch (Exception e) {
                return null;
            }
        }
    }

    public interface IJsonMappable extends IJsonable {
        String getMapKey();

        static <T extends IJsonMappable> void readJsonableMap(JsonObject o, LinkedHashMap<String, T> list, String tagname, Class<T> clazz) {
            list.clear();
            if (o.has(tagname)) {
                JsonArray g = JsonUtil.getAsArray(o, tagname); //o.getAsJsonArray(tagname);
                for (JsonElement element : g) {
                    T def = IJsonable.create(clazz);
                    JsonObject eo;
                    /*try {
                        eo = element.getAsJsonObject();
                    } catch (Exception e) {
                        return;
                    }*/
                    eo = JsonUtil.asObject(element);
                    def.fromJson(eo);
                    list.put(def.getMapKey(), def);
                }
            }

        }

        static <T extends IJsonMappable> void writeJsonableMap(JsonObject o, LinkedHashMap<String, T> list, String tagname) {
            if (!list.isEmpty()) {
                JsonArray g = new JsonArray();
                for (T def : list.values()) {
                    g.add(def.toJson());
                }
                o.add(tagname, g);
            }
        }

        static <T extends IJsonMappable> void copyMap(Map<String, T> source, Map<String, T> destination) {
            for (Map.Entry<String, T> e : source.entrySet()) {
                destination.put(e.getKey(), (T)(e.getValue().copy()));
            }
        }
    }
}
