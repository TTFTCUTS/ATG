package ttftcuts.atg.gen;

import java.util.Random;

public class ATGPerlin {
	
	Random rand;
	double scalex, scaley, scalez;
	int[] permutation;
	
	public ATGPerlin(Random rand, double scalex, double scaley, double scalez) {
		this.rand = rand;
		this.scalex = 1.0D/scalex;
		this.scaley = 1.0D/scaley;
		this.scalez = 1.0D/scalez;
		
		this.permutation = new int[512];
		
		for ( int i = 0; i < 256; i++ ) {
			int n = this.rand.nextInt(256);
			this.permutation[i] = n;
			this.permutation[i+256] = n;
		}
	}
	
	public ATGPerlin(Random rand, double scale) {
		this(rand, scale, scale, scale);
	}
	
	public ATGPerlin(Random rand) {
		this(rand, 1,1,1);
	}
	
	public double noise(double ix, double iy, double iz) {
		double x = ix*this.scalex,
			   y = iy*this.scaley,
			   z = iz*this.scalez;
		
		int X = (int)Math.floor(x) & 255,                  // FIND UNIT CUBE THAT
			Y = (int)Math.floor(y) & 255,                  // CONTAINS POINT.
			Z = (int)Math.floor(z) & 255;
			x -= Math.floor(x);                                // FIND RELATIVE X,Y,Z
			y -= Math.floor(y);                                // OF POINT IN CUBE.
			z -= Math.floor(z);
		double u = fade(x),                                // COMPUTE FADE CURVES
			   v = fade(y),                                // FOR EACH OF X,Y,Z.
			   w = fade(z);
		int A = this.permutation[X  ]+Y, AA = this.permutation[A]+Z, AB = this.permutation[A+1]+Z,      // HASH COORDINATES OF
		    B = this.permutation[X+1]+Y, BA = this.permutation[B]+Z, BB = this.permutation[B+1]+Z;      // THE 8 CUBE CORNERS,
		
		return lerp(w, lerp(v, lerp(u, grad(this.permutation[AA  ], x  , y  , z   ),  // AND ADD
							           grad(this.permutation[BA  ], x-1, y  , z   )), // BLENDED
							   lerp(u, grad(this.permutation[AB  ], x  , y-1, z   ),  // RESULTS
							           grad(this.permutation[BB  ], x-1, y-1, z   ))),// FROM  8
					   lerp(v, lerp(u, grad(this.permutation[AA+1], x  , y  , z-1 ),  // CORNERS
						 	           grad(this.permutation[BA+1], x-1, y  , z-1 )), // OF CUBE
						 	   lerp(u, grad(this.permutation[AB+1], x  , y-1, z-1 ),
							           grad(this.permutation[BB+1], x-1, y-1, z-1 ))));
	}
	
	public double normNoise(double x, double y, double z) {
		return (this.noise(x,y,z) + 1)*0.49 + 0.02;
	}
	
	static double fade(double t) {
		return t * t * t * ( t * ( t * 6 - 15 ) + 10 );
	}
	
	static double lerp(double t, double a, double b) {
		return a + t * ( b-a );
	}
	
	static double grad( int hash, double x, double y, double z ) {
		int h = hash & 15;
		double u = h<8 ? x : y;
		double v = h<4 ? y : h==12||h==14 ? x : z;
		
		return ( (h&1) == 0 ? u : -u ) + ( (h&2) == 0 ? v : -v );
	}
}
