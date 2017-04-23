package ttftcuts.atg.generator.biome;

import com.google.gson.JsonObject;
import ttftcuts.atg.util.JsonUtil;
import ttftcuts.atg.util.MathUtil;

public abstract class BiomeModParameter<T> {
    public final T defaultValue;

    public BiomeModParameter(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public abstract void writeToJson(JsonObject json, String tag, Object value);
    public abstract T readFromJson(JsonObject json, String tag);

    // ########## Implementations ##########

    public static class IntParameter extends BiomeModParameter<Integer> {
        public final int lowerLimit;
        public final int upperLimit;

        public IntParameter(int defaultValue, int lowerLimit, int upperLimit) {
            super(defaultValue);
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
        }

        public IntParameter(int value) {
            this(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        @Override
        public void writeToJson(JsonObject json, String tag, Object value) {
            json.addProperty(tag, MathUtil.clamp((int)value, lowerLimit,upperLimit));
        }

        @Override
        public Integer readFromJson(JsonObject json, String tag) {
            return MathUtil.clamp(JsonUtil.get(json, tag, this.defaultValue), lowerLimit, upperLimit);
        }
    }

    public static class DoubleParameter extends BiomeModParameter<Double> {
        public final double lowerLimit;
        public final double upperLimit;

        public DoubleParameter(double value, double lowerLimit, double upperLimit) {
            super(value);
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
        }

        public DoubleParameter(double defaultValue) {
            this(defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
        }

        @Override
        public void writeToJson(JsonObject json, String tag, Object value) {
            json.addProperty(tag, MathUtil.clamp((double)value, lowerLimit,upperLimit));
        }

        @Override
        public Double readFromJson(JsonObject json, String tag) {
            return MathUtil.clamp(JsonUtil.get(json, tag, this.defaultValue), lowerLimit, upperLimit);
        }
    }

    public static class StringParameter extends BiomeModParameter<String> {
        public StringParameter(String defaultValue) {
            super(defaultValue);
        }

        @Override
        public void writeToJson(JsonObject json, String tag, Object value) {
            json.addProperty(tag, value.toString());
        }

        @Override
        public String readFromJson(JsonObject json, String tag) {
            return JsonUtil.get(json, tag, this.defaultValue);
        }
    }

    public static class BooleanParameter extends BiomeModParameter<Boolean> {
        public final boolean defaultValue;

        public BooleanParameter(boolean value) {
            super(value);
            this.defaultValue = value;
        }

        @Override public void writeToJson(JsonObject json, String tag, Object value) {
            json.addProperty(tag, (boolean)value);
        }

        @Override public Boolean readFromJson(JsonObject json, String tag) {
            return JsonUtil.get(json, tag, this.defaultValue);
        }
    }
}
