package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import animation.Animator;
import entities.entityFrameworks.AnimatedEntity;
import entities.entityFrameworks.Camera;
import renderEngine.DisplayManager;

public class Player extends AnimatedEntity {

	private static final String MODEL_FILE = "player";
	private static final float RUN_SPEED = 12f;
	private static final float STRAFE_SPEED = RUN_SPEED - 2f;
	private static final float JUMP_FORCE = 15;
	
	private Camera playerCamera;
	
	private float currentSpeed  = 0;
	private float currentStrafe = 0;
	
	
	public Player(Vector3f position, float rotX, float rotY, float rotZ) {
		super(MODEL_FILE, position, new Vector3f(rotX, rotY, rotZ), 0.25f);
		this.addHitbox("main", new Vector3f(0,0,0), new Vector3f(1,3,1));
		this.setCollision(true, this.getHitbox("main"));
		this.usesGravity();
		this.isDynamic();
		this.getAnimator().setAnimatorSpeed(2.75f);
		this.getAnimator().setEndingType(Animator.LOOP);
	}
	
	public void setCamera(Camera camera) {
		this.playerCamera = camera;
	}
	
	public void update() {
		super.update();
		if (this.playerCamera != null) {
			this.playerCamera.update();
		}
	}
	
	protected void updateLogic() {
		Boolean moving = true;
		Boolean beganMoving = false;
		// if 'w' or 's' is pressed
		if (this.dx == 0.0f & this.dz == 0.0f) {
			moving = false;
		}
		this.move();
		if (!(this.dx == 0.0f & this.dz == 0.0f) & !moving) {
			beganMoving = true;
		}
		if (beganMoving) {
			this.playAnimation("Walking");
		}
		if (this.dx == 0.0f & this.dz == 0.0f) {
			this.stopAnimation();
		}
	}
	
	private void move() {
		// Moving method
		checkInputs();
		//super.rotate(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float angleChange = Mouse.getDX() * 0.3f;
		this.rotate(0, -angleChange, 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float diStrafe = currentStrafe * DisplayManager.getFrameTimeSeconds();
		dx =  (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		dz =  (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		dx += (float) (diStrafe * Math.sin(Math.toRadians(super.getRotY()-90)));
		dz += (float) (diStrafe * Math.cos(Math.toRadians(super.getRotY()-90)));
	}
	
	private void checkInputs() {

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				this.currentSpeed = RUN_SPEED*2;
			}
			else {
				this.currentSpeed = RUN_SPEED;
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeed = -STRAFE_SPEED;
		} else {
			this.currentSpeed = 0;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentStrafe = -STRAFE_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentStrafe = STRAFE_SPEED;
		} else {
			this.currentStrafe = 0;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
	}
	
	private void jump() {
		if (!inAir) {
			inAir = true;
			this.ySpeed = JUMP_FORCE;
		}
	}
	
}
