package ttftcuts.atg.tweaks;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ttftcuts.atg.ATG;
import ttftcuts.atg.util.CoordCache;
import ttftcuts.atg.util.CoordPair;
import ttftcuts.atg.util.GeneralUtil;
import ttftcuts.atg.util.MathUtil;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SideOnly(Side.CLIENT)
public class GrassColours {
    private static final String[] GRASS_COLOR = {"a", "field_180291_a", "GRASS_COLOR"};
    private static final String[] COLOR_RESOLVER = {"ais$a", "net.minecraft.world.biome.BiomeColorHelper$ColorResolver"};
    private static final String[] GET_COLOR_AT_POS = {"a", "func_180283_a", "getColorAtPos"};

    public static LoadingCache<GrassCacheKey, Biome> grassCache;

    public static void init() {
        ATG.logger.info("ATTEMPTING TO COMMIT GREAT EVIL:");
        try {
            doImmenseEvil();
        } catch(Throwable e) {
            e.printStackTrace();
        }
        MinecraftForge.EVENT_BUS.register(new Listener());

        grassCache = CacheBuilder.newBuilder()
            .maximumSize(2048)
            .build(
                new CacheLoader<GrassCacheKey, Biome>() {
                    @Override
                    public Biome load(GrassCacheKey key) {
                        World world = Minecraft.getMinecraft().world;

                        //ATG.logger.info("Requested ID: "+key.dim+", local world id: "+world.provider.getDimension());

                        if (world.provider.getDimension() == key.dim) {
                            return world.getBiome(new BlockPos(key.x, 63, key.z));
                        }

                        return Biomes.DEFAULT;
                    }
                }
            );
    }

    public static void doImmenseEvil() throws Exception {
        // de-finalise the grass colour field
        Field grass_color = ReflectionHelper.findField(BiomeColorHelper.class, GRASS_COLOR);
        grass_color.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");

        AccessController.doPrivileged((PrivilegedAction) () -> {
            modifiers.setAccessible(true);
            return null;
        });

        modifiers.setInt(grass_color, grass_color.getModifiers() & ~Modifier.FINAL);

        // get the interface
        Class colorResolver = ReflectionHelper.getClass(Minecraft.class.getClassLoader(), COLOR_RESOLVER);

        // get what the field was so it can be wrapped
        Object wrappedResolver = grass_color.get(null);

        // get the version of the method used by the object to be wrapped - avoids exceptions for calling an abstract method
        Class wrappedResolverClass = wrappedResolver.getClass();
        Method wrappedGetColorAtPos = ReflectionHelper.findMethod(wrappedResolverClass, null, GET_COLOR_AT_POS, Biome.class, BlockPos.class);

        // build a proxy
        Method getColorAtPos = ReflectionHelper.findMethod(colorResolver, null, GET_COLOR_AT_POS, Biome.class, BlockPos.class);
        Object proxy = Proxy.newProxyInstance(colorResolver.getClassLoader(), new Class[] { colorResolver }, new GrassHandler(getColorAtPos, wrappedResolver, wrappedGetColorAtPos) );

        // set the field
        grass_color.set(null, proxy);
    }

    private static class GrassHandler implements InvocationHandler {
        public final Method abstractGetColorAtPos;
        public final Object wrappedResolver;
        public final Method wrappedGetColorAtPos;

