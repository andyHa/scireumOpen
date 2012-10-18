/**
 * Copyright (c) 2012 scireum GmbH - Andreas Haufler - aha@scireum.de
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package examples;

import com.scireum.open.xml.NodeHandler;
import com.scireum.open.xml.NodeHandlerKey;
import com.scireum.open.xml.SAXElement;
import com.scireum.open.xml.exception.StopParsingException;
import com.scireum.open.xml.StructuredNode;
import com.scireum.open.xml.XMLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import static com.scireum.open.xml.UtilNodeHandlerKey.attributeValueMatches;

/**
 * Small example class which show how to use the {@link com.scireum.open.xml.XMLReader}.
 */
public class NISCExampleXML{

	public static void main(String[] args) throws Exception {
		XMLReader r = new XMLReader();
		// We can add several handlers which are triggered for a given node
		// name. The complete sub-dom of this node is then parsed and made
		// available as a StructuredNode

        final NodeHandler nodeHandler = new NodeHandler(){
            @Override
            public void process(StructuredNode node) throws StopParsingException{
                try{
//                    for(SAXElement saxElement : node.getPathToGlobalRoot()){
//                        System.out.print("/");
//                        System.out.print(saxElement.getQName());
//                    }
//                    System.out.print("/");
//                    System.out.println(node.getNodeName());
//
//                    System.out.println("--Attribute Matched------");
////                        System.out.println(node.queryString("elemOverride/@name"));
//                    for(StructuredNode elemOverride : node.queryNodeList("elemOverride")){
//                        System.out.println(elemOverride.queryString("@name"));
//                    }
//                    System.out.println(node.queryValue("price").asDouble(0d));
//                    System.out.println("-------------------------");


                    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    final Document newXmlDocument = documentBuilder.newDocument();
                    Node parentNode = null;
                    for(SAXElement element : node.getPathToGlobalRoot()){
                        Node newNode = newXmlDocument.createElement(element.getQName());
                        if(parentNode != null){ // effectively an 'if not first' expression //
                            // not first node, so append to current parentNode //
                            parentNode.appendChild(newNode);
                        } else{ // was first node, so append it to new document root //
                            newXmlDocument.appendChild(newNode);
                        }
                        parentNode = newNode;
                    }
                    if(parentNode == null){
                        throw new SAXException("Did not parse expected XML fragment.");
                    }
                    parentNode.appendChild(newXmlDocument.importNode(node.getDOMNode(), true));

                    prettyPrint(newXmlDocument);
                } catch(Exception e){
                    e.printStackTrace();
                }
                throw new StopParsingException();
            }

            private void prettyPrint(Document newXmlDocument) throws TransformerException{
                TransformerFactory transformerFactory = TransformerFactory.newInstance(
                    //// request this transformer factory only if debugging //
                    //// and wanting 'pretty print' of the Document         //
                    "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl",
                    NISCExampleXML.class.getClassLoader()
                );
                Transformer transformer = transformerFactory.newTransformer();
                try{
                    //Setup indenting to "pretty print" ... nice for debugging //
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    transformer.transform(
                        new DOMSource(newXmlDocument),
                        new StreamResult(outputStream)
                    );

                    final String stringXML = outputStream.toString();
                    System.out.println(stringXML);
                    //return this.handler.getScreenFromXml(stringXML);
                } finally{
                    transformer.reset();
                }
            }
        };

        // this is the bread-n-butter bit ...                                           //
        // it allows selective application of the associated NodeHandler implementation //
        final NodeHandlerKey nodeHandlerKey = new NodeHandlerKey(){
            final String _screen = "screen";
            final String _name = "name";
//            final String _nameValue = "absAuditInquiry";
//            final String _nameValue = "mrCatalogItem";
            final String _nameValue = "zipCodeMaintenanceSis";
            @Override
            public boolean matches(String uri, String localName, String qName, Attributes attributes){
                return _screen.equals(qName) &&
                    attributeValueMatches(_name, _nameValue, attributes);
            }
        };
        r.addHandler(nodeHandlerKey, nodeHandler);

		// parse a full production file //
        final FileInputStream stream = new FileInputStream("src/examples/repository.xml");

        long startTime = System.nanoTime();
        try{
            r.parse(stream);
        } catch(StopParsingException e){
            /* ignore */
            /* This was the quickest way to short-circuit parsing without reworking API too much */
        }
        long endTime = System.nanoTime();

        final long delta = endTime - startTime;
        System.out.println(delta > 1000000 ? (delta / 1000000) + "ms" : delta + "ns");

    }
}
