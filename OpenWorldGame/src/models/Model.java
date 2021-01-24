package models;


public class Model {
	
	protected RawModel rawModel;
	
	protected float shineDamper = 1;
	protected float reflectivity = 0;
	
	private boolean useFakeLighting = false;
	
	public Model(RawModel rawModel) {
		this.rawModel = rawModel;
	}

	public RawModel getRawModel() {
		return rawModel;
	}
	
	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	
}
