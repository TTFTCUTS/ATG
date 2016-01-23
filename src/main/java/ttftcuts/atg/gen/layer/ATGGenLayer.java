package ttftcuts.atg.gen.layer;

import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerFuzzyZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;

public abstract class ATGGenLayer extends GenLayer {

	public long seed;
	
	public ATGGenLayer(long seed) {
		super(seed);
		this.seed = seed;
	}

	public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType worldtype) {
		// terrain variables
		ATGGenLayerRarity rarity = new ATGGenLayerRarity(seed * 96872431L);
		ATGGenLayerInland inland = new ATGGenLayerInland(seed * 12650832L);
		ATGGenLayer height = new ATGGenLayerHeight(seed * 12650832L, inland, rarity);
		ATGGenLayer temp = new ATGGenLayerTemperature(seed * 37214290L, height, inland);
		ATGGenLayer moisture = new ATGGenLayerMoisture(seed * 344592745L, temp, inland);
				
		// "alternate" noise, use under question
		//ATGGenLayerAltNoise alt1 = new ATGGenLayerAltNoise(seed * 29472848L);
		//ATGGenLayerAltNoise alt2 = new ATGGenLayerAltNoise(seed * 84723372L);
		
		// variety blobs
		GenLayer blobbase = new ATGGenLayerBlobs(seed * 23941541L);
		GenLayer fuzz = new GenLayerFuzzyZoom(seed * 98362841L, blobbase);
		GenLayer zoomed = GenLayerZoom.magnify(seed * 93285216L, fuzz, 1);
		GenLayer sub = new ATGGenLayerSub(seed * 52749136L, zoomed);
		fuzz = new GenLayerFuzzyZoom(seed * 62854134L, sub);
		zoomed = GenLayerZoom.magnify(seed * 93285216L, fuzz, Math.max(1, 5));//ATGMainConfig.genModBlobScale.getInt(4) + 1));
		//GenLayer blob = new GenLayerVoronoiZoom(seed * 56195024L, zoomed);
		GenLayer blob = zoomed;
		
		//GenLayer blobtest = new ATGGenLayerBlobTest(seed * 38134912L, blob);
		
		// final biomes!
		ATGGenLayer biome = new ATGGenLayerBiomes(seed * 39192845L, height, temp, moisture, inland, rarity, blob);
		
		return new GenLayer[] {biome, biome, height, temp, moisture};
	}
	
	public abstract int getInt(int x, int z);
	public abstract double getDouble(int x, int z);

}
