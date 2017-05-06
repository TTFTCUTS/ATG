package ttftcuts.atg.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class CoordCache<T extends CoordPair> {
    public final long size;
    public long collisions = 0;

    private Cache<Integer, T> cache;

    public CoordCache(long size) {
        this.size = size;

        this.cache = CacheBuilder.newBuilder()
                .maximumSize(size)
                .build();
    }

    public T get(int x, int z) {
        T entry = this.cache.getIfPresent(MathUtil.coordHash(x,z));

        if (entry != null && (entry.x != x || entry.z != z)) {
            collisions++;
            return null;
        }

        return entry;
    }

    public T put(int x, int z, T value) {
        this.cache.put(MathUtil.coordHash(x,z), value);
        return value;
    }
}
