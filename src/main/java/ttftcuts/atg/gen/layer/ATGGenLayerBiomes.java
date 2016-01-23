package ttftcuts.atg.gen.layer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ttftcuts.atg.biome.BiomeGroup;
import ttftcuts.atg.biome.BiomeSortable;
import ttftcuts.atg.gen.ATGBiomeManager;
import ttftcuts.atg.utils.ATGUtils;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraftforge.common.MinecraftForge;

public class ATGGenLayerBiomes extends ATGGenLayer {

	protected ATGGenLayer height;
	protected ATGGenLayer temp;
	protected ATGGenLayer moisture;
	protected ATGGenLayer inland;
	protected ATGGenLayer rarity;
	protected GenLayer blob;
	protected Random random;
	protected Random fuzz;

	//protected boolean ebxl;
	
	//private List<ATGBiomeGroup> biomeset; 
	//private List<ATGBiomeSortable> suitables;
	private int[] layerCache;
	private int blobval;
	
	private int sealevel;
	private double seadouble;
	private double sealimit;
	private double landlimit;

	public ATGGenLayerBiomes(long seed, ATGGenLayer height, ATGGenLayer temp, ATGGenLayer moisture, ATGGenLayer inland, ATGGenLayer rarity, GenLayer blob){
		super(seed);
		this.height = height;
		this.temp = temp;
		this.moisture = moisture;
		this.inland = inland;
		this.rarity = rarity;
		
		this.sealevel = 63; //ATGMainConfig.genModSeaLevel.getInt(63);
		this.seadouble = (this.sealevel+1) / 256.0;
		this.sealimit = seadouble - 0.02;
		this.landlimit = seadouble + 0.001;
		
		this.blob = blob;

		this.random = new Random(seed);
		this.fuzz = new Random(0);

		//this.biomeset = new ArrayList<ATGBiomeGroup>();
		//this.suitables = new ArrayList<ATGBiomeSortable>();
		this.layerCache = new int[32768];
		
		this.blobval = 0;
	}

	private double getFuzz(int x, int z, int salt) {
		double out = 0.0D;

		int ox = 3847234;
		int oz = 8362482;

		long xm = x*ox;
		long zm = z*oz;

		this.fuzz.setSeed((xm^zm)+salt);
		out += this.fuzz.nextDouble();

		this.fuzz.setSeed(((xm+ox)^zm)+salt);
		out += this.fuzz.nextDouble();

		this.fuzz.setSeed(((xm-ox)^zm)+salt);
		out += this.fuzz.nextDouble();

		this.fuzz.setSeed((xm^(zm+oz))+salt);
		out += this.fuzz.nextDouble();

		this.fuzz.setSeed((xm^(zm-oz))+salt);
		out += this.fuzz.nextDouble();

		return (out*0.2 - 0.5);
	}

	private double getFertility(double temp, double moisture, double height) {
		return Math.max(0, moisture * 1.15 - ( Math.abs( temp - 0.65 ) ) - ( height - 0.5 ) );
	}

