package toolBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

public class Convert {
	
	public static Matrix4f stringToMatrix4f(String input) {
		List<Float> floats = new ArrayList<Float>();
		for (String str : input.split(" ")) {
			floats.add(Float.parseFloat(str));
		}
		return floatsToMatrix4f(floats);
	}
	
	public static Matrix4f floatsToMatrix4f(float[] input) {
		List<Float> floats = new ArrayList<Float>();
		for (Float num : input) {
			floats.add(num);
		}
		
		return floatsToMatrix4f(floats);
	}
	
	public static Matrix4f floatsToMatrix4f(List<Float> floats) {
		Matrix4f matrix = new Matrix4f();

		matrix.m00 = floats.get(0);
		matrix.m10 = floats.get(1);
		matrix.m20 = floats.get(2);
		matrix.m30 = floats.get(3);

		matrix.m01 = floats.get(4);
		matrix.m11 = floats.get(5);
		matrix.m21 = floats.get(6);
		matrix.m31 = floats.get(7);

		matrix.m02 = floats.get(8);
		matrix.m12 = floats.get(9);
		matrix.m22 = floats.get(10);
		matrix.m32 = floats.get(11);

		matrix.m03 = floats.get(12);
		matrix.m13 = floats.get(13);
		matrix.m23 = floats.get(14);
		matrix.m33 = floats.get(15);
		
		return matrix;
		
	}
	
	public static Matrix4f[] stringToMatrix4fArray(String input) {
		float[] floats = stringToFloatArray(input);
		int matrixCount = floats.length/16;
		if (floats.length%16 > 0) System.err.println("Unable to produce Matrix array. Improper count of floats");
		
		Matrix4f[] out = new Matrix4f[matrixCount];
		for (int i = 0; i < matrixCount; i++) {
			out[i] = floatsToMatrix4f(Arrays.copyOfRange(floats, i*16, (i+1)*16));
		}
		return out;
	}
	
	public static float[] stringToFloatArray(String input) {
		float[] out = new float[input.split(" ").length];
		int index = 0;
		for (String num : input.split(" ")) {
			out[index++] = Float.parseFloat(num);
		}
		return out;
	}
	
	public static int[] stringToIntArray(String input) {
		int[] out = new int[input.split(" ").length];
		int index = 0;
		for (String num : input.split(" ")) {
			out[index++] = Integer.parseInt(num);
		}
		return out;
	}
	
}
