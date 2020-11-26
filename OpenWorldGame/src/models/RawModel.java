package models;

import java.util.List;

import renderEngine.Loader;

public class RawModel {
	
	private int vaoID;
	private int vertexCount;
	
	private List<Integer> vboList;
	
	public RawModel(int vaoID, int vertexCount, List<Integer> vboList) {  // Creates an object that stores VAO of model and amount of vertexes
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.vboList = vboList;
	}

	public int getVaoID() {
		return vaoID;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public void remove() {
		Loader.deleteVAO(vaoID);
		for (int vbo : vboList) {
			Loader.deleteVBO(vbo);
		}
	}
}