        public GrassHandler(Method abstractGetColorAtPos, Object wrappedResolver, Method wrappedGetColorAtPos) {
            this.abstractGetColorAtPos = abstractGetColorAtPos;
            this.abstractGetColorAtPos.setAccessible(true);
            this.wrappedResolver = wrappedResolver;
            this.wrappedGetColorAtPos = wrappedGetColorAtPos;

            ATG.logger.info(abstractGetColorAtPos);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.equals(this.abstractGetColorAtPos)) {
                Biome biome = (Biome)args[0];
                BlockPos pos = (BlockPos)args[1];

                World world = Minecraft.getMinecraft().world;

                if (world != null && GeneralUtil.isWorldATG(world)) {
                    // probe the current world biomes to eliminate most cases of it being the wrong one (I hope)
                    Biome probe = world.getBiome(pos);
                    if (probe == biome) {
                        return getGrassColour(world, biome, pos);
                    }
                }

                if (wrappedResolver != null) {
                    return wrappedGetColorAtPos.invoke(this.wrappedResolver, biome, pos);
                }

                return 0xFF00FF;
            }
            return method.invoke(this.wrappedResolver, args);
        }
    }

    public static class GrassCacheKey {
        public final int x;
        public final int z;
        public final int dim;

        public GrassCacheKey(int x, int z, int dim) {
            this.x = x;
            this.z = z;
            this.dim = dim;
        }

        public GrassCacheKey(World world, BlockPos pos) {
            this(pos.getX(), pos.getZ(), world.provider.getDimension());
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof GrassCacheKey)) { return false; }
            GrassCacheKey o = (GrassCacheKey)other;
            return this.x == o.x && this.z == o.z && this.dim == o.dim;
        }

        @Override
        public int hashCode() {
            return (this.dim << 13) ^ MathUtil.coordHash(this.x, this.z);
        }
    }

    public static void clearCache() {
        //ATG.logger.info("Clearing grass colour biome cache");
        grassCache.invalidateAll();
    }

    public static int getGrassColour(World world, Biome biome, BlockPos pos) {
        int rad = 5;
        //int divisor = (rad*2 + 1);
        //divisor *= divisor;

        int divisor = 0;

        int r = 0;
        int g = 0;
        int b = 0;

        /*Map<Biome, Integer> biomeColours = new HashMap<>();
        Biome ib;
        int col,x,z;

        for (BlockPos.MutableBlockPos ipos : BlockPos.getAllInBoxMutable(pos.add(-rad, 0, -rad), pos.add(rad, 0, rad)))
        {
            //ib = grassCache.getUnchecked(new GrassCacheKey(world, ipos));
            ib = world.getBiome(ipos);

            if (biomeColours.containsKey(ib)) {
                col = biomeColours.get(ib);
            } else {
                col = ib.getGrassColorAtPos(pos);
                biomeColours.put(ib, col);
            }

            r += (col & 0xFF0000) >> 16;
            g += (col & 0x00FF00) >> 8;
            b += (col & 0x0000FF);
        }*/

        int chunkx = Math.floorDiv(pos.getX(), 16);
        int chunkz = Math.floorDiv(pos.getZ(), 16);

        int cx = pos.getX() - chunkx * 16;
        int cz = pos.getZ() - chunkz * 16;

        if (cx <= rad) {
            chunkx--;
            cx += 16;
        }

        if (cz <= rad) {
            chunkz--;
            cz += 16;
        }

        Chunk[][] chunks = {
                {world.getChunkFromChunkCoords(chunkx, chunkz), world.getChunkFromChunkCoords(chunkx, chunkz+1)},
                {world.getChunkFromChunkCoords(chunkx+1, chunkz), world.getChunkFromChunkCoords(chunkx+1, chunkz+1)}
        };

        // b = iteration coord, i = chunk list coord
        int bx, bz, ix, iz, col;
        Chunk chunk;
        Biome ib;

        BlockPos.MutableBlockPos ipos = new BlockPos.MutableBlockPos();

        for (int x = -rad; x<= rad; x++) {
            for (int z = -rad; z<= rad; z++) {


                bx = cx + x;
                bz = cz + z;
                ix = Math.floorDiv(bx, 16);
                iz = Math.floorDiv(bz, 16);

                if (ix > 0) {
                    bx -= 16;
                }
                if (iz > 0) {
                    bz -= 16;
                }

                chunk = chunks[ix][iz];

                if (chunk != null) {
                    ipos.setPos(pos.getX() + x, 0, pos.getZ() + z);

                    ib = Biome.getBiome(chunk.getBiomeArray()[bz * 16 + bx]);
                    if (ib == null) {
                        ib = Biomes.DEFAULT;
                    }
                    col = ib.getGrassColorAtPos(ipos);

                    r += (col & 0xFF0000) >> 16;
                    g += (col & 0x00FF00) >> 8;
                    b += (col & 0x0000FF);

                    divisor++;
                }
            }
        }

        if (divisor == 0) {
            return 0xFF00FF;
        }

        return (r / divisor & 255) << 16 | (g / divisor & 255) << 8 | b / divisor & 255;
    }

    @SideOnly(Side.CLIENT)
    public static class Listener {

        @SubscribeEvent
        public void login(PlayerEvent.PlayerLoggedInEvent event) {
            clearCache(event);
        }

        @SubscribeEvent
        public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
            clearCache(event);
        }

        public void clearCache(PlayerEvent event) {
            if (event.player.world.isRemote && event.player == Minecraft.getMinecraft().player) {
                GrassColours.clearCache();
            }
        }
    }
}
