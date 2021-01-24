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
	
	public AnimatedModel(RawModel rawModel, String[] boneNames, Skeleton skeleton) {
		super(rawModel);
		this.boneNames = boneNames;
		this.skeleton = skeleton;
		this.animator = new Animator(this);
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
