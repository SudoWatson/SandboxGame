package animation;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

public class Bone {

	private String name;
	private int boneIndex;
	private List<Bone> children = new ArrayList<Bone>();
	
	private Matrix4f animatedTransform =  new Matrix4f();  // Matrix used to bring bone to its animated position
	
	// Local-Relative to it's parent	// Bind-According to its original position, not an animated position
	private final Matrix4f localBindTransform;
	private Matrix4f inverseBindTransform;
	
	
	public Bone(int index, String name, Matrix4f localBindTransform) {
		if (name == null) System.out.println("Name is null");
		if (localBindTransform == null) System.out.println("Matrix is null");
		this.boneIndex = index;
		this.name = name;
		this.localBindTransform = localBindTransform;
	}
	
	public void addChild(Bone child) {
		this.children.add(child);
	}

	public String getName() {return this.name;}

	public int getBoneIndex() {return this.boneIndex;}

	public List<Bone> getChildren() {return this.children;}

	public Matrix4f getLocalBindTransform() {return localBindTransform;}

	public Matrix4f getInverseBindTransform() {return inverseBindTransform;}
	
	public Matrix4f getAnimatedTransform() {
		return new Matrix4f(this.animatedTransform);
	}
	
	public void calcInverseBindTransform(Matrix4f parentBindTransform) {
		Matrix4f bindTransform = Matrix4f.mul(parentBindTransform, this.localBindTransform, null);
		this.inverseBindTransform = Matrix4f.invert(bindTransform, null);
		for (Bone child : this.children) {
			if (!(child == null)) child.calcInverseBindTransform(bindTransform);
		}
	}
	
	public void resetAnimatedTransform() {
		this.animatedTransform = new Matrix4f();
		for (Bone child : this.children) {
			child.resetAnimatedTransform();
		}
	}
	
	public void animateTransform(Matrix4f[] transforms, Matrix4f parentTransform) {
		Matrix4f desiredLocalTransform = transforms[this.boneIndex];
		Matrix4f desiredTransform = Matrix4f.mul(parentTransform, desiredLocalTransform, null);
		Matrix4f.mul(desiredTransform, this.inverseBindTransform, this.animatedTransform);
		
		for (Bone child : this.children) {
			child.animateTransform(transforms, desiredTransform);
		}
		
	}
	
	public String toString() {
		
		String outString = ("\n\nName: " + this.name + "    ID: " + Integer.toString(this.boneIndex) + "\n"
				+ "Matrix: " + this.localBindTransform.toString()
				+ "InvMat: ");
		if (this.inverseBindTransform == null) outString += "Null\n";
		else outString += this.inverseBindTransform.toString();
		outString += "Children: " + this.children;
		
		return outString;
	}
	
}
