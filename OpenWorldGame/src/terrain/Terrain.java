package terrain;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import toolBox.Maths;

public class Terrain {
	
	private float x;
	private float z;
	private float edgeDist;
	
	private float[][] heights;
	
	private RawModel model;
	
	
	public Terrain(int terrainX, int terrainZ, float edgeDist, float[][] heights, RawModel model){
		this.x = terrainX;
		this.z = terrainZ;
		this.edgeDist = edgeDist;
		this.heights = heights;
		this.model = model;
	}
	
	
	
	public float getX() {
		return x;
	}



	public float getZ() {
		return z;
	}



	public RawModel getRawModel() {
		return model;
	}

	
	public float getHeightOfTerrain(float worldX, float worldZ) {
		float terrainX = (worldX - this.x);
		float terrainZ = (worldZ - this.z);
		float gridSquareSize = (heights.length /  heights.length) * edgeDist;
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		
		if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
			return 0;
		}
		
		float xCoord = (terrainX % gridSquareSize / gridSquareSize);
		float zCoord = (terrainZ % gridSquareSize / gridSquareSize);
		
		float answer;

		if (xCoord <= (1-zCoord)) {
			answer = Maths.barryCentric(new Vector3f(0,
							heights[gridX + 0][gridZ + 0], 0), new Vector3f(1,
							heights[gridX + 1][gridZ + 0], 0), new Vector3f(0,
							heights[gridX + 0][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths.barryCentric(new Vector3f(1,
							heights[gridX + 1][gridZ + 0], 0), new Vector3f(1,
							heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
							heights[gridX + 0][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		return answer;
	}

}
