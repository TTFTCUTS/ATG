package ttftcuts.atg.biome;

public class BiomeSortable implements Comparable<BiomeSortable> {
	
	public BiomeGroup biomegroup;
	public double suitability;

	public BiomeSortable(BiomeGroup b, double s) {
		this.biomegroup = b;
		this.suitability = s;
	}
	
	@Override
	public int compareTo(BiomeSortable other) {
		return this.suitability > other.suitability ? -1 : this.suitability < other.suitability ? 1 : 0;
	}
}
