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
package com.scireum.open.xml;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * This class is a combination of DOM and SAX parsing since import files like
 * BMECat files are generally too big for DOM parsers but SAX is too
 * inconvenient, this class extracts part of the SAX-stream, converts them into
 * sub-DOMs and calls the application for each sub-DOM.
 * 
 * @author aha
 * 
 */
public final class XMLReader extends DefaultHandler {

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
        if(this.activeHandlers.isEmpty()){
            return;
        }
		// Delegate to active handlers...
		String cData = new String(ch).substring(start, start + length);
		for (SAX2DOMHandler handler : this.activeHandlers) {
			handler.text(cData);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		// Consider iterating over all activeHandler which are not complete
		// yet and raise an exception.
		// For now this is simply ignored to make processing more robust.
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
        this.pathToOriginalRoot.pop();
        if(this.activeHandlers.isEmpty()){
            return;
        }
		// Delegate to active handlers and deletes them if they are finished...
		Iterator<SAX2DOMHandler> it = this.activeHandlers.iterator();
		while (it.hasNext()) {
			SAX2DOMHandler handler = it.next();
			if (handler.endElement(snapshot(this.pathToOriginalRoot), uri, qName)) {
				it.remove();
			}
		}
	}

    @Override
	public void processingInstruction(String target, String data) throws SAXException {
        if(this.activeHandlers.isEmpty()){
            return;
        }
		// Delegate to active handlers...
		for (SAX2DOMHandler handler : this.activeHandlers) {
			handler.processingInstruction(target, data);
		}
	}

	@Override
	public void startElement(
        String uri, String localName, String name, Attributes attributes
    ) throws SAXException {

        this.pathToOriginalRoot.push(ImmutableSAXElement.build(uri, name));
        if(!this.activeHandlers.isEmpty()){
            // Delegate to active handlers...
            for (SAX2DOMHandler handler : this.activeHandlers) {
                handler.startElement(uri, name, attributes);
            }
        }

		// Test if start of new handler is necessary
		try {
			// QName qualifiedName = new QName(uri, localName);
			Set<NodeHandler> notifyHandlers =
                resolveNodeHandlers(uri, localName, name, attributes);
            if(notifyHandlers == null){
                return;
            }
            for(NodeHandler handler : notifyHandlers){
                this.activeHandlers.add(
                    new SAX2DOMHandler(handler, uri, name, attributes)
                );
            }
        } catch (ParserConfigurationException e) {
			throw new SAXException(e);
		}
	}

	/**
	 * Registers a new handler for a qualified name of a node. Handlers are
	 * invoked AFTER the complete node was read. Since documents like BMECat
	 * usually don't mix XML-data, namespaces are ignored for now which eases
	 * the processing a lot (especially xpath related tasks). Namespaces however
	 * could be easily added by replacing String with QName here.
	 */
	public void addHandler(String name, NodeHandler handler) {
		this.handlers.put(new QNameOnlyNodeHandlerKey(name), handler);
	}

    public void addHandler(NodeHandlerKey key, NodeHandler handler) {
        this.handlers.put(key, handler);
    }

//    // not used so commented //
//    /**
//     * Returns a XMLNode for the given w3c node.
//     */
//    public static StructuredNode convert(Node node){
//        return new XMLNodeImpl(null, node);
//    }

//	class UserInterruptException extends RuntimeException {
//		private static final long serialVersionUID = -7454219131982518216L;
//	}

	/**
	 * Parses the given stream and using the given monitor
	 */
	public void parse(InputStream stream)
        throws ParserConfigurationException, SAXException, IOException
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = null;
        try {
			saxParser = factory.newSAXParser();

            org.xml.sax.XMLReader reader = saxParser.getXMLReader();
			reader.setEntityResolver(new EntityResolver() {

				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {
					URL url = new URL(systemId);
					// Check if file is local
					if ("file".equals(url.getProtocol())) {
						// Check if file exists
						File file = new File(url.getFile());
						if (file.exists()) {
							return new InputSource(new FileInputStream(file));
						}
					}
					return null;
				}
			});
			reader.setContentHandler(this);
			reader.parse(new InputSource(stream));
		} finally {
            if(saxParser != null){
                saxParser.reset();
            }
            stream.close();
		}
	}

    private Map<NodeHandlerKey, NodeHandler> handlers = new HashMap<NodeHandlerKey, NodeHandler>();
    private List<SAX2DOMHandler> activeHandlers = new ArrayList<SAX2DOMHandler>();
    private final Stack<SAXElement> pathToOriginalRoot = new Stack<SAXElement>();

    private Set<NodeHandler> resolveNodeHandlers(String uri, String localName, String name, Attributes attributes){
        if(this.handlers.isEmpty()){
            return null;
        }
        Set<NodeHandler> result = new LinkedHashSet<NodeHandler>();
        for(Map.Entry<NodeHandlerKey, NodeHandler> entry : this.handlers.entrySet()){
            NodeHandlerKey currentKey = entry.getKey();
            if(currentKey.matches(uri, localName, name, attributes)){
                result.add(entry.getValue());
            }
        }
        return (!result.isEmpty() ? result : null);
    }

    private static Stack<? extends SAXElement> snapshot(Stack<SAXElement> currentPathToOriginalRoot){
        if(currentPathToOriginalRoot == null || currentPathToOriginalRoot.isEmpty()){
            return null;
        }
        Stack<SAXElement> copy = new Stack<SAXElement>();
        for(SAXElement element : currentPathToOriginalRoot){
            copy.push(element);
        }
        return copy;
    }

}
