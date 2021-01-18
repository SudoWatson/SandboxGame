package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import debug.Debug;
import engineTester.Main;
import entities.entityFrameworks.AnimatedEntity;
import entities.entityFrameworks.Camera;
import entities.entityFrameworks.Entity;
import entities.entityFrameworks.Hitbox;
import entities.entityFrameworks.Light;
import models.AnimatedModel;
import models.GUIObject;
import models.Model;
import renderEngine.renderers.AnimatedRenderer;
import renderEngine.renderers.DebugRenderer;
import renderEngine.renderers.EntityRenderer;
import renderEngine.renderers.GUIRenderer;
import renderEngine.renderers.TerrainRenderer;
import renderEngine.renderers.TextRenderer;
import shaders.animation.AnimationShader;
import shaders.debug.DebugShader;
import shaders.statics.StaticShader;
import shaders.terrain.TerrainShader;
import terrain.Terrain;

public class MasterRenderer {

	private static final Vector3f SKY_COLOR = new Vector3f(0.5f, 0.7f, 1);  // Default Background Color
	//private static final Vector3f SKY_COLOR = new Vector3f(0,0,0);  // Default Background Color

	private static final float FOG_DENSITY = 0.01f;
	private static final float FOG_GRADIENT = 2;
	
	private static final float FOV = 100;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	private Matrix4f projectionMatrix;

	private StaticShader shader = new StaticShader();
	private AnimationShader animationShader = new AnimationShader();
	private TerrainShader terrainShader = new TerrainShader();
	private DebugShader debugShader = new DebugShader();

	private EntityRenderer entityRenderer;
	private AnimatedRenderer animationRenderer;
	private TerrainRenderer terrainRenderer;
	private DebugRenderer hitboxRenderer;
	private GUIRenderer guiRenderer;
	private TextRenderer textRenderer;

	private Map<Model, List<Entity>> entities = new HashMap<Model, List<Entity>>();
	private Map<AnimatedModel, List<AnimatedEntity>> animatedEntities = new HashMap<AnimatedModel, List<AnimatedEntity>>();  // TEMPORARY
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<List<Hitbox>> hitboxes = new ArrayList<List<Hitbox>>();
	
	public MasterRenderer() {
		enableCulling();
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(shader, projectionMatrix);
		animationRenderer = new AnimatedRenderer(animationShader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		hitboxRenderer = new DebugRenderer(debugShader, projectionMatrix);
		guiRenderer = new GUIRenderer();
		textRenderer = new TextRenderer();
	}
	
	public Matrix4f getProjectionMatrix() {return this.projectionMatrix;}
	
	public void render(List<Light> lights, Camera camera) {
		prepare();
		shader.start();
		shader.loadFog(SKY_COLOR, FOG_DENSITY, FOG_GRADIENT);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);;
		entityRenderer.render(entities);
		shader.stop();

		animationShader.start();
		animationShader.loadFog(SKY_COLOR, FOG_DENSITY, FOG_GRADIENT);
		animationShader.loadLights(lights);
		animationShader.loadViewMatrix(camera);;
		animationRenderer.render(animatedEntities);
		animationShader.stop();
		
		terrainShader.start();
		terrainShader.loadFog(SKY_COLOR, FOG_DENSITY, FOG_GRADIENT);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		
		if (Debug.showHitboxes || Debug.showCoordLines || Debug.showDebugLines) {
			debugShader.start();
			debugShader.loadFog(SKY_COLOR, FOG_DENSITY, FOG_GRADIENT);
			debugShader.loadViewMatrix(camera);
			hitboxRenderer.render();
			debugShader.stop();
		}
		
		guiRenderer.render(GUIObject.GUIObjects);
		textRenderer.render(Main.textBoxes);

		entities.clear();
		animatedEntities.clear();
		terrains.clear();
		hitboxes.clear();
	}

	private void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);  // Tests triangles and renders them in order
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);  // Clears
		GL11.glClearColor(SKY_COLOR.x, SKY_COLOR.y, SKY_COLOR.z, 1);  // Sets background color
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void addEntity(Entity entity) {  // Adds entities to a hashmap  // Entities go to a list of their own type
		Model entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void addAnimatedEntity(AnimatedEntity entity) {  // Adds entities to a hashmap  // Entities go to a list of their own type
		AnimatedModel entityModel = entity.getModel();
		List<AnimatedEntity> batch = animatedEntities.get(entityModel);  // TEMPORARY
		if (batch != null) {
			batch.add(entity);
		} else {
			List<AnimatedEntity> newBatch = new ArrayList<AnimatedEntity>();
			newBatch.add(entity);
			animatedEntities.put(entityModel, newBatch);
		}
	}
	
	public void addTerrain(Terrain terrain) {  // Adds terrain to list of terrains
		terrains.add(terrain);
	}
	
	public void addHitbox(List<Hitbox> hitbox) {
		hitboxes.add(hitbox);
	}
	
	public void cleanUp() {
		shader.cleanup();
		animationShader.cleanup();
		terrainShader.cleanup();
		debugShader.cleanup();
		
		guiRenderer.cleanUp();
		textRenderer.cleanUp();
	}

	private void createProjectionMatrix(){
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}
}
