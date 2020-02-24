package html;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NodeTest {

	@Test
	void test() {
		Node html = new Node(null, "html");
		Node head = new Node(null, "head");
		Node title = new Node(null, "title");
		Node titleText = new Node("JLearner", null);
		html.addChild(head);
		head.addChild(title);
		title.addChild(titleText);
		
		Node body = new Node(null, "body");
		html.addChild(body);
		Node h1 = new Node(null, "h1");
		Node h1Text = new Node("JLearner", null);
		h1.addChild(h1Text);
		Node p = new Node(null, "p");
		p.addChild(new Node("Please ", null));
		Node b = new Node(null, "b");
		b.addChild(new Node("practice", null));
		p.addChild(b);
		p.addChild(new Node(".", null));
		body.addChild(h1);
		body.addChild(p);
		
		assertEquals("<html><head><title>JLearner</title></head><body><h1>JLearner</h1><p>Please <b>practice</b>.</p></body></html>",
				html.toString());
		
		assert p.getParent() == body;
		assert body.getParent() == html;
		assert b.getParent() == p;
		
		Node pCopy = p.copy();
		body.addChild(pCopy);
		
		assertEquals(
				"<html><head><title>JLearner</title></head><body><h1>JLearner</h1>"
				+ "<p>Please <b>practice</b>.</p><p>Please <b>practice</b>.</p></body></html>",
				html.toString());
		
		h1.detach();
		body.addChild(h1);

		assertEquals(
				"<html><head><title>JLearner</title></head><body>"
				+ "<p>Please <b>practice</b>.</p><p>Please <b>practice</b>.</p><h1>JLearner</h1></body></html>",
				html.toString());
	}

}
