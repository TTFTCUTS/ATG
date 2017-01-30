package ttftcuts.atg.noise;

import java.util.Random;

public class JordanTurbulence extends Noise {
    OpenSimplexNoise generator;
    double scale;

    OctaveNoise distortion;

    int octaves;
    double lacunarity;
    double gain1;
    double gain;
    double warp0;
    double warp;
    double damp0;
    double damp;
    double damp_scale;

    double distortion_magnitude;
    int distortion_octaves;
    double distortion_scale;
    double distortion_gain;

    public JordanTurbulence(Random rand, double scale, int octaves, double lacunarity, double gain1, double gain, double warp0, double warp, double damp0, double damp, double damp_scale, int distortion_octaves, double distortion_scale, double distortion_magnitude, double distortion_gain) {
        this.generator = new OpenSimplexNoise(rand.nextLong());
        this.scale = scale;

        this.octaves = octaves;
        this.lacunarity = lacunarity;
        this.gain1 = gain1;
        this.gain = gain;
        this.warp0 = warp0;
        this.warp = warp;
        this.damp0 = damp0;
        this.damp = damp;
        this.damp_scale = damp_scale;
        this.distortion_octaves = distortion_octaves;
        this.distortion_scale = distortion_scale;
        this.distortion_magnitude = distortion_magnitude;
        this.distortion_gain = distortion_gain;

        this.distortion = new OctaveNoise(rand, this.scale / distortion_scale, distortion_octaves, lacunarity, distortion_gain);
    }

    @Override
    public double getValue(int x, int z) {
        this.distortion.offset = 0.0;
        double dx = x + (this.distortion.getValue(x,z)*0.5 + 0.5) * this.scale * this.distortion_magnitude;
        this.distortion.offset = 688889.0;
        double dz = z + (this.distortion.getValue(x,z)*0.5 + 0.5) * this.scale * this.distortion_magnitude;
        return this.generateRaw(dx,dz);
    }

    protected double generateRaw(double x, double z) {
        double xs = x / scale;
        double zs = z / scale;

        OpenSimplexNoise.PointData3D n = this.generator.evalDeriv(xs, zs, 0.5);

        double dfactor = 2.0;

        double n2 = n.value * n.value;
        double n2x = n.ddx * n.value;
        double n2z = n.ddy * n.value;

        double sum = n2;
        double dsum_warp_x = warp0 * n2x * dfactor;
        double dsum_warp_z = warp0 * n2z * dfactor;
        double dsum_damp_x = damp0 * n2x;
        double dsum_damp_z = damp0 * n2z;

        double amp = gain1;
        double freq = lacunarity;
        double damped_amp = amp * gain;

        for (int i=1; i<octaves; i++) {
            n = this.generator.evalDeriv(((xs * freq) + dsum_warp_x), ((zs * freq) + dsum_warp_z), i*10 + 0.5);
            n2 = (n.value * n.value * 1.5) + 0.1;
            n2x = n.ddx * n.value;
            n2z = n.ddy * n.value;

            sum += n2 * damped_amp;
            dsum_warp_x += warp * n2x * dfactor;
            dsum_warp_z += warp * n2z * dfactor;
            dsum_damp_x += damp * n2x;
            dsum_damp_z += damp * n2z;

            freq *= lacunarity;
            amp *= gain;
            damped_amp = amp * (1-damp_scale/(1+(dsum_damp_x*dsum_damp_x + dsum_damp_z*dsum_damp_z)));
        }

        return sum;
    }
}
