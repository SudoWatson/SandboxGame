package toolBox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import debug.DebugLine;
import entities.entityFrameworks.Camera;

public class Maths {
	
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f transMatrix = new Matrix4f();
		transMatrix.setIdentity();
		Matrix4f.translate(translation, transMatrix, transMatrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), transMatrix, transMatrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), transMatrix, transMatrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), transMatrix, transMatrix);
		Matrix4f.scale(new Vector3f(scale,scale,scale), transMatrix, transMatrix);
		return transMatrix;
	}
	
	public static Matrix4f createTransformationmatrix(Vector2f translation, Vector2f scale) {
		Matrix4f transMatrix = new Matrix4f();
		transMatrix.setIdentity();
		Matrix4f.translate(translation, transMatrix, transMatrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), transMatrix, transMatrix);
		return transMatrix;
	}
	
	public static Matrix4f createTransformationmatrix(Vector2f translation) {
		Matrix4f transMatrix = new Matrix4f();
		transMatrix.setIdentity();
		Matrix4f.translate(translation, transMatrix, transMatrix);
		return transMatrix;
	}
	
	public static Matrix4f createRayTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale, float yOffset) {
		translation.y+=yOffset;
		Matrix4f transMatrix = new Matrix4f();
		transMatrix.setIdentity();
		Matrix4f.translate(translation, transMatrix, transMatrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1,0,0), transMatrix, transMatrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), new Vector3f(0,1,0), transMatrix, transMatrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.z), new Vector3f(0,0,1), transMatrix, transMatrix);
		Matrix4f.scale(new Vector3f(scale.x,0,0), transMatrix, transMatrix);
		return transMatrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f scale) {
		Matrix4f transMatrix = new Matrix4f();
		transMatrix.setIdentity();
		Matrix4f.translate(translation, transMatrix, transMatrix);
		Matrix4f.scale(scale, transMatrix, transMatrix);
		return transMatrix;
	}
	
	public static Matrix4f createTransformationMatrix(DebugLine debugLine) {
		Vector3f scale = Maths.subVecs(debugLine.end, debugLine.start);
		Vector3f translation = new Vector3f(debugLine.start);
		Matrix4f transMatrix = new Matrix4f();
		transMatrix.setIdentity();
		Matrix4f.translate(translation, transMatrix, transMatrix);
		Matrix4f.scale(scale, transMatrix, transMatrix);
		return transMatrix;
	}

	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1,0,0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0,1,0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
	public static Vector3f subVecs(Vector3f vec1, Vector3f vec2) {
		Vector3f retVec = new Vector3f();
		retVec.x = vec1.x-vec2.x;
		retVec.y = vec1.y-vec2.y;
		retVec.z = vec1.z-vec2.z;
		return retVec;
	}
	
	public static Vector3f addVecs(Vector3f vec1, Vector3f vec2) {
		Vector3f retVec = new Vector3f();
		retVec.x = vec1.x+vec2.x;
		retVec.y = vec1.y+vec2.y;
		retVec.z = vec1.z+vec2.z;
		return retVec;
	}
	
	public static Vector3f multVecs(Vector3f vec1, Vector3f vec2) {
		Vector3f retVec = new Vector3f();
		retVec.x = vec1.x*vec2.x;
		retVec.y = vec1.y*vec2.y;
		retVec.z = vec1.z*vec2.z;
		return retVec;
	}
	
	public static Vector3f divVecs(Vector3f vec1, Vector3f vec2) {
		Vector3f retVec = new Vector3f();
		retVec.x = vec1.x/vec2.x;
		retVec.y = vec1.y/vec2.y;
		retVec.z = vec1.z/vec2.z;
		return retVec;
	}
	
	public static Vector3f calcNormal(Vector3f vertex0, Vector3f vertex1, Vector3f vertex2) {
		Vector3f tangentA = Vector3f.sub(vertex1, vertex0, null);
		Vector3f tangentB = Vector3f.sub(vertex2, vertex0, null);
		Vector3f normal = Vector3f.cross(tangentA, tangentB, null);
		normal.normalise();
		return normal;
	}
	
	public static float clamp(float value, float min, float max){
		return Math.max(Math.min(value, max), min);
	}
	
	public static float blend(float value1, float value2, float blendFactor) {
		return ((value2-value1)*blendFactor) + value1;
	}
	
	public static Vector3f blend(Vector3f vector1, Vector3f vector2, float blendFactor) {
		return new Vector3f(blend(vector1.x,vector2.x,blendFactor),blend(vector1.y,vector2.y,blendFactor),blend(vector1.z,vector2.z,blendFactor));
	}
	
	public static float getDistance(Vector3f pos1, Vector3f pos2) {
		float distance = (float) Math.sqrt(Math.pow(pos2.x-pos1.x, 2) + Math.pow(pos2.y-pos1.y, 2));
		distance = (float) Math.sqrt(Math.pow(distance, 2) + Math.pow(pos2.z-pos1.z, 2));
		return distance;
	}
	
}
