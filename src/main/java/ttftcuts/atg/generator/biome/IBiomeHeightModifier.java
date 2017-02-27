package ttftcuts.atg.generator.biome;

import javax.annotation.Nullable;
import java.util.Map;

public interface IBiomeHeightModifier {
    double getModifiedHeight(int x, int z, double height, @Nullable Map<String,Object> args);

    // TODO: Will eventually be the basis of a settings UI for the modifier per biome, probably don't implement for now
    default Map<String, Object> getSettings() { return null; }
}
