package renderEngine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;

import models.RawModel;

public class Loader {

	private List<Integer> vaos = new ArrayList<Integer>();  // List of all VAOs
	private List<Integer> vbos = new ArrayList<Integer>();  // List of all VBOs
	private List<Texture> textures = new ArrayList<Texture>();  // List of all Textures

	public RawModel loadToVAO(float[] positions, float[] normals, float[] materialColors) {
		int vaoID = createVAO();  // Creates Vertex Array Object (VAO) for model
		
		// Anything being used by a shader must be put into an array of the models VAO
		storeDataInAttributeList(0, 3, positions);  // Puts vertex positions into address 0 of VAO
		storeDataInAttributeList(1, 4, materialColors);  // Puts material colors into address 1 of VAO
		storeDataInAttributeList(2, 3, normals);  // Puts normals into address 2 of VAO
		unbindVAO();
		return new RawModel(vaoID, positions.length);
	}

	public RawModel loadToVAO(float[] positions, float[] normals, float[] materialColors, int[] indices) {
		int vaoID = createVAO();  // Creates Vertex Array Object (VAO) for model
		
		// Anything being used by a shader must be put into an array of the models VAO
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);  // Puts vertex positions into address 0 of VAO
		storeDataInAttributeList(1, 3, materialColors);  // Puts material colors into address 1 of VAO
		storeDataInAttributeList(2, 3, normals);  // Puts normals into address 2 of VAO
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	public RawModel loadHitbox(float[] positions, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays();  // Creates a new VAO
		vaos.add(vaoID);  // Adds to list of VAOs for easy removal
		GL30.glBindVertexArray(vaoID);  // Binds VAO so methods refer to this VAO
		return vaoID;  // Returns VAIO ID so we can access it
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);  // Creates a float buffer
		buffer.put(data);  // Writes data into buffer
		buffer.flip();  // Flips buffer mode from writing to reading
		return buffer;
	}

	private IntBuffer storeDataInIntBuffer(int[] data) {  // Same as above ^^ but integers not floats
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private void bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers();  // Creates new VBO
		vbos.add(vboID);  // Adds to list of VBOs
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);  // Binds VBO
		IntBuffer buffer = storeDataInIntBuffer(indices);  // Creates buffer of vertex index orders
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);  // Fills VBO with buffer 
		// Don't unbind this buffer
	}
	
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers();  // Creates a Vertex Buffer Array (VBO)
		vbos.add(vboID);  // Adds to list of VBOs for easy removal
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);  // Binds buffer so methods refer to this VBO
		FloatBuffer buffer = storeDataInFloatBuffer(data);  // Creates buffer
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);  // Puts buffer as VBO  // GL_STATIC_DRAW-buffer data won't change
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		/*							  Array			  Dimensions		Data	  Normal- Extra	 Offset of
		 *							index of VAO	   Of Data:			Type       ized   Data	 Starting
		 * 											  							   		 Between	Pos
		 * 											  3D (X,Y,Z)		   				 Vertexes
		*/
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);  // Unbind VBO  // 0 means unbind current VBO/VAO
	}
	
	private void unbindVAO() {
		GL30.glBindVertexArray(0);  // Unbinds VAO
	}
	
	
	public void cleanUp() {  // Goes through list of all made VAOs, VBOs, and textures and deletes them
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (Texture texture : textures) {
			GL11.glDeleteTextures(texture.getTextureID());
		}
	}
	
}
