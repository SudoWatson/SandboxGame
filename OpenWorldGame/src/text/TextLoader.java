package text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import models.RawModel;
import renderEngine.Loader;
import text.font.FontData;

public class TextLoader {
	
	private static final int DESIRED_PADDING = 3;  // Not entirely sure what this does yet
	private static final int FONT_UNIT = 35;  // An inputed font of this is equivelant to a screen font of 1
	
	public static RawModel loadText(TextBox textBox) {
		
		// Sets up information for Raw Model
		List<Vector2f> vertices = new ArrayList<Vector2f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		float[] verticesArray = null;
		float[] texturesArray = null;
		
		// Gets some information that will be needed
		float aspectRatio = ((float) (Display.getWidth())) / ((float) (Display.getHeight()));

		float fontSize = textBox.getFont().getFontSize()/FONT_UNIT;

		float maxWidth = textBox.getScale().x*Display.getWidth();
		@SuppressWarnings("unused")
		float maxHeight = textBox.getScale().y*Display.getHeight();
		String text = textBox.getString() + " ";
		FontData fd = textBox.getFont().getFontData();
		Vector4f padding = fd.getPadding();
		int paddingTop		= (int) padding.x;
		int paddingBot		= (int) padding.z;
		int paddingLeft		= (int) padding.y;
		int paddingRight	= (int) padding.w;
		
		int paddingWidth = paddingLeft + paddingRight;
		int paddingHeight = paddingTop + paddingBot;
		
		int lineHeight = (int) ((fd.getLineHeight() - paddingHeight) * fontSize);
		int imageWidth = fd.getImageWidth();
		

		List<Integer> charIDs  = Arrays.stream(fd.getCharID()).boxed().collect(Collectors.toList());
		
		String word = "";
		
		float wordLength = 0;
		Vector2f cursor = new Vector2f(0,0);
		
		// Gathers all the information for each character in the string
		for (int i = 0; i < text.length(); i++) {
			char character = text.charAt(i);
			int index		= charIDs.indexOf((int) character);
			if (index < 0) System.err.println("Character '" + character + "' with ASCII value '" + ((int) character) + "' not found in font " + textBox.getFont().getFontName());
			double xAdv		= (fd.getxAdv()[index] - paddingWidth);
			
			if (character == ' ' || (int) character == 10) {
				if ((cursor.x+wordLength) > maxWidth) {  // Converts character space to screen space
					cursor.x = 0;
					cursor.y -= lineHeight;
				}
				
				for (int j = 0; j < word.length(); j++) {
					character		= word.charAt(j);
					index			= charIDs.indexOf((int) character);
					float xTex		= (fd.getX()[index] + (paddingLeft - DESIRED_PADDING) / imageWidth);
					float yTex		= (fd.getY()[index] + (paddingTop - DESIRED_PADDING) / imageWidth);
					float width		= (fd.getWidth()[index] - (paddingWidth - (2*DESIRED_PADDING)));
					float height	= (fd.getHeight()[index] - (paddingHeight - (2*DESIRED_PADDING)));
					float xOff		= (fd.getxOff()[index] + paddingLeft - DESIRED_PADDING) * fontSize;
					float yOff		= (fd.getyOff()[index] + paddingTop - DESIRED_PADDING) * fontSize;
						  xAdv		= (fd.getxAdv()[index] - paddingWidth) * fontSize / aspectRatio;
					
					float top = (cursor.y-yOff);
					float left = (cursor.x+xOff);
					float right = (float) (left + width * fontSize / aspectRatio);
					float bottom = (top - height * fontSize);

					// Triangle 1
					vertices.add(new Vector2f(left,top));
					vertices.add(new Vector2f(right,bottom));
					vertices.add(new Vector2f(right,top));
					
					textures.add(new Vector2f(xTex,yTex));
					textures.add(new Vector2f(xTex+width,yTex+height));
					textures.add(new Vector2f(xTex+width,yTex));
					
					// Triangle 2
					vertices.add(new Vector2f(left,top));
					vertices.add(new Vector2f(left,bottom));
					vertices.add(new Vector2f(right,bottom));
					
					textures.add(new Vector2f(xTex,yTex));
					textures.add(new Vector2f(xTex,yTex+height));
					textures.add(new Vector2f(xTex+width,yTex+height));
					
					cursor.x += xAdv + paddingWidth/2;
					
				}
				
				// Adds the space or new line
				character = text.charAt(i);
				if (character == ' ') {  // Space
					cursor.x += 23 * fontSize;
				}
				if ((int) character == 10) {  // '\n' New Line
					cursor.x = 0;
					cursor.y -= lineHeight;
				}
				
				word = "";
				wordLength = 0;
				
			} else {
				word += character;
				wordLength += xAdv + paddingWidth/2;
			}
			
			
		}
		
		int vertexPointer = 0;
		int texturePointer = 0;
		verticesArray = new float[vertices.size()*2];
		texturesArray = new float[vertices.size()*2];
		for (int i = 0; i < vertices.size(); i++) {
			verticesArray[vertexPointer++] = pixelToOGL(vertices.get(i).x);
			verticesArray[vertexPointer++] = pixelToOGL(vertices.get(i).y);
			
			texturesArray[texturePointer++] = textures.get(i).x/imageWidth;
			texturesArray[texturePointer++] = textures.get(i).y/imageWidth;
		}
		
		return Loader.loadToVAO(verticesArray, texturesArray);
	}
	
	private static float pixelToOGL(float value) {
		return (value/(Display.getWidth()/2))-1;
	}
	
}