	@Override
	public int[] getInts(int x, int z, int w, int h) {
		int[] heightData = this.height.getInts(x, z, w, h);
		int[] tempData = this.temp.getInts(x, z, w, h);
		int[] moistureData = this.moisture.getInts(x, z, w, h);
		int[] inlandData = this.inland.getInts(x, z, w, h);
		//int[] rarityData = this.rarity.getInts(x, z, w, h);

		int[] data = this.layerCache;		
		
		List<BiomeGroup> biomeset = new ArrayList<BiomeGroup>(); 
		List<BiomeSortable> suitables = new ArrayList<BiomeSortable>();
		
		int pos;
		int biome;
		BiomeGroup finalgroup = null;
		BiomeGroup eventgroup = null;
		for (int dz = 0; dz < h; ++dz)
		{
			for (int dx = 0; dx < w; ++dx)
			{
				pos = dx+dz*w;
				double height = ( heightData[pos] )* 0.00390625D;  // /256
				double temp = ( tempData[pos] + this.getFuzz(x+dx,z+dz,345)*6 )* 0.00390625D; // /256
				
				if ( true ) { //!ATGMainConfig.useClimate.getBoolean(false) ) {
					temp = ATGUtils.spreadRange(temp, 0.4, 1.5, -0.15);//1.3
				}
				
				double moisture = ( moistureData[pos] + this.getFuzz(x+dx,z+dz,103)*4 ) * 0.00390625D; // /256
				moisture = ATGUtils.spreadRange(moisture, 0.4, 1.5, 0.07);//1.2

				double inland = inlandData[pos] * 0.00390625D; // /256;

				temp += (Math.max(0, inland-0.5));
				moisture -= (Math.max(0, inland-0.5));

				biome = BiomeGenBase.plains.biomeID;

				double fertility = getFertility(temp, moisture, height);

				/*if ( ATGBiomeList.volcano.isPresent() && height >= MathHelper.getRandomDoubleInRange(this.random, 0.973, 0.982) ) { //0.977
					// volcano at peaks overrides all!
					biome = ATGBiomeList.volcano.get().biomeID;
				} else {*/
				{
					// normal biomes
					biomeset.clear();
					
					double heightFuzz = this.getFuzz(x+dx,z+dz,345) * 0.00390625D;
					
					if ( height - ( heightFuzz * 0.5 ) < this.sealimit ) { //0.23 //0.238
						// sea biomes
						biomeset.addAll( ATGBiomeManager.sea().values() );
						biome = BiomeGenBase.ocean.biomeID;
					} else if ( height < this.landlimit ) { //0.251
						// beach biomes
						biomeset.addAll( ATGBiomeManager.coast().values() );
						biome = BiomeGenBase.beach.biomeID;
					} else {
						// land biomes
						biomeset.addAll( ATGBiomeManager.land().values() );
						biome = BiomeGenBase.plains.biomeID;
					}
					
					suitables.clear();
					
					if ( !biomeset.isEmpty() ) {

						double bh;
						double bt;
						double bm;
						double bf;
	
						double dt;
						double dm;
						double df;
	
						double suitability;
						BiomeGroup b;
	
						for ( int i=0; i< biomeset.size(); i++ ) {
							b = biomeset.get(i);
							b.process();
	
							if ( b == null || b.getCount() == 0 || !b.generate ) {
								continue;
							}
							
							if ( height + heightFuzz > b.maxHeight || height + heightFuzz < b.minHeight ) {
								continue;
							}
							
							bh = b.height;
							bt = b.temp;
							bm = b.rainfall;
	
							bt = ATGUtils.spreadRange(bt, 0.4, 1.3, -0.3);
							bm = ATGUtils.spreadRange(bm, 0.4, 1.2, 0.07);
	
							bf = getFertility(bt,bm,bh);
							
							dt = 1-Math.abs(temp-bt);
							dm = 1-Math.abs(moisture-bm);
							df = 1-Math.abs(fertility-bf);
	
							suitability = b.suitability + df*0.5 + dt + dm;
							
							suitables.add(new BiomeSortable(b,suitability));
						}

						Collections.sort(suitables);
						
						if ( suitables.size() > 0 ) {
							finalgroup = suitables.get(0).biomegroup;
							
							// if events are enabled, send away!
							/*if ( ATGBiomeManager.sendGroupAssignmentEvents ) {
								ATGBiomeGroupAssignmentEvent event = new ATGBiomeGroupAssignmentEvent(x+dx, z+dz, height, temp, moisture, finalgroup.name);
								MinecraftForge.TERRAIN_GEN_BUS.post(event);
								if (event.modified) {
									eventgroup = ATGBiomeManager.getGroupFromName(event.group);
									eventgroup.process();
									if (eventgroup != null && eventgroup.getCount() > 0) {
										finalgroup = eventgroup;
									}
								}
							}*/
							
							this.blobval = blob.getInts(
									x+dx + (int)finalgroup.ox,
									z+dz + (int)finalgroup.oz,
							1, 1)[0];
							
							try {
								biome = finalgroup.getBiome(this.blobval).biomeID;
							} catch (NullPointerException e) {
								System.out.println("ATG: Something went wrong when getting the biome at "+(x+dx)+","+(z+dz)+" from the "+finalgroup.name+" group:");
								e.printStackTrace();
							}
							
							suitables.get(0).biomegroup = null; // maybe help plz?
							finalgroup = null;
							eventgroup = null;
						}
					}
				}
				data[pos] = biome;
			}
		}
		biomeset.clear();
		suitables.clear();
		biomeset = null;
		suitables = null;
		return data;
	}

	@Override
	public int getInt(int x, int z) {
		return this.getInts(x,z,1,1)[0];
	}
	public double getDouble(int x, int z) {
		// not applicable to this layer
		return 0D;
	}

}
