package renderEngine.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import shaders.terrain.TerrainShader;
import terrain.Terrain;
import toolBox.Maths;

public class TerrainRenderer {
	
	private TerrainShader shader;
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(List<Terrain> terrains) {
		for (Terrain terrain: terrains) {
			prepareTerrain(terrain);
			loadTransformationMatrix(terrain);
			//						Draw Type				Amount of Vertices						Starts at position 0
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);  // Draws Terrain
			unbindTerrain();
		}
	}
	
	private void prepareTerrain(Terrain terrain) {
		GL32.glProvokingVertex(GL32.GL_FIRST_VERTEX_CONVENTION);
		RawModel rawModel = terrain.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());  // Binds VAO of model being rendered
		// Must enable all used arrays
		GL20.glEnableVertexAttribArray(0);  // Enables array at address 0 of VAO  // Address 0 holds vertex positions
		GL20.glEnableVertexAttribArray(1);  // Enables array at address 1 of VAO  // Address 1 holds material colors
		GL20.glEnableVertexAttribArray(2);  // Enables array at address 2 of VAO  // Address 2 holds normals
		// Gets and sends specular lighting to shaders
		shader.loadShineVariables(1,0);
	}
	
	private void unbindTerrain() {
		GL20.glDisableVertexAttribArray(0);  // Disables array at address 0 of VAO
		GL20.glDisableVertexAttribArray(1);  // Disables array at address 1 of VAO
		GL20.glDisableVertexAttribArray(2);  // Disables array at address 2 of VAO
		GL30.glBindVertexArray(0);  // Unbinds VAO
	}
	
	private void loadTransformationMatrix(Terrain terrain) {
		// Generates and sends transformation matrix to shaders
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0.1f, terrain.getZ()), 0,0,0,1);
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
}
