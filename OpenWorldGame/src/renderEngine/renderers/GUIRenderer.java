package renderEngine.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import models.GUIObject;
import models.RawModel;
import renderEngine.Loader;
import shaders.gui.GUIShader;
import toolBox.Maths;

public class GUIRenderer {
	
	private final RawModel quad;
	private GUIShader shader;
	
	public GUIRenderer() {
		float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
		this.quad = Loader.loadToVAO(positions);
		this.shader = new GUIShader();
	}
	
	public void render(List<GUIObject> guiObjects) {
		this.shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for (GUIObject guiObject : guiObjects) {
			GL13.glActiveTexture(0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, guiObject.getTexture());
			Matrix4f transMatrix = Maths.createTransformationmatrix(guiObject.getPosition(), guiObject.getScale());
			shader.loadTransformationMatrix(transMatrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		this.shader.stop();
	}
	
	public void cleanUp() {
		this.shader.cleanup();
	}
	
}
