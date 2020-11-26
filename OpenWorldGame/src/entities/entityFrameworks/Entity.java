package entities.entityFrameworks;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import engineTester.Main;
import models.Model;
import renderEngine.DisplayManager;
import renderEngine.OBJLoader;
import toolBox.Maths;

public class Entity {

	private static final float GRAVITY = -50;
	private static final float STEP_THRESHOLD = 0.5f;

	protected float dx = 0;
	protected float dy = 0;
	protected float dz = 0;
	protected float ySpeed = 0;

	protected boolean inAir = false;
	
	private Entity riding = null;
	private Vector3f ridingPos = null;
	
	private List<Hitbox> collisionBoxes = new ArrayList<Hitbox>();
	private List<Hitbox> hitboxes = new ArrayList<Hitbox>();
	private List<String> hbNames = new ArrayList<String>();
	
	private String modelName;
	
	private Model model;
	
	private Vector3f position;
	private Vector3f estimatedScale;
	
	private float rotX, rotY, rotZ;
	private float scale;
	private float dirArrowHeight;
	
	private boolean gravity = false;
	private boolean dynamic = false;


	
	// Takes Filename For Mesh and Materials File - No Texture Image
	public Entity(String model, Vector3f position, Vector3f rotation) {
		this(new Model(OBJLoader.loadObjModel(model)), position, rotation.x, rotation.y, rotation.z, 1);
	}
	
	// Takes Filename For Mesh and Materials File - No Texture Image
	public Entity(String model, Vector3f position, Vector3f rotation, float scale) {
		this(new Model(OBJLoader.loadObjModel(model)), position, rotation.x, rotation.y, rotation.z, scale);
	}
	
	// Takes Filename For Mesh and Materials File
	public Entity(String model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this(new Model(OBJLoader.loadObjModel(model)), position, rotX, rotY, rotZ, scale);
		this.modelName = model;
	}
	
	// Makes an Entity From a Pre-Made TexturedModel
	public Entity(Model model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.modelName = "MissingName";
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.estimatedScale = new Vector3f();
	}
	
	// Moves according to object specific method
	protected void updateLogic() {
		
	}
	
	// Stops object from colliding into other objects
	private void rebound() {
		// Fix this method. Have a rebound method for rebounding X Y & Z. Try to rebound the smallest distance, if it still hits something, rebound the second smallest, etc.
		float dx = 0;
		float dy = 0;
		float dz = 0;
		
		// Movable method?
		for (Entity entity : Main.colidables) {
			if (entity != this) {
				if (this.collidesWith(entity)) {
					List<Hitbox> boxes = this.exactCollision(entity);										// Tests every entity in Main.colidables;	Only tests 1 hitbox that it's coliding with
					Hitbox thisBox = boxes.get(0);
					Hitbox thatBox = boxes.get(1);
					Vector3f range = thisBox.getCollisionRange(thatBox);
					if (range.y <= STEP_THRESHOLD) {
						if (range.y != 0) {
							if (thisBox.getPosition().y >= thatBox.getPosition().y) {
								dy = range.y;
							} else {
								dy = -range.y;
							}
						}
						Vector3f ridingNewPos = new Vector3f(entity.getPosition());
						if (this.ridingPos != null) {
							Vector3f ridingRelPos = Maths.subVecs(ridingNewPos, this.ridingPos);
							dx += ridingRelPos.x;															// Potential problem with +='s and multiple collisions at once
							dy += ridingRelPos.y;
							dz += ridingRelPos.z;
						}
						this.riding = entity;
						this.ridingPos = ridingNewPos;
					} else if (range.x < range.y && range.x < range.z) {
						if (thisBox.getPosition().x >= thatBox.getPosition().x) {
							if (dx > 0) {
								dx += range.x;
							} else dx = range.x;
						} else {
							if (dx < 0) {
								dx += -range.x;
							} else dx = -range.x;
						}
					} else if (range.y < range.x && range.y < range.z) {
						if (thisBox.getPosition().y >= thatBox.getPosition().y) {
							if (dy > 0) {
								dy += range.y;
							} else dy = range.y;
						} else {
							if (dy < 0) {
								dy += -range.y;
							} else dy = -range.y;
						}
					} else if (range.z < range.x && range.z < range.y) {
						if (thisBox.getPosition().z >= thatBox.getPosition().z) {
							if (dz > 0) {
								dz += range.z;
							} else dz = range.z;
						} else {
							if (dz < 0) {
								dz += -range.z;
							} else dz = -range.z;
						}
					}
				}
			}
		}
		this.increasePosition(dx, dy, dz);
		if (this.riding != null) {
			if (!this.collidesWith(this.riding)) {
				this.riding = null;
				this.ridingPos = null;
			}
		}
		
	}
	
	private void fall() {
		ySpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();

		
		dy = ySpeed * ( DisplayManager.getFrameTimeSeconds());
		

		if (this.getPosition().y < Main.terrainCells.get(0).getHeightOfTerrain(this.getPosition().x, this.getPosition().z)) {
			ySpeed = 0;
			inAir = false;
			this.setHeight(Main.terrainCells.get(0).getHeightOfTerrain(this.getPosition().x, this.getPosition().z));
		}
	}
	
	public void usesGravity() {
		this.usesGravity(true);
	}
	
	public void usesGravity(boolean gravity) {
		this.gravity = gravity;
	}
	
	public void isDynamic() {
		this.isDynamic(true);
	}
	
