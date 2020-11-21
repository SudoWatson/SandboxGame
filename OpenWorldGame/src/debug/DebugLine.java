package debug;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class DebugLine {
	public static List<DebugLine> DebugLines = new ArrayList<DebugLine>();
	
	public Vector3f start;
	public Vector3f end;
	
	public DebugLine(Vector3f start, Vector3f end) {
		this.start = start;
		this.end = end;
		DebugLines.add(this);
	}
}
