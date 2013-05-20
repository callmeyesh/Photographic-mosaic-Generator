/**
 * @author Yeshwanth Venkatesh
 * 
 * @version 3.0 6th December 2012
 * 
 */

public class Target extends KdTree.LAB {

	//Represents a part within the target image.
	//Where each part has the dimensions same as that
	//of a Tile.
	//x, y, z represents the LAB attributes respectively.
	public Target(double x, double y, double z) {
		super(x, y, z);
	}
	
}
