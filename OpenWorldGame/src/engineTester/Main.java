package engineTester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import debug.Debug;
import entities.Player;
import entities.Pumpkin;
import entities.entityFrameworks.AnimatedEntity;
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
	public static List<List<AnimatedEntity>> animatedEntities;
	public static List<Terrain> terrainCells;
	public static List<List<Hitbox>> hitboxes;
	public static List<TextBox> textBoxes;
	public static List<Light> lights;
	public static List<Entity> colidables;
	public static Player player;
	public static MasterRenderer renderer;
	
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		/*
		 * NOW I NEED TO DO THE ANIMATION SHADERS
		 */
		
		// Initialize Objects etc
		DisplayManager.createDisplay();
		Random rnd = new Random();
		
		renderer = new MasterRenderer();

		entities = new ArrayList<List<Entity>>();
		animatedEntities = new ArrayList<List<AnimatedEntity>>();
		terrainCells = new ArrayList<Terrain>();  // List in World class
		textBoxes = new ArrayList<TextBox>();
		lights = new ArrayList<Light>();  // List will be in Light class
		hitboxes = new ArrayList<List<Hitbox>>();  // List will be in Hitbox class
		colidables = new ArrayList<Entity>();  // List will be in Entity class


		Light sun = new Light(new Vector3f(20000,20000,2000), new Vector3f(1,1,1));
		lights.add(sun);
		
		int terrainSize = 250;
		Terrain terrain = TerrainGenerator.generateFlatTerrain(-(terrainSize/2), -(terrainSize/2), terrainSize);
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
		for (int i = 0; i < 100; i ++) {
			float x = rnd.nextFloat() * terrainSize - terrainSize/2;
			float z = rnd.nextFloat() * terrainSize - terrainSize/2;
			Entity tree = new Entity(treeModel, new Vector3f(x,terrain.getHeightOfTerrain(x, z), z),0,(int) (rnd.nextFloat()*360),0,1);
			tree.addHitbox("main", new Vector3f(0,0,0), new Vector3f(1,3,1));
			tree.addHitbox("leaves", new Vector3f(0,3,0), new Vector3f(2.5f,1,2.5f));
			hitboxes.add(tree.getHitboxes());
			tree.setCollision(true, "main");
			tree.setCollision(true, "leaves");
			trees.add(tree);
		}

		List<Entity> grasses = new ArrayList<Entity>();
		entities.add(grasses);
		Model grassModel = new Model(OBJLoader.loadObjModel("grass"));
		for (int i = 0; i < 100; i ++) {
			float x = rnd.nextFloat() * terrainSize - terrainSize/2;
			float z = rnd.nextFloat() * terrainSize - terrainSize/2;
			Entity grass = new Entity(grassModel, new Vector3f(x,terrain.getHeightOfTerrain(x, z), z),0,(int) (rnd.nextFloat()*360),0,1.5f);
			grasses.add(grass);
		}
		
		
		
		GUIObject crossHair = new GUIObject(Loader.loadTexture("crosshair"), new Vector2f(), new Vector2f(0.02f,0.02f*16/9));
		
		Pumpkin pumpkin = new Pumpkin(new Vector3f(10,0,0));
		
		AnimatedEntity test1 = new AnimatedEntity("animatedPlayer", new Vector3f(0,terrain.getHeightOfTerrain(0, 0), 0));
		test1.getModel().animator.playAnimation("Walking");
		List<AnimatedEntity> animatedTests = new ArrayList<AnimatedEntity>();
		animatedTests.add(test1);
		animatedEntities.add(animatedTests);
		
		entities.add(Pumpkin.pumpkins);
		// Game Loop
		boolean run = false;
		while (!Display.isCloseRequested()) {
			// ---------------------- Logic/Update ----------------------
			player.update();


			if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
				test1.pauseAnimation();
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
				test1.resumeAnimation();
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_J)) {
				test1.stopAnimation();
			}
			//System.out.println(test.getSkeleton().getAnimatedTransform());
			//test.getSkeleton().getAnimatedTransform();
			Debug.update();
			// ---------------------- Render ----------------------
			for (List<Entity> entityList : entities) {
				for (Entity entity : entityList) {  // Updates and renders all entities so it loops only once
					entity.update();
					renderer.addEntity(entity);
					camera.getRay().castTo(entity);
				}
			}
			
			for (List<AnimatedEntity> entityList : animatedEntities) {
				for (AnimatedEntity entity : entityList) {  // Updates and renders all entities so it loops only once
					entity.update();
					//System.out.println(entity.getModel().getSkeleton().getAnimatedTransform());
					renderer.addAnimatedEntity(entity);
				}
			}
			
			for (Terrain terrn : terrainCells) {
				renderer.addTerrain(terrn);
			}
			
			hitboxes.remove(player.getHitboxes());
			if (camera.getCameraStyle() != 1) {
				renderer.addEntity(player);
				hitboxes.add(player.getHitboxes());
			}
			
			
			
			renderer.render(lights, camera);
			DisplayManager.updateDisplay();
		}
		
		// ----------- Clean Ups -----------
		renderer.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
		
	}

}
