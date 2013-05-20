import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * @author Yeshwanth Venkatesh
 * 
 * @version 3.0 6th December 2012
 * 
 */
public class Main {

	/**
	 * Global variables to store:
	 * 
	 * Target Image Directory,
	 * Tile Image Directory,
	 * Output Image Directory,
	 * Target Image Format,
	 * Output Image Format,
	 * Reuse Limit,
	 * Tile Dimensions
	 */
	private static String TARGET_IMG_DIR = "";
	private static String TILE_IMG_DIR = "";
	private static String OUTPUT_IMG_DIR = "";
	private static String TARGET_IMG_FORMAT = "";
	private static String OUTPUT_IMG_FOMRAT = "";
	private static Integer REUSE_LIMIT = null;
	private static Integer[] TILEDIMENSION = new Integer[2];

	// Image formats that are supported.
	private static final String[] IMAGE_EXTS = { "jpg", "jpeg", "bmp", "gif",
			"png", "tiff" };

	/**
	 * The main method performs the validation on the input arguments. It passes
	 * each arguments to a private method for validation.
	 * 
	 * @param String
	 *            Linux path to a file that contains the target image.
	 * @param Sting
	 *            Linux path to an existing directory that contains the tiles.
	 * 
	 * @param [Optional] String Linux path for the image file to be created.
	 * 
	 * @param [Optional] Number of times a tile can be reused
	 * 
	 * @throws Throwable
	 * 
	 */
	public static void main(String[] args) throws Throwable {

		// Checks if the correct number of arguments are provided.
		// Terminate if false.
		try {
			//Case 1: When user has provided:
			//		  1. Path to the target image.
			//        2. Path to the tile images directory.
			if (args.length == 2) {

				new Main().validateFilePath(args[0], IMAGE_EXTS);

				new Main().validateFolderPath(args[1], IMAGE_EXTS);

				new Main().validateTileDimension(Main.getTARGET_IMG_DIR(), Main
						.getTILE_IMG_DIR());

				setOUTPUT_IMG_DIR(genOutputFilename(args[0]));

				Main.setREUSE_LIMIT(100000);

				GenerateTileOutput.GenerateOutput();

			} 
			//Case 2: When user has provided:
			//		  1. Path to the target image.
			//        2. Path to the tile images directory.
			//        3. Path to the output image
			else if (args.length == 4 && args[2].equals("-o")) {

				new Main().validateFilePath(args[0], IMAGE_EXTS);

				new Main().validateFolderPath(args[1], IMAGE_EXTS);

				new Main().validateTileDimension(Main.getTARGET_IMG_DIR(), Main
						.getTILE_IMG_DIR());

				new Main().validateOutputPath(args[3], IMAGE_EXTS);

				Main.setREUSE_LIMIT(1000000);

				GenerateTileOutput.GenerateOutput();

			} 
			//Case 3: When user has provided:
			//		  1. Path to the target image.
			//        2. Path to the tile images directory.
			//        3. Reuse limit for a single tile image.
			else if (args.length == 4 && args[2].equals("-r")) {

				new Main().validateFilePath(args[0], IMAGE_EXTS);

				new Main().validateFolderPath(args[1], IMAGE_EXTS);

				new Main().validateTileDimension(Main.getTARGET_IMG_DIR(), Main
						.getTILE_IMG_DIR());

				new Main().validateRepeat(Integer.parseInt(args[3]));

				Main.setOUTPUT_IMG_DIR(genOutputFilename(args[0]));

				GenerateTileOutput.GenerateOutput();

			} 
			//Case 4: When user has provided:
			//		  1. Path to the target image.
			//        2. Path to the tile images directory.
			//        3. Path to the output image.
			//        4. Reuse limit for a single tile image.
			else if (args.length == 6 && args[2].equals("-o")
					&& args[4].equals("-r")) {

				new Main().validateFilePath(args[0], IMAGE_EXTS);

				new Main().validateFolderPath(args[1], IMAGE_EXTS);

				new Main().validateTileDimension(Main.getTARGET_IMG_DIR(), Main
						.getTILE_IMG_DIR());

				new Main().validateOutputPath(args[3], IMAGE_EXTS);

				new Main().validateRepeat(Integer.parseInt(args[5]));

				GenerateTileOutput.GenerateOutput();

			} 
			// For any other combination of invalid arguments
			// the program will report an error to the user
			// and will exit.
			else {
				System.err
						.println("Invalid input arguments.Please read the README file.");
				System.exit(0);
			}

		} catch (NumberFormatException e) {
			System.out
					.println("Repeat count parameter should be an unsigned integer value");
			System.exit(0);
		}

	}

