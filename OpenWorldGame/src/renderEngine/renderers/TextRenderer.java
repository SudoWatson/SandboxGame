package renderEngine.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import shaders.text.TextShader;
import text.TextBox;
import toolBox.Maths;

public class TextRenderer {
	
	private TextShader shader;
	
	public TextRenderer() {
		this.shader = new TextShader();
	}
	
	public void render(List<TextBox> textBoxes) {
		this.shader.start();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for (TextBox textBox : textBoxes) {
			GL30.glBindVertexArray(textBox.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textBox.getFont().getFontData().getTextureID());
			Matrix4f transMatrix = Maths.createTransformationmatrix(textBox.getPosition());
			shader.loadTransformationMatrix(transMatrix);
			shader.loadColor(textBox.getFont().getColor());
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, textBox.getVertexCount());
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		this.shader.stop();
	}
	
	public void cleanUp() {
		this.shader.cleanup();
	}
}
