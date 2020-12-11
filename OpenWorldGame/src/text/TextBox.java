package text;

import org.lwjgl.util.vector.Vector2f;

import engineTester.Main;
import models.RawModel;
import text.font.Font;

public class TextBox {
	
	private final Vector2f POSITION, SCALE;

	private RawModel textMesh;
	private int vertexCount;
	private int vaoID;
	
	private String text;
	private Font font;
	
	
	public TextBox(String text, Font font, Vector2f position, Vector2f scale) {
		this.POSITION = position;
		this.SCALE = scale;
		this.text = text;
		this.font = font;
		this.setMesh();
	}
	
	public void show() {
		Main.textBoxes.add(this);
	}
	
	public void hide() {
		Main.textBoxes.remove(this);
	}
	
	public Vector2f getPosition() {
		return this.POSITION;
	}
	
	public Vector2f getScale() {
		return this.SCALE;
	}
	
	public String getString() {
		return this.text;
	}
	
	public int getVaoID() {
		return this.vaoID;
	}
	
	public int getVertexCount() {
		return this.vertexCount;
	}
	
	public Font getFont() {
		return this.font;
	}
	
	public void setText(String newText) {
		this.textMesh.remove();
		this.text = newText;
		this.setMesh();
	}
	
	private void setMesh() {
		this.textMesh = TextLoader.loadText(this);
		this.vaoID = textMesh.getVaoID();
		this.vertexCount = this.textMesh.getVertexCount();
	}
	
}
