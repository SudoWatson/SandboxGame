package shaders.debug;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.entityFrameworks.Camera;
import shaders.ShaderProgram;
import toolBox.Maths;


public class DebugShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/shaders/debug/debugVertexShader.glsl";
	private static final String FRAGMENT_FILE = "/shaders/debug/debugFragmentShader.glsl";


	private int location_color;
	private int location_density;
	private int location_gradient;
	private int location_skyColor;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_transformationMatrix;

	public DebugShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_color = super.getUniformLocation("color");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");
		location_skyColor = super.getUniformLocation("skyColor");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}


	
	public void loadColor(Vector3f color) {
		super.loadVector(location_color, color);
	}
	
	public void loadFog(Vector3f skyColor, float fogDensity, float fogGradient) {
		super.loadVector(location_skyColor, skyColor);
		super.loadFloat(location_density, fogDensity);
		super.loadFloat(location_gradient, fogGradient);
	}
	
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	
}
