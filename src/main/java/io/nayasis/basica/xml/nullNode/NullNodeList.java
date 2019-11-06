package io.nayasis.basica.xml.nullNode;

import io.nayasis.basica.xml.Xml;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NullNodeList implements NodeList {

	@Override
    public Node item( int index ) {
	    return Xml.NULL_NODE;
    }

	@Override
    public int getLength() {
	    return 0;
    }

}
