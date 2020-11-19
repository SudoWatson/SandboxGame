package renderEngine.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engineTester.Main;
import entities.entityFrameworks.Entity;
import entities.entityFrameworks.Hitbox;
import models.RawModel;
import shaders.debug.DebugShader;
import toolBox.Maths;

public class DebugRenderer {

	private final Vector3f HITBOX_COLOR = new Vector3f(0,1,1);
	private final Vector3f HITBOX_DIR_COLOR = new Vector3f(1,0,0);
	private final Vector3f COORD_COLOR = new Vector3f(1,1,0);
	
	private final RawModel prism;
	private final RawModel arrow;
	private final RawModel coord;
	
	private int vertexCount;
	
	private DebugShader shader;
	// Heads Up Coord lines dont do well with negative position because negative numbers different
	public DebugRenderer(DebugShader shader, Matrix4f projectionMatrix) {
		float[] hitboxPositions = {1, -1, -1, 1, -1, 1, -1, -1, 1, -1, -1, -1, 1, 1, -1, 1, 1, 1, -1, 1, 1, -1, 1, -1};
		int[] hitboxIndices = {0,1,1,2,2,3,3,0,4,5,5,6,6,7,7,4,0,4,1,5,2,6,3,7};
		float[] lookingPositions = {0,0,0,1.25f,0,0};
		int[] lookingIndices = {0,1};
		float[] linePositions = {0,-1,0,0,1,0};
		int[] lineIndices = {0,1};
		
		vertexCount = hitboxIndices.length;

		prism = Main.loader.loadHitbox(hitboxPositions, hitboxIndices);
		arrow = Main.loader.loadHitbox(lookingPositions,lookingIndices);
		coord = Main.loader.loadHitbox(linePositions,lineIndices);
		
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(List<List<Hitbox>> hitboxGroups) {
		shader.start();
		
		// Renders Hitboxes
		shader.loadColor(HITBOX_COLOR);
		GL30.glBindVertexArray(prism.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		
		for (List<Hitbox> hitboxes : hitboxGroups) {
			for (Hitbox hitbox : hitboxes) {
				prepareHB(hitbox);
				GL11.glDrawElements(GL11.GL_LINES, vertexCount, GL11.GL_UNSIGNED_INT, 0);  // Draws hitbox
			}
		}
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		
		// Renders Looking Arrows
		shader.loadColor(HITBOX_DIR_COLOR);
		GL30.glBindVertexArray(arrow.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		
		for (List<Hitbox> hitboxes : hitboxGroups) {
			Hitbox workingBox = null;
			float volume = 0;
			for (Hitbox hitbox : hitboxes) {
				float thisVolume = hitbox.getScale().x*hitbox.getScale().y*hitbox.getScale().z;
				if (thisVolume > volume) {
					volume = thisVolume;
					workingBox = hitbox;
				}
			}
			prepareArrow(workingBox, volume);
			GL11.glDrawElements(GL11.GL_LINES, 2, GL11.GL_UNSIGNED_INT, 0);  // Draws looking arrow
		}
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		
		// Renders Coordinate Lines
		shader.loadColor(COORD_COLOR);
		GL30.glBindVertexArray(coord.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		
// Create lines on each coordinate line
		Vector3f centerPos = new Vector3f(Main.player.getPosition());

//		if (centerPos.x%10 >= 5) {
//			centerPos.x = centerPos.x+(10-centerPos.x%10);
//		} else if (centerPos.x%10 < 5) {
//				centerPos.x = centerPos.x-(centerPos.x%10);
//		}
//		if (centerPos.z%10 >= 5) {
//			centerPos.z = centerPos.z+(10-centerPos.x%10);
//		} else if (centerPos.x%10 < 5) {
//				centerPos.z = centerPos.z-(centerPos.x%10);
//		}

		centerPos.x -= centerPos.x%10;
		centerPos.y = 0;
		centerPos.z -= centerPos.z%10;
		
		if (hitboxGroups.size() > 0) {
			for (int i = 0; i <2; i++) {
				for (int c = 0; c < 10; c++) {
					Vector3f thisCoord = new Vector3f(Maths.addVecs(centerPos, new Vector3f(c,0,i*10)));
					prepareCoords(thisCoord);
					GL11.glDrawElements(GL11.GL_LINES, 2, GL11.GL_UNSIGNED_INT, 0);  // Draws coord
				}
				
				for (int c = 0; c < 10; c++) {
					Vector3f thisCoord = new Vector3f(Maths.addVecs(centerPos, new Vector3f(i*10,0,c)));
					prepareCoords(thisCoord);
					GL11.glDrawElements(GL11.GL_LINES, 2, GL11.GL_UNSIGNED_INT, 0);  // Draws coord
				}
			}
		}
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		
		shader.stop();
	}
	
	private void prepareHB(Hitbox hitbox) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(hitbox.getPosition(),hitbox.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	private void prepareArrow(Hitbox hitbox, float volume) {
		Entity parent = hitbox.getParent();
		Vector3f trans = new Vector3f(parent.getPosition());
		Vector3f rot = new Vector3f(parent.getRotation());
		Vector3f scale = new Vector3f(parent.getEstimatedScale());
		//trans.x += scale.x;
		Matrix4f transformationMatrix = Maths.createRayTransformationMatrix(trans,rot,scale,parent.getDirArrowHeight());
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	private void prepareCoords(Vector3f position) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(position,new Vector3f(1,100,1));
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
}




