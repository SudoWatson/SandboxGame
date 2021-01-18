package renderEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;

public class Loader {

	private static List<Integer> vaos = new ArrayList<Integer>();  // List of all VAOs
	private static List<Integer> vbos = new ArrayList<Integer>();  // List of all VBOs
	private static List<Integer> textures = new ArrayList<Integer>();  // List of all Textures
	
	// Model Loader
	public static RawModel loadToVAO(float[] positions, float[] normals, float[] materialColors) {
		int vaoID = createVAO();  // Creates Vertex Array Object (VAO) for model
		List<Integer> vboList = new ArrayList<Integer>();
		
		// Anything being used by a shader must be put into an array of the models VAO
//		storeDataInAttributeList(VAO Address, Data Dimensions, Variable Parameter, List of VBOs so the object can easily remove itself);
		storeDataInAttributeList(0, 3, positions, vboList);  // Puts vertex positions into address 0 of VAO
		storeDataInAttributeList(1, 4, materialColors, vboList);  // Puts material colors into address 1 of VAO
		storeDataInAttributeList(2, 3, normals, vboList);  // Puts normals into address 2 of VAO
		unbindVAO();
		return new RawModel(vaoID, positions.length, vboList);
	}
	
	// Animation Loader
	public static RawModel loadToVAOAnimated(float[] positions, float[] normals, float[] materialColors, float[] weights, int[] jointIDs) {
		int vaoID = createVAO();  // Creates Vertex Array Object (VAO) for model
		List<Integer> vboList = new ArrayList<Integer>();
		
		// Anything being used by a shader must be put into an array of the models VAO
//		storeDataInAttributeList(VAO Address, Data Dimensions, Variable Parameter, List of VBOs so the object can easily remove itself);
		storeDataInAttributeList(0, 3, positions, vboList);  // Puts vertex positions into address 0 of VAO
		storeDataInAttributeList(1, 4, materialColors, vboList);  // Puts material colors into address 1 of VAO
		storeDataInAttributeList(2, 3, normals, vboList);  // Puts normals into address 2 of VAO
		storeDataInAttributeList(3, 3, weights, vboList);  // Puts weights into address 2 of VAO	// Weight values used for specific vertex
		storeIntDataInAttributeList(4, 3, jointIDs, vboList);  // Puts jointIDs into address 4 of VAO  // Bone index that that weight corresponds to
		System.out.println(Arrays.toString(jointIDs));
		unbindVAO();
		return new RawModel(vaoID, positions.length, vboList);
	}
	
