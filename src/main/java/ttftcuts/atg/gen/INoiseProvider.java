package ttftcuts.atg.gen;

public interface INoiseProvider {
	public double getHeight(int x, int z);
	
	public int getHeightInt(int x, int z);
	
	public double getInland(int x, int z);
}
