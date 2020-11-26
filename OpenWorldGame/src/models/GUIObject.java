package models;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

public class GUIObject {
	
	public static List<GUIObject> GUIObjects = new ArrayList<GUIObject>();
	
	private int texture;
	private Vector2f position;
	private Vector2f scale;
	
	public GUIObject(int texture, Vector2f position, Vector2f scale) {
		this.texture = texture;
		this.position = position;
		this.scale = scale;
		GUIObjects.add(this);
	}

	public int getTexture() {
		return texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getScale() {
		return scale;
	}
	
}
