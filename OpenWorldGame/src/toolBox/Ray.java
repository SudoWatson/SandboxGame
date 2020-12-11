package toolBox;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.entityFrameworks.Camera;
import entities.entityFrameworks.Entity;
import entities.entityFrameworks.Hitbox;

public class Ray {
	
	private Camera cam;
	
	private Vector3f pos;
	private Vector3f end;
	private Vector3f off;
	private Vector3f dir;
	
	private Vector4f projectionVector;
	
	private Matrix4f projMat;
	private Matrix4f viewMat;
	
	private float maxLength = 20f;
	
	private Entity intersects = null;
	
	public Ray(Camera cam, Matrix4f projection) {
		this.cam = cam;
		this.pos = this.cam.getPosition();
		this.end = new Vector3f();
		this.projMat = projection;
		this.projectionVector = toEyeCoords(new Vector4f(0,0,-1,1));
		this.viewMat = Maths.createViewMatrix(this.cam);
		this.update();
	}
	
	public void castTo(Entity entity) {
		if (testCollision(entity)) {
			if (intersects == null) {
				intersects = entity;
			} else {
				if (Maths.getDistance(this.pos, entity.getPosition()) < Maths.getDistance(this.pos, intersects.getPosition())) {
					intersects = entity;
				}
			}
		}
	}
	
	private boolean testCollision(Entity entity) {
		// Line AABB collision thanks to SketchpunkLabs @ 'youtube.com/watch?v=4h-jlOBsndU' !
		if (Maths.getDistance(this.pos, entity.getPosition()) > this.maxLength) return false;
		
		Hitbox box = entity.getHitbox("main");
		float temp;
		
		// Projects the X position to the ray
		float xMin = (box.getMin().x - this.pos.x) / this.off.x;
		float xMax = (box.getMax().x - this.pos.x) / this.off.x;
		
		if (xMax < xMin) {  // Swaps the 2 if needed
			temp = xMax;
			xMax = xMin;
			xMin = temp;
		}
		
		// Projects the Y position to the ray
		float yMin = (box.getMin().y - this.pos.y) / this.off.y;
		float yMax = (box.getMax().y - this.pos.y) / this.off.y;
		
		if (yMax < yMin) {
			temp = yMax;
			yMax = yMin;
			yMin = temp;
		}
		
		// Projects the Z position to the ray
		float zMin = (box.getMin().z - this.pos.z) / this.off.z;
		float zMax = (box.getMax().z - this.pos.z) / this.off.z;
		
		if (zMax < zMin) {
			temp = zMax;
			zMax = zMin;
			zMin = temp;
		}

		// Find the closest projected positions to the object
		float min = (xMin > yMin) ? xMin : yMin;
		float max = (xMax < yMax) ? xMax : yMax;
		
		// If not in bounds, not collision
		if (xMin > yMax || yMin > xMax) return false;
		if (min > zMax || zMin > max) return false;
		
		// Sets proper min/max if necessary
		if (zMin > min) min = zMin;
		if (zMax < max) max = zMax;
		
		
		return true;
	}
	
	public void update() {
		this.viewMat = Maths.createViewMatrix(this.cam);
		convertCoords();
		this.off = Maths.multVecs(this.dir, new Vector3f(this.maxLength,this.maxLength,this.maxLength));
		this.end = Vector3f.add(this.off, this.pos, null);
		if (intersects != null) {
			if (Mouse.next()) {
				if (Mouse.getEventButton() > -1) {
					//if (Mouse.isButtonDown(Mouse.getEventButton())) {
						intersects.rayClick(Mouse.getEventButton(), Mouse.getEventButtonState());
					//}
				}
			}
			else intersects.rayHover();
			
			intersects = null;
		}
	}
	
	private void convertCoords() {
		this.dir = toWorldCoords(projectionVector);
	}

	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = Matrix4f.invert(this.projMat, null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(this.viewMat, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}
	
	public Vector3f getDir() {return this.dir;}
	
	public Vector3f getPos() {return this.pos;}
	
	public Vector3f getEnd() {return this.end;}
	
}
