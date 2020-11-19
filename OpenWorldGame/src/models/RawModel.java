package models;

public class RawModel {
	
	private int vaoID;
	private int vertexCount;
	
	public RawModel(int vaoID, int vertexCount) {  // Creates an object that stores VAO of model and amount of vertexes
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	public int getVaoID() {
		return vaoID;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
}
