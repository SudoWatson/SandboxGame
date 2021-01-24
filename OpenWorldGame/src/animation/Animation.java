package animation;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

public class Animation {
	
	/*
	 * List of list of keyFrames and list of list of transformation matrices
	 * When animating a bone, take the index of the bone to find all of that bone's keyframes and matrices
	 * Use that bone's keyframes to find the current time, and interpolate between the matrices
	 * Affect that bone by its matrix, and recursively affect it's children, then recursively find the childrens trans matrices...etc
	 */
	
	private static List<String> animationNames = new ArrayList<String>();
	private static List<Animation> animations = new ArrayList<Animation>();
	
	private final String name;
	private final float timeLength; // Length of animation in seconds
	private final List<String> boneNames;
	private final List<float[]> keyFrames;
	private final List<Matrix4f[]> transformationMatrices;
	
	public Animation(String name, float timeLength, List<String> boneNames, List<float[]> keyFrames2, List<Matrix4f[]> matrices) {
		this.name = name;
		this.timeLength = timeLength;
		this.boneNames = boneNames;
		this.keyFrames = keyFrames2;
		this.transformationMatrices = matrices;
		animationNames.add(name);
		animations.add(this);
		
	}

	public String getName() {
		return name;
	}

	public float getTimeLength() {
		return timeLength;
	}

	public List<String> getBoneNames() {
		return boneNames;
	}

	public List<float[]> getKeyFrames() {
		return keyFrames;
	}

	public List<Matrix4f[]> getTransformationMatrices() {
		return transformationMatrices;
	}
	
	public String toString() {		
		String out = "Animation Name: " + this.name + "		Lasts for: " + Float.toString(this.timeLength) + " sec.\n"
				+ "Uses bones: " + this.boneNames;
		return out;
	}
	
	public void removeAnimation() {
		animationNames.remove(this.name);
		animations.remove(this);
	}
	
	public static Animation getAnimation(String animationName) {
		int animInd = animationNames.indexOf(animationName);
		if (animInd < 0) System.out.println("Unknown animation: " + animationName); 
		return animations.get(animInd);
	}
	
}
