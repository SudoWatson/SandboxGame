package shaders.text;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;


public class TextShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/shaders/text/textVertexShader.glsl";
	private static final String FRAGMENT_FILE = "/shaders/text/textFragmentShader.glsl";


	private int location_color;
	private int location_transformationMatrix;

	public TextShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_color = super.getUniformLocation("color");
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	
	public void loadColor(Vector3f color) {
		super.loadVector(location_color, color);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	
}
