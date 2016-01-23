package ttftcuts.atg.biome;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import ttftcuts.atg.gen.ATGBiomeManager;
import ttftcuts.atg.gen.BiomeMod;
import ttftcuts.atg.gen.layer.ATGGenLayerBlobs;
import ttftcuts.atg.utils.ATGUtils;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGroup {

	public String name;
	public double temp;
	public double rainfall;
	public double height;
	public double minHeight;
	public double maxHeight;
	public long blobsalt;
	public boolean generate;
	
	// blob map offsets
	public int ox = 0;
	public int oz = 0;
	
	public double suitability = 0;
	
	private Map<Integer, Double> biomes;
	
	private NavigableMap<Double, BiomeGenBase> biomesFinal;
	private double totalWeight;
	
	public BiomeGroup(String name, double temp, double rainfall, double height, double minHeight, double maxHeight, long blobsalt, boolean generate) {
		this.name = name;
		this.temp = temp;
		this.rainfall = rainfall;
		this.height = height;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.blobsalt = blobsalt;
		this.generate = generate;
		
		this.ox = (int)( ( ATGUtils.xorShift64( 2846 * ATGUtils.xorShift64(blobsalt + 7391834) - blobsalt ) ) % Integer.MAX_VALUE);
		this.oz = (int)( ( ATGUtils.xorShift64( 9672 * ATGUtils.xorShift64(blobsalt + 4517384) - blobsalt ) ) % Integer.MAX_VALUE);
		
		this.biomes = new TreeMap<Integer, Double>();/* new Comparator<BiomeGenBase>() {
			public int compare(BiomeGenBase o1, BiomeGenBase o2) {
				return o1.biomeID > o2.biomeID ? 1:-1;
			}
		});*/
	}
	
	public BiomeGroup(String name, double temp, double rainfall, long blobsalt) {
		this(name, temp, rainfall, 0.5, blobsalt);
	}
	
	public BiomeGroup(String name, double temp, double rainfall, double height, long blobsalt) {
		this(name, temp, rainfall, height, 0.0, 1.0, blobsalt, true);
	}
	
	public BiomeGroup(String name, double temp, double rainfall, long blobsalt, boolean generate) {
		this(name, temp, rainfall, 0.5, blobsalt, generate);
	}
	
	public BiomeGroup(String name, double temp, double rainfall, double height, long blobsalt, boolean generate) {
		this(name, temp, rainfall, height, 0.0, 1.0, blobsalt, generate);
	}
		
	public String getName() {
		return this.name;
	}
	
	public int getCount() {
		return this.biomesFinal.size();
	}
	
	public boolean containsBiome(BiomeGenBase biome) {
		return this.biomesFinal.containsValue(biome);
	}
	
	public BiomeGroup modSuitability(double suitability) {
		this.suitability = suitability;
		return this;
	}
	
	public BiomeGroup addBiome( BiomeGenBase biome, double weight, BiomeMod mod ) {
		addBiome(biome, weight);
		ATGBiomeManager.setBiomeMod(biome, mod);
		return this;
	}
	
	public BiomeGroup addBiome( BiomeGenBase biome, double weight ) {
		if (biome != null) {
			if ( this.biomes.containsKey(biome.biomeID) ) {
				this.biomes.put(biome.biomeID, this.biomes.get(biome) + weight );
			} else {
				this.biomes.put(biome.biomeID, weight);
			}
		}
		return this;
	}
	
	public BiomeGroup addBiome( BiomeGenBase biome ) {
		this.addBiome( biome, 1.0 );
		return this;
	}
	
	public BiomeGroup addBiome( BiomeGenBase biome, BiomeMod mod) {
		this.addBiome( biome, 1.0, mod );
		return this;
	}
	
	public BiomeGroup removeBiome( BiomeGenBase biome ) {
		if ( biome != null) {
			this.biomes.remove(biome.biomeID);
		}
		return this;
	}
	
	public BiomeGroup replaceBiome( BiomeGenBase old, BiomeGenBase replacement, double weight ) {
		this.removeBiome(old);
		this.addBiome(replacement, weight);
		return this;
	}
	
	public BiomeGroup replaceBiome( BiomeGenBase old, BiomeGenBase replacement, double weight, BiomeMod mod ) {
		this.replaceBiome( old, replacement, weight );
		ATGBiomeManager.setBiomeMod(replacement, mod);
		return this;
	}
	
	public BiomeGroup replaceBiome( BiomeGenBase old, BiomeGenBase replacement ) {
		this.removeBiome(old);
		this.addBiome(replacement);
		return this;
	}
	
	public BiomeGroup replaceBiome( BiomeGenBase old, BiomeGenBase replacement, BiomeMod mod ) {
		this.removeBiome(old);
		this.addBiome(replacement, mod);
		return this;
	}
	
	public void process() {
		if ( this.biomesFinal == null ) {
			//System.out.println("Biome list: " + this.biomes);
			this.biomesFinal = new TreeMap<Double, BiomeGenBase>();
			this.totalWeight = 0;
			BiomeGenBase[] biomeList = BiomeGenBase.getBiomeGenArray();
			
			for ( Map.Entry<Integer, Double> entry : this.biomes.entrySet() ) {
				if ( true ) { //ATGBiomeConfig.generate[entry.getKey()].getBoolean(true) ) {
				
					if ( 1.0 > 0 ) { //ATGBiomeConfig.biomeChance[entry.getKey()].getDouble(1.0) > 0) {
						this.totalWeight += entry.getValue() * 1.0; //ATGBiomeConfig.biomeChance[entry.getKey()].getDouble(1.0);
						this.biomesFinal.put(this.totalWeight, biomeList[entry.getKey()]);
					}
				}
			}
		}
	}
	
	public BiomeGenBase getBiome(int random) {
		this.process();
		int nbiome = random & ((int) Math.pow(2, ATGGenLayerBlobs.biomebits)-1);
		int nsub = random & (((int) Math.pow(2, ATGGenLayerBlobs.subbits)-1) << ATGGenLayerBlobs.biomebits);
		nsub >>= ATGGenLayerBlobs.biomebits;
		
		double biomeno = (nbiome * this.totalWeight) / ATGGenLayerBlobs.granularity;
		
		if ( this.biomesFinal.isEmpty() ) { 
			System.out.println("ATG: Biome group "+this.name+" is empty but being queried! Returning plains as fallback.");
			return BiomeGenBase.plains; 
		}
		
		BiomeGenBase biome = BiomeGenBase.plains;
		try {
			biome = this.biomesFinal.ceilingEntry( biomeno ).getValue();
		} catch (NullPointerException e) {
			System.out.println("ATG: Something went wrong when getting a suitable biome from the "+this.name+" biome group:");
			e.printStackTrace();
			return BiomeGenBase.plains;
		}
		
		BiomeMod mod = ATGBiomeManager.getMod(biome);
			
		if ( mod != null ) {
			
			BiomeGenBase subBiome = mod.getSubBiome(nsub / (double)ATGGenLayerBlobs.subgranularity);
			//System.out.println("Out: " + subBiome);
			if ( subBiome != null ) {
				biome = subBiome;
			}
		}
		
		//System.out.println("input: " + random + "  derived biome val: " + nbiome + "  derived sub-biome val: " + nsub + "  Final biome derived: " + biome.biomeName);
		
		return biome;
	}
}
