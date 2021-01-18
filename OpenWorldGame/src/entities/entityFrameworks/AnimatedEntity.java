package entities.entityFrameworks;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.AnimatedModel;
import renderEngine.XMLLoader;

public class AnimatedEntity {
	
	private Vector3f position;
	private Vector3f rotation;
	
	private AnimatedModel model;
	
	
	public AnimatedEntity(String modelFileName, Vector3f position) {
		this.model = XMLLoader.loadXMLObject(modelFileName);
		this.position = position;
		this.rotation = new Vector3f(0,0,0);
	}
	
	public void update() {
		this.model.animator.update();
	}
	
	public Vector3f getPosition() {
		return this.position;
	}
	
	public Vector3f getRotation() {
		return this.rotation;
	}

	public AnimatedModel getModel() {
		// TODO Auto-generated method stub
		return this.model;
	}
	
	public Matrix4f getTransform() {  // WHY DOES IT ONLY WORK LIKE THIS
		return this.model.getSkeleton().getAnimatedTransform();
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
