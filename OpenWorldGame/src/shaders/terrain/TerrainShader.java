package shaders.terrain;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.entityFrameworks.Camera;
import entities.entityFrameworks.Light;
import shaders.ShaderProgram;
import toolBox.Maths;

public class TerrainShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/shaders/terrain/terrainVertexShader.glsl";
	private static final String FRAGMENT_FILE = "/shaders/terrain/terrainFragmentShader.glsl";
	
	private static final int MAX_LIGHTS = 4;
	
	// Initialize all variables to be passed in to shaders
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColor;
	private int location_density;
	private int location_gradient;
	private int location_lightColors[];
	private int location_lightPositions[];
	private int location_lightAttenuations[];
	
	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		// Bind all 'in' variables of vertexShader to an array in VAO
		// Binds attributes at array [int] of object VAO to variable "[variableName]" in vertexShader
		super.bindAttribute(0, "position");  // Array 0 of VAO to "position" variable  // Array holds vertex positions
		super.bindAttribute(1, "color");  // Array 1 of VAO to "textureCoords" variable  // Array holds texture coords
		super.bindAttribute(2, "normal");  // Array 2 of VAO to "textureCoords" variable  // Array holds normals
	}

	@Override
	protected void getAllUniformLocations() {
		// Store location of all variables in shader programs
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_skyColor = super.getUniformLocation("skyColor");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");

		location_lightColors = new int[MAX_LIGHTS];
		location_lightPositions = new int[MAX_LIGHTS];
		location_lightAttenuations = new int[MAX_LIGHTS];
		for (int i=0; i<MAX_LIGHTS; i++) {
			location_lightColors[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_lightPositions[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightAttenuations[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
		
	}
	
	// Loads matrixes/vectors into variables of shader programs
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	public void loadLights(List<Light> lights) {
		for (int i=0; i<MAX_LIGHTS; i++) {
			if (i<lights.size()) {
				super.loadVector(location_lightColors[i], lights.get(i).getColor());
				super.loadVector(location_lightPositions[i], lights.get(i).getPosition());
				super.loadVector(location_lightAttenuations[i], lights.get(i).getAttenuation());
			} else {
				super.loadVector(location_lightColors[i], new Vector3f(0,0,0));
				super.loadVector(location_lightPositions[i], new Vector3f(0,0,0));
				super.loadVector(location_lightAttenuations[i], new Vector3f(1,0,0));
			}
		}
	}
	
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadFog(Vector3f skyColor, float fogDensity, float fogGradient) {
		super.loadVector(location_skyColor, skyColor);
		super.loadFloat(location_density, fogDensity);
		super.loadFloat(location_gradient, fogGradient);
	}
	
}
