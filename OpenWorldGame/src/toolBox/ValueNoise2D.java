package toolBox;

import java.util.Random;

public class ValueNoise2D {

	private int OCTAVES = 3;
	private float RANGE = 20f;  // Ranges from -range/2 - range/2 (kinda almost)
	private float ROUGHNESS = 0.1f;
	
	private Random random = new Random();
	private int seed;

	public ValueNoise2D() {
		this.seed = random.nextInt(999999999);
	}

	public ValueNoise2D(float val) {
		this.seed = random.nextInt(999999999);
		this.RANGE = val;
	}
	
	public ValueNoise2D(int seed) {
		this.seed = random.nextInt(seed);
	}
	
	public void setOctaves(int numOfOctaves) {
		this.OCTAVES = numOfOctaves;
	}
	
	public void setRange(float maxValue) {
		this.RANGE = maxValue;
	}
	
	public void setRoughness(float roughness) {
		this.ROUGHNESS = roughness;
	}
	
	public float getValue(int x, int z) {
		float total = 0;
		float d = (float) Math.pow(2, OCTAVES-1);
		for (int i = 0; i < OCTAVES; i++) {
			float freq = (float) (Math.pow(2, i)/d);
			float amp = (float) Math.pow(ROUGHNESS, i) * RANGE;
			total += getInterpolatedNoise(x*freq, z*freq) * amp;  // x z will be at most x,z
		}
		return total;
	}
	
	private float interpolate(float a, float b, float blend) {
		double theta = blend * Math.PI;
		float f = (float) (1f-Math.cos(theta)) * 0.5f;
		return a * (1f-f) + b*f;
	}
	
	private float getNoise(int x, int z) {
		random.setSeed(x*49622+z*325176+this.seed);
		return random.nextFloat() * 2f - 1f;
	}
	
	private float getSmoothNoise(int x, int z) {
		float corners = (getNoise(x-1,z-1) + getNoise(x-1,z+1) + getNoise(x+1,z-1) + getNoise(x+1,z+1))/16f;
		float edges = (getNoise(x-1,z) + getNoise(x,z+1) + getNoise(x+1,z) + getNoise(x,z-1))/8f;
		float center = getNoise(x,z)/4f;
		return corners+edges+center;
	}
	
	private float getInterpolatedNoise(float x, float z) {
		int intX = (int) x;
		int intZ = (int) z;
		float fracX = x-intX;
		float fracZ = z-intZ;

		float v1 = getSmoothNoise(intX, intZ);
		float v2 = getSmoothNoise(intX+1, intZ);
		float v3 = getSmoothNoise(intX, intZ+1);
		float v4 = getSmoothNoise(intX+1, intZ+1);

		float i1 = interpolate(v1,v2,fracX);
		float i2 = interpolate(v3,v4,fracX);

		return interpolate(i1,i2,fracZ);
	}
	
}
