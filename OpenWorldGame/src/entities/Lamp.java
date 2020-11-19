//package entities;
//
//import org.lwjgl.util.vector.Vector3f;
//
//import engineTester.Main;
//import entities.entityFrameworks.Entity;
//import entities.entityFrameworks.Light;
//import renderEngine.Loader;
//
//public class Lamp extends Entity {
//	private static final String MODEL_FILE = "lamp";
//	private static final String TEXTURE_FILE = "lamp";
//	
//	Light light;
//
//	
//	public Lamp(Vector3f position) {
//		super(MODEL_FILE, TEXTURE_FILE, position, 0, 0, 0, 1);
//		this.light = new Light(new Vector3f(0,0,0), new Vector3f(1,1,1), new Vector3f(1,0.01f,0.002f));
//		this.setPosition(position);
//		Main.lights.add(this.light);
//		this.addHitbox("Main", new Vector3f(0,0,0), new Vector3f(0.5f,7,0.5f));
//		this.addHitbox("base", new Vector3f(0,0,0), new Vector3f(2,0.5f,2));
//		Main.hitboxes.add(this.getHitboxes());
//	}
//	
//	public Lamp(Loader loader, Vector3f position, Vector3f lightColor) {
//		super(MODEL_FILE, TEXTURE_FILE, position, 0, 0, 0, 1);
//		this.light = new Light(new Vector3f(0,0,0), lightColor, new Vector3f(1,0.01f,0.002f));
//		this.setPosition(position);
//		Main.lights.add(this.light);
//	}
//	
//	public Lamp(Loader loader, Vector3f position, Vector3f lightColor, Vector3f attenuation) {
//		super(MODEL_FILE, TEXTURE_FILE, position, 0, 0, 0, 1);
//		this.light = new Light(position, lightColor, attenuation);
//		this.setPosition(position);
//		Main.lights.add(this.light);
//	}
//	
//	public void setPosition(Vector3f position) {
//		super.setPosition(position);
//		this.light.setPosition(new Vector3f(position.x,position.y+8,position.z));
//		this.getModel().getTexture().setUseFakeLighting(true);
//	}
//	
//	public void increasePosition(float dx, float dy, float dz) {
//		super.increasePosition(dx, dy, dz);
//		this.light.increasePosition(dx,dy,dz);
//	}
//	
//}
