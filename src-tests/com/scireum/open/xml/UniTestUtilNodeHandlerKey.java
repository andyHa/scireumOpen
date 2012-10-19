package com.scireum.open.xml;

import junit.framework.TestCase;
import org.xml.sax.Attributes;

/**
 * @author Jason Mosser
 */
public class UniTestUtilNodeHandlerKey extends TestCase{

    public void testAttributeValueMatches() throws Exception{
        assertTrue(UtilNodeHandlerKey.attributeValueMatches(
            "mockAttributeName", "mockAttributeValue", new MockAttributes()
        ));
    }

    public void testAttributeValueDoesNotMatch() throws Exception{
        assertFalse(UtilNodeHandlerKey.attributeValueMatches(
            "mockAttributeName", "notTheValueYouAreLookingFor", new MockAttributes()
        ));
    }

    private static class MockAttributes implements Attributes{
        @Override
        public int getLength(){
            throw new UnsupportedOperationException();
        }

        @Override
        public String getURI(int index){
            throw new UnsupportedOperationException();
        }

        @Override
        public String getLocalName(int index){
            throw new UnsupportedOperationException();
        }

        @Override
        public String getQName(int index){
            throw new UnsupportedOperationException();
        }

        @Override
        public String getType(int index){
            throw new UnsupportedOperationException();
        }

        @Override
        public String getValue(int index){
            if(index == -1){
                return null;
            }
            if(index != 0){
                throw new UnsupportedOperationException();
            }
            return "mockAttributeValue";
        }

        @Override
        public int getIndex(String uri, String localName){
            throw new UnsupportedOperationException();
        }

        @Override
        public int getIndex(String qName){
            if(!"mockAttributeName".equals(qName)){
                throw new UnsupportedOperationException();
            }
            return 0;
        }

        @Override
        public String getType(String uri, String localName){
            throw new UnsupportedOperationException();
        }

        @Override
        public String getType(String qName){
            throw new UnsupportedOperationException();
        }

        @Override
        public String getValue(String uri, String localName){
            throw new UnsupportedOperationException();
        }

        @Override
        public String getValue(String qName){
            throw new UnsupportedOperationException();
        }
    }
}
