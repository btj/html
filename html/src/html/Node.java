package html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import javax.xml.transform.stream.StreamSource;

/**
 * Trees of instances of this class can be built to represent HTML documents.
 */
public class Node {
	
	/**
	 * Keeps track of all instances of this class, without keeping the garbage collector from collecting unreachable instances.
	 * 
	 * <p>This field serves only to help express and check the formal specifications of this class.
	 */
	private static WeakHashMap<Node, Void> allInstances = new WeakHashMap<Node, Void>();
	
	/**
	 * Returns the set of all instances of this class as a {@code Predicate<Node>}, so that
	 * clients can test whether a given instance is in the set, but cannot enumerate all instances.
	 * 
	 * <p>This method serves only to help express and check the formal specification of this class.
	 */
	public Predicate<Node> getAllInstances() {
		WeakHashMap<Node, Void> copy = new WeakHashMap<Node, Void>(allInstances);
		return n -> copy.containsKey(n);
	}
	
	private String text;
	private String tag;
	private Node parent;
	private ArrayList<Node> children;
	
	public String getText() {
		return text;
	}
	
	public String getTag() {
		return tag;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public Node[] getChildren() {
		return children.toArray(new Node[0]);
	}
	
	public HashSet<Node> getDescendants() {
		HashSet<Node> descendants = new HashSet<Node>();
		descendants.add(this);
		//for (int i = 0; i < children.size(); i++)
		//    descendants.addAll(children.get(i).getDescendants());
		for (Node child : children)
			descendants.addAll(child.getDescendants());
		return descendants;
	}
	
	/**
	 * Returns whether the subtree rooted at this node equals the subtree rooted at the given node.
	 */
	public boolean equals(Node other) {
		return
				Objects.equals(this.getText(), other.getText()) &&
				Objects.equals(this.getTag(), other.getTag()) &&
				this.getChildren().length == other.getChildren().length &&
				IntStream.range(0, this.getChildren().length).allMatch(i -> this.getChildren()[i].equals(other.getChildren()[i]));
	}
	
	/**
	 * @pre Exactly one of the given text or the given tag must be null.
	 *     | (text == null) != (tag == null)
	 * @post This node's text equals the given text
	 *     | getText() == text
	 * @post This node's tag equals the given tag
	 *     | getTag() == tag
	 * @post This node is a root node
	 *     | getParent() == null
	 * @post This node has no children
	 *     | getChildren().length == 0
	 */
	public Node(String text, String tag) {
		allInstances.put(this, null);
		children = new ArrayList<Node>();
		this.text = text;
		this.tag = tag;
	}
	
	/**
	 * @pre This node is not a text node.
	 *    | getText() == null
	 * @pre The given node is a root node.
	 *    | newChild.getParent() == null
	 * @post The given node's parent equals this node.
	 *    | newChild.getParent() == this
	 * @post The given node is the last child of this node.
	 *    | getChildren()[getChildren().length - 1] == newChild
	 * @post This node's old children are still its children
	 *    | Arrays.equals(old(getChildren()), 0, old(getChildren()).length, getChildren(), 0, old(getChildren()).length)
	 */
	public void addChild(Node newChild) {
		this.children.add(newChild);
		newChild.parent = this;
	}
	
	/**
	 * Returns whether the two given sets intersect.
	 */
	public static boolean intersects(HashSet<Node> s1, HashSet<Node> s2) {
		return s1.stream().anyMatch(n -> s2.contains(n));
		// there exists an element n of s1 such that s2.contains(n)
	}
	
	/**
	 * Returns a copy of the subtree rooted at this node.
	 * @post The resulting node is a root node
	 *     | result.getParent() == null
	 * @post The resulting node and its descendants did not yet exist before this method call.
	 *     | result.getDescendants().stream().allMatch(n -> !old(getAllInstances()).test(n))
	 * @post The resulting node and its descendants are different from this node and its descendants.
	 *      !intersects(result.getDescendants(), this.getDescendants())
	 * @post The subtree rooted at the resulting node equals the subtree rooted at this node.
	 *     | result.equals(this)
	 */
	public Node copy() {
		Node copy = new Node(text, tag);
		for (Node child : children)
			copy.addChild(child.copy());
		return copy;
	}
	
	/**
	 * Returns a new array whose elements are the elements of the given array with the given element removed.
	 * 
	 * @pre The given element occurs once in the given array.
	 *    | IntStream.range(0, array.length).anyMatch(i ->
	 *    |     array[i] == node && IntStream.range(0, array.length).allMatch(j -> j == i || array[j] != node))
	 */
	public static Node[] remove(Node[] array, Node node) {
		Node[] result = new Node[array.length - 1];
		int j = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == node) {
			} else {
				result[j] = array[i];
				j++;
			}
		}
		return result;
	}
	
	/**
	 * Detaches this node from its parent.
	 * @pre This node is not a root node
	 *     | getParent() != null
	 * @post This node is a root node
	 *     | getParent() == null
	 * @post This node's parent's children are its old children with this node removed.
	 *     | Arrays.equals(old(getParent()).getChildren(), remove(old(getParent().getChildren()), this))
	 */
	public void detach() {
		parent.children.remove(this);
		this.parent = null;
	}
	
	public String toString() {
		if (text != null)
			return text;
		String result = "<" + tag + ">";
		for (Node child : children)
			result += child.toString();
		result += "</" + tag + ">";
		return result;
	}
	
	public Node[] getDescendantsWithTag(String tag) {
		HashSet<Node> descendants = getDescendants();
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		for (Node descendant : descendants)
			if (Objects.equals(descendant.getTag(), tag))
				resultNodes.add(descendant);
		return resultNodes.toArray(new Node[0]);
	}

}
