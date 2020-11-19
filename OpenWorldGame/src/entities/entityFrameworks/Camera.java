package entities.entityFrameworks;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import entities.Player;

public class Camera {
	
	private final float MAX_ZOOM = 40;
	
	private float distanceFromPlayer = 10;
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(0,0,0);
	private float pitch = 0;
	private float yaw = 0;
	private float roll = 0;
	private Player player;
	
	
	public Camera(Player player) {
		this.player = player;
		this.player.setCamera(this);
	}
	
	public void move() {  // Runs every frame
		checkInputs();
		calculateZoom();
		calculateAngles();
		calculateCameraPosition(horizontalDistance(), verticalDistance());
		this.yaw = 180-(player.getRotY() + angleAroundPlayer);
	}
	
	private void checkInputs() {

		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1)) {
			this.distanceFromPlayer = 0;
			this.pitch = 0;
		}
	}
	
	public Vector3f getPosition() {
		return position;
	}
	public float getPitch() {
		return pitch;
	}
	public float getYaw() {
		return yaw;
	}
	public float getRoll() {
		return roll;
	}
	
	public int getCameraStyle() {
		int style = 0;
		if (this.distanceFromPlayer == 0) {
			style = 1;
		} else {
			style = 3;
		}
		return style;
	}
	
	private float horizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float verticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.02f;
		distanceFromPlayer -= zoomLevel;
		if (distanceFromPlayer < 0) distanceFromPlayer = 0;
		else if (distanceFromPlayer > MAX_ZOOM) distanceFromPlayer = MAX_ZOOM;
	}
	
	private void calculateAngles() {
		float pitchChange = Mouse.getDY() * 0.1f;
		this.pitch -= pitchChange;

		if (this.pitch > 90) this.pitch = 90;
		if (this.pitch < -90) this.pitch = -90;
	}

	private void calculateCameraPosition(float horizDist, float vertDist) {
		float theta = player.getRotY();// + angleAroundPlayer;
		float xOff = (float) (horizDist * Math.sin(Math.toRadians(theta)));
		float zOff = (float) (horizDist * Math.cos(Math.toRadians(theta)));
		float yOff = 2.8f;
		position.x = player.getPosition().x - xOff;
		position.z = player.getPosition().z - zOff;
		position.y = player.getPosition().y + vertDist + yOff;
	}
	
}
