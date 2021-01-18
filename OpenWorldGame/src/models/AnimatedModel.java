package models;

import java.util.Arrays;

import org.lwjgl.util.vector.Matrix4f;

import animation.Animator;
import animation.Bone;

public class AnimatedModel {
	
	private RawModel rawModel;
	private String[] boneNames;
	private Bone skeleton;
	public Animator animator;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean useFakeLighting = false;
	
	public AnimatedModel(RawModel rawModel, String[] boneNames, Bone rootBone) {
		this.rawModel = rawModel;
		this.boneNames = boneNames;
		this.skeleton = rootBone;
		this.animator = new Animator(this);
	}

	public RawModel getRawModel() {
		return rawModel;
	}
	
	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	
	public String[] getBoneNames() {
		return this.boneNames;
	}
	
	public Bone getSkeleton() {
		return this.skeleton;
	}
	
	public Matrix4f[] getJointTransforms() {
		int boneNum = this.boneNames.length;
		Matrix4f[] out = new Matrix4f[boneNum];
		
		getJointTransform(out, this.getSkeleton());
		return out;
	}
	
	private void getJointTransform(Matrix4f[] destination, Bone bone) {
		int index = Arrays.asList(boneNames).indexOf(bone.getName());
		destination[index] = bone.getAnimatedTransform();
		
		for (Bone child : bone.getChildren()) {
			getJointTransform(destination, child);
		}
	}
}
