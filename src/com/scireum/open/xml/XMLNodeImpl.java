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

import com.scireum.open.commons.Value;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Default implementation for {@link StructuredNode}.  Wraps instance of {@link Node} that
 * was extracted from origin document.  Also provides <code>pathToOriginalRoot</code>
 * which represents the node names above current content in the origin document.
 */
final class XMLNodeImpl implements StructuredNode {

    private static final XPathFactory XPATH = XPathFactory.newInstance();
    private final Stack<? extends SAXElement> pathToOriginalRoot;
    private Node node;

	public XMLNodeImpl(Stack<? extends SAXElement> pathToOriginalRoot, Node root) {
        this.pathToOriginalRoot = pathToOriginalRoot;
        this.node = root;
	}

	@Override
	public StructuredNode queryNode(String path) throws XPathExpressionException {
        Node result = evaluate(path, XPathConstants.NODE);
        if (result == null) {
            return null;
        }
        return new XMLNodeImpl(this.pathToOriginalRoot, result);
	}

    @Override
	public List<StructuredNode> queryNodeList(String path) throws XPathExpressionException {
        NodeList result = evaluate(path, XPathConstants.NODESET);
        if (result == null) {
            return Collections.emptyList();
        }
		List<StructuredNode> resultList = new ArrayList<StructuredNode>(result.getLength());
		for (int i = 0; i < result.getLength(); i++) {
			resultList.add(new XMLNodeImpl(this.pathToOriginalRoot, result.item(i)));
		}
		return resultList;
	}

	@Override
	public StructuredNode[] queryNodes(String xPath) throws XPathExpressionException {
		List<StructuredNode> nodes = queryNodeList(xPath);
		return nodes.toArray(new StructuredNode[nodes.size()]);
	}

	@Override
	public String queryString(String xPath) throws XPathExpressionException {
        Object result = evaluate(xPath, XPathConstants.NODE);
		if (result == null) {
			return null;
		}
		if (result instanceof Node) {
            return trimmedString(((Node)result).getTextContent());
        }
		return trimmedString(result.toString());
	}

    @Override
    public Value queryValue(String xPath) throws XPathExpressionException {
        return Value.of(queryString(xPath));
    }

    @Override
	public boolean isEmpty(String xPath) throws XPathExpressionException {
		String result = queryString(xPath);
		return result == null || "".equals(result);
	}

	@Override
	public String getNodeName() {
		return node.getNodeName();
	}

    @Override
    public Node getDOMNode(){
        return this.node;
    }

    @Override
    public Stack<? extends SAXElement> getPathToOriginalRoot(){
        return this.pathToOriginalRoot;
    }

    @Override
	public String toString() {
		return getNodeName();
	}

    private <T> T evaluate(String xPathString, QName returnType) throws XPathExpressionException{
        XPath xPath = XPATH.newXPath();
        XPathExpression expression = xPath.compile(xPathString);
        try{
            //noinspection unchecked
            return (T)expression.evaluate(this.node, returnType);
        } finally{
            xPath.reset();
        }
    }

    private static String trimmedString(String s){
        return (s != null ? s.trim() : s);
    }

}
