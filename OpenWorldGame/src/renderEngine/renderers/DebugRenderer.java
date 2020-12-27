package renderEngine.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import debug.Debug;
import debug.DebugLine;
import debug.DebugPoint;
import engineTester.Main;
import entities.entityFrameworks.Entity;
import entities.entityFrameworks.Hitbox;
import models.RawModel;
import renderEngine.Loader;
import shaders.debug.DebugShader;
import toolBox.Maths;

public class DebugRenderer {

	private final Vector3f HITBOX_COLOR = new Vector3f(0,1,1);
	private final Vector3f HITBOX_DIR_COLOR = new Vector3f(1,0,0);
	private final Vector3f COORD_COLOR = new Vector3f(1,1,0);
	private final Vector3f CORNER_COLOR = new Vector3f(0,0,1);
	private final Vector3f SQUARE_COLOR = new Vector3f(1,0,0);
	private final Vector3f DEBUG_LINE_COLOR = new Vector3f(1,0,0);

	private final RawModel hitBoxModel;
	private final RawModel pointModel;
	private final RawModel arrow;
	private final RawModel coord;
	private final RawModel sqare;
	private final RawModel dLine;
	
	private final int coordLineCount = 15;
	
	private int vertexCount;
	
	private DebugShader shader;
	// Heads Up Coord lines dont do well with negative position because negative numbers different
	public DebugRenderer(DebugShader shader, Matrix4f projectionMatrix) {	//this		this
		float[] cubePositions = {1, -1, -1, 1, -1, 1, -1, -1, 1, -1, -1, -1, 1, 1, -1, 1, 1, 1, -1, 1, 1, -1, 1, -1};
		int[] hitboxIndices = {0,1,1,2,2,3,3,0,4,5,5,6,6,7,7,4,0,4,1,5,2,6,3,7};
		int[] pointIndices = {0,1,2,0,2,3,4,6,5,4,7,6,0,4,5,0,4,5,0,5,1,1,5,6,1,6,2,2,6,7,2,7,3,3,7,4,3,4,0}; //3740
		float[] lookingPositions = {0,0,0,1.25f,0,0};
		int[] lookingIndices = {0,1};
		float[] linePositions = {0,-1,0,0,1,0};
		int[] lineIndices = {0,1};
		float[] squarePositions = {0.5f,0,0.5f,0.5f,0,-0.5f,-0.5f,0,-0.5f,-0.5f,0,0.5f};
		int[] squareIndices = {0,1,1,2,2,3,3,0};
		float[] dLinePositions = {0,0,0,1,1,1};
		int[] dLineIndices = {0,1};
		
		vertexCount = hitboxIndices.length;

		hitBoxModel = Loader.loadHitbox(cubePositions, hitboxIndices);
		pointModel = Loader.loadHitbox(cubePositions, pointIndices);
		arrow = Loader.loadHitbox(lookingPositions,lookingIndices);
		coord = Loader.loadHitbox(linePositions,lineIndices);
		sqare = Loader.loadHitbox(squarePositions,squareIndices);
		dLine = Loader.loadHitbox(dLinePositions,dLineIndices);
		
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	// Renders Hitboxes
	public void render() {
		shader.start();
		
		// Renders Hitboxes
		if (Debug.showHitboxes) {
			shader.loadColor(HITBOX_COLOR);
			GL30.glBindVertexArray(hitBoxModel.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			
			for (List<Hitbox> hitboxes : Main.hitboxes) {
				for (Hitbox hitbox : hitboxes) {
					prepareCube(hitbox.getPosition(), hitbox.getScale());
					GL11.glDrawElements(GL11.GL_LINES, vertexCount, GL11.GL_UNSIGNED_INT, 0);  // Draws hitbox
				}
			}
			
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
			
			// Renders Directional Arrows
			shader.loadColor(HITBOX_DIR_COLOR);
			GL30.glBindVertexArray(arrow.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			
			for (List<Hitbox> hitboxes : Main.hitboxes) {
				Hitbox workingBox = null;
				float volume = 0;
				for (Hitbox hitbox : hitboxes) {
					float thisVolume = hitbox.getScale().x*hitbox.getScale().y*hitbox.getScale().z;
					if (thisVolume > volume) {
						volume = thisVolume;
						workingBox = hitbox;
					} else if (workingBox == null) {
						workingBox = hitbox;
					}
				}
				prepareArrow(workingBox, volume);
				GL11.glDrawElements(GL11.GL_LINES, 4, GL11.GL_UNSIGNED_INT, 0);  // Draws looking arrow
			}
			
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
		}
		

		// Renders Coordinate Lines
		if (Debug.showCoordLines) {
			// Renders Verticle Coordinate Lines
			shader.loadColor(COORD_COLOR);
			GL30.glBindVertexArray(coord.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			
			Vector3f centerPos = new Vector3f(Main.player.getPosition());
			
			if (centerPos.x >= 0) 	centerPos.x -= centerPos.x%coordLineCount;
			else 					centerPos.x -= centerPos.x%coordLineCount + coordLineCount;
			centerPos.y = 0;
			if (centerPos.z >= 0) 	centerPos.z -= centerPos.z%coordLineCount;
			else 					centerPos.z -= centerPos.z%coordLineCount + coordLineCount;
			
			for (int i = 0; i <2; i++) {
				for (int c = 0; c < coordLineCount; c++) {
					Vector3f thisCoord = new Vector3f(Maths.addVecs(centerPos, new Vector3f(c,0,i*coordLineCount)));
					if (thisCoord.x%coordLineCount != 0) {
						prepareCoords(thisCoord);
						GL11.glDrawElements(GL11.GL_LINES, 2, GL11.GL_UNSIGNED_INT, 0);  // Draws coord
					}
				}
				// Adds the final corner line	  vvv
				for (int c = 0; c < coordLineCount+1; c++) {
					Vector3f thisCoord = new Vector3f(Maths.addVecs(centerPos, new Vector3f(i*coordLineCount,0,c)));
					if (thisCoord.z%coordLineCount != 0) {
						prepareCoords(thisCoord);
						GL11.glDrawElements(GL11.GL_LINES, 2, GL11.GL_UNSIGNED_INT, 0);  // Draws coord
					}
				}
			}
			
			shader.loadColor(CORNER_COLOR);
			for (int i = -1; i < 3; i ++) {
				for (int j = -1; j < 3; j++) {
					Vector3f thisCoord = new Vector3f(centerPos);
					thisCoord.x += i*coordLineCount;
					thisCoord.z += j*coordLineCount;
					prepareCoords(thisCoord);
					GL11.glDrawElements(GL11.GL_LINES, 2, GL11.GL_UNSIGNED_INT, 0);
				}
			}
			
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);

			// Renders Horizontal Coordinate Squares
			shader.loadColor(SQUARE_COLOR);
			GL30.glBindVertexArray(sqare.getVaoID());
			GL20.glEnableVertexAttribArray(0);

			centerPos.x += coordLineCount/2 + 0.5f;
			centerPos.y = Main.player.getPosition().y;
			centerPos.z += coordLineCount/2 + 0.5f;
			if (centerPos.y >= 0) 	centerPos.y -= centerPos.y%coordLineCount;
			else 					centerPos.y -= centerPos.y%coordLineCount + coordLineCount;

			for (int i = 0; i < 2*coordLineCount; i++) {
				float height = Math.round(centerPos.y) + i;
				Vector3f finalPos = new Vector3f(centerPos);
				finalPos.y = height;
				prepareSquare(finalPos);
				GL11.glDrawElements(GL11.GL_LINES, 8, GL11.GL_UNSIGNED_INT, 0);
			}
			
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
		}
		

		// Renders debug arrows
		if (Debug.showDebugLines) {
			shader.loadColor(DEBUG_LINE_COLOR);
			GL30.glBindVertexArray(dLine.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			for (DebugLine line : DebugLine.DebugLines) {
				prepareLine(line);
				GL11.glDrawElements(GL11.GL_LINES, 2, GL11.GL_UNSIGNED_INT, 0);
			}
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
		}
		

		// Renders debug points
		if (Debug.showDebugLines) {
			GL30.glBindVertexArray(pointModel.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			for (DebugPoint point : DebugPoint.DebugPoints) {
				shader.loadColor(point.color);
				prepareCube(point.pos, new Vector3f(0.1f,0.1f,0.1f));
				GL11.glDrawElements(GL11.GL_TRIANGLES, pointModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
		}
		
		
		
		shader.stop();
	}
	
	// Prepares a Hitbox to be rendered
	private void prepareCube(Vector3f pos, Vector3f scale) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(pos, scale);
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	// Prepares directional arrows
	private void prepareArrow(Hitbox hitbox, float volume) {
		Entity parent = hitbox.getParent();
		Vector3f trans = new Vector3f(parent.getPosition());
		Vector3f rot = new Vector3f(parent.getRotation());
		Vector3f scale = new Vector3f(parent.getEstimatedScale());
		//trans.x += scale.x;
		Matrix4f transformationMatrix = Maths.createRayTransformationMatrix(trans,rot,scale,parent.getDirArrowHeight());
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	// Prepares verticle coordinate lines
	private void prepareCoords(Vector3f position) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(position,new Vector3f(1,100,1));
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	// Prepares square coordinate lines
	private void prepareSquare(Vector3f position) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(position,new Vector3f(coordLineCount,1,coordLineCount));
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	// Prepares debug lines
	private void prepareLine(DebugLine line) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(line);
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
}