	public void isDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	
	// Is Movable
	// Has some sort of moving method
	
	public void update() {
		this.updateLogic();
		
		if (this.gravity) {
			this.fall();
		}
		
		if (this.dynamic) {
			this.rebound();
		}
		
		this.increasePosition(dx, dy, dz);
	}
	
	public void rotate(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}
	
	public Model getModel() {return model;}

	public void setModel(Model model) {this.model = model;}

	public Vector3f getPosition() {return position;}

	public void setPosition(Vector3f position) {
		this.position = position;
		this.dirArrowHeight = this.position.y/4*3;
	}
	
	public void increasePosition(float dx, float dy, float dz) {
		this.position = Maths.addVecs(this.position, new Vector3f(dx,dy,dz));
		this.dirArrowHeight += dy;
	}

	public void increaseHeight(float dHeight) {this.increasePosition(0, dHeight, 0);}
	
	public void setHeight(float height) {
		this.position.y = height;
	}
	
	public Vector3f getRotation() {return new Vector3f(this.rotX, this.rotY, this.rotZ);}
	
	public void setRotation(Vector3f rotation) {
		this.rotX = rotation.x;
		this.rotY = rotation.y;
		this.rotZ = rotation.z;
	}

	public float getRotX() {return rotX;}

	public void setRotX(float rotX) {this.rotX = rotX;}

	public float getRotY() {return rotY;}

	public void setRotY(float rotY) {this.rotY = rotY;}

	public float getRotZ() {return rotZ;}

	public void setRotZ(float rotZ) {this.rotZ = rotZ;}

	public float getScale() {return scale;}

	public void setScale(float scale) {this.scale = scale;}
	
	public Vector3f getEstimatedScale() {return this.estimatedScale;}
	
	public float getDirArrowHeight() {return this.dirArrowHeight;}
	
	public void setDirArrowHeight(float relativeHeight) {this.dirArrowHeight = relativeHeight;}
	
	public void addHitbox(String name, Vector3f relativePosition, Vector3f scale) {
		if (this.hitboxes.size() <= 0) {
			this.dirArrowHeight = scale.y/4*3;
		}
		relativePosition.y += scale.y/2;
		hitboxes.add(new Hitbox(relativePosition, scale, this));
		hbNames.add(name);
		Vector3f tempScale = new Vector3f();
		for (Hitbox hitbox : this.hitboxes) {
			tempScale = Maths.addVecs(tempScale, hitbox.getScale());
		}
		this.estimatedScale = (Maths.divVecs(tempScale, new Vector3f(this.hitboxes.size(), this.hitboxes.size(), this.hitboxes.size())));
	}
	
	public Hitbox getHitbox(String name) {
		int hbID = this.hbNames.indexOf(name);
		if (hbID < 0) System.err.println("No hitbox of name '" + name + "'  associatated with " + this.modelName);
		return this.hitboxes.get(hbID);
	}
	
	public List<Hitbox> getHitboxes() {
		return this.hitboxes;
	}
	
	public List<Hitbox> getCollisionBoxes() {
		return this.collisionBoxes;
	}
	
	public String getHBName(Hitbox hitbox) {
		int hbID = this.hitboxes.indexOf(hitbox);
		return this.hbNames.get(hbID);
	}
	
	// Returns true if called entity collides with parameter entity at all
	public boolean collidesWith(Entity entity) {
		boolean collides = false;
		for (Hitbox hitbox : this.getCollisionBoxes()) {
			for (Hitbox hitbox2 : entity.getCollisionBoxes()) {
				if (hitbox.intersects(hitbox2)) {
					collides = true;
					break;
				}
			}
			if (collides) break;
		}
		return collides;
	}
	
	// Returns the hitbox of parameter entity that called entity collides with
	public Hitbox collidesAt(Entity entity) {
		Hitbox collide = null;
		for (Hitbox hitbox : this.hitboxes) {
			for (Hitbox hitbox2 : entity.getCollisionBoxes()) {
				if (hitbox.intersects(hitbox2)) {
					collide = hitbox2;
					break;
				}
			}
			if (collide != null) break;
		}
		return collide;
	}
	
	// Returns hitboxes of both entities that collide
	public List<Hitbox> exactCollision(Entity entity) {
		List<Hitbox> collides = new ArrayList<Hitbox>();
		for (Hitbox hitbox : this.hitboxes) {
			for (Hitbox hitbox2 : entity.getCollisionBoxes()) {
				if (hitbox.intersects(hitbox2)) {
					collides.add(hitbox);
					collides.add(hitbox2);
					break;
				}
			}
			if (collides.size() > 0) break;
		}
		return collides;
	}
	
	public void setCollision(boolean colidable, Hitbox box) {
		if (!this.collisionBoxes.contains(box)) {
			this.collisionBoxes.add(box);
		}
		if (!Main.colidables.contains(this)) {
			Main.colidables.add(this);
		}
	}
	
	public void setCollision(boolean colidable, String hitboxName) {
		Hitbox box = this.getHitbox(hitboxName);
		if (!this.collisionBoxes.contains(box)) {
			this.collisionBoxes.add(box);
		}
		if (!Main.colidables.contains(this)) {
			Main.colidables.add(this);
		}
	}
	
//	private void reboundX() {
//		
//	}
//	
//	private void reboundY() {
//		
//	}
//	
//	private void reboundZ() {
//		
//	}
	
}


