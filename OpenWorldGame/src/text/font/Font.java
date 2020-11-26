package text.font;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class Font {
	
	public static final int REGULAR = 0;
	public static final int GLOW = 1;
	public static final int SHADOW = 2;
	
	public static final int NORMAL = 0;
	public static final int BOLD = 1;
	public static final int ITALIC = 2;
	public static final int BOLD_ITALIC = 3;
	
	private final float FONT_SIZE;
	private final float FONT_TYPE;
	private final float lineHeight = 0.03f;
	private final String FONT_NAME;
	private final Vector3f COLOR;
	
	private final FontData fontData;
	
	private List<Integer> effects;
	
	public Font(String fontFile, Vector3f color, float fontSize, int fontType) {
		this.FONT_TYPE = fontType;
		if (this.FONT_TYPE == 1) {
			this.FONT_NAME = fontFile+"_Bold";
		} else if (this.FONT_TYPE == 2) {
			this.FONT_NAME = fontFile+"_Italic";
		} else if (this.FONT_TYPE == 3) {
			this.FONT_NAME = fontFile+"_Bold_Italic";
		} else {
			this.FONT_NAME = fontFile+"_Regular";
		}
		this.COLOR = color;
		this.FONT_SIZE = fontSize;
		this.effects = new ArrayList<Integer>();
		this.fontData = FontLoader.loadFont(this.FONT_NAME);
	}
	
	public void applyEffect(int effect) {
		if (!this.effects.contains(effect)) {
			effects.add(effect);
		}
	}
	
	public void removeEffect(int effect) {
		if (this.effects.contains(effect)) {
			effects.remove(effects.indexOf(effect));
		}
	}
	
	public float getFontSize() {
		return this.FONT_SIZE;
	}
	
	public String getFontName() {
		return this.FONT_NAME;
	}
	
	public Vector3f getColor() {
		return this.COLOR;
	}
	
	public FontData getFontData() {
		return this.fontData;
	}
	
	public double getLineHeight() {
		return this.lineHeight;
	}
	
}
