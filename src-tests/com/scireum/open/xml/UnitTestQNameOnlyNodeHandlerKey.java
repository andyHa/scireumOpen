package com.scireum.open.xml;

import junit.framework.TestCase;

/**
 * @author Jason Mosser
 */
public class UnitTestQNameOnlyNodeHandlerKey extends TestCase{

    public void testMatches() throws Exception{
        NodeHandlerKey key = new QNameOnlyNodeHandlerKey("aQName");

        assertTrue(key.matches(null, null, "aQName", null));
        assertFalse(key.matches(null, null, "notTheQNameYouAreLookingFor", null));
    }

}