	/**
	 * It validates the file path by checking if the file exists. The file
	 * should only have a jpg, jpeg, bmp, gif, png & tiff extensions. If the
	 * validation fails, the program is terminated.
	 * 
	 * @param String
	 *            Linux path to a file that contains the target image.
	 * @param String
	 *            Array Containing extensions that are supported.
	 * @throws IOException
	 * @return void
	 * 
	 */
	private void validateFilePath(String filePath, String[] ext)
			throws IOException {

		try {
			//Creating a new file instance.
			File dir = new File(filePath);

			String targetImageFormat;

			boolean isSupportedImage = false;
			//Check if the specified file exists
			if (dir.exists()) {
				//Create an iterator for image reader.
				ImageInputStream iis = ImageIO.createImageInputStream(dir);
				Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

				iis.flush();
				iis.close();
				
				//Check if the image has data to be read.
				//If not, then throw an error and terminate the
				//program.
				if (!readers.hasNext()) {
					System.out.println("Exception while reading target image."
							+ dir);
					System.exit(0);

				} 
				//Else, read the properties of the image
				else {
					ImageReader reader = readers.next();
					//Special Case:
					//Java Advanced Image Library reads TIFF image format
					//as "tif".
					//Therefore, if the a TIFF file is encountered
					//during the execution of the code then, set the
					//variable name as "tiff".
					if (reader.getFormatName().equals("tif")) {
						targetImageFormat = "tiff";
					} 
					//For any other format, use the string returned
					//by the Imagereader
					else {
						targetImageFormat = reader.getFormatName();
					}
					//Check if image format is one of the supported
					//legal format.
					for (int i = 0; i < ext.length; i++) {
						if (ext[i].equalsIgnoreCase(targetImageFormat)) {
							isSupportedImage = true;
							break;

						} else {
							isSupportedImage = false;
						}
					}
					//If the target image passes the validation
					//then set the required variables.
					if (isSupportedImage) {
						Main.setTARGET_IMG_DIR(filePath);
						Main.setTARGET_IMG_FORMAT(targetImageFormat
								.toLowerCase());
					} 
					//If the validation fails, then throw a message and
					//terminate the program.
					else {
						System.out.println("No file with " + ext[0] + " or "
								+ ext[1] + " or " + ext[2] + " or " + ext[3]
								+ " or " + ext[4] + " or " + ext[5]
								+ " was found in. " + dir);
						System.exit(0);
					}
				}
			} 
			//If the target image does not exists, then throw an appropriate
			//message and terminate the program.
			else {
				System.out.println("Target image does not exists in the path: "
						+ dir);
				System.exit(0);
			}
		} catch (Exception e) {
			System.out.println("Invalid Target Image path.");
			System.exit(0);
		}
	}

