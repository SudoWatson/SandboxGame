package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.entityFrameworks.Camera;
import entities.entityFrameworks.Entity;
import entities.entityFrameworks.Hitbox;
import entities.entityFrameworks.Light;
import models.Model;
import renderEngine.renderers.DebugRenderer;
import renderEngine.renderers.EntityRenderer;
import renderEngine.renderers.TerrainRenderer;
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
	private TerrainShader terrainShader = new TerrainShader();
	private DebugShader hitboxShader = new DebugShader();

	private EntityRenderer entityRenderer;
	private TerrainRenderer terrainRenderer;
	private DebugRenderer hitboxRenderer;
	
	private Map<Model, List<Entity>> entities = new HashMap<Model, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<List<Hitbox>> hitboxes = new ArrayList<List<Hitbox>>();
	
	public MasterRenderer() {
		enableCulling();
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		hitboxRenderer = new DebugRenderer(hitboxShader, projectionMatrix);
	}
	
	public void render(List<Light> lights, Camera camera) {
		prepare();
		shader.start();
		shader.loadFog(SKY_COLOR, FOG_DENSITY, FOG_GRADIENT);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);;
		entityRenderer.render(entities);
		shader.stop();
		
		terrainShader.start();
		terrainShader.loadFog(SKY_COLOR, FOG_DENSITY, FOG_GRADIENT);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		
		hitboxShader.start();
		hitboxShader.loadFog(SKY_COLOR, FOG_DENSITY, FOG_GRADIENT);
		hitboxShader.loadViewMatrix(camera);
		hitboxRenderer.render(hitboxes);
		hitboxShader.stop();
		
		entities.clear();
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
	
	public void addTerrain(Terrain terrain) {  // Adds terrain to list of terrains
		terrains.add(terrain);
	}
	
	public void addHitbox(List<Hitbox> hitbox) {
		hitboxes.add(hitbox);
	}
	
	public void cleanUp() {
		shader.cleanup();
		terrainShader.cleanup();
		hitboxShader.cleanup();
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
