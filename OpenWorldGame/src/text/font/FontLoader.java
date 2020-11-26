package text.font;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.util.vector.Vector4f;

import renderEngine.Loader;

public class FontLoader {
	
	public static FontData loadFont(String fontFile) {
		int[] charID, x, y, width, height, xOff, yOff, xAdv;
		Vector4f padding = null;
		int lineHeight = 100;
		int imageWidth = 512;
		
		
		FileReader fontFileReader = null;
		try {
			fontFileReader = new FileReader(new File("res/fonts/"+fontFile+".fnt"));
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't load file res/fonts/" + fontFile + ".fnt");
			e.printStackTrace();
		}
		
		BufferedReader fontReader = new BufferedReader(fontFileReader);
		String line;
		String[] currentLine;
		
		try {
			while (true) {
				line = fontReader.readLine();
				if (line.startsWith("info")) {
					currentLine = line.split("\\D+");  // Takes only the numbers
					padding = new Vector4f(Integer.parseInt(currentLine[8]), Integer.parseInt(currentLine[9]), Integer.parseInt(currentLine[10]), Integer.parseInt(currentLine[11]));
				} else if (line.startsWith("common")) {
					currentLine = line.split("\\D+");  // Takes only the numbers
					lineHeight = Integer.parseInt(currentLine[1]);
					imageWidth = Integer.parseInt(currentLine[3]);
				} else if (line.startsWith("chars")) {
					break;
				}
			}
			currentLine = line.split(" ");
			int characters = Integer.parseInt(currentLine[1].split("=")[1]);

			charID	= new int[characters];
			x		= new int[characters];
			y		= new int[characters];
			width	= new int[characters];
			height	= new int[characters];
			xOff	= new int[characters];
			yOff	= new int[characters];
			xAdv	= new int[characters];
			
			for (int i = 0; i < characters; i++) {	// Rotate through all character data
				line = fontReader.readLine();
				if (!line.startsWith("char ")) {
					break;
				}
				
				// Sets all character data
				currentLine = line.split("\\D+");  // Takes only the numbers
				charID[i]	= Integer.parseInt(currentLine[1]);
				x[i]		= Integer.parseInt(currentLine[2]);
				y[i]		= Integer.parseInt(currentLine[3]);
				width[i]	= Integer.parseInt(currentLine[4]);
				height[i]	= Integer.parseInt(currentLine[5]);
				xOff[i]		= Integer.parseInt(currentLine[6]);
				yOff[i]		= Integer.parseInt(currentLine[7]);
				xAdv[i]		= Integer.parseInt(currentLine[8]);
			}
			
			return new FontData(charID, x, y, width, height, xOff, yOff, xAdv, padding, lineHeight, imageWidth, Loader.loadFontMap(fontFile));
		} catch (IOException e) {
			System.err.println("Error reading from file " + "res/fonts/"+fontFile+".fnt");
			e.printStackTrace();
		}
		System.err.println("This shouldn't be shown. Something messed up when loading " + "res/fonts/"+fontFile+".fnt" + " In FontLoader class");
		return new FontData(null, null, null, null, null, null, null, null, null, 0, 0, 0);
	}
	
}
