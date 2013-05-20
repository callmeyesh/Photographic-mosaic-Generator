/**
 * @author Yeshwanth Venkatesh
 * 
 * @version 3.0 6th December 2012
 * 
 */

public class Tile extends KdTree.LAB {
	//Reuse limit for each tile
	private int reuseLimit = 0;
	//Tilename
	private String tileName;

	//Creating a Tile Object by calling the constructor
	public Tile(double x, double y, double z, String tileName, int reuseLimit) {
		super(x, y, z);
		this.tileName = tileName;
		this.setReuseLimit(reuseLimit);
	}
	
	// Getter and Setter Methods for each attribute
	public int getReuseLimit() {
		return reuseLimit;
	}

	public int setReuseLimit(int reuseLimit) {
		this.reuseLimit = reuseLimit;
		return reuseLimit;
	}

	public String getTile_name() {
		return (Main.getTILE_IMG_DIR() + "/" + tileName);
	}

	public void setTile_name(String tile_name) {
		this.tileName = tile_name;
	}

	/**
	 * {@inheritDoc}
	 */
	//Overriding the toString method to convert the
	//required attributes to a String.
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(" tileName=").append(tileName);
		builder.append(" Limit=").append(reuseLimit);
		return builder.toString();
	}

}
