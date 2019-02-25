package io.nayasis.common.xml.nullNode;

import io.nayasis.common.xml.Xml;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;

public class NullAttr extends NullNode implements Attr  {

    public String getName() {
	    return null;
    }

    public boolean getSpecified() {
	    return false;
    }

    public String getValue() {
	    return null;
    }

    public void setValue( String value ) throws DOMException {}

    public Element getOwnerElement() {
	    return Xml.NULL_ELEMENT;
    }

    public TypeInfo getSchemaTypeInfo() {
	    return null;
    }

    public boolean isId() {
	    return false;
    }

}
