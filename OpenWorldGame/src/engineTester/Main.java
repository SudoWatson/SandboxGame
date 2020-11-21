package engineTester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import debug.DebugLine;
import entities.Player;
import entities.entityFrameworks.Camera;
import entities.entityFrameworks.Entity;
import entities.entityFrameworks.Hitbox;
import entities.entityFrameworks.Light;
import models.Model;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrain.Terrain;
import terrain.TerrainGenerator;

public class Main {
	
	public static boolean showHitboxes =  false;
	public static boolean showCoordLines = false;
	public static boolean showDebugLines = false;
	
	public static Loader loader;
	
	public static List<List<Entity>> entities;
	public static List<Terrain> terrainCells;
	public static List<List<Hitbox>> hitboxes;
	public static List<Light> lights;
	public static List<Entity> colidables;
	public static Player player;
	
	
	public static void main(String[] args) {
		
		// Initialize Objects etc
		DisplayManager.createDisplay();
		Random rnd = new Random();
		
		loader = new Loader();
		MasterRenderer renderer = new MasterRenderer();
		
		entities = new ArrayList<List<Entity>>();
		terrainCells = new ArrayList<Terrain>();
		lights = new ArrayList<Light>();
		hitboxes = new ArrayList<List<Hitbox>>();
		colidables = new ArrayList<Entity>();


		Light sun = new Light(new Vector3f(20000,20000,2000), new Vector3f(1,1,1));
		lights.add(sun);
		
		int terrainSize = 250;
		Terrain terrain = TerrainGenerator.generateTerrain(-(terrainSize/2), -(terrainSize/2), terrainSize);
		terrainCells.add(terrain);
		
		
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
		
		
		
		DebugLine testLine = new DebugLine(new Vector3f(0,0,0), new Vector3f(10,10,10));
		
		
		
		
		
		// Game Loop
		while (!Display.isCloseRequested()) {
			// Logic/Update
			if (Keyboard.isKeyDown(Keyboard.KEY_F2)) {
				if (Keyboard.isKeyDown(Keyboard.KEY_A) ) {
					System.out.println("F2 + ");
					System.out.println("H:   Show Hitboxes");
					System.out.println("C:   Show Coordinate Lines");
					System.out.println("L:   Show Debug Lines");
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_H)) showHitboxes = true;
				if (Keyboard.isKeyDown(Keyboard.KEY_C)) showCoordLines = true;
				if (Keyboard.isKeyDown(Keyboard.KEY_L)) showDebugLines = true;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_F3)) {
				if (Keyboard.isKeyDown(Keyboard.KEY_A) ) {
					System.out.println("F3 + ");
					System.out.println("H:   Hide Hitboxes");
					System.out.println("C:   Hide Coordinate Lines");
					System.out.println("H:   Hide Debug Lines");
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_H)) showHitboxes = false;
				if (Keyboard.isKeyDown(Keyboard.KEY_C)) showCoordLines = false;
				if (Keyboard.isKeyDown(Keyboard.KEY_L)) showDebugLines = false;
			}
			
			player.update();
//			pumpkin.update();
//			
//			
//			pineTree.update();
			
			
			// Render
			//renderer.addEntity(pumpkin);
			for (List<Entity> entityList : entities) {
				for (Entity entity : entityList) {
					renderer.addEntity(entity);
					entity.update();
				}
			}
			
			for (Terrain terrn : terrainCells) {
				renderer.addTerrain(terrn);
			}
			
			if (showHitboxes) {
				for (List<Hitbox> hitbox : hitboxes) {
					renderer.addHitbox(hitbox);
				}
			}
			
			if (camera.getCameraStyle() != 1) renderer.addEntity(player);
			
			
			
			
			renderer.render(lights, camera);
			DisplayManager.updateDisplay();
		}
		
		// Clean Ups
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		
	}

}
