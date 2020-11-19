package terrain;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import engineTester.Main;
import models.RawModel;
import toolBox.Maths;
import toolBox.ValueNoise2D;

public class TerrainGenerator {

	// Colors seem to be the same across 2 triangles. Normals too?
	
	private static final int DEF_SZ = 100;  // Default Size of terrain
	private static final float DEF_DIST = 3f;  // Default distance between vertices
	private static final float DEF_HEIGHT = 20f;  // Default max height of terrain

	private static final ValueNoise2D heightNoise = new ValueNoise2D(DEF_HEIGHT);
	private static final ValueNoise2D colorHeightNoise = new ValueNoise2D(3);
	private static final ValueNoise2D colorNoise = new ValueNoise2D(0.4f);	
	
	private static final Vector3f topColor = new Vector3f(0.502f,0.5176f,0.5294f);  // Stone Grey
	private static final Vector3f midColor = new Vector3f(0.01f,1,0.25f);			// Grassy Green
	private static final Vector3f botColor = new Vector3f(0.8235f,0.6667f,0.4275f);	// Sand Yellow

	private static final float upperLine = 15;
	private static final float lowerLine = 5;
	
	
	public static Terrain generateTerrain(int x, int y) {
		float[][] heights = generateHeights(DEF_SZ);
		return generateTerrain(x, y, DEF_DIST, generateHeights(DEF_SZ), generateColors(DEF_SZ, heights));
	}

	public static Terrain generateTerrain(int x, int y, int size) {
		float[][] heights = generateHeights(size);
		return generateTerrain(x, y, DEF_DIST, generateHeights(size), generateColors(size, heights));
	}
	
	public static Terrain generateTerrain(int x, int y, float edgeDist, float[][] heights, Vector3f[][] colors) {
		
		RawModel model = createTerrainMesh(edgeDist, heights, colors);
		
		return new Terrain(x,y, edgeDist,heights,model);
	}
	
	private static float[][] generateHeights(int size) {
		float[][] heights = new float[size][size];

		for (int i = 0; i < heights.length; i++) {
			for (int j = 0; j < heights.length; j++) {
				heights[i][j] = heightNoise.getValue(i, j);
			}
		}
		
		return heights;
	}
	
	private static Vector3f[][] generateColors(int size, float[][] heights) {
		Vector3f[][] colors = new Vector3f[size][size*2];  // Twice as wide because 2 triangles = 1 square
		for (int i = 0; i < colors.length; i++) {
			for (int j = 0; j < colors.length; j++) {
				for (int k = 0; k < 2; k++) {
					Vector3f color = new Vector3f(midColor);
					// Gets the average height of the triangle
					float heightNoise = colorHeightNoise.getValue(i, j);
					float height = 0;
					if (i >= colors.length-1 || j >= colors.length-1) {} 
					else if (k == 0) {
						height = (heights[j][i] + heights[j+1][i] + heights[j+1][i+1])/3;  // Gets the average height of the left triangle
					} else if (k == 1) {
						height = (heights[j][i] + heights[j][i+1] + heights[j+1][i+1])/3;  // Gets the average height of the right triangle
					}
					
					// Changes triangle color based on height
					if (height > upperLine-DEF_HEIGHT/2 + heightNoise) color = new Vector3f(topColor);
					//else if (heights[i][j] > upperLine-DEF_HEIGHT/2 - heightNoise) color = Maths.blend(midColor,topColor,colorBlendNoise.getValue(i, j)+0.5f);
					else if (height < lowerLine-DEF_HEIGHT/2 - heightNoise) color = new Vector3f(botColor);
					//else if (heights[i][j] < upperLine-DEF_HEIGHT/2 + heightNoise) color = Maths.blend(botColor,topColor,colorBlendNoise.getValue(i, j)+0.5f);
					else {
						// Get steepest angle
						// Blend green with brown for the more steep it is
					}
					
					// Adds variety to color
					color.x += colorNoise.getValue(j+99999, i*2+k+999999)*3/4;
					color.y += colorNoise.getValue(j, i*2+k);
					color.z += colorNoise.getValue(j+9999, i*2+k+9999999);
					colors[i][j*2+k] = color;
				}
			}
		}

		return colors;
	}
	
