package com.scireum.open.xml;

import org.xml.sax.Attributes;

/**
 * Utility methods relating to
 * {@link NodeHandlerKey#matches(String, String, String, org.xml.sax.Attributes)}
 * implementations will go here.
 *
 * @author Jason Mosser
 */
public final class UtilNodeHandlerKey{

    /**
     * @param attributeName to search with in given <code>attributes</code>
     * @param attributeValue to test for equality within the given <code>attributes</code>
     * @param attributes zero-to-many attributes from an XML element.
     * @return <code>true</code> if and only if an <code>attributeValue</code> equality is
     * identified in given <code>attributes</code>
     */
    public static boolean attributeValueMatches(String attributeName, String attributeValue, Attributes attributes){
        return attributeValue.equals(attributes.getValue(attributes.getIndex(attributeName)));
    }

    private UtilNodeHandlerKey() { /* no-op */ }

}
