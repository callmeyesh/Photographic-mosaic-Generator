import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Yeshwanth Venkatesh
 * 
 * @version 3.0 6th December 2012
 * 
 */

/**
 * Reference
 * 
 * http://en.wikipedia.org/wiki/Nearest_neighbour_search
 *
 * http://www.autonlab.com/
 * autonweb/14665/version/2/part/5/data/moore-tutorial.pdf
 * 
 */

public class nearestNeighbour<TREE extends KdTree.LAB> {

	/**
	 * Nearest Neighbor Search Algorithm
	 * 
	 * @param value
	 *            to find neighbors of.
	 * 
	 * @return collection of TREE neighbors.
	 */

	public Collection<KdTree.LAB> nearestNeighbourSearch(TREE value) {

		if (value == null)
			return null;

		int K = 1;

		// Map used for results
		TreeSet<KdTree.TreeNode> results = new TreeSet<KdTree.TreeNode>(
				new KdTree.EuclideanDistance(value));

		// To Find the closest leaf node
		KdTree.TreeNode prevNode = null;

		// Root of the Tree
		KdTree.TreeNode node = KdTree.root;

		while (node != null) {
			if (KdTree.TreeNode.compareTo(node.depth, node.k, node.LABobj,
					value) < 0) {
				// The value is greater
				prevNode = node;
				node = node.greater;
			} else {
				// The value is lesser
				prevNode = node;
				node = node.lesser;
			}
		}

		KdTree.TreeNode leafNode = prevNode;

		if (leafNode != null) {
			// Maintain nodes already processed
			Set<KdTree.TreeNode> seen = new HashSet<KdTree.TreeNode>();

			// Move up the tree
			node = leafNode;

			while (node != null) {
				// Recursively search the tree
				searchNode(value, node, K, results, seen);
				node = node.parent;
			}
		}

		// Collection to hold the results
		Collection<KdTree.LAB> collection = new ArrayList<KdTree.LAB>(K);

		// Iterate over the collection to get better results
		for (KdTree.TreeNode kdNode : results) {
			collection.add((KdTree.LAB) kdNode.LABobj);
		}

		return collection;
	}

	/**
	 * 
	 * @param value
	 *            to find neighbors of.
	 * 
	 * @param node
	 *            root of the tree
	 * 
	 * @param results
	 *            Map to containing results
	 * 
	 * @param seen
	 *            Map containing all the visited nodes
	 * 
	 * 
	 */
	private static final <TREE extends KdTree.LAB> void searchNode(TREE value,
			KdTree.TreeNode node, int K, TreeSet<KdTree.TreeNode> results,
			Set<KdTree.TreeNode> seen) {

		// Add the node to the map
		seen.add(node);

		// Search node
		KdTree.TreeNode lastNode = null;

		Double lastDistance = Double.MAX_VALUE;
		
		// if result has nodes, get the best node and its 
		// distance
		if (results.size() > 0) {
			lastNode = results.last();
			lastDistance = lastNode.LABobj.euclideanDistance(value);
		}

		Double nodeDistance = node.LABobj.euclideanDistance(value);

		// Add the node in the result
		if (nodeDistance.compareTo(lastDistance) < 0) {
			if (results.size() == K && lastNode != null)
				results.remove(lastNode);
			results.add(node);
		} else if (nodeDistance.equals(lastDistance)) {
			results.add(node);
		} else if (results.size() < K) {
			results.add(node);
		}

		lastNode = results.last();

		lastDistance = lastNode.LABobj.euclideanDistance(value);

		// current axis
		int axis = node.depth % node.k;

		// get the node with lesser value
		KdTree.TreeNode lesser = node.lesser;

		// get the node with greater value
		KdTree.TreeNode greater = node.greater;

		// Search child branches
		if (lesser != null && !seen.contains(lesser)) {

			// Add the node to the Map
			seen.add(lesser);

			double nodePoint = Double.MIN_VALUE;

			double valuePlusDistance = Double.MIN_VALUE;

			// Get Distance based on the AXIS
			if (axis == KdTree.AXIS_L) {
				nodePoint = node.LABobj.l;
				valuePlusDistance = value.l - lastDistance;
			} else if (axis == KdTree.AXIS_A) {
				nodePoint = node.LABobj.a;
				valuePlusDistance = value.a - lastDistance;
			} else {
				nodePoint = node.LABobj.b;
				valuePlusDistance = value.b - lastDistance;
			}

			boolean intersectionRegion = ((valuePlusDistance <= nodePoint) ? true
					: false);

			// Iterate down the branch with lesser value
			if (intersectionRegion)
				searchNode(value, lesser, K, results, seen);
		}

		// Search parent branches
		if (greater != null && !seen.contains(greater)) {

			// Add node to the Map
			seen.add(greater);

			double nodePoint = Double.MIN_VALUE;

			double valuePlusDistance = Double.MIN_VALUE;

			// Get Distance based on the AXIS
			if (axis == KdTree.AXIS_L) {
				nodePoint = node.LABobj.l;
				valuePlusDistance = value.l + lastDistance;
			} else if (axis == KdTree.AXIS_A) {
				nodePoint = node.LABobj.l;
				valuePlusDistance = value.l + lastDistance;
			} else {
				nodePoint = node.LABobj.b;
				valuePlusDistance = value.b + lastDistance;
			}
			boolean intersectionRegion = ((valuePlusDistance >= nodePoint) ? true
					: false);

			// Iterate down the branch with the larger value
			if (intersectionRegion)
				searchNode(value, greater, K, results, seen);
		}
	}

}
