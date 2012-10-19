package com.scireum.open.xml;

import junit.framework.TestCase;

/**
 * @author Jason Mosser
 */
public class UnitTestImmutableSAXElement extends TestCase{

    public void testBuildSameObject() throws Exception{
        final SAXElement e1 = ImmutableSAXElement.build("aURI", "aQName");
        assertNotNull(e1);

        final SAXElement e2 = ImmutableSAXElement.build("aURI", "aQName");
        assertNotNull(e2);

        assertSame(e1, e2);

        assertFalse(ImmutableSAXElement.contains("X", "Y"));
        assertTrue(ImmutableSAXElement.contains("aURI", "aQName"));

        assertTrue(e1.equals(e2));
    }

    public void testBuildDifferentObject() throws Exception{
        final SAXElement e1 = ImmutableSAXElement.build("aURI_999", "aQName_999");
        assertNotNull(e1);
        final SAXElement e2 = ImmutableSAXElement.build("aURI_2", "aQName_2");
        assertNotNull(e2);

        assertNotSame(e1, e2);

        assertFalse(ImmutableSAXElement.contains("X", "Y"));
        assertTrue(ImmutableSAXElement.contains("aURI_999", "aQName_999"));
        assertTrue(ImmutableSAXElement.contains("aURI_2", "aQName_2"));

        assertFalse(e1.equals(e2));
    }

}
