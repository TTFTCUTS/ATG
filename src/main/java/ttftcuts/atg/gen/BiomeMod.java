package ttftcuts.atg.gen;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;

import ttftcuts.atg.api.IGenMod;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;


public class BiomeMod {
	public Map<BiomeGenBase, Double> subBiomes;
	public NavigableMap<Double, BiomeGenBase> subBiomesFinal;
	public double totalSubWeight = 0;
	public Optional<IGenMod> genMod = Optional.empty();
	
	public BiomeMod() {
		this.subBiomes = new TreeMap<BiomeGenBase, Double>( new Comparator<BiomeGenBase>() {
			public int compare(BiomeGenBase o1, BiomeGenBase o2) {
				return o1.biomeID > o2.biomeID ? 1:-1;
			}
		});
	}
	
	private void process() {
		if ( this.subBiomesFinal == null ) {
			this.subBiomesFinal = new TreeMap<Double, BiomeGenBase>();
			this.totalSubWeight = 0;
			
			for ( Map.Entry<BiomeGenBase, Double> entry : this.subBiomes.entrySet() ) {
				if ( true ) { //ATGBiomeConfig.generate[entry.getKey().biomeID].getBoolean(true)) {
				
					if ( 1.0 > 0 ) { //ATGBiomeConfig.biomeChance[entry.getKey().biomeID].getDouble(1.0) > 0 ) {
						this.totalSubWeight += entry.getValue() * 1.0; //ATGBiomeConfig.biomeChance[entry.getKey().biomeID].getDouble(1.0);
						this.subBiomesFinal.put(this.totalSubWeight, entry.getKey());
					}
				}
			}
			
			this.totalSubWeight += 1.0;
		}
	}
	
	public BiomeGenBase getSubBiome(double random) {
		if ( this.subBiomes.isEmpty() ) {
			return null;
		}
		this.process();
		//System.out.println("Sub pick: "+ random +" - "+ random*this.totalSubWeight +"/"+ this.totalSubWeight);
		
		Map.Entry<Double, BiomeGenBase> entry = this.subBiomesFinal.ceilingEntry(random * this.totalSubWeight);
		
		return entry != null ? entry.getValue() : null;
	}
	
	public BiomeMod addSubBiome(BiomeGenBase biome, double weight) {
		if ( !this.subBiomes.containsKey(biome) ) {
			this.subBiomes.put(biome, weight);
		} else {
			this.subBiomes.put(biome, this.subBiomes.get(biome) + weight);
		}
		return this;
	}
	
	public BiomeMod addGenMod( IGenMod mod ) {
		this.genMod = Optional.of(mod);
		return this;
	}
	
	public int modify( World world, int height, Random random, double rawHeight, int x, int z ) {
		if ( this.genMod.isPresent() ) {
			return this.genMod.get().modify(world, height, random, rawHeight, x, z);
		}
		return height;
	}
	
	public double noiseFactor() {
		if ( this.genMod.isPresent() ) {
			return this.genMod.get().noiseFactor();
		}
		return 1.0;
	}
}
