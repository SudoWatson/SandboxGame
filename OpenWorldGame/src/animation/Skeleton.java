package animation;

import org.lwjgl.util.vector.Matrix4f;

public class Skeleton {
	
	private Bone[] rootBones;
	
	public Skeleton(Bone[] rootBones) {
		this.rootBones = rootBones;
	}
	
	public void animateTransform(Matrix4f[] transforms, Matrix4f parentTransform) {
		for (Bone bone : this.rootBones) {
			bone.animateTransform(transforms, parentTransform);
		}
	}
	
	public void resetAnimatedTransform() {
		for (Bone bone : this.rootBones) {
			bone.resetAnimatedTransform();
		}
	}
	
	public void calcInverseBindMatrices(Matrix4f parentMatrix) {
		for (Bone bone : this.rootBones) {
			bone.calcInverseBindTransform(parentMatrix);
		}
	}
	
	public Bone[] getRootBones() {
		return this.rootBones;
	}
	
}
