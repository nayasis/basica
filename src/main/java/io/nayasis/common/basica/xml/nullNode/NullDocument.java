package io.nayasis.common.basica.xml.nullNode;

import io.nayasis.common.basica.xml.Xml;
import org.w3c.dom.*;

public class NullDocument extends NullNode implements Document {

	@Override
    public DocumentType getDoctype() {
	    return null;
    }

	@Override
    public DOMImplementation getImplementation() {
	    return null;
    }

	@Override
    public Element getDocumentElement() {
	    return Xml.NULL_ELEMENT;
    }

	@Override
    public Element createElement( String tagName ) throws DOMException {
	    return Xml.NULL_ELEMENT;
    }

	@Override
    public DocumentFragment createDocumentFragment() {
	    return null;
    }

	@Override
    public Text createTextNode( String data ) {
	    return null;
    }

	@Override
    public Comment createComment( String data ) {
	    return null;
    }

	@Override
    public CDATASection createCDATASection( String data ) throws DOMException {
	    return null;
    }

	@Override
    public ProcessingInstruction createProcessingInstruction( String target, String data ) throws DOMException {
	    return null;
    }

	@Override
    public Attr createAttribute( String name ) throws DOMException {
	    return Xml.NULL_ATTR;
    }

	@Override
    public EntityReference createEntityReference( String name ) throws DOMException {
	    return null;
    }

	@Override
    public NodeList getElementsByTagName( String tagname ) {
	    return Xml.NULL_NODE_LIST;
    }

	@Override
    public Node importNode( Node importedNode, boolean deep ) throws DOMException {
	    return Xml.NULL_NODE;
    }

	@Override
    public Element createElementNS( String namespaceURI, String qualifiedName ) throws DOMException {
	    return Xml.NULL_ELEMENT;
    }

	@Override
    public Attr createAttributeNS( String namespaceURI, String qualifiedName ) throws DOMException {
	    return Xml.NULL_ATTR;
    }

	@Override
    public NodeList getElementsByTagNameNS( String namespaceURI, String localName ) {
	    return Xml.NULL_NODE_LIST;
    }

	@Override
    public Element getElementById( String elementId ) {
	    return Xml.NULL_ELEMENT;
    }

	@Override
    public String getInputEncoding() {
	    return null;
    }

	@Override
    public String getXmlEncoding() {
	    return null;
    }

	@Override
    public boolean getXmlStandalone() {
	    return false;
    }

	@Override
    public void setXmlStandalone( boolean xmlStandalone ) throws DOMException {
    }

	@Override
    public String getXmlVersion() {
	    return null;
    }

	@Override
    public void setXmlVersion( String xmlVersion ) throws DOMException {
    }

	@Override
    public boolean getStrictErrorChecking() {
	    return false;
    }

	@Override
    public void setStrictErrorChecking( boolean strictErrorChecking ) {
    }

	@Override
    public String getDocumentURI() {
	    return null;
    }

	@Override
    public void setDocumentURI( String documentURI ) {
    }

	@Override
    public Node adoptNode( Node source ) throws DOMException {
	    return Xml.NULL_NODE;
    }

	@Override
    public DOMConfiguration getDomConfig() {
	    return null;
    }

	@Override
    public void normalizeDocument() {
    }

	@Override
    public Node renameNode( Node n, String namespaceURI, String qualifiedName ) throws DOMException {
	    return Xml.NULL_NODE;
    }

}
