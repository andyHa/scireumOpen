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

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Stack;

/**
 * Used to create a dom-tree for incoming nodes.
 */
final class SAX2DOMHandler {

	public SAX2DOMHandler(
        NodeHandler handler, String uri, String name, Attributes attributes
    ) throws ParserConfigurationException {
		this.nodeHandler = handler;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder loader = factory.newDocumentBuilder();
		this.document = loader.newDocument();
		createElement(name, attributes);
	}

	public Node getRoot() {
		return this.root;
	}

	public void startElement(String uri, String name, Attributes attributes) {
		createElement(name, attributes);
	}

	public void processingInstruction(String target, String data) {
		ProcessingInstruction instruction =
            this.document.createProcessingInstruction(target, data);
		this.currentNode.appendChild(instruction);
	}

	public boolean endElement(
        Stack<? extends SAXElement> pathToGlobalRoot, String uri, String name
    ) throws SAXException {
		if (!currentNode.getNodeName().equals(name)) {
			throw new DOMException(DOMException.SYNTAX_ERR,
					"Unexpected end-tag: " + name + " expected: "
							+ currentNode.getNodeName());
		}
		return nodeUp(pathToGlobalRoot);
	}

	public void text(String data) {
        this.currentNode.appendChild(this.document.createTextNode(data));
	}

	public NodeHandler getNodeHandler() {
		return this.nodeHandler;
	}

    private NodeHandler nodeHandler;
    private Document document;
    private Node root;
    private Node currentNode;

    private boolean nodeUp(Stack<? extends SAXElement> pathToOriginalRoot) throws SAXException{
        if(isComplete()){
            this.nodeHandler.process(new XMLNodeImpl(pathToOriginalRoot, this.root));
            return true;
        }
        this.currentNode = this.currentNode.getParentNode();
        return false;
    }

    private boolean isComplete(){
        return this.currentNode.equals(this.root);
    }

    private void createElement(String name, Attributes attributes){
        Element element = this.document.createElement(name);
        for(int i = 0; i < attributes.getLength(); i++){
            String attrName = attributes.getLocalName(i);
            if(attrName == null || "".equals(attrName)){
                attrName = attributes.getQName(i);
            }
            if(attrName != null || !"".equals(attrName)){
                element.setAttribute(attrName, attributes.getValue(i));
            }
        }
        if(this.currentNode != null){
            this.currentNode.appendChild(element);
        } else{
            this.root = element;
            this.document.appendChild(element);
        }
        this.currentNode = element;
    }

}
