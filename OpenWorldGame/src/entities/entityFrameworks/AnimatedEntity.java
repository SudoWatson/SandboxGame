package entities.entityFrameworks;

import org.lwjgl.util.vector.Vector3f;

import animation.Animator;
import models.AnimatedModel;
import renderEngine.XMLLoader;

public class AnimatedEntity extends Entity {
	
	private AnimatedModel model;
	
	
	public AnimatedEntity(String modelFileName, Vector3f position, float scale) {
		super(XMLLoader.loadXMLObject(modelFileName), position, 0, 0, 0, scale);
		this.model = XMLLoader.loadXMLObject(modelFileName);
	}
	
	public AnimatedEntity(String modelFileName, Vector3f position, Vector3f rotation, float scale) {
		super(XMLLoader.loadXMLObject(modelFileName), position, rotation.x, rotation.y, rotation.z, scale);
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
