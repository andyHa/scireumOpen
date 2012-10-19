package com.scireum.open.xml;

import org.xml.sax.Attributes;

/**
 * A utility implementation that will match only against <code>qName</code> of an XML
 * element.
 *
 * @author Jason Mosser
 */
public final class QNameOnlyNodeHandlerKey implements NodeHandlerKey{

    private final String qName;

    public QNameOnlyNodeHandlerKey(String qName){
        this.qName = qName;
    }

    @Override
    public boolean matches(String uri, String localName, String qName, Attributes attributes){
        return this.qName.equals(qName);
    }

}
