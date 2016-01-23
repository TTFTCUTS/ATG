package ttftcuts.atg.gen;

import java.util.Random;

import ttftcuts.atg.ATG;
import ttftcuts.atg.utils.Vector3;

public class AltNoise {
	
	public final double scalex;
	public final double scaley;
	public final double scalez;
	
	private Vector3[] samples;
	private long seed;
	private Random rand;
	
	public AltNoise(long seed, double scalex, double scaley, double scalez) {
		this.scalex = 1.0 / scalex;
		this.scaley = 1.0 / scaley;
		this.scalez = 1.0 / scalez;
		
		this.seed = seed;
		this.rand = new Random(seed);
		
		this.samples = new Vector3[256];
		for (int i=0; i<this.samples.length; i++) {
			this.samples[i] = Vector3.randomUnitVector(this.rand);
		}
	}
	
	public AltNoise(long seed, double scale) {
		this(seed, scale, scale, scale);
	}
	
	public double noise(double x, double y, double z) {
		x *= scalex;
		y *= scaley;
		z *= scalez;
		
		int x0 = (int)Math.floor(x);
		int x1 = x0 + 1;
		int y0 = (int)Math.floor(y);
		int y1 = y0 + 1;
		int z0 = (int)Math.floor(z);
		int z1 = z0 + 1;
		
		double fx = x-x0;
		double fy = y-y0;
		double fz = z-z0;
		
		double xs = deriv1(fx);
		double ys = deriv1(fy);
		double zs = deriv1(fz);
		
		double g000 = this.gradNoise(x, y, z, x0, y0, z0);
		double g100 = this.gradNoise(x, y, z, x1, y0, z0);
		double g010 = this.gradNoise(x, y, z, x0, y1, z0);
		double g110 = this.gradNoise(x, y, z, x1, y1, z0);
		double g001 = this.gradNoise(x, y, z, x0, y0, z1);
		double g101 = this.gradNoise(x, y, z, x1, y0, z1);
		double g011 = this.gradNoise(x, y, z, x0, y1, z1);
		double g111 = this.gradNoise(x, y, z, x1, y1, z1);
		
		double front_top = lerp(g000,g100, xs);
		double front_bot = lerp(g010,g110, xs);
		
		double back_top = lerp(g001,g101, xs);
		double back_bot = lerp(g011,g111, xs);
		
		double front = lerp(front_top, front_bot, ys);
		double back = lerp(back_top, back_bot, ys);
		
		return lerp(front, back, zs);
	}
	
