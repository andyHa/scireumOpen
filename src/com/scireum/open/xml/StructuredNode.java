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

import javax.xml.xpath.XPathExpressionException;
import java.util.List;
import java.util.Stack;

/**
 * Represents a structured node, which is part of a {@link StructuredInput}. <--AHA, is this left over from proprietary version?
 * 
 * @author aha
 * 
 */
public interface StructuredNode {

	/**
     * @param xPath relative path at which a {@link StructuredNode} will be extracted from
     *              current node content.
     * @return an instance of {@link StructuredNode} or <code>null</code> if evaluation of
     * given <code>xPath</code> results in <code>null</code>.
     */
	StructuredNode queryNode(String xPath) throws XPathExpressionException;

	/**
     * @param xPath relative path at which zero-to-many {@link StructuredNode} instances
     *              will be extracted from current node content.
     * @return collection of zero-to-many {@link StructuredNode} instances.
     * never <code>null</code>.
     */
	List<StructuredNode> queryNodeList(String xPath) throws XPathExpressionException;

	/**
     * @see StructuredNode#queryNodeList(String)
     */
	StructuredNode[] queryNodes(String xPath) throws XPathExpressionException;

	/**
     * @param xPath relative path at which a {@link String} will be extracted from current
     *              node content.
     * @return {@link String} representation of content resolved by given <code>xPath</code>
     * or <code>null</code> if evaluation result is <code>null</code>.
     */
	String queryString(String xPath) throws XPathExpressionException;

	/**
     * @param xPath relative path at which a {@link Value} instance will be extracted from
     *              current node content.
     * @return {@link Value} representation of content resolved by given <code>xPath</code>.
     * never <code>null</code>.
     */
	Value queryValue(String xPath) throws XPathExpressionException;

	/**
     * @param xPath relative path to test against current node content.
     * @return <code>true</code> if and only if no content found for given <code>xPath</code>.
     */
	boolean isEmpty(String xPath) throws XPathExpressionException;

	/**
	 * Returns the current node name.
	 */
	String getNodeName();

    /**
     * @return parsed result in its {@link Node} object form.  Useful when creating a
     * sub-document.
     */
    Node getDOMNode();

    /**
     * @return a collection of {@link SAXElement} representing nodes above this node in
     * the original document.  Useful when creating a sub-document.
     */
    Stack<? extends SAXElement> getPathToOriginalRoot();

}
