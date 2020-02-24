package html;

import java.util.HashSet;

public class Node {
	
	/**
	 * @invar Text nodes do not have children
	 *    | text == null || dummyChild.nextSibling == dummyChild
	 * @invar The nextSibling association is consistent.
	 *    | nextSibling == null || nextSibling.previousSibling == this
	 * @invar The previousSibling association is consistent.
	 *    | previousSibling == null || previousSibling.nextSibling == this
	 * @invar This node is either a root node or a child node
	 *    | (nextSibling == null) == (previousSibling == null)
	 * @invar A dummy sibling is reachable in the nextSibling direction
	 *    | getLaterSiblings().stream().anyMatch(n -> n.parent != null)
	 * @invar A dummy sibling is reachable in the previousSibling direction
	 *    | getEarlierSiblings().stream().anyMatch(n -> n.parent != null)
	 */
	private String text;
	private String tag;
	private Node nextSibling;
	private Node previousSibling;
	private Node parent;
	private Node dummyChild;
	
	private HashSet<Node> getLaterSiblings() {
		HashSet<Node> siblings = new HashSet<>();
		for (Node sibling = nextSibling; sibling != null && !siblings.contains(sibling); sibling = sibling.nextSibling) {
			siblings.add(sibling);
			if (sibling.parent != null)
				break;
		}
		return siblings;
	}
	
	private HashSet<Node> getEarlierSiblings() {
		HashSet<Node> siblings = new HashSet<>();
		for (Node sibling = previousSibling; sibling != null && !siblings.contains(sibling); sibling = sibling.previousSibling) {
			siblings.add(sibling);
			if (sibling.parent != null)
				break;
		}
		return siblings;
	}
	
	public Node getNextSibling() { return nextSibling; }
	public Node getPreviousSibling() { return previousSibling; }
	
	private Node(Node parent) {
		this.parent = parent;
		nextSibling = this;
		previousSibling = this;
	}
	
	/**
	 * @pre Exactly one of the given text or the given tag must be null.
	 */
	public Node(String text, String tag) {
		this.text = text;
		this.tag = tag;
		this.dummyChild = new Node(this);
	}
	
	/**
	 * @pre The given node is a root node.
	 *    | newChild.getNextSibling() == null
	 */
	public void addChild(Node newChild) {
		newChild.nextSibling = this.dummyChild;
		newChild.previousSibling = this.dummyChild.previousSibling;
		newChild.nextSibling.previousSibling = newChild;
		newChild.previousSibling.nextSibling = newChild;
	}
	
	public Node getParent() {
		if (nextSibling == null)
			return null;
		Node node = this;
		while (node.parent == null)
			node = node.nextSibling;
		return node.parent;
	}
	
	public Node copy() {
		Node copiedNode = new Node(text, tag);
		for (Node child = dummyChild.nextSibling; child != dummyChild; child = child.nextSibling)
			copiedNode.addChild(child.copy());
		return copiedNode;
	}
	
	public void detach() {
		if (nextSibling == null)
			return;
		nextSibling.previousSibling = previousSibling;
		previousSibling.nextSibling = nextSibling;
		nextSibling = null;
		previousSibling = null;
	}
	
	public String toString() {
		if (text != null)
			return text;
		String result = "<" + tag + ">";
		for (Node child = dummyChild.nextSibling; child != dummyChild; child = child.nextSibling)
			result += child.toString();
		result += "</" + tag + ">";
		return result;
	}

}
