package animation;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.AnimatedModel;
import renderEngine.DisplayManager;
import toolBox.Quaternion;

public class Animator {

	public static final int END = 0;
	public static final int PAUSE = 1;
	public static final int LOOP = 2;
	
	
	private int endingType;
	
	private float time;
	private float speed;
	
	private boolean paused = false;
	
	private Animation animation;
	
	private Matrix4f[] transforms;
	
	public final AnimatedModel model;
	
	
	public Animator(AnimatedModel animModel) {
		this.endingType = 1;
		this.time = 0;
		this.speed = 1;
		this.model = animModel;
		this.transforms = new Matrix4f[this.model.getBoneNames().length];
		
	}
	
	
	public void update() {
		if (this.animation == null) return;

		if (!this.paused) {
			this.increaseTime();
			
			for (Bone bone : this.model.getSkeleton().getRootBones()) {
				this.calculateTransforms(bone, this.transforms);
			}
			this.model.getSkeleton().animateTransform(this.transforms, new Matrix4f());
		}
	}
	
	public float getTime() {
		return this.time;
	}
	
	public void setAnimatorSpeed(float speedMultiplier) {
		this.speed = speedMultiplier;
	}
	
	public void setEndingType(int endingType) {
		this.endingType = endingType;
	}
	
	public void playAnimation(String animationName) {
		this.time = 0;
		this.animation = Animation.getAnimation(animationName);
		this.paused = false;
	}
	
	public void resumeAnimation() {
		this.paused = false;
	}
	
	public void pauseAnimation() {
		this.paused = true;
	}
	
	public void stopAnimation() {
		this.paused = true;
		this.animation = null;
		this.model.getSkeleton().resetAnimatedTransform();
	}
	
	private void increaseTime() {
		this.time += DisplayManager.getFrameTimeSeconds() * this.speed;
		if (this.time >= this.animation.getTimeLength()) {
			if (this.endingType == PAUSE) {
				this.time = this.animation.getTimeLength() - 0.001f;
				this.pauseAnimation();
			} else if (this.endingType == LOOP) {
				this.time %= this.animation.getTimeLength();
			}
		}
	}
	
	private void calculateTransforms(Bone bone, Matrix4f[] transformations) {
		int boneDex = bone.getBoneIndex();
		float[] keyFrames = this.animation.getKeyFrames().get(boneDex);
		Matrix4f[] transforms = this.animation.getTransformationMatrices().get(boneDex);
		
		int prevFrameInd = 0;
		int nextFrameInd = 0;
		for (int i = 1; i < keyFrames.length; i++) {
			nextFrameInd = i;
			if (keyFrames[nextFrameInd] > this.time) {
				break;
			}
			prevFrameInd = i;
		}
		
		float blend = (time-keyFrames[prevFrameInd]) / (keyFrames[nextFrameInd] - keyFrames[prevFrameInd]);
		Matrix4f transform = interpolateTransformation(transforms[prevFrameInd], transforms[nextFrameInd], blend);  // Destination transform
		
		transformations[boneDex] = transform;
		
		for (Bone child : bone.getChildren()) {
			this.calculateTransforms(child, transformations);
		}
	}
	
	private static Matrix4f interpolateTransformation(Matrix4f matrix1, Matrix4f matrix2, float blend) {
		Quaternion rotation1 = Quaternion.fromMatrix(matrix1);
		Quaternion rotation2 = Quaternion.fromMatrix(matrix2);

		Vector3f position1 = new Vector3f(matrix1.m30, matrix1.m31, matrix1.m32);
		Vector3f position2 = new Vector3f(matrix2.m30, matrix2.m31, matrix2.m32);
		
		Vector3f pos = interpolatePos(position1, position2, blend);
		Quaternion rot = Quaternion.interpolate(rotation1, rotation2, blend);
		
		Matrix4f out = new Matrix4f();
		out.translate(pos);
		Matrix4f.mul(out, rot.toRotationMatrix(), out);
		
		return out;
	}
	
	private static Vector3f interpolatePos(Vector3f start, Vector3f end, float blend) {
		float x = start.x + (end.x - start.x) * blend;
		float y = start.y + (end.y - start.y) * blend;
		float z = start.z + (end.z - start.z) * blend;
		return new Vector3f(x, y, z);
	}
	
	
	
	
}
