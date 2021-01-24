package models;

import java.util.Arrays;

import org.lwjgl.util.vector.Matrix4f;

import animation.Animator;
import animation.Bone;
import animation.Skeleton;

public class AnimatedModel extends Model {
	
	private String[] boneNames;
	private Skeleton skeleton;
	public Animator animator;
	
	private boolean useFakeLighting = false;
	
	public AnimatedModel(RawModel rawModel, String[] boneNames, Skeleton skeleton) {
		super(rawModel);
		this.boneNames = boneNames;
		this.skeleton = skeleton;
		this.animator = new Animator(this);
	}

	public RawModel getRawModel() {
		return this.rawModel;
	}
	
	public boolean isUseFakeLighting() {
		return this.useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

	public float getShineDamper() {
		return this.shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return this.reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	
	public String[] getBoneNames() {
		return this.boneNames;
	}
	
	public Skeleton getSkeleton() {
		return this.skeleton;
	}
	
	public Matrix4f[] getJointTransforms() {
		int boneNum = this.boneNames.length;
		Matrix4f[] out = new Matrix4f[boneNum];
		
		for (Bone bone : this.skeleton.getRootBones()) {
			getJointTransform(out, bone);
		}
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
