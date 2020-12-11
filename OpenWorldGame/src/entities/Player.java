package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import engineTester.Main;
import entities.entityFrameworks.Camera;
import entities.entityFrameworks.Entity;
import models.Model;
import renderEngine.DisplayManager;
import renderEngine.OBJLoader;

public class Player extends Entity {

	private static final String MODEL_FILE = "blob";
	private static final float RUN_SPEED = 12f;
	private static final float STRAFE_SPEED = RUN_SPEED - 2f;
	private static final float JUMP_FORCE = 15;
	
	private Camera playerCamera;
	
	private float currentSpeed  = 0;
	private float currentStrafe = 0;
	
	
	public Player(Vector3f position, float rotX, float rotY, float rotZ) {
		super(new Model(OBJLoader.loadObjModel(MODEL_FILE)), position, rotX, rotY, rotZ, 1);
		this.addHitbox("main", new Vector3f(0,0,0), new Vector3f(2,3.25f,2));
		Main.hitboxes.add(this.getHitboxes());
		this.setCollision(true, this.getHitbox("main"));
		this.usesGravity();
		this.isDynamic();
	}
	
	public void setCamera(Camera camera) {
		this.playerCamera = camera;
	}
	
	public void update() {
		super.update();
		this.playerCamera.update();
	}
	
	protected void updateLogic() {
		this.move();
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
