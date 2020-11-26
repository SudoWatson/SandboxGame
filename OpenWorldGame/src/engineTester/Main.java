package engineTester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import debug.Debug;
import entities.Player;
import entities.entityFrameworks.Camera;
import entities.entityFrameworks.Entity;
import entities.entityFrameworks.Hitbox;
import entities.entityFrameworks.Light;
import models.GUIObject;
import models.Model;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrain.Terrain;
import terrain.TerrainGenerator;
import text.TextBox;

public class Main {
	
	public static List<List<Entity>> entities;
	public static List<Terrain> terrainCells;
	public static List<List<Hitbox>> hitboxes;
	public static List<TextBox> textBoxes;
	public static List<Light> lights;
	public static List<Entity> colidables;
	public static Player player;
	
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		// Initialize Objects etc
		DisplayManager.createDisplay();
		Random rnd = new Random();
		
		MasterRenderer renderer = new MasterRenderer();
		
		entities = new ArrayList<List<Entity>>();
		terrainCells = new ArrayList<Terrain>();
		textBoxes = new ArrayList<TextBox>();
		lights = new ArrayList<Light>();
		hitboxes = new ArrayList<List<Hitbox>>();
		colidables = new ArrayList<Entity>();


		Light sun = new Light(new Vector3f(20000,20000,2000), new Vector3f(1,1,1));
		lights.add(sun);
		
		int terrainSize = 250;
		Terrain terrain = TerrainGenerator.generateTerrain(-(terrainSize/2), -(terrainSize/2), terrainSize);
		terrainCells.add(terrain);
		
		// Game Setup ---------------------------------------------------------------------------------------
		player = new Player(new Vector3f(0,0,0),0,45,0);
		Camera camera = new Camera(player);
		
		Entity pineTree = new Entity("pineTree", new Vector3f(5,0,-5),0,0,0,1);
		pineTree.increasePosition(0, terrain.getHeightOfTerrain(pineTree.getPosition().x, pineTree.getPosition().z), 0);
		pineTree.addHitbox("main", new Vector3f(0,0,0), new Vector3f(2,8,2));
		pineTree.setCollision(true, "main");
		pineTree.usesGravity();
		pineTree.isDynamic();
		hitboxes.add(pineTree.getHitboxes());
		entities.add(new ArrayList<Entity>(Arrays.asList(pineTree)));

		List<Entity> trees = new ArrayList<Entity>();
		entities.add(trees);
		Model treeModel = new Model(OBJLoader.loadObjModel("loliPopTree"));
		for (int i = 0; i < 1000; i ++) {
			float x = rnd.nextFloat() * 1000 - 500;
			float z = rnd.nextFloat() * 1000 - 500;
			Entity tree = new Entity(treeModel, new Vector3f(x,terrain.getHeightOfTerrain(x, z), z),0,(int) (rnd.nextFloat()*360),0,1);
			tree.addHitbox("trunk", new Vector3f(0,0,0), new Vector3f(1,3,1));
			tree.addHitbox("leaves", new Vector3f(0,3,0), new Vector3f(2.5f,1,2.5f));
			hitboxes.add(tree.getHitboxes());
			tree.setCollision(true, "trunk");
			tree.setCollision(true, "leaves");
			trees.add(tree);
		}
		
		Entity pumpkin = new Entity("pumpkinLow", new Vector3f(0,0,10), new Vector3f(0,0,0));
		//pumpkin.increasePosition(0, terrain.getHeightOfTerrain(pumpkin.getPosition().x, pumpkin.getPosition().z), 0);
		entities.add(new ArrayList<Entity>(Arrays.asList(pumpkin)));
		pumpkin.usesGravity();
		pumpkin.addHitbox("main", new Vector3f(), new Vector3f(2,2,2));
		hitboxes.add(pumpkin.getHitboxes());
		pumpkin.setCollision(true, "main");
		pumpkin.isDynamic();
		
		
		GUIObject crossHair = new GUIObject(Loader.loadTexture("crosshair"), new Vector2f(), new Vector2f(0.02f,0.02f*16/9));
		
		
		
		
		
		// Game Loop
		while (!Display.isCloseRequested()) {
			// ---------------------- Logic/Update ----------------------
			player.update();
			

			Debug.update();
			// ---------------------- Render ----------------------
			for (List<Entity> entityList : entities) {
				for (Entity entity : entityList) {
					entity.update();
					renderer.addEntity(entity);
				}
			}
			
			for (Terrain terrn : terrainCells) {
				renderer.addTerrain(terrn);
			}
			
			if (camera.getCameraStyle() != 1) renderer.addEntity(player);
			
			
			
			renderer.render(lights, camera);
			DisplayManager.updateDisplay();
		}
		
		// ----------- Clean Ups -----------
		renderer.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
		
	}

}