	// Terrain Loader
	public static RawModel loadToVAO(float[] positions, float[] normals, float[] materialColors, int[] indices) {
		int vaoID = createVAO();  // Creates VAO for terrain
		List<Integer> vboList = new ArrayList<Integer>();
		
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions, vboList);  // Puts vertex positions into address 0 of VAO
		storeDataInAttributeList(1, 3, materialColors, vboList);  // Puts material colors into address 1 of VAO
		storeDataInAttributeList(2, 3, normals, vboList);  // Puts normals into address 2 of VAO
		unbindVAO();
		return new RawModel(vaoID, indices.length, vboList);
	}
	
	// GUI Loader
	public static RawModel loadToVAO(float[] positions) {
		int vaoID = createVAO();  // Creates VAO for GUI Object
		List<Integer> vboList = new ArrayList<Integer>();
		
		storeDataInAttributeList(0, 2, positions, vboList);  // Puts vertex positions into address 0 of VAO
		unbindVAO();
		return new RawModel(vaoID, positions.length/2, vboList);
	}
	
	// Text Loader
	public static RawModel loadToVAO(float[] positions, float[] textures) {
		int vaoID = createVAO();  // Creates VAO for GUI Object
		List<Integer> vboList = new ArrayList<Integer>();

		storeDataInAttributeList(0, 2, positions, vboList);  // Puts vertex positions into address 0 of VAO
		storeDataInAttributeList(1, 2, textures, vboList);  // Puts texture coordinates into address 1 of VAO
		unbindVAO();
		return new RawModel(vaoID, positions.length/2, vboList);
	}
	
	public static RawModel loadHitbox(float[] positions, int[] indices) {
		int vaoID = createVAO();
		List<Integer> vboList = new ArrayList<Integer>();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions, vboList);
		unbindVAO();
		return new RawModel(vaoID, indices.length, vboList);
	}
	
	public static int loadTexture(String fileName) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG",  new FileInputStream("res/textures/" + fileName + ".png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
	public static int loadFontMap(String fileName) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG",  new FileInputStream("res/fonts/" + fileName + ".png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
	private static int createVAO() {
		int vaoID = GL30.glGenVertexArrays();  // Creates a new VAO
		vaos.add(vaoID);  // Adds to list of VAOs for easy removal
		GL30.glBindVertexArray(vaoID);  // Binds VAO so methods refer to this VAO
		return vaoID;  // Returns VAIO ID so we can access it
	}

	private static FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);  // Creates a float buffer
		buffer.put(data);  // Writes data into buffer
		buffer.flip();  // Flips buffer mode from writing to reading
		return buffer;
	}

	private static IntBuffer storeDataInIntBuffer(int[] data) {  // Same as above ^^ but integers not floats
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private static void bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers();  // Creates new VBO
		vbos.add(vboID);  // Adds to list of VBOs
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);  // Binds VBO
		IntBuffer buffer = storeDataInIntBuffer(indices);  // Creates buffer of vertex index orders
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);  // Fills VBO with buffer 
		// Don't unbind this buffer
	}
	
	private static void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data, List<Integer> vboDestination) {
		int vboID = GL15.glGenBuffers();  // Creates a Vertex Buffer Array (VBO)
		vbos.add(vboID);  // Adds to list of VBOs for easy removal
		if (vboDestination != null) {
			vboDestination.add(vboID);
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);  // Binds buffer so methods refer to this VBO
		FloatBuffer buffer = storeDataInFloatBuffer(data);  // Creates buffer
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);  // Puts buffer as VBO  // GL_STATIC_DRAW-buffer data won't change
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		/*							  Array			  Dimensions		Data	  Normal- Extra	 Offset of
		 *							index of VAO	   Of Data:			Type       ized   Data	 Starting
		 * 											  							   		 Between	Pos
		 *																  				 Vertexes
		*/
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);  // Unbind VBO  // 0 means unbind current VBO/VAO
	}
	
	private static void storeIntDataInAttributeList(int attributeNumber, int coordinateSize, int[] data, List<Integer> vboDestination) {
		int vboID = GL15.glGenBuffers();  // Creates a Vertex Buffer Array (VBO)
		vbos.add(vboID);  // Adds to list of VBOs for easy removal
		if (vboDestination != null) {
			vboDestination.add(vboID);
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);  // Binds buffer so methods refer to this VBO
		IntBuffer buffer = storeDataInIntBuffer(data);  // Creates buffer
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);  // Puts buffer as VBO  // GL_STATIC_DRAW-buffer data won't change
		
		GL30.glVertexAttribIPointer(attributeNumber, coordinateSize, GL11.GL_INT, 0, 0);
		/*							  Array			  Dimensions		Data  Stride Offset of
		 *							index of VAO	   Of Data:			Type         Starting
		 * 											  							   	    Pos
		*/
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);  // Unbind VBO  // 0 means unbind current VBO/VAO
	}
	
	private static void unbindVAO() {
		GL30.glBindVertexArray(0);  // Unbinds VAO
	}
	
	
	public static void cleanUp() {  // Goes through list of all made VAOs, VBOs, and textures and deletes them
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (Integer texture : textures) {
			GL11.glDeleteTextures(texture);
		}
	}
	
	public static void deleteVBO(int vboID) {
		vbos.remove(vbos.indexOf(vboID));
		GL15.glDeleteBuffers(vboID);
	}
	
	public static void deleteVAO(int vaoID) {
		vaos.remove(vaos.indexOf(vaoID));
		GL30.glDeleteVertexArrays(vaoID);
	}
	
}
