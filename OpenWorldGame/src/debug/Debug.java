package debug;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import text.TextBox;
import text.font.Fonts;

public class Debug {


	public static boolean showFPS =  false;
	public static boolean showHitboxes =  false;
	public static boolean showCoordLines = false;
	public static boolean showDebugLines = false;
	
	private static double lastTime = 0;
	private static double currentTime = 0;
	private static float FPS = 0;

	private static TextBox fpsTextBox = new TextBox("", Fonts.monospaced, new Vector2f(0,2), new Vector2f(0.5f, 1));
	private static TextBox debugInfo = new TextBox(
			"F2: Show     F3: Hide\n" + 
			"F#+I:   Debug List\n" +  
			"F#+H:   Hitboxes\n" + 
			"F#+C:   Coordinate Lines\n" + 
			"F#+L:   Debug Lines\n" + 
			"F#+F:   FPS Counter",
			Fonts.monospaced, new Vector2f(0,1.9f), new Vector2f(0.5f, 1));
	
	
	public static String test;
	
	public static void update() {
		// ********** Update Keystrokes ********** \\
		if (Keyboard.isKeyDown(Keyboard.KEY_F2)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_I) ) debugInfo.show();
			if (Keyboard.isKeyDown(Keyboard.KEY_H)) showHitboxes = true;
			if (Keyboard.isKeyDown(Keyboard.KEY_C)) showCoordLines = true;
			if (Keyboard.isKeyDown(Keyboard.KEY_L)) showDebugLines = true;
			if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
				showFPS = true;
				fpsTextBox.show();
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_F3)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_I) ) debugInfo.hide();
			if (Keyboard.isKeyDown(Keyboard.KEY_H)) showHitboxes = false;
			if (Keyboard.isKeyDown(Keyboard.KEY_C)) showCoordLines = false;
			if (Keyboard.isKeyDown(Keyboard.KEY_L)) showDebugLines = false;
			if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
				showFPS = false;
				fpsTextBox.hide();
			}
		}
		
		// ********** Update FPS ********** \\
		if (showFPS) {
			currentTime = System.nanoTime();
			FPS++;
			if (currentTime-lastTime >= 1e+9) {
				FPS /= ((currentTime-lastTime)/1e+9);
				fpsTextBox.setText("FPS: " + (Math.round(FPS*100)/100.0));  // Rounds FPS to 2 decimal places
				FPS = 0;
				lastTime = currentTime;
			}
		}
	}
	
}
