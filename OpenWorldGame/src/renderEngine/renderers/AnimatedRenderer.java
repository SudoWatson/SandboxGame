package renderEngine.renderers;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.entityFrameworks.AnimatedEntity;
import models.AnimatedModel;
import models.RawModel;
import renderEngine.MasterRenderer;
import shaders.animation.AnimationShader;
import toolBox.Maths;

public class AnimatedRenderer {
	
	private AnimationShader shader;
	
	
	public AnimatedRenderer(AnimationShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Map<AnimatedModel,List<AnimatedEntity>> entities) {
		for (AnimatedModel model : entities.keySet()) {
			prepareAnimatedModel(model);
			List<AnimatedEntity> batch = entities.get(model);
			for (AnimatedEntity entity : batch) {
				prepareEntity(entity);
				//						Draw Type				Amount of Vertices
				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getRawModel().getVertexCount());  // Draws low poly entity - doesn't use indices
			}
			unbindTexturedModel();
		}
	}
	
	private void prepareAnimatedModel(AnimatedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());  // Binds VAO of model being rendered
		// Must enable all used arrays
		GL20.glEnableVertexAttribArray(0);  // Enables array at address 0 of VAO  // Address 0 holds vertex positions
		GL20.glEnableVertexAttribArray(1);  // Enables array at address 1 of VAO  // Address 1 holds materials
		GL20.glEnableVertexAttribArray(2);  // Enables array at address 2 of VAO  // Address 2 holds normals
		GL20.glEnableVertexAttribArray(3);  // Enables array at address 2 of VAO  // Address 2 holds normals
		GL20.glEnableVertexAttribArray(4);  // Enables array at address 2 of VAO  // Address 2 holds normals
		
		
		// Sets up any shaders requiring object-dependent variables
		shader.loadFakeLighting(model.isUseFakeLighting());
		shader.loadShineVariables(model.getShineDamper(), model.getReflectivity());
	}
	
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);  // Disables array at address 0 of VAO
		GL20.glDisableVertexAttribArray(1);  // Disables array at address 1 of VAO
		GL20.glDisableVertexAttribArray(2);  // Disables array at address 2 of VAO
		GL20.glDisableVertexAttribArray(3);  // Disables array at address 2 of VAO
		GL20.glDisableVertexAttribArray(4);  // Disables array at address 2 of VAO
		GL30.glBindVertexArray(0);  // Unbinds VAO
		MasterRenderer.enableCulling();
	}
	
	private void prepareEntity(AnimatedEntity entity) {
		// Generates and sends transformation matrix to shaders
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation().x,
				entity.getRotation().y, entity.getRotation().z, entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadJointTransforms(entity.getModel().getJointTransforms());
	}
}
