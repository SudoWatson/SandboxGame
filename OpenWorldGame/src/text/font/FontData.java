package text.font;

import org.lwjgl.util.vector.Vector4f;

public class FontData {

	private int texture;
	private final int[] charID, x, y, width, height, xOff, yOff, xAdv;
	private final Vector4f padding;
	private final int lineHeight, imageWidth;
	
	
	public FontData(int[] charID, int[] x, int[] y, int[] width, int[] height, int[] xOff, int[] yOff, int[] xAdv, Vector4f padding, int lineHeight, int imageWidth, int texture) {
		super();
		this.charID = charID;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.xOff = xOff;
		this.yOff = yOff;
		this.xAdv = xAdv;
		this.padding = padding;
		this.lineHeight = lineHeight;
		this.imageWidth = imageWidth;
		this.texture = texture;
	}

	public int[] getCharID() {
		return charID;
	}

	public int[] getX() {
		return x;
	}

	public int[] getY() {
		return y;
	}

	public int[] getWidth() {
		return width;
	}

	public int[] getHeight() {
		return height;
	}

	public int[] getxOff() {
		return xOff;
	}

	public int[] getyOff() {
		return yOff;
	}

	public int[] getxAdv() {
		return xAdv;
	}

	public Vector4f getPadding() {
		return padding;
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getTextureID() {
		return texture;
	}
	
	
	
}
