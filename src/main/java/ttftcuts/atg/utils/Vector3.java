package ttftcuts.atg.utils;

import java.util.Random;

public final class Vector3 {

	public final double x;
	public final double y;
	public final double z;
	
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3() {
		this(0,0,0);
	}
	
	public static Vector3 randomUnitVector(Random rand) {
		double theta = rand.nextDouble() * Math.PI * 2;
		double height = rand.nextDouble() * 2 - 1;
		double o = Math.sqrt(1 - height*height);
		return new Vector3(Math.cos(theta)*o, Math.sin(theta)*o, height);
	}
	
	public double dot(Vector3 other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}
	
	public Vector3 add(Vector3 o) {
		return new Vector3(x + o.x, y + o.y, z + o.z);
	}
	
	public Vector3 sub(Vector3 o) {
		return new Vector3(x - o.x, y - o.y, z - o.z);
	}
	
	public Vector3 mult(double m) {
		return new Vector3(x*m, y*m, z*m);
	}
}
