package shaders.gui;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;


public class GUIShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/shaders/gui/guiVertexShader.glsl";
	private static final String FRAGMENT_FILE = "/shaders/gui/guiFragmentShader.glsl";


	private int location_transformationMatrix;

	public GUIShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	
}
