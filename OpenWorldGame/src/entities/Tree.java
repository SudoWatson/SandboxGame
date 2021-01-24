package entities;

import org.lwjgl.util.vector.Vector3f;

import animation.Animator;
import entities.entityFrameworks.AnimatedEntity;

public class Tree extends AnimatedEntity {
	
	private static final String MODEL_FILE = "loliPopTree";
	
	public Tree(Vector3f position, float rotX, float rotY, float rotZ) {
		super(MODEL_FILE, position, new Vector3f(rotX, rotY, rotZ), 1);
		this.addHitbox("main", new Vector3f(0,0,0), new Vector3f(1,3,1));
		this.addHitbox("leaves", new Vector3f(0,3,0), new Vector3f(2.5f,1,2.5f));
		this.setCollision(true, "main");
		this.setCollision(true, "leaves");
		this.setCollision(true, this.getHitbox("main"));
		this.usesGravity();
		this.getAnimator().setEndingType(Animator.PAUSE);
	}
	
	public void rayClick(int button, boolean state) {
		if (state) {
			if (button == 0) {
				this.playAnimation("Fall");
				this.getAnimator().setEndingType(Animator.PAUSE);
			}
		}
	}
}