	private static RawModel createTerrainMesh(float edgeDist, float[][] heights, Vector3f[][] trnColors) {
		int size = heights.length;
		int lengthSize = size*2-2;
		int indicesLength = (lengthSize*3/2) + (lengthSize*3*(size-3)) + (size-1) * 6 + (size-1)*3 + 1;
		
		List<Vector3f> colorsArray = new ArrayList<Vector3f>();
		List<Vector3f> normalsArray = new ArrayList<Vector3f>();
		List<Vector3f> verticesArray = new ArrayList<Vector3f>();
		
		// Upper group of squares
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				colorsArray.add(trnColors[col][row*2]);
				normalsArray.add(new Vector3f(0,0,0));
				verticesArray.add(new Vector3f(col*edgeDist,heights[col][row],row*edgeDist));
				
				if (!(col == 0 || col==size-1) && row < size-2) {
					colorsArray.add(trnColors[col][row*2+1]);
					normalsArray.add(new Vector3f(0,1,0));
					verticesArray.add(new Vector3f(col*edgeDist,heights[col][row],row*edgeDist));
				}
				
			}
		}
		

		int[] indices = new int[indicesLength];
		float[] colors = new float[colorsArray.size()*3];
		float[] normals = new float[normalsArray.size()*3];
		float[] vertices = new float[verticesArray.size()*3];

		int vertexPointer = 0;
		for (int i = 0; i < verticesArray.size(); i++) {
			vertices[vertexPointer++] = verticesArray.get(i).x;
			vertices[vertexPointer++] = verticesArray.get(i).y;
			vertices[vertexPointer++] = verticesArray.get(i).z;
		}
		
		
		
		// Store all the indices, normals, and colors
		// Normals and colors have to be calculated once for each triangle (twice each run)
		// Normals and colors only need to be stored at the provoking vertex (first vertex)
		
		int indexPointer = 0;
		// Main rows of squares			Dont want the last 3 rows
		for (int i = 0; i < verticesArray.size()-(lengthSize+size*2); i+=2) {
			int topLeft = i;
			int topRight = topLeft+1;
			int bottomLeft = i+lengthSize;
			int bottomRight = bottomLeft+1;

			Vector3f normal1 = Maths.calcNormal(verticesArray.get(topLeft), verticesArray.get(bottomLeft), verticesArray.get(bottomRight));
			Vector3f normal2 = Maths.calcNormal(verticesArray.get(topRight), verticesArray.get(topLeft), verticesArray.get(bottomRight));

			Vector3f color1 = colorsArray.get(topLeft);
			Vector3f color2 = colorsArray.get(topRight);
			if (color1 == null) color1 = new Vector3f(0.5f,0,0.5f);
			if (color2 == null) color2 = new Vector3f();
			

			colors[indexPointer] = color1.x;
			normals[indexPointer] = normal1.x;
			indices[indexPointer++] = topLeft;
			
			colors[indexPointer] = color1.y;
			normals[indexPointer] = normal1.y;
			indices[indexPointer++] = bottomLeft;
			
			colors[indexPointer] = color1.z;
			normals[indexPointer] = normal1.z;
			indices[indexPointer++] = bottomRight;


			colors[indexPointer] = color2.x;
			normals[indexPointer] = normal2.x;
			indices[indexPointer++] = topRight;

			colors[indexPointer] = color2.y;
			normals[indexPointer] = normal2.y;
			indices[indexPointer++] = topLeft;

			colors[indexPointer] = color2.z;
			normals[indexPointer] = normal2.z;
			indices[indexPointer++] = bottomRight;
			
		}
		
		// Second to last row of squares
		for (int i = verticesArray.size()-(lengthSize+size*2); i < verticesArray.size()-(size*2); i += 2) {
			int topLeft = i;
			int topRight = topLeft+1;
			int bottomLeft = (verticesArray.size()-(size*2)) + ((i%lengthSize)/2);
			int bottomRight = bottomLeft+1;

			Vector3f normal1 = Maths.calcNormal(verticesArray.get(topLeft), verticesArray.get(bottomLeft), verticesArray.get(bottomRight));
			Vector3f normal2 = Maths.calcNormal(verticesArray.get(topRight), verticesArray.get(topLeft), verticesArray.get(bottomRight));

			Vector3f color1 = colorsArray.get(topLeft);
			Vector3f color2 = colorsArray.get(topRight);
			if (color1 == null) color1 = new Vector3f(0.5f,0,0.5f);
			if (color2 == null) color2 = new Vector3f();

			colors[indexPointer] = color1.x;
			normals[indexPointer] = normal1.x;
			indices[indexPointer++] = topLeft;
			
			colors[indexPointer] = color1.y;
			normals[indexPointer] = normal1.y;
			indices[indexPointer++] = bottomLeft;
			
			colors[indexPointer] = color1.z;
			normals[indexPointer] = normal1.z;
			indices[indexPointer++] = bottomRight;


			colors[indexPointer] = color2.x;
			normals[indexPointer] = normal2.x;
			indices[indexPointer++] = topRight;

			colors[indexPointer] = color2.y;
			normals[indexPointer] = normal2.y;
			indices[indexPointer++] = topLeft;

			colors[indexPointer] = color2.z;
			normals[indexPointer] = normal2.z;
			indices[indexPointer++] = bottomRight;
		}
		
		// This is a bit messed up but oh well. Its only 1 row.
		// Last row of squares
		for (int i = verticesArray.size()-(size*2); i < verticesArray.size()-(size)-1; i ++) {
			int topLeft = i;
			int topRight = topLeft+1;
			int bottomLeft = i+size;
			int bottomRight = bottomLeft+1;

			Vector3f normal1 = Maths.calcNormal(verticesArray.get(topLeft), verticesArray.get(bottomLeft), verticesArray.get(bottomRight));
			Vector3f normal2 = Maths.calcNormal(verticesArray.get(topRight), verticesArray.get(topLeft), verticesArray.get(bottomRight));

			Vector3f color1 = colorsArray.get(topLeft);
			Vector3f color2 = colorsArray.get(topRight);
			if (color1 == null) color1 = new Vector3f(0.5f,0,0.5f);
			if (color2 == null) color2 = new Vector3f();

			colors[indexPointer] = color1.x;
			normals[indexPointer] = normal1.x;
			indices[indexPointer++] = topLeft;
			
			colors[indexPointer] = color1.y;
			normals[indexPointer] = normal1.y;
			indices[indexPointer++] = bottomLeft;
			
			colors[indexPointer] = color1.z;
			normals[indexPointer] = normal1.z;
			indices[indexPointer++] = bottomRight;


			colors[indexPointer] = color2.x;
			normals[indexPointer] = normal2.x;
			indices[indexPointer++] = topRight;

			colors[indexPointer] = color2.y;
			normals[indexPointer] = normal2.y;
			indices[indexPointer++] = topLeft;

			colors[indexPointer] = color2.z;
			normals[indexPointer] = normal2.z;
			indices[indexPointer++] = bottomRight;
		}
		
		return Main.loader.loadToVAO(vertices, normals, colors, indices);
	}
	
}
