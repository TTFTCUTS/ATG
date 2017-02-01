package ttftcuts.atg.util;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import ttftcuts.atg.generator.ChunkProviderATG;

public abstract class GeneralUtil {

    /***
     * Gets the ChunkProviderATG associated with a given World, or null if the provider is not from ATG.
     * If the provider is a ChunkProviderServer, checks the chunkGenerator field of that instead.
     * @param world The world from which to get the provider.
     * @return The World's provider if it is a ChunkProviderATG, or null if not.
     */
    public static ChunkProviderATG getATGProvider(World world) {
        IChunkProvider provider = world.getChunkProvider();
        ChunkProviderATG atgprovider = null;

        if (provider instanceof ChunkProviderATG) {
            atgprovider = (ChunkProviderATG)provider;
        } else if (provider instanceof ChunkProviderServer) {
            if (((ChunkProviderServer) provider).chunkGenerator instanceof ChunkProviderATG) {
                atgprovider = (ChunkProviderATG)((ChunkProviderServer) provider).chunkGenerator;
            }
        }

        return atgprovider;
    }
}
