import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Yeshwanth Venkatesh
 * 
 * @version 3.0 6th December 2012
 * 
 */

/**
 * Reference
 * 
 * http://en.wikipedia.org/wiki/K-d_tree
 * 
 * http://en.wikipedia.org/wiki/Nearest_neighbour_search
 * 
 * http://www.autonlab.com/
 * autonweb/14665/version/2/part/5/data/moore-tutorial.pdf
 * 
 * http://en.wikipedia.org/wiki/Color_difference
 * 
 * http://en.wikipedia.org/wiki/L*a*b*
 * 
 */


public class KdTree<TREE extends KdTree.LAB> {

	public int k = 3;

	public static TreeNode root = null;

	/**
	 * 
	 * @param list
	 *            of LABs.
	 */
	public KdTree(List<LAB> list) {
		root = createNode(list, k, 0);
	}

	// Each AXIS in the Tree
	public static final int AXIS_L = 0;
	public static final int AXIS_A = 1;
	public static final int AXIS_B = 2;

	// Compare the value in AXIS_L
	private static final Comparator<LAB> COMPARE_L = new Comparator<LAB>() {

		public int compare(LAB obj1, LAB obj2) {
			if (obj1.l < obj2.l)
				return -1;
			if (obj1.l > obj2.l)
				return 1;
			return 0;
		}
	};

	// Compare the value in AXIS_A
	private static final Comparator<LAB> COMPARE_A = new Comparator<LAB>() {

		public int compare(LAB obj1, LAB obj2) {
			if (obj1.a < obj2.a)
				return -1;
			if (obj1.a > obj2.a)
				return 1;
			return 0;
		}
	};

	// Compare the value in AXIS_B
	private static final Comparator<LAB> COMPARE_B = new Comparator<LAB>() {

		public int compare(LAB obj1, LAB obj2) {
			if (obj1.b < obj2.b)
				return -1;
			if (obj1.b > obj2.b)
				return 1;
			return 0;
		}
	};

	/**
	 * Create node from list of LABs.
	 * 
	 * @param list
	 *            of LAB values.
	 * @param k
	 *            of the tree.
	 * @param depth
	 *            depth of the node.
	 * @return node created.
	 */
	private static TreeNode createNode(List<LAB> LABValues, int k, int depth) {
		// Check if the List is empty
		if (LABValues == null || LABValues.size() == 0)
			return null;

		int axis = depth % k;

		// Sort the values based on the AXIS
		if (axis == AXIS_L)
			Collections.sort(LABValues, COMPARE_L);
		else if (axis == AXIS_A)
			Collections.sort(LABValues, COMPARE_A);
		else
			Collections.sort(LABValues, COMPARE_B);

		// Get Median to decide the root
		int medianIndex = LABValues.size() / 2;

		TreeNode node = new TreeNode(k, depth, LABValues.get(medianIndex));

		if (LABValues.size() > 0) {
			// Recursively Build the tree for left subtree.
			if ((medianIndex - 1) >= 0) {
				List<LAB> lesserNodes = LABValues.subList(0, medianIndex);

				if (lesserNodes.size() > 0) {
					node.lesser = createNode(lesserNodes, k, depth + 1);
					node.lesser.parent = node;
				}
			}
			// Recursively Build the tree for right subtree.
			if ((medianIndex + 1) <= (LABValues.size() - 1)) {
				List<LAB> greaterNodes = LABValues.subList(medianIndex + 1,
						LABValues.size());
				if (greaterNodes.size() > 0) {
					node.greater = createNode(greaterNodes, k, depth + 1);
					node.greater.parent = node;
				}
			}
		}

		return node;
	}

	//  Distance between two LAB points
	public static class EuclideanDistance implements Comparator<TreeNode> {

		private LAB point = null;

		public EuclideanDistance(LAB point) {
			this.point = point;
		}
		
		// To compare two nodes based on there Euclidean Distance
		public int compare(TreeNode obj1, TreeNode obj2) {
			Double dis1 = point.euclideanDistance(obj1.LABobj);
			Double dis2 = point.euclideanDistance(obj2.LABobj);
			if (dis1.compareTo(dis2) < 0)
				return -1;
			else if (dis2.compareTo(dis1) < 0)
				return 1;
			return obj1.LABobj.compareTo(obj2.LABobj);
		}
	};

	// Subclass for each Node in the tree
	public static class TreeNode implements Comparable<TreeNode> {

		int k = 3;
		int depth = 0;
		LAB LABobj = null;
		TreeNode parent = null;
		TreeNode lesser = null;
		TreeNode greater = null;

		public TreeNode(LAB LABobj) {
			this.LABobj = LABobj;
		}

		public TreeNode(int k, int depth, LAB id) {
			this(id);
			this.k = k;
			this.depth = depth;

		}

		//Compare the two LAB points
		public static int compareTo(int depth, int k, LAB obj1, LAB obj2) {
			int axis = depth % k;
			if (axis == AXIS_L)
				return COMPARE_L.compare(obj1, obj2);
			return COMPARE_A.compare(obj1, obj2);
		}

		@Override
		public int compareTo(TreeNode o) {
			return compareTo(depth, k, this.LABobj, o.LABobj);
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("k=").append(k);
			builder.append(" depth=").append(depth);
			builder.append(" id=").append(LABobj.getRGB());
			return builder.toString();
		}
	}

	// Subclass to hold the individual value of the node
	// Also to store the Name of the image that node 
	// belongs to and the number of times that node
	// was reused.
	public static class LAB implements Comparable<LAB> {

		double l = Double.NEGATIVE_INFINITY;
		double a = Double.NEGATIVE_INFINITY;
		double b = Double.NEGATIVE_INFINITY;

		String tileName;

		int reuseLimit;

		public LAB(double l, double a, double b, String tileName, int reuseLimit) {
			this.l = l;
			this.a = a;
			this.b = b;
			this.tileName = tileName;
			this.reuseLimit = reuseLimit;
		}

		public ArrayList<Double> getRGB() {
			ArrayList<Double> RGBlist = new ArrayList<Double>();
			RGBlist.add(l);
			RGBlist.add(a);
			RGBlist.add(b);
			return RGBlist;
		}

		public String getTileName() {
			return (tileName);
		}

		public Integer getTileLimit() {
			return (reuseLimit);
		}

		public void setTileLimit(int reuseLimit) {
			this.reuseLimit = reuseLimit;
		}

		public LAB(double l, double a, double b) {
			this.l = l;
			this.a = a;
			this.b = b;

		}

		/**
		 * Computes the Euclidean distance from this point to the other.
		 * 
		 * @param obj1
		 *            other point.
		 *            
		 * @return euclidean distance.
		 */
		public double euclideanDistance(LAB obj1) {
			return DeltaE(obj1, this);
		}

		/**
		 * Distance Metric [DeltaE] from one point to the other.
		 * 
		 * @param obj1
		 *            first point.
		 * @param obj2
		 *            second point.
		 *            
		 * @return distance Metric.
		 */
		private static final double DeltaE(LAB obj1, LAB obj2) {
			return Math.sqrt(Math.pow((obj1.l - obj2.l), 2)
					+ Math.pow((obj1.a - obj2.a), 2)
					+ Math.pow((obj1.b - obj2.b), 2));
		};

		@Override
		public int compareTo(LAB o) {
			int lComp = COMPARE_L.compare(this, o);
			if (lComp != 0)
				return lComp;
			int aComp = COMPARE_A.compare(this, o);
			return aComp;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("(");
			builder.append(l).append(", ");
			builder.append(a).append(", ");
			builder.append(b);
			builder.append(")");
			return builder.toString();
		}

	}

}
