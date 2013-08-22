package com.scireum.open.xml;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author jaran
 */
public class XMLReaderTest {


    @Test
    public void parse_parses_text_elements_correctly() throws IOException, SAXException, ParserConfigurationException {

        XMLReader reader = new XMLReader();

        reader.addHandler("book", new NodeHandler() {
            @Override
            public void process(StructuredNode node) {

                String text = null;
                String lnr = null;
                try {
                    text = node.queryString("description/text()");
                } catch (XPathExpressionException e) {
                    fail(e.getMessage());
                }
                System.out.println(text);
                assertTrue(text.equals("Description"));
            }
        });

        reader.parse(getClass().getResourceAsStream("/books.xml"));
    }

}
