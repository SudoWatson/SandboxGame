package entities.entityFrameworks;

import org.lwjgl.util.vector.Vector3f;

import animation.Animator;
import models.AnimatedModel;
import renderEngine.XMLLoader;

public class AnimatedEntity extends Entity {
	
	private AnimatedModel model;
	
	
	public AnimatedEntity(String modelFileName, Vector3f position) {
		super(XMLLoader.loadXMLObject(modelFileName), position, 0.0f, 0.0f, 0.0f, 1.0f);
		this.model = XMLLoader.loadXMLObject(modelFileName);
	}
	
	public AnimatedEntity(String modelFileName, Vector3f position, Vector3f rotation) {
		super(XMLLoader.loadXMLObject(modelFileName), position, 0.0f, 0.0f, 0.0f, 1.0f);
		this.model = XMLLoader.loadXMLObject(modelFileName);
	}

	protected void classUpdate() {
		this.model.animator.update();
	}
	
	protected Animator getAnimator() {
		return this.model.animator;
	}
	
	public AnimatedModel getModel() {
		return this.model;
	}
	
//	public Matrix4f getTransform() {  // WHY DOES IT ONLY WORK LIKE THIS
//		return this.model.getSkeleton().getAnimatedTransform();
//	}
	
	public void playAnimation(String animationName) {
		this.model.animator.playAnimation(animationName);
	}
	
	public void pauseAnimation() {
		this.model.animator.pauseAnimation();
	}
	
	public void resumeAnimation() {
		this.model.animator.resumeAnimation();
	}
	
	public void stopAnimation() {
		this.model.animator.stopAnimation();
	}
	
}
