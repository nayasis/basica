package io.nayasis.common.xml.nullNode;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static io.nayasis.common.xml.Xml.NULL_NODE;

public class NullNodeList implements NodeList {

	@Override
    public Node item( int index ) {
	    return NULL_NODE;
    }

	@Override
    public int getLength() {
	    return 0;
    }

}
