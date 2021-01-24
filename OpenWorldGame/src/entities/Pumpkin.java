package entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import engineTester.Main;
import entities.entityFrameworks.Entity;

public class Pumpkin extends Entity {
	
	public static List<Entity> pumpkins = new ArrayList<Entity>();
	
	public Pumpkin(Vector3f pos) {
		this(pos, new Vector3f());
	}
	
	public Pumpkin(Vector3f pos, Vector3f rot) {
		super("pumpkinLow", new Vector3f(pos), new Vector3f(rot));
		this.usesGravity();
		this.addHitbox("main", new Vector3f(), new Vector3f(1.5f,1.5f,1.5f));
		this.setCollision(true, "main");
		this.isDynamic();
		Main.hitboxes.add(this.getHitboxes());
		pumpkins.add(this);
	}
	
	public void rayClick(int button, boolean state) {
		if (state) {
			if (button == 0) {
				new Pumpkin(Vector3f.add(this.getPosition(), new Vector3f(2,0,0), null));
			}
		}
	}
	
}
