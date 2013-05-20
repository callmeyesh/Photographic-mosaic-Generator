import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

/**
 * @author Yeshwanth Venkatesh
 * 
 * @version 2 25th October 2012
 * 
 */

/**
 * Reference
 * 
 * http://en.wikipedia.org/wiki/Color_difference
 * 
 * http://en.wikipedia.org/wiki/L*a*b*
 * 
 * http://web.jfet.org/color-conversions.html
 * 
 */

public class ProcessTile {

	/**
	 * 
	 * Process each tile iamge. Calculate the CIELAB co-ordinates [L*,a,b] for
	 * each tile
	 * 
	 * @return list List of CIELAB co-ordinates [L*,a,b] for each tile
	 * 
	 * */

	public static List<KdTree.LAB> ProcessTileFiles() throws IOException {

		File tilesFolder = new File(Main.getTILE_IMG_DIR());

		String[] tilesName = tilesFolder.list();

		// Initialise reuseLimit
		int reuseLimit = 0;

		// Number of the tile
		int numberOfTiles = tilesFolder.list().length;

		// Creating a temporary buffer of tile image.
		BufferedImage currentTile = null;

		List<KdTree.LAB> tileRGB = new ArrayList<KdTree.LAB>();

		// For each tile calculate the CIELAB co-ordinates [L*,a,b]
		// Then insert it in to the List
		for (int i = 0; i < numberOfTiles; i++) {
			if (tilesName[i].equals(".DS_Store") == false) {
				currentTile = ImageIO.read(new File(tilesFolder
						.getAbsolutePath() + "/" + tilesName[i]));
				
				ArrayList<Double> temp = fromRGB(currentTile);
				double l = temp.get(0);
				double a = temp.get(1);
				double b = temp.get(2);

				tileRGB.add(new KdTree.LAB(l, a, b, tilesName[i], reuseLimit));

				currentTile.flush();
			}
		}

		return tileRGB;
	}

	/**
	 * It takes the Image calculated the RGB of that Image. Generate CIELAB
	 * color coordinates of that Image.
	 * 
	 * @return ArryaList List of CIELAB co-ordinates [L*,a,b]
	 */
	public static ArrayList<Double> fromRGB(BufferedImage source) {
		float red = 0;
		float green = 0;
		float blue = 0;

		int count = 0;
		int pixel = 0;
		
		//List to store the average RGB values
		ArrayList<Float> RGB = new ArrayList<Float>();

		// Loop over each pixel of the file.
		// Perform bitwise operation on the pixel
		// to obtain Red, Green and Blue colors.
		// 1. 16 bit right shift to obtain Red color.
		// 2. 8 bit right shift to obtain Greeb color.
		// 3. No shift required for Blue color.
		for (int i = 0; i < source.getWidth(); i++) {
			for (int j = 0; j < source.getHeight(); j++) {
				pixel = source.getRGB(i, j);
				red += pixel >> 16 & 0xFF;
				green += pixel >> 8 & 0xFF;
				blue += pixel & 0xFF;
				count++;
			}
		}
		//Calculate the average RGB values
		//individually.
		red /= count;
		green /= count;
		blue /= count;
		
		//Add each value to the ArrayList
		RGB.add(red);
		RGB.add(green);
		RGB.add(blue);
		
		//Pass it on to the function to extract Lab
		//attributes and the return the final result.
		return (ProcessTile.toLAB(RGB));

	}
	
	private static final double N = 4.0 / 29.0;

	/**
	 * Function the applies the forward transformation on
	 * the average RGB attributes of an image.
	 * 
	 * Take an Arraylist of RGB and converts into CIELAB co-ordinates [L*,a,b]
	 * 
	 * @return Arraylist List of CIELAB co-ordinates [L*,a,b]
	 * 
	 * */
	public static ArrayList<Double> toLAB(ArrayList<Float> rGB) {
		ArrayList<Double> LAB = new ArrayList<Double>();
		double l = f(rGB.get(1));
		double L = 116.0 * l - 16.0;
		double a = 500.0 * (f(rGB.get(0)) - l);
		double b = 200.0 * (l - f(rGB.get(2)));
		LAB.add(L);
		LAB.add(a);
		LAB.add(b);
		return LAB;
	}
	
	//Function f(t): the performs two types of matches
	//to convert the points to Lab colorspace.
	private static double f(double x) {
		//match in value
		if (x > 216.0 / 24389.0) {
			return Math.cbrt(x);
		}
		//match in slope
		else {
			return (841.0 / 108.0) * x + N;
		}
	}
}