	/**
	 * It validates the directory path by checking if the directory exists. All
	 * files in that directory should only be of the supported format.
	 * 
	 * 
	 * If the validation fails terminate the program.
	 * 
	 * @param String
	 *            Linux path to a file that contains the target image.
	 * @param String
	 *            Array Containing extensions that are supported.
	 * @throws Throwable
	 * 
	 */
	private void validateFolderPath(String folderPath, final String[] ext)
			throws Throwable {
		try {
			// Creating a new file instance.
			File dir = new File(folderPath);

			// Check if the file is a directory.
			if (dir.isDirectory() == false) {
				System.out.println("Invalid Tile directory: " + folderPath);
				System.exit(0);
			}
			ArrayList<File> files = new ArrayList<File>();
			// Get the total number of files in that directory.
			int totalFiles = dir.listFiles().length;
			if (totalFiles == 0) {
				System.out.println("Tiles directory is empty: " + folderPath);
				System.exit(0);
			}
			// Add files into the ArrayList
			for (int i = 0; i < totalFiles; i++) {
				files.add(dir.listFiles()[i]);
			}			
			String tileImageFormat;
			boolean isSupportedImage = false;
			boolean first_tile_format = false;			
			//Get the image format of the first tile image.
			ImageInputStream iis = ImageIO.createImageInputStream(files.get(0));
			Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
			iis.flush();
			iis.close();			
			//Check if the tile exists or is readable.
			if (!readers.hasNext()) {
				System.out.println(" Something is wrong with the image format "
						+ dir);
				System.exit(0);
			} 
			//Read image properties
			else {
				ImageReader reader = readers.next();				
				//Special Case:
				//Java Advanced Image Library reads TIFF image format
				//as "tif".
				//Therefore, if the a TIFF file is encountered
				//during the execution of the code then, set the
				//variable name as "tiff".
				if (reader.getFormatName().equals("tif")) {
					tileImageFormat = "tiff";
				} 
				//For any other format, use the string returned
				//by the Imagereader.
				else {
					tileImageFormat = reader.getFormatName();
				}				
				//Checking if the first tile(within the tiles directory)
				// image format is one of the legal image formats.
				for (int y = 0; y < ext.length; y++) {
					if (ext[y].equalsIgnoreCase(tileImageFormat)) {
						first_tile_format = true;
						break;
					} else {
						first_tile_format = false;
					}
				}				
				//If validation for supported image
				//format fails, then terminate the program.
				if (!first_tile_format) {
					System.out.println("No Tile with " + ext[0] + " or "
							+ ext[1] + " or " + ext[2] + " or " + ext[3]
							+ " or " + ext[4] + " or " + ext[5]
							+ " was found in. " + dir);
					System.exit(0);

				}
				//Iterating over each file within the tiles
				//directory to check whether they all are of the
				//same format.
				for (int i = 0; i < files.size(); i++) {
					ImageInputStream tempIIS = ImageIO
							.createImageInputStream(files.get(i));
					Iterator<ImageReader> tempReaders = ImageIO
							.getImageReaders(tempIIS);

					tempIIS.flush();
					tempIIS.close();

					ImageReader tempReader = tempReaders.next();
					String temp_format = "";
					//Special Case:
					//Java Advanced Image Library reads TIFF image format
					//as "tif".
					//Therefore, if the a TIFF file is encountered
					//during the execution of the code then, set the
					//variable name as "tiff".
					if (tempReader.getFormatName().equals("tif")) {
						temp_format = "tiff";
					} 
					//For any other image format, use the string returned
					//by the Imagereader.
					else {
						temp_format = tempReader.getFormatName();
					}
					//Condition that checks if all the tiles
					//are of same format.
					if (tileImageFormat.equalsIgnoreCase(temp_format)) {
						isSupportedImage = true;

					} else {
						isSupportedImage = false;
						break;
					}
				}
			}			
			//If validation for supported image format
			//is passed, then set the tiles folder path.
			if (isSupportedImage) {
				Main.setTILE_IMG_DIR(folderPath);
			} 
			//else, display proper error message and
			//terminate the program.
			else {
				System.out
						.println("Please verify the path to the tiles image folder");
				System.out
						.println("All the images should be in the same format.");
				System.out.println("Allowed Image formats are :-");
				System.out.println("BMP    (bitmap image file)");
				System.out.println("GIF    (Graphics Interchange Format)");
				System.out.println("JPEG   (Joint Photographic Experts Group)");
				System.out.println("PNG    (Portable Network Graphics)");
				System.out.println("TIFF   (Tagged Image File Format)");
				System.exit(0);
			}
		}
		catch (Exception e) {
			System.out.println("Invalid Tiles Folder Path.");
			System.exit(0);
		}
	}

