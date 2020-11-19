//package entities.entityFrameworks;
//
//import org.lwjgl.util.vector.Vector3f;
//
//import entities.entityFrameworks.Entity;
//
//
//public class Hitbox {
//
//	private Vector3f min = new Vector3f(0,0,0);
//	private Vector3f max = new Vector3f(0,0,0);
//	private Vector3f position;
//	private Vector3f scale;
//	
//	private Entity parent;
//	
//	
//	public Hitbox (Vector3f absolutePosition, Vector3f scale, Entity parent) {
//		this.scale = new Vector3f(scale.x/2, scale.y/2, scale.z/2);
//		hitBoxSetPositonTESTINGPURPOSE(absolutePosition);
//		this.parent = parent;
//	}
//	
//	
//	
//	public boolean intersects(Hitbox box) {
//		return  (this.min.x < box.max.x && this.max.x > box.min.x) &&
//				(this.min.y <= box.max.y && this.max.y >= box.min.y) &&
//				(this.min.z < box.max.z && this.max.z > box.min.z);
//	}
//	
//	public Vector3f getCollisionRange(Hitbox box) {
//		Vector3f range = new Vector3f();
//
//		range.x = this.max.x - box.min.x;
//		if (box.max.x - this.min.x < range.x) {
//			range.x = box.max.x - this.min.x;
//		}
//		range.y = this.max.y - box.min.y;
//		if (box.max.y - this.min.y < range.y) {
//			range.y = box.max.y - this.min.y;
//		}
//		range.z = this.max.z - box.min.z;
//		if (box.max.z - this.min.z < range.z) {
//			range.z = box.max.z - this.min.z;
//		}
//		
//		return range;
//	}
//	
//	public float getDistanceTo(Hitbox box) {
//		float distance = 0;
//		Vector3f range = this.getCollisionRange(box);
//		distance = (float) Math.sqrt((range.x * range.x) + (range.y * range.y));
//		distance = (float) Math.sqrt((range.z * range.z) + (distance * distance));
//		return distance;
//	}
//	
//	public Vector3f getMax() {
//		return this.max;
//	}
//	
//	public Vector3f getMin() {
//		return this.min;
//	}
//	
//	public Vector3f getScale() {
//		return this.scale;
//	}
//	
//	public Vector3f getPosition() {
//		return this.position;
//	}
//	
//	public Entity getParent() {
//		return this.parent;
//	}
//	
//	private void increaseOnlyPosition(Vector3f dPosition) {
//		this.position.x += dPosition.x;
//		this.position.y += dPosition.y;
//		this.position.z += dPosition.z;
//	}
//	
//	private void increaseMin(Vector3f dPosition) {
//		this.min.x += dPosition.x;
//		this.min.y += dPosition.y;
//		this.min.z += dPosition.z;
//	}
//	
//	private void increaseMax(Vector3f dPosition) {
//		this.max.x += dPosition.x;
//		this.max.y += dPosition.y;
//		this.max.z += dPosition.z;
//	}
//	
//	public void increasePosition(Vector3f dPosition) {
//		increaseOnlyPosition(dPosition);
//		increaseMax(dPosition);
//		increaseMin(dPosition);
//	}
//	
//	public void hitBoxSetPositonTESTINGPURPOSE(Vector3f position) {
//		this.position = position;
//		this.position.y += this.scale.y;
//		this.max.x = position.x + this.scale.x;
//		this.max.y = position.y + this.scale.y;
//		this.max.z = position.z + this.scale.z;
//		this.min.x = position.x - this.scale.x;
//		this.min.y = position.y - this.scale.y;
//		this.min.z = position.z - this.scale.z;
//	}
//	
//}