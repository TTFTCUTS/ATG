package ttftcuts.atg.generator.biome;

import javax.annotation.Nullable;
import java.util.Map;

public interface IBiomeHeightModifier {
    double getModifiedHeight(int x, int z, double height, @Nullable Map<String,Object> args);

    default Map<String, BiomeModParameter> getSettings() { return null; }
}
