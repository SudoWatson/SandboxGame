package shaders.animation;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.entityFrameworks.Camera;
import entities.entityFrameworks.Light;
import shaders.ShaderProgram;
import toolBox.Maths;


public class AnimationShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/shaders/animation/animationVertexShader.glsl";
	private static final String FRAGMENT_FILE = "/shaders/animation/animationFragmentShader.glsl";

	public static final int MAX_LIGHTS = 4;
	public static final int MAX_JOINTS = 50;
	
	// Initialize all variables to be passed in to shaders
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColor;
	private int location_density;
	private int location_gradient;
	private int location_lightColors[];
	private int location_lightPositions[];
	private int location_lightAttenuations[];
	private int location_jointTransformations[];
	
	public AnimationShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "materialColor");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "weights");
		super.bindAttribute(4, "jointIDs");
	}

	@Override
	protected void getAllUniformLocations() {
		// Store location of all variables in shader programs
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColor = super.getUniformLocation("skyColor");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");

		location_lightColors = new int[MAX_LIGHTS];
		location_lightPositions = new int[MAX_LIGHTS];
		location_lightAttenuations = new int[MAX_LIGHTS];

		location_jointTransformations = new int[MAX_JOINTS];
		
		for (int i=0;i<MAX_LIGHTS;i++) {
			location_lightColors[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_lightPositions[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightAttenuations[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
		
		for (int i=0;i<MAX_JOINTS;i++) {
			location_jointTransformations[i] = super.getUniformLocation("jointTransforms[" + i + "]");
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
	
	public void loadJointTransforms(Matrix4f[] jointTransforms) {
		//System.out.println(Arrays.toString(jointTransforms));
		for (int i = 0; i < MAX_JOINTS; i++) {
			if (i<jointTransforms.length) {
				super.loadMatrix(location_jointTransformations[i], jointTransforms[i]);
			} else {
				super.loadMatrix(location_jointTransformations[i], new Matrix4f());
			}	
		}
	}
	
	public void loadLights(List<Light> lights) {
		for (int i=0; i < MAX_LIGHTS; i++) {
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
	
	public void loadFakeLighting(Boolean useFake) {
		super.loadBoolean(location_useFakeLighting, useFake);
	}
	
	public void loadFog(Vector3f skyColor, float fogDensity, float fogGradient) {
		super.loadVector(location_skyColor, skyColor);
		super.loadFloat(location_density, fogDensity);
		super.loadFloat(location_gradient, fogGradient);
	}
}