	/**
	 * Checks if all the tile dimensions (Width x Height) are divisisble by
	 * target images dimensions.
	 * 
	 * @param String
	 *            Linux path to a file that contains the target image.
	 * @param String
	 *            Linux path to an existing directory that contains the tiles.
	 * 
	 * @return void
	 */
	private void validateTileDimension(String targetPath, String tilePath)
			throws IOException {
		try {
			// Create a buffer of target image.
			BufferedImage targetBuffer = ImageIO.read(new File(targetPath));
			// Creating a new file instance.
			File tileDirectory = new File(tilePath);
			// Total number of tiles in directory
			int totalFile = tileDirectory.list().length;
			boolean isDimensionSet = false;
			// Check if the tile width and height are divisible by
			// target width and height.
			for (int i = 0; i < totalFile; i++) {
				BufferedImage tileBuffer = ImageIO.read(new File(tilePath + "/"
						+ tileDirectory.list()[i]));				
				//Check that tile width is a divisor of the target image width.
				//Check that tile height is a divisor of the target image height.
				if (((targetBuffer.getWidth() % tileBuffer.getWidth()) != 0)
						|| (targetBuffer.getHeight() % tileBuffer.getHeight() != 0)) {
					System.out
							.println("Incorrect Tile Dimensions: The width of each tile " +
									"must be a divisor of the width of the target image, " +
									"and the height of each tile must be a divisor of the " +
									"height of the target image.");
					tileBuffer.flush();
					System.exit(0);
				} else {
					//If dimensions of tiles are not yet initialized,
					//then set the tile dimensions.
					if (!isDimensionSet) {
						Main.setTILEDIMENSION(0, tileBuffer.getWidth());
						Main.setTILEDIMENSION(1, tileBuffer.getHeight());
						isDimensionSet = true;
					} 
					//Condition that checks if all the tiles have
					//same dimensions.
					else if (Main.getTILEDIMENSION(0) != tileBuffer
							.getWidth()
							&& Main.getTILEDIMENSION(1) != tileBuffer
									.getHeight()) {
						System.out
								.println("All the tiles should be of same dimensions");
						System.exit(0);
					}
					tileBuffer.flush();
				}
			}
			targetBuffer.flush();
		} catch (Exception e) {
			System.out.println("All Tile images should have same dimensions.");
			System.exit(0);
		}
	}

	/**
	 * It validates the file path by checking if the path exists.
	 * 
	 * @param String
	 *            Linux path to a file that contains the target image.
	 * @param String
	 *            Array Containing extensions that are supported.
	 * 
	 * @return void
	 */
	private void validateOutputPath(String folderPath, String[] ext) {
		try {
			// Creating a new file instance.
			File dir = new File(folderPath);
			File parentDir = new File(dir.getParent());
			// Get the name of the file in the abstract path.
			String outputImgName = dir.getName();			
			//Check the the parent directory exists
			if (parentDir.exists()) {
				//Check if the specified output name
				//contains a legal image suffix/extension
				if (outputImgName.lastIndexOf(".") != -1
						&& (outputImgName.endsWith(ext[0])
								|| outputImgName.endsWith(ext[1])
								|| outputImgName.endsWith(ext[2])
								|| outputImgName.endsWith(ext[3])
								|| outputImgName.endsWith(ext[4]) || outputImgName
								.endsWith(ext[5]))) {
					Main.setOUTPUT_IMG_DIR(folderPath);
					Main.setOUTPUT_IMG_FOMRAT(outputImgName.substring(
							outputImgName.lastIndexOf("."), outputImgName
									.length() - 1));
					return;
				}
				//Check if the the output name for mosaic
				//does not has any suffix.
				else if (outputImgName.lastIndexOf(".") == -1) {
					Main.setOUTPUT_IMG_DIR(folderPath);
					Main.setOUTPUT_IMG_FOMRAT("NO_EXT");
					return;
				} 
				//Check if output name contains an
				//unknown suffix.
				else {
					Main.setOUTPUT_IMG_DIR(folderPath);
					Main.setOUTPUT_IMG_FOMRAT("UNKNOWN");
					return;
				}
			} 
			//If parent directory does not exists, then
			//throw an appropriate error, and
			//terminate the program.
			else {
				System.out
						.println("Specified Directory for Output Image does not exists: "
								+ dir.getParent());
				System.exit(0);
			}
		} catch (Exception e) {
			System.out.println("Invalid Output Directory Name");
			System.exit(0);
		}
	}

	/**
	 * If the output folder is not provided, generate the output folder path and
	 * the output file name.
	 * 
	 * */
	private static String genOutputFilename(String arg)
			throws FileNotFoundException, IOException {
		//Create an instance file with target image path.
		File targetInput = new File(arg);		
		//Extract the parent directory of the target image.
		File parentDir = new File(targetInput.getParent());		
		//Get the image format of the target image.
		String imageFormat = Main.getTARGET_IMG_FORMAT();		
		//Variable to store the final output
		String finalOut = "";
		try {
			//Check if the target image exists.
			if (targetInput.exists()) {
				String outputDir = parentDir.getAbsolutePath() + "/";
				String targetFileName = targetInput.getName();
				//Check if it has a suffix/extension.
				if (targetFileName.lastIndexOf(".") != -1) {
					finalOut = outputDir
							+ targetFileName.substring(0, targetFileName
									.lastIndexOf(".")) + "_out." + imageFormat;
				}
				//else, if it does contain a suffix.
				else {
					finalOut = outputDir + targetFileName + "_out";
				}
			} 
			//if target image does not exists,
			//then throw an error and terminate the program.
			else {
				System.out
						.println("Specified Directory for Output Image does not exists: "
								+ targetInput.getParent());
				System.exit(0);
			}
		} catch (Exception e) {
			System.out.println("Error Generating output path for the Mosaic");
			System.exit(0);
		}
		//Set the target image format and return the
		//final output path.
		Main.setOUTPUT_IMG_FOMRAT(imageFormat);
		return finalOut;
	}

