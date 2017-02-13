package ttftcuts.atg.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class CoordCache<T extends CoordPair> extends LinkedHashMap<Integer,T> {
    public final int size;
    public long collisions = 0;

    public CoordCache(int size) {
        this.size = size;
    }

    public T get(int x, int z) {
        T entry = this.get(coordHash(x, z));

        if (entry != null && (entry.x != x || entry.z != z)) {
            collisions++;
            return null;
        }

        return entry;
    }

    public T put(int x, int z, T value) {
        return this.put(coordHash(x, z), value);
    }

    public boolean containsKey(int x, int z) {
        return this.containsKey(coordHash(x, z));
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return this.size() > this.size;
    }

    public static int coordHash(int x, int z) {
        int hash = 31;
        hash = ((hash + x) << 13) - (hash + x);
        hash = ((hash + z) << 13) - (hash + z);
        return hash;
    }
}
