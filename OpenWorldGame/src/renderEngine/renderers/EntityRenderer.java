package renderEngine.renderers;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.entityFrameworks.Entity;
import models.Model;
import models.RawModel;
import renderEngine.MasterRenderer;
import shaders.statics.StaticShader;
import toolBox.Maths;

public class EntityRenderer {
	
	private StaticShader shader;
	
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Map<Model,List<Entity>> entities) {
		for (Model model : entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch) {
				prepareEntity(entity);
				//						Draw Type				Amount of Vertices
				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getRawModel().getVertexCount());  // Draws low poly entity - doesn't use indices
			}
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(Model model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());  // Binds VAO of model being rendered
		// Must enable all used arrays
		GL20.glEnableVertexAttribArray(0);  // Enables array at address 0 of VAO  // Address 0 holds vertex positions
		GL20.glEnableVertexAttribArray(1);  // Enables array at address 1 of VAO  // Address 1 holds materials
		GL20.glEnableVertexAttribArray(2);  // Enables array at address 2 of VAO  // Address 2 holds normals
		
		
		// Sets up any shaders requiring object-dependent variables
		shader.loadFakeLighting(model.isUseFakeLighting());
		shader.loadShineVariables(model.getShineDamper(), model.getReflectivity());
		
		//GL13.glActiveTexture(GL13.GL_TEXTURE0);  // Enables OpenGL Texture holder thing
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTexture().getTextureID());  // Binds Texture to Active Texture Slot
	}
	
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);  // Disables array at address 0 of VAO
		GL20.glDisableVertexAttribArray(1);  // Disables array at address 1 of VAO
		GL20.glDisableVertexAttribArray(2);  // Disables array at address 2 of VAO
		GL30.glBindVertexArray(0);  // Unbinds VAO
		MasterRenderer.enableCulling();
	}
	
	private void prepareEntity(Entity entity) {
		// Generates and sends transformation matrix to shaders
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
				entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
}
