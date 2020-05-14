package com.github.nayasis.basica.xml.nullNode;

import com.github.nayasis.basica.xml.Xml;
import org.w3c.dom.*;

public class NullNode implements Node {

    public NullNode() {}

    public String getNodeName() {
        return null;
    }

    public String getNodeValue() throws DOMException {
        return null;
    }

    public short getNodeType() {
        return -1;
    }

    public Node getParentNode() {
        return Xml.NULL_NODE;
    }

    public NodeList getChildNodes() {
        return Xml.NULL_NODE_LIST;
    }


    public Node getFirstChild() {
        return Xml.NULL_NODE;
    }


    public Node getLastChild() {
        return Xml.NULL_NODE;
    }


    public Node getPreviousSibling() {
        return Xml.NULL_NODE;
    }


    public Node getNextSibling() {
        return Xml.NULL_NODE;
    }


    public NamedNodeMap getAttributes() {
        return null;
    }


    public Document getOwnerDocument() {
        return Xml.NULL_DOCUMENT;
    }


    public boolean hasChildNodes() {
        return false;
    }


    public Node cloneNode(boolean deep) {
        return Xml.NULL_NODE;
    }


    public void normalize() {}


    public boolean isSupported(String feature, String version) {
        return false;
    }


    public String getNamespaceURI() {
        return null;
    }


    public String getPrefix() {
        return null;
    }

    public String getLocalName() {
        return null;
    }

    public String getBaseURI(){
        return null;
    }

    public boolean hasAttributes() {
        return false;
    }

    public void setNodeValue(String nodeValue) throws DOMException {}


    public Node insertBefore(Node newChild, Node refChild) {
        return Xml.NULL_NODE;
    }


    public Node replaceChild(Node newChild, Node oldChild) {
    	return Xml.NULL_NODE;
    }


    public Node removeChild(Node oldChild) {
    	return Xml.NULL_NODE;
    }

    public Node appendChild( Node newChild ) throws DOMException {
		return Xml.NULL_NODE;
    }

    public void setPrefix( String prefix ) throws DOMException {}

    public short compareDocumentPosition( Node other ) throws DOMException {
	    return 0;
    }

    public String getTextContent() throws DOMException {
	    return null;
    }

    public void setTextContent( String textContent ) throws DOMException {}

    public boolean isSameNode( Node other ) {
    	return other == null || other == Xml.NULL_NODE;
    }

    public String lookupPrefix( String namespaceURI ) {
	    return null;
    }

    public boolean isDefaultNamespace( String namespaceURI ) {
	    return false;
    }

    public String lookupNamespaceURI( String prefix ) {
	    return null;
    }

    public boolean isEqualNode( Node arg ) {
    	return arg == null || arg == Xml.NULL_NODE;
    }

    public Object getFeature( String feature, String version ) {
	    return null;
    }

    public Object setUserData( String key, Object data, UserDataHandler handler ) {
	    return null;
    }

    public Object getUserData( String key ) {
	    return null;
    }


}
