import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * @author Yeshwanth Venkatesh
 * 
 * @version 3.0 6th December 2012
 * 
 */

public class ProcessTarget {

	/**
	 * 
	 * Process the Target Image, calculate the CIELAB co-ordinates [L*,a,b] for
	 * each grid.
	 * 
	 * @return List of CIELAB co-ordinates [L*,a,b] for each grid
	 * 
	 * */
	public static List<KdTree.LAB> ProcessTargetFile() throws IOException {

		// Create a buffer of target image
		BufferedImage targetBuffer = ImageIO.read(new File(Main
				.getTARGET_IMG_DIR()));

		// List to store the LAB attributes of each part of the Target image
		List<KdTree.LAB> targetRGB = new ArrayList<KdTree.LAB>();

		// Dimensions of a Tile images (in pixels)
		int tileWidth = Main.getTILEDIMENSION(0);
		int tileHeight = Main.getTILEDIMENSION(1);

		// Total number of cells in Target image
		int totalGrid = createGrid(targetBuffer, tileWidth, tileHeight);

		// calculate the number of columns
		int columns = (int) Math.round(targetBuffer.getWidth() / tileWidth);

		int row = 0;
		int col = 0;

		//Starting Coordinates
		int x = 0;
		int y = 0;

		//To keep the track of number of columns
		//covered.
		int counter = 0;

		// for each grid calculate the CIELAB co-ordinates [L*,a,b]
		for (int i = 0; i < totalGrid; i++) {

			// Create buffered image of each grid in the target file
			BufferedImage box = targetBuffer.getSubimage(x, y, tileWidth,
					tileHeight);

			// List to store the Lab attributes, from the
			// average RGB values.
			ArrayList<Double> temp = ProcessTile.fromRGB(box);
			double l = temp.get(0);
			double a = temp.get(1);
			double b = temp.get(2);
			
			//Add the object of Target to the
			//to the list containing the Lab attributes of
			//all the sections within the target image.
			targetRGB.add(new Target(l, a, b));
			
			//Clear the current buffered image.
			box.flush();
			
			//Increment the counter to keep the
			//track of the columns covered so far.
			counter++;
			
			//If the current column is the last
			//column then, re-initialize the value
			//of the counter.
			if ((counter % columns) == 0) {
				counter = 0;
				col = counter % columns;
			} 
			//Else, calculate the next column value.
			else {
				col = counter % columns;
			}
			//Calculate the next starting point for the
			//next section in the target image.
			x = tileWidth * col;
			y = tileHeight * row;
			
			//if the current column is the last column
			//then go to the next row.
			if (col == (columns - 1)) {
				row++;
			}

			box.flush();

		}
		targetBuffer.flush();
		//return the final list of Target
		//Objects, contains its Lab attributes.
		return targetRGB;
	}

	/**
	 * Function to calculate the number of sections that
	 * we need create on the target image.
	 * 
	 * @param SourceImage
	 *            Target Image
	 * 
	 * @param tile_x
	 *            Tiles x co-ordinate
	 * 
	 * @param tile_y
	 *            Tiles y co-ordinate
	 * 
	 * @return int Number of grids in the Target Image
	 * 
	 * */
	public static int createGrid(BufferedImage sourceImage, int tile_x,
			int tile_y) {
		
		//Number of sections = Area of the target image/Area of a single tile.
		int grid = (int) Math.round((sourceImage.getHeight() * sourceImage
				.getWidth()) / (tile_x * tile_y));

		return grid;

	}

}
