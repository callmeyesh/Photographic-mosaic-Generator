import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Yeshwanth Venkatesh
 * 
 * @version 3.0 6th December 2012
 * 
 */

public class processMosaic {

	/**
	 * It takes in the following parameter and calls the processBuilder to pass
	 * the values to the shell script to create the final output
	 * 
	 * @param String
	 *            Path to the source image.
	 * @param String
	 * 			  Path to the destination where image needs to be saved.
	 * 
	 */
	public static void jpegToImage(String temp_img, String destination)
			throws IOException, InterruptedException {

		// get the current location to create a valid path for the shell script
		File currentLocation = new File(processMosaic.class
				.getProtectionDomain().getCodeSource().getLocation().getPath());

		// create a path to the shell script
		String executeShellScript = currentLocation.getParentFile().toString()
				+ "/convert.sh";

		String output = new String(destination);

		// pass values as a command line argument to execute the shell script
		ProcessBuilder pb = new ProcessBuilder("/bin/sh", executeShellScript,
				temp_img, output);

		pb.redirectErrorStream(true);

		// start the process
		Process p = pb.start();

		BufferedReader br = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		
		String line = null;

		while ((line = br.readLine()) != null) {
			//System.out.println(line);
		}
		
		br.close();

		p.destroy();
	}

	/**
	 * It takes in the following parameter and calls the processBuilder to pass
	 * the values to the shell script to create the final output
	 * 
	 * @param String
	 *            All the tiles needed to create an image
	 * @param Integer
	 * 			  Width of the output image
	 * @param Integer
	 *            Height of the output image.
	 * @param String
	 * 			  Path to the destination where image needs to be saved.
	 * 
	 */
	public static void photoMosaic(String req_tiles, int width, int height,
			String destination) throws IOException, InterruptedException {

		// get the current location to create a valid path for the shell script
		File currentLocation = new File(processMosaic.class
				.getProtectionDomain().getCodeSource().getLocation().getPath());

		// create a path to the shell script
		String executeShellScript = currentLocation.getParentFile().toString()
				+ "/generateMosaic.sh";

		String tiles = new String(req_tiles);

		String widthOutput = Integer.toString(width);

		String heightOutput = Integer.toString(height);

		String output = new String(destination);

		// pass values as a command line argument to execute the shell script
		ProcessBuilder pb = new ProcessBuilder("/bin/sh", executeShellScript,
				tiles, widthOutput, heightOutput, output);

		pb.redirectErrorStream(true);

		// start the process
		Process p = pb.start();

		BufferedReader br = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line = null;

		while ((line = br.readLine()) != null) {
			//System.out.println(line);
		}
		 
		br.close();

		p.destroy();
	}
}