	private double[] derivative(double x, double y, double z) {
		x *= scalex;
		y *= scaley;
		z *= scalez;
		
		int x0 = (int)Math.floor(x);
		int x1 = x0 + 1;
		int y0 = (int)Math.floor(y);
		int y1 = y0 + 1;
		int z0 = (int)Math.floor(z);
		int z1 = z0 + 1;
		
		double fx0 = x-x0;
		double fy0 = y-y0;
		double fz0 = z-z0;
		
		double fx1 = x-x1;
		double fy1 = y-y1;
		double fz1 = z-z1;
		
		double u = deriv1(fx0);
		double v = deriv1(fy0);
		double w = deriv1(fz0);
		
		Vector3 g000 = this.grad(x0, y0, z0);
		Vector3 g100 = this.grad(x1, y0, z0);
		Vector3 g010 = this.grad(x0, y1, z0);
		Vector3 g110 = this.grad(x1, y1, z0);
		Vector3 g001 = this.grad(x0, y0, z1);
		Vector3 g101 = this.grad(x1, y0, z1);
		Vector3 g011 = this.grad(x0, y1, z1);
		Vector3 g111 = this.grad(x1, y1, z1);
		
		double dot000 = g000.x * fx0 + g000.y * fy0 + g000.z * fz0;
		double dot100 = g100.x * fx1 + g100.y * fy0 + g100.z * fz0;
		double dot010 = g010.x * fx0 + g010.y * fy1 + g010.z * fz0;
		double dot110 = g110.x * fx1 + g110.y * fy1 + g110.z * fz0;
		double dot001 = g001.x * fx0 + g001.y * fy0 + g001.z * fz1;
		double dot101 = g101.x * fx1 + g101.y * fy0 + g101.z * fz1;
		double dot011 = g011.x * fx0 + g011.y * fy1 + g011.z * fz1;
		double dot111 = g111.x * fx1 + g111.y * fy1 + g111.z * fz1;
		
		double ud = 30 * fx0 * fx0 * (fx0 - 1) * (fx0 - 1);
		double vd = 30 * fy0 * fy0 * (fy0 - 1) * (fy0 - 1);
		double wd = 30 * fz0 * fz0 * (fz0 - 1) * (fz0 - 1);
		
		double n = dot000 
				+ u*(dot100 - dot000)
				+ v*(dot010 - dot000)
				+ w*(dot001 - dot000)
				+ u*v*(dot110 - dot010 - dot100 + dot000)
				+ u*w*(dot101 - dot001 - dot100 + dot000)
				+ v*w*(dot011 - dot001 - dot010 + dot000)
				+ u*v*w*(dot111 - dot011 - dot101 + dot001 - dot110 + dot010 + dot100 - dot000);
		
		double nx = g000.x
			 	+ ud*  	 (dot100 - dot000)
			 	+ u*   	 (g100.x - g000.x)
			 	+ v*   	 (g010.x - g000.x)
			 	+ w*   	 (g001.x - g000.x)
			 	+ ud*v*	 (dot110 - dot010 - dot100 + dot000)
			 	+ u*v*   (g110.x - g010.x - g100.x + g000.x)
			 	+ ud*w*  (dot101 - dot001 - dot100 + dot000)
			 	+ u*w* 	 (g101.x - g001.x - g100.x - g000.x)
			 	+ v*w*   (g011.x - g001.x - g010.x + g000.x)
			 	+ ud*v*w*(dot111 - dot011 - dot101 + dot001 - dot110 + dot010 + dot100 - dot000)
			 	+ u*v*w* (g111.x - g011.x - g101.x + g001.x - g110.x + g010.x + g100.x - g000.x);
		
		double ny = g000.y
			 	+ u*     (g100.y - g000.y)
			 	+ vd*    (dot010 - dot000)
			 	+ v*     (g010.y - g000.y)
			 	+ w*     (g001.y - g000.y)
			 	+ u*vd*  (dot110 - dot010 - dot100 + dot000)
			 	+ u*v*   (g110.y - g010.y - g100.y + g000.y)
			 	+ u*w*   (g101.y - g001.y - g100.y + g000.y)
			 	+ vd*w*  (dot011 - dot001 - dot010 + dot000)
			 	+ v*w*   (g011.y - g001.y - g010.y + g000.y)
			 	+ u*vd*w*(dot111 - dot011 - dot101 + dot001 - dot110 + dot010 + dot100 - dot000)
			 	+ u*v*w* (g111.y - g011.y - g101.y + g001.y - g110.y + g010.y + g100.y - g000.y);
		
		double nz = g000.z
			 	+ u*     (g100.z - g000.z)
			 	+ v*     (g010.z - g000.z)
			 	+ wd*    (dot001 - dot000)
			 	+ w*     (g001.z - g000.z)
			 	+ u*v*   (g110.z - g010.z - g100.z + g000.z)
			 	+ u*wd*  (dot101 - dot001 - dot100 + dot000)
			 	+ u*w*   (g101.z - g001.z - g100.z + g000.z)
			 	+ v*wd*  (dot011 - dot001 - dot010 + dot000)
			 	+ v*w*   (g011.z - g001.z - g010.z + g000.z)
			 	+ u*v*wd*(dot111 - dot011 - dot101 + dot001 - dot110 + dot010 + dot100 - dot000)
			 	+ u*v*w* (g111.z - g011.z - g101.z + g001.z - g110.z + g010.z + g100.z - g000.z);
		
		return new double[] {n, nx, ny, nz};
	}
	
	private double noiseOctavesScaled(double x, double y, double z, int octaves, double lacunarity, double gain, double scaling) {
		double sum = 0.0;
		double scale = 1.0;
		double amp = 1.0;
		
		double n;
		
		for (int i=0; i<octaves; i++) {
			n = this.noise(x * scale * scaling + i*688889, y * scale * scaling + i*968041, z * scale * scaling + i*739397);
			sum += n * amp;
			scale *= lacunarity;
			amp *= gain;
		}
		
		return sum * 0.5 + 0.5;
	}
	
