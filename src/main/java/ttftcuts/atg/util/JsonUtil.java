package ttftcuts.atg.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.javaws.exceptions.InvalidArgumentException;
import ttftcuts.atg.ATG;

import java.util.Map;

public abstract class JsonUtil {
    public static final JsonParser PARSER = new JsonParser();

    public static <T> T get(JsonObject json, String tag, T fallback) {
        if (!json.has(tag) || json.get(tag) == null) {
            ATG.logger.warn("Json parsing: tag " +tag+ " is empty");
            return fallback;
        }
        return as(json.get(tag), fallback);
    }

    public static <T> T as(JsonElement element, T fallback) {
        T val = fallback;
        try {
            val = getValue(element, fallback);
        } catch (IllegalStateException|ClassCastException e) {
            ATG.logger.warn("Incorrect value type: " +fallback.getClass()+", falling back to " +fallback);
        } catch (InvalidArgumentException e) {
            ATG.logger.warn("Unhandled fallback type: " +fallback.getClass());
        }
        return val;
    }

    private static <T> T getValue(JsonElement element, T compared) throws IllegalStateException, ClassCastException, InvalidArgumentException {
        if (compared instanceof Double) {
            return (T)(new Double(element.getAsDouble()));
        }
        else if (compared instanceof Integer) {
            return (T)(new Integer(element.getAsInt()));
        }
        else if (compared instanceof JsonObject) {
            return (T)element.getAsJsonObject();
        }
        else if (compared instanceof JsonArray) {
            return (T)element.getAsJsonArray();
        }
        else if (compared instanceof String) {
            return (T)element.getAsString();
        }
        else if (compared instanceof Boolean) {
            return (T)(new Boolean(element.getAsBoolean()));
        }

        throw new InvalidArgumentException(null);
    }

    public static JsonObject getAsObject(JsonObject json, String tag) {
        return get(json, tag, new JsonObject());
    }

    public static JsonObject asObject(JsonElement element) {
        return as(element, new JsonObject());
    }

    public static JsonArray getAsArray(JsonObject json, String tag) {
        return get(json, tag, new JsonArray());
    }

    public static JsonArray asArray(JsonElement element) {
        return as(element, new JsonArray());
    }
}
