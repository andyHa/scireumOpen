package com.scireum.open.xml;

import examples.ExampleXML;
import examples.NISCExampleXML;
import junit.framework.TestCase;

/**
 * @author Jason Mosser
 */
public class UnitTestNISCExampleXML extends TestCase{

    // appears to hold memory without gc() calls //

    @Override
    protected void setUp() throws Exception{
        System.gc();
        System.gc();
        System.gc();
        System.gc();
    }

    @Override
    protected void tearDown() throws Exception{
        System.gc();
        System.gc();
        System.gc();
        System.gc();
    }

    public void testMain() throws Exception{
        ExampleXML.main(null);
    }

    public void testNISCMain() throws Exception{
        NISCExampleXML.main(null);
    }




}