	public double noiseOctaves(double x, double y, double z, int octaves, double lacunarity, double gain) {
		return noiseOctavesScaled(x,y,z, octaves, lacunarity, gain, 1.0);
	}
	
	public double swissTurbulence(double x, double z, int octaves, double lacunarity, double gain, double warp) {
		double sum = 0.0;
		double scale = 1.0;
		double amp = 1.0;
		
		double dsumx = 0.0;
		double dsumz = 0.0;
		
		double[] n;
		
		for (int i=0; i<octaves; i++) {
			n = this.derivative((x + dsumx * warp) * scale, i * 688889, (z + dsumz * warp) * scale);
			
			//ATG.logger.info("deriv: "+n[0]+", ("+n[1]+","+n[2]+","+n[3]+")");
			
			sum += amp * (1.0 - Math.abs(n[0]));
			dsumx += n[1] * amp * -n[0];
			dsumz += n[3] * amp * -n[0];
			scale *= lacunarity;
			amp *= gain * Math.min(1.0, Math.max(0.0, sum));
		}
		
		return sum;
	}
	
	public double jordanTurbulenceRaw(double x, double z, int octaves, double lacunarity, double gain0, double gain, double warp0, double warp, double damp0, double damp, double dampscale) {
		double amp = gain0;
		double scale = lacunarity;
		double damped_amp = amp * gain;
		double dsx = 1.0 / this.scalex;
		double dsz = 1.0 / this.scalez;
		
		double[] n = this.derivative(x * scale, 0, z * scale);
		double sum = n[0]*n[0];
		
		double dsumx_warp = n[1] * n[0] * warp0 * dsx;
		double dsumz_warp = n[3] * n[0] * warp0 * dsz;
		
		double dsumx_damp = n[1] * n[0] * damp0 * dsx;
		double dsumz_damp = n[3] * n[0] * damp0 * dsz;
		
		for (int i=1; i<octaves; i++) {
			n = this.derivative(x*scale + dsumx_warp, i * 688889, z*scale + dsumz_warp);
			sum += damped_amp * ((n[0] * n[0] * 1.5) + 0.1);
			dsumx_warp += n[1] * n[0] * warp * dsx;
			dsumz_warp += n[3] * n[0] * warp * dsz;
			dsumx_damp += n[1] * n[0] * damp * dsx;
			dsumz_damp += n[3] * n[0] * damp * dsz;
			scale *= lacunarity;
			amp *= gain;
			damped_amp = amp * (1.0 - dampscale / (1.0 + dsumx_damp * dsumx_damp + dsumz_damp * dsumz_damp));
		}
		
		return sum;
	}
	
	public double jordanTurbulence(double x, double z, int octaves, double lacunarity, double gain0, double gain, double warp0, double warp, double damp0, double damp, double dampscale, int distortionoctaves, double distortionscale, double distortion, double distortiongain) {
		x += noiseOctavesScaled(x, 5, z, distortionoctaves, lacunarity, distortiongain, distortionscale) * distortion * distortionscale * this.scaley;
		z += noiseOctavesScaled(x, 9, z, distortionoctaves, lacunarity, distortiongain, distortionscale) * distortion * distortionscale * this.scaley;
		return jordanTurbulenceRaw(x, z, octaves, lacunarity, gain0, gain, warp0, warp, damp0, damp, dampscale);
	}
	
	//############
	
	double slerp(double n) {
		return n * n * (3 - 2 * n);
	}
	double lerp(double a, double b, double n) {
		return a*(1.0-n) + b*n;
	}
	double deriv1(double n) {
		return n * n * n * (n * (n * 6 - 15) + 10);
	}
	double deriv2(double n) {
		return n * n * (n * (n * 30 - 60) + 30);
	}
	double deriv3(double n) {
		return n * n * n * (n * (n * 36 - 75) + 40);
	}
	
	private Vector3 grad(int x, int y, int z) {
		long index = (1619 * x + 31337 * y + 6971 * z + 1013 * this.seed) & 0xffffffff;
		index ^= (index >> 8);
		index &= 0xff;
		
		return this.samples[(int) index];
	}
	
	private double gradNoise(double fx, double fy, double fz, int ix, int iy, int iz) {
		Vector3 grad = this.grad(ix, iy, iz);
		Vector3 point = new Vector3(fx-ix, fy-iy, fz-iz);
		
		return grad.dot(point);
	}
}
