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
import com.scireum.open.xml.StructuredNode;
import com.scireum.open.xml.XMLReader;
import com.scireum.open.xml.exception.StopParsingException;

import javax.xml.xpath.XPathExpressionException;
import java.io.FileInputStream;

/**
 * Small example class which show how to use the {@link XMLReader}.
 */
public class ExampleXML {

	public static void main(String[] args) throws Exception {
		XMLReader r = new XMLReader();
		// We can add several handlers which are triggered for a given node
		// name. The complete sub-dom of this node is then parsed and made
		// available as a StructuredNode
		r.addHandler("node", new NodeHandler() {

			@Override
			public void process(StructuredNode node) throws StopParsingException{
				try {
					// We can now conveniently query the sub-dom of each node
					// using XPATH:
					System.out.println(node.queryString("name"));
					System.out.println(node.queryValue("price").asDouble(0d));
					if (!node.isEmpty("resources/resource[@type='test']")) {
						System.out.println(node
								.queryString("resources/resource[@type='test']"));
					}
				} catch (XPathExpressionException e) {
					e.printStackTrace();
				}
			}
		});
		// Parse our little test file. Note, that this could be easily processed
		// with a DOM-parser and only serves as showcase. Real life input files
		// would be much bigger...
		r.parse(new FileInputStream("src/examples/test.xml"));
	}
}
