package debug;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class DebugPoint {
	public static List<DebugPoint> DebugPoints = new ArrayList<DebugPoint>();
	
	public Vector3f pos;
	public Vector3f color;
	
	public DebugPoint(Vector3f pos) {
		this.pos = pos;
		this.setColor(new Vector3f(1,0,0));
		DebugPoints.add(this);
	}
	
	public void setColor(Vector3f color) {
		this.color = color;
	}
}