	/**
	 * When the reuse parameter is provided check if we could create the mosaic
	 * 
	 * */
	private void validateRepeat(int r_count) throws IOException {
		try {
			//Create a File instance of the target image.
			File targetImg = new File(Main.getTARGET_IMG_DIR());
			
			//Create a buffered image of the target image.
			BufferedImage target_image = ImageIO.read(targetImg);
			
			//Get the instance of the tile directory
			File tileDir = new File(Main.getTILE_IMG_DIR());
			
			//Check the number of cells in the target image.
			int num_cells = (target_image.getWidth() * target_image.getHeight())
					/ (Main.getTILEDIMENSION(0) * Main.getTILEDIMENSION(1));
			
			//Number of tile images available.
			int numTiles = tileDir.list().length;
			
			//Condition to check if we can create a photomosaic
			//with given repeat constraints.
			if (num_cells <= numTiles * r_count) {
				Main.setREUSE_LIMIT(r_count);
				target_image.flush();
			} 
			//If the validation fails the throw an error
			//and terminate the program.
			else {
				System.out
						.println("Not enough tiles to generate the Photomosaic with the repeat contraint "
								+ r_count);
				target_image.flush();
				System.exit(0);
			}
		} catch (Exception e) {
			System.out.println("Repeat Count should be an unsigned integer.");
		}
	}

	// Getter and setters for the attributes of the Main class.
	public static String getTARGET_IMG_DIR() {
		return TARGET_IMG_DIR;
	}

	public static void setTARGET_IMG_DIR(String tARGET_IMG_DIR) {
		TARGET_IMG_DIR = tARGET_IMG_DIR;
	}

	public static String getTILE_IMG_DIR() {
		return TILE_IMG_DIR;
	}

	public static void setTILE_IMG_DIR(String tILE_IMG_DIR) {
		TILE_IMG_DIR = tILE_IMG_DIR;
	}

	public static String getOUTPUT_IMG_DIR() {
		return OUTPUT_IMG_DIR;
	}

	public static void setOUTPUT_IMG_DIR(String oUTPUT_IMG_DIR) {
		OUTPUT_IMG_DIR = oUTPUT_IMG_DIR;
	}

	/**
	 * @return the rEUSE_LIMIT
	 */
	public static Integer getREUSE_LIMIT() {
		return REUSE_LIMIT;
	}

	/**
	 * @param rEUSE_LIMIT
	 *            the rEUSE_LIMIT to set
	 */
	public static void setREUSE_LIMIT(Integer rEUSE_LIMIT) {
		REUSE_LIMIT = rEUSE_LIMIT;
	}

	/**
	 * @return the tARGET_IMG_FORMAT
	 */
	public static String getTARGET_IMG_FORMAT() {
		return TARGET_IMG_FORMAT;
	}

	/**
	 * @param tARGET_IMG_FORMAT
	 *            the tARGET_IMG_FORMAT to set
	 */
	public static void setTARGET_IMG_FORMAT(String tARGET_IMG_FORMAT) {
		TARGET_IMG_FORMAT = tARGET_IMG_FORMAT;
	}

	/**
	 * @return the tARGET_TEMP_DIR
	 */
	public static Integer getTILEDIMENSION(int i) {
		return TILEDIMENSION[i];
	}

	/**
	 * @param tARGET_TEMP_DIR
	 *            the tARGET_TEMP_DIR to set
	 */
	public static void setTILEDIMENSION(int i, int value) {
		TILEDIMENSION[i] = value;
	}

	public static String getOUTPUT_IMG_FOMRAT() {
		return OUTPUT_IMG_FOMRAT;
	}

	public static void setOUTPUT_IMG_FOMRAT(String oUTPUTIMGFOMRAT) {
		OUTPUT_IMG_FOMRAT = oUTPUTIMGFOMRAT;
	}

}
