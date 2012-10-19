package com.scireum.open.xml;

import org.xml.sax.Attributes;

/**
 * Similar in concept to a 'matcher' pattern, except here there are finite things that
 * can be matched against.  Utilized by {@link XMLReader} in order to determine what
 * (if any) {@link NodeHandler} implementations are applicable to current state.
 *
 * @author Jason Mosser
 */
public interface NodeHandlerKey{

    /**
     * @param uri as specified by the {@link org.xml.sax.helpers.DefaultHandler#startElement(String, String, String, org.xml.sax.Attributes)} method.
     * @param localName as specified by the {@link org.xml.sax.helpers.DefaultHandler#startElement(String, String, String, org.xml.sax.Attributes)} method.
     * @param qName as specified by the {@link org.xml.sax.helpers.DefaultHandler#startElement(String, String, String, org.xml.sax.Attributes)} method.
     * @param attributes as specified by the {@link org.xml.sax.helpers.DefaultHandler#startElement(String, String, String, org.xml.sax.Attributes)} method.
     * @return <code>true</code> if and only if implementation can handle a node as
     * described by given arguments.
     */
    boolean matches(String uri, String localName, String qName, Attributes attributes);

}
