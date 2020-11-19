package toolBox;

import java.util.Random;

public class ValueNoise1D {

	private int OCTAVES = 3;
	private float RANGE = 20f;  // Ranges from -range/2 - range/2 (kinda almost)
	private float ROUGHNESS = 0.1f;
	
	private Random random = new Random();
	private int seed;

	public ValueNoise1D() {
		this.seed = random.nextInt(999999999);
	}

	public ValueNoise1D(float val) {
		this.seed = random.nextInt(999999999);
		this.RANGE = val;
	}
	
	public ValueNoise1D(int seed) {
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
	
	public float getValue(int x) {
		float total = 0;
		float d = (float) Math.pow(2, OCTAVES-1);
		for (int i = 0; i < OCTAVES; i++) {
			float freq = (float) (Math.pow(2, i)/d);
			float amp = (float) Math.pow(ROUGHNESS, i) * RANGE;
			total += getInterpolatedNoise(x*freq) * amp;
		}
		return total;
	}
	
	private float interpolate(float a, float b, float blend) {
		double theta = blend * Math.PI;
		float f = (float) (1f-Math.cos(theta)) * 0.5f;
		return a * (1f-f) + b*f;
	}
	
	private float getNoise(int x) {
		random.setSeed(x*49622+this.seed);
		return random.nextFloat() * 2f - 1f;
	}
	
	private float getSmoothNoise(int x) {
		float corners = (getNoise(x-1) + getNoise(x-1) + getNoise(x+1) + getNoise(x+1))/16f;
		float edges = (getNoise(x-1) + getNoise(x) + getNoise(x+1) + getNoise(x))/8f;
		float center = getNoise(x)/4f;
		return corners+edges+center;
	}
	
	private float getInterpolatedNoise(float x) {
		int intX = (int) x;
		float fracX = x-intX;

		float v1 = getSmoothNoise(intX);
		float v2 = getSmoothNoise(intX+1);

		return interpolate(v1,v2,fracX);
	}
	
}
