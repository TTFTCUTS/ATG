package ttftcuts.atg.generator.biome;

public interface IBiomeHeightModifier {
    double getModifiedHeight(int x, int z, double height);
}
