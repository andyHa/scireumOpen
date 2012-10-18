package com.scireum.open.xml;

/**
 * Basic abstraction of an XML element given SAX parser context.
 *
 * @author Jason Mosser
 */
public interface SAXElement{

    String getURI();

    String getQName();

}
