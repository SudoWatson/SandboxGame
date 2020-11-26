package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {  // DisplayManager because it manages a display. It is not a display itself
	
	private static final float ASPEC_RATIO = 16f/9f;
	private static final int HEIGHT = 720;
	private static final int WIDTH = (int) (HEIGHT*ASPEC_RATIO);
	private static final int FPS_CAP = 120;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay() {
		
		ContextAttribs attribs = new ContextAttribs(3,2).withForwardCompatible(true).withProfileCore(true);
		//									  3,2 for OpenGL             Misc. Attributes
		//										Version 3.2             I don't know about
		
		try {
			
			Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT));
			Display.create(new PixelFormat(), attribs);  // Applies attributes
			Display.setTitle("Game");
			
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);  // Where to render relative to the window
		// (0,0,WIDTH/2, HEIGHT/2) would render on bottom half of window

		Mouse.setGrabbed(true);
		lastFrameTime = getCurrentTime();
	}
	
	public static void updateDisplay() {
		Display.sync(FPS_CAP);  // Updates display at max of FPS_CAP
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f;
		lastFrameTime = currentFrameTime;
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}
	
	
	public static void closeDisplay() {
		Display.destroy();  // Closes display
	}
	
	private static long getCurrentTime() {
		return Sys.getTime()*1000 / Sys.getTimerResolution();
	}
	
}
