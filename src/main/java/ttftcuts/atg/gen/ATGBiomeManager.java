package ttftcuts.atg.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ttftcuts.atg.biome.BiomeGroup;
import net.minecraft.world.biome.BiomeGenBase;

public class ATGBiomeManager {
	protected static Map<String, BiomeGroup> landGroups = new HashMap<String, BiomeGroup>();
	protected static Map<String, BiomeGroup> seaGroups = new HashMap<String, BiomeGroup>();
	protected static Map<String, BiomeGroup> coastGroups = new HashMap<String, BiomeGroup>();
	
	private static BiomeMod[] genMods = new BiomeMod[256];
	
	public static boolean sendGroupAssignmentEvents = false;
		
	public ATGBiomeManager() {}
	
	public static BiomeGroup addBiomeGroup(Map<String, BiomeGroup> grouplist, String name, double temp, double rainfall, double height, double minHeight, double maxHeight, long salt, boolean generate) {
		if ( grouplist.containsKey(name) ) { return null; }
		BiomeGroup group = new BiomeGroup(name, temp, rainfall, height, minHeight, maxHeight, salt, generate);
		return addBiomeGroup(grouplist, group);
	}
	
	public static BiomeGroup addBiomeGroup(Map<String, BiomeGroup> grouplist, BiomeGroup group) {
		if ( grouplist.containsKey(group.name) ) { return null; }
		grouplist.put(group.getName(), group);
		return group;
	}
	
	public static BiomeGroup addLandGroup(String name, double temp, double rainfall, double height, double minHeight, double maxHeight, long salt, boolean generate) {
		return addBiomeGroup(landGroups, name, temp, rainfall, height, minHeight, maxHeight, salt, generate);
	}
	
	public static BiomeGroup addSeaGroup(String name, double temp, double rainfall, double height, double minHeight, double maxHeight, long salt, boolean generate) {
		return addBiomeGroup(seaGroups, name, temp, rainfall, height, minHeight, maxHeight, salt, generate);
	}
	
	public static BiomeGroup addCoastGroup(String name, double temp, double rainfall, double height, double minHeight, double maxHeight, long salt, boolean generate) {
		return addBiomeGroup(coastGroups, name, temp, rainfall, height, minHeight, maxHeight, salt, generate);
	}
	
	public static BiomeGroup addLandGroup(String name, double temp, double rainfall, double height, long salt, boolean generate) {
		return addBiomeGroup(landGroups, name, temp, rainfall, height, 0.0, 1.0, salt, generate);
	}
	
	public static BiomeGroup addSeaGroup(String name, double temp, double rainfall, double height, long salt, boolean generate) {
		return addBiomeGroup(seaGroups, name, temp, rainfall, height, 0.0, 1.0, salt, generate);
	}
	
	public static BiomeGroup addCoastGroup(String name, double temp, double rainfall, double height, long salt, boolean generate) {
		return addBiomeGroup(coastGroups, name, temp, rainfall, height, 0.0, 1.0, salt, generate);
	}
	
	public static BiomeGroup addLandGroup(String name, double temp, double rainfall, double height, double minHeight, double maxHeight, long salt) {
		return addBiomeGroup(landGroups, name, temp, rainfall, height, minHeight, maxHeight, salt, true);
	}
	
	public static BiomeGroup addSeaGroup(String name, double temp, double rainfall, double height, double minHeight, double maxHeight, long salt) {
		return addBiomeGroup(seaGroups, name, temp, rainfall, height, minHeight, maxHeight, salt, true);
	}
	
	public static BiomeGroup addCoastGroup(String name, double temp, double rainfall, double height, double minHeight, double maxHeight, long salt) {
		return addBiomeGroup(coastGroups, name, temp, rainfall, height, minHeight, maxHeight, salt, true);
	}
	
	public static BiomeGroup addLandGroup(String name, double temp, double rainfall, double height, long salt) {
		return addBiomeGroup(landGroups, name, temp, rainfall, height, 0.0, 1.0, salt, true);
	}
	
	public static BiomeGroup addSeaGroup(String name, double temp, double rainfall, double height, long salt) {
		return addBiomeGroup(seaGroups, name, temp, rainfall, height, 0.0, 1.0, salt, true);
	}
	
	public static BiomeGroup addCoastGroup(String name, double temp, double rainfall, double height, long salt) {
		return addBiomeGroup(coastGroups, name, temp, rainfall, height, 0.0, 1.0, salt, true);
	}
	
	public static BiomeGroup addLandGroup(BiomeGroup group) {
		return addBiomeGroup(landGroups, group);
	}
	
	public static BiomeGroup addSeaGroup(BiomeGroup group) {
		return addBiomeGroup(seaGroups, group);
	}
	
	public static BiomeGroup addCoastGroup(BiomeGroup group) {
		return addBiomeGroup(coastGroups, group);
	}
	
	public static BiomeGroup land(String name) {
		return landGroups.get(name);
	}
	
	public static BiomeGroup sea(String name) {
		return seaGroups.get(name);
	}
	
	public static BiomeGroup coast(String name) {
		return coastGroups.get(name);
	}
	
	public static BiomeGroup getGroupFromName(String name) {
		BiomeGroup group = land(name);
		if ( group != null ) { return group; }
		group = sea(name);
		if ( group != null ) { return group; }
		return coast(name);
	}
	
	public static Map<String, BiomeGroup> land() {
		return landGroups;
	}
	
	public static Map<String, BiomeGroup> sea() {
		return seaGroups;
	}
	
	public static Map<String, BiomeGroup> coast() {
		return coastGroups;
	}
	
	public static BiomeMod setBiomeMod( BiomeGenBase biome, BiomeMod modifier ) {
		if (biome != null) {
			return setBiomeMod( biome.biomeID, modifier );
		}
		return modifier;
	}
	
	public static BiomeMod setBiomeMod( int biomeID, BiomeMod modifier ) {
		if ( biomeID > 0 ) {
			genMods[biomeID] = modifier;
		}
		return modifier;
	}
	
	public static BiomeMod addBiomeMod( BiomeGenBase biome ) {
		BiomeMod m = getMod(biome);
		if (m != null) {
			return m;
		}
		return setBiomeMod( biome, new BiomeMod() );
	}
	
	public static BiomeMod addBiomeMod( int biomeID ) {
		BiomeMod m = getMod(biomeID);
		if (m != null) {
			return m;
		}
		return setBiomeMod( biomeID, new BiomeMod() );
	}
	
	public static BiomeMod getMod( int biomeID ) {
		return genMods[biomeID];
	}
	
	public static BiomeMod getMod( BiomeGenBase biome ) {
		return getMod( biome.biomeID );
	}
	
	public static List<String> getGroupsFromBiome( BiomeGenBase biome ) {
		List<String> groups = new ArrayList<String>();
		
		int i;
		for (i=0; i<landGroups.size(); i++) {
			if ( landGroups.get(i).containsBiome(biome) ) {
				if ( !groups.contains( landGroups.get(i).name ) ) {
					groups.add( landGroups.get(i).name );
				}
			}
		}
		
		for (i=0; i<coastGroups.size(); i++) {
			if ( coastGroups.get(i).containsBiome(biome) ) {
				if ( !groups.contains( coastGroups.get(i).name ) ) {
					groups.add( coastGroups.get(i).name );
				}
			}
		}
		
		for (i=0; i<seaGroups.size(); i++) {
			if ( seaGroups.get(i).containsBiome(biome) ) {
				if ( !groups.contains( seaGroups.get(i).name ) ) {
					groups.add( seaGroups.get(i).name );
				}
			}
		}
		
		return groups;
	}
}
