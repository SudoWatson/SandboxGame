package renderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import models.RawModel;
import toolBox.Maths;

public class OBJLoader {

	private static List<String> modelNames = new ArrayList<String>();
	private static List<RawModel> rawModels = new ArrayList<RawModel>();
	
	public static RawModel loadObjModel(String fileName) {
		if (!modelNames.contains(fileName)) createObjModel(fileName);
		return rawModels.get(modelNames.indexOf(fileName));
	}
	
	private static void createObjModel(String fileName) {
		String matFileName = "missingMaterial.mtl";
		boolean material = false;
		
		FileReader objFileReader = null;
		try {
			objFileReader = new FileReader(new File("res/models/"+fileName+".obj"));
		} catch (FileNotFoundException e) { 
			System.err.println("Couldn't load file res/models/" + fileName + ".obj");
			try {
				objFileReader = new FileReader(new File("res/models/missingModel.obj"));
			} catch (FileNotFoundException e1) {
				System.err.println("Missing Model file not found.");
				e1.printStackTrace();
			}
		}
		
		BufferedReader objReader = new BufferedReader(objFileReader);
		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Vector4f> materials = new ArrayList<Vector4f>();
		List<Vector4f> materialColors = new ArrayList<Vector4f>();
		List<String> materialNames = new ArrayList<String>();
		List<Integer> indices = new ArrayList<Integer>();
		float[] verticesArray = null;
		float[] normalsArray = null;
		float[] materialsArray = null;
		int[] indicesArray = null;
		
		try {
			while (true) {
				line = objReader.readLine();
				String[] currentLine = line.split(" ");
				if (line.startsWith("mtllib")) {
					matFileName = currentLine[1];
					material = true;
				} else if (line.startsWith("v ")) {
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
							Float.parseFloat(currentLine[3]));
					vertices.add(vertex);
				} else if (line.startsWith("vn ")) {
					Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
							Float.parseFloat(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ") || line.startsWith("usemtl")) {
					break;
				}
				
			}
			
			if (material) {
				FileReader matFileReader = null;
				try {
					matFileReader = new FileReader(new File("res/materials/"+matFileName));
				} catch (FileNotFoundException e) {
					System.err.println("Couldn't load file res/materials/" + matFileName);
					try {
						matFileReader = new FileReader(new File("res/materials/missingMaterial.mtl"));
					} catch (FileNotFoundException e1) {
						System.err.println("Missing Material file not found. Ironic\n");
						e1.printStackTrace();
					}
				}
				
				BufferedReader matReader = new BufferedReader(matFileReader);
				String matLine;
				matLine = matReader.readLine();
				
				try {
					while (matLine != null) {
						String[] currentLine = matLine.split(" ");
						if (matLine.startsWith("newmtl ")) {
							materialNames.add(currentLine[1]);
						} else if (matLine.startsWith("Kd ")) {
							Vector4f materialColor = new Vector4f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
									Float.parseFloat(currentLine[3]), 1);
							materialColors.add(materialColor);
						}
						matLine = matReader.readLine();
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				matReader.close();
				
			} else materialColors.add(new Vector4f (0,0,0,0));

			Vector4f mat = new Vector4f(1,1,1,1);
			while (line != null) {
				if (!line.startsWith("f ") && !line.startsWith("usemtl")) {  // Line we don't care about
					line = objReader.readLine();
					continue;
				}
				
				if (line.startsWith("usemtl ")) {  // Line telling the material
					String[] currentLine = line.split(" ");
					int index = materialNames.indexOf(currentLine[1]);
					if (index < 0) {
						System.err.println("Material " + currentLine[1] + " not found in res/models/" + matFileName + ".obj");
					}
					mat = materialColors.get(index);
					line = objReader.readLine();
					continue;
				}
				
				
				// f Line
				materials.add(mat);
				
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");

				indices.add(Integer.parseInt(vertex1[0])-1);
				indices.add(Integer.parseInt(vertex2[0])-1);
				indices.add(Integer.parseInt(vertex3[0])-1);
				
				line = objReader.readLine();
			}
			
			objReader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		indicesArray = new int[indices.size()];
		normalsArray = new float[indices.size()*3];
		verticesArray = new float[indices.size()*3];
		materialsArray = new float[indices.size()*4];
		int vertexPointer = 0;
		for (int i = 0; i < indices.size(); i++) {
			// Stores a single vertex
			int index = indices.get(i);
			indicesArray[i] = index;
			verticesArray[vertexPointer++] = vertices.get(index).x;
			verticesArray[vertexPointer++] = vertices.get(index).y;
			verticesArray[vertexPointer++] = vertices.get(index).z;
			
			if (i%3 == 2) {  // 3 Triangle vertices have been calculated; Calculate the normal for the triangle
				Vector3f vertex0 = vertices.get(indices.get(i-2));
				Vector3f vertex1 = vertices.get(indices.get(i-1));
				Vector3f vertex2 = vertices.get(indices.get(i-0));
				
				Vector3f normal = Maths.calcNormal(vertex0, vertex1, vertex2);
				
				int normalPointer = vertexPointer-9;
				while (normalPointer < vertexPointer) {
					normalsArray[normalPointer++] = normal.x;
					normalsArray[normalPointer++] = normal.y;
					normalsArray[normalPointer++] = normal.z;
				}
			}
		}
		int materialPointer = 0;
		for (int i = 0; i < materials.size(); i++) {
			Vector4f mat = new Vector4f(materials.get(i));
			for (int m = 0; m < 3; m++) {
				materialsArray[materialPointer++] = mat.x;
				materialsArray[materialPointer++] = mat.y;
				materialsArray[materialPointer++] = mat.z;
				materialsArray[materialPointer++] = mat.w;
			}
		}
		
		if (fileName == "sd") {
			System.out.println(fileName);
			System.out.println(Arrays.toString(verticesArray));
		}
		
		modelNames.add(fileName);
		rawModels.add(Loader.loadToVAO(verticesArray, normalsArray, materialsArray));
	}
	
	
}
