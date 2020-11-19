package entities.entityFrameworks;

import org.lwjgl.util.vector.Vector3f;

import entities.entityFrameworks.Entity;
import toolBox.Maths;


public class Hitbox {

	private Vector3f min = new Vector3f(0,0,0);
	private Vector3f max = new Vector3f(0,0,0);
	private Vector3f relPos;
	private Vector3f scale;
	
	private Entity parent;
	
	
	public Hitbox (Vector3f relativePosition, Vector3f scale, Entity parent) {
		this.scale = new Vector3f(scale.x/2, scale.y/2, scale.z/2);
		this.relPos = relativePosition;
		this.parent = parent;
		calculatePosition();
	}
	
	
	
	public boolean intersects(Hitbox box) {
		this.calculatePosition();
		box.calculatePosition();
		
		return  (this.min.x < box.max.x && this.max.x > box.min.x) &&
				(this.min.y <= box.max.y && this.max.y >= box.min.y) &&
				(this.min.z < box.max.z && this.max.z > box.min.z);
	}
	
	public Vector3f getCollisionRange(Hitbox box) {
		this.calculatePosition();
		box.calculatePosition();
		
		Vector3f range = new Vector3f();

		range.x = this.max.x - box.min.x;
		if (box.max.x - this.min.x < range.x) {
			range.x = box.max.x - this.min.x;
		}
		range.y = this.max.y - box.min.y;
		if (box.max.y - this.min.y < range.y) {
			range.y = box.max.y - this.min.y;
		}
		range.z = this.max.z - box.min.z;
		if (box.max.z - this.min.z < range.z) {
			range.z = box.max.z - this.min.z;
		}
		
		return range;
	}
	
	public float getDistanceTo(Hitbox box) {
		this.calculatePosition();
		box.calculatePosition();
		
		float distance = 0;
		Vector3f range = this.getCollisionRange(box);
		distance = (float) Math.sqrt((range.x * range.x) + (range.y * range.y));
		distance = (float) Math.sqrt((range.z * range.z) + (distance * distance));
		return distance;
	}
	
	public Vector3f getMax() {
		this.calculatePosition();
		return this.max;
	}
	
	public Vector3f getMin() {
		this.calculatePosition();
		return this.min;
	}
	
	public Vector3f getScale() {
		return this.scale;
	}
	
	public Vector3f getPosition() {
		this.calculatePosition();
		return Maths.addVecs(this.relPos, this.parent.getPosition());
	}
	
	public Entity getParent() {
		return this.parent;
	}
	
	public void calculatePosition() {
		Vector3f position = this.parent.getPosition();
		this.max.x = position.x + this.scale.x;
		this.max.y = position.y + 2*this.scale.y;
		this.max.z = position.z + this.scale.z;
		this.min.x = position.x - this.scale.x;
		this.min.y = position.y;
		this.min.z = position.z - this.scale.z;
	}
	
}