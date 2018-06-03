/*   This file is part of lanSimulation.
 *
 *   lanSimulation is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   lanSimulation is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with lanSimulation; if not, write to the Free Software
 *   Foundation, Inc. 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *   Copyright original Java version: 2004 Bart Du Bois, Serge Demeyer
 *   Copyright C++ version: 2006 Matthias Rieger, Bart Van Rompaey
 */
package lanSimulation.internals;

import java.io.IOException;
import java.io.Writer;

import lanSimulation.Network;

/**
A <em>Node</em> represents a single Node in a Local Area Network (LAN).
Several types of Nodes exist.
 */
public class Node {
	//enumeration constants specifying all legal node types
	/**
    A node with type NODE has only basic functionality.
	 */
	//public static final byte NODE = 0;
	/**
    A node with type WORKSTATION may initiate requests on the LAN.
	 */
	//public static final byte WORKSTATION = 1;
	/**
    A node with type PRINTER may accept packages to be printed.
	 */
	//public static final byte PRINTER = 2;

	/**
    Holds the type of the Node.
	 */
	//public byte type_;
	/**
    Holds the name of the Node.
	 */
	public String name_;
	/**
    Holds the next Node in the token ring architecture.
    @see lanSimulation.internals.Node
	 */
	public Node nextNode_;
	
	public Node() {
		
	}
	
	public Node(String name) {
		name_ = name;
	}
	
	/**
Construct a <em>Node</em> with given #type and #name.
<p><strong>Precondition:</strong> (type >= NODE) & (type <= PRINTER);</p>
	 */
	/*
	public Node(byte type, String name) {
		assert (type >= NODE) & (type <= PRINTER);
		type_ = type;
		name_ = name;
		nextNode_ = null;
	}
*/
	/**
Construct a <em>Node</em> with given #type and #name, and which is linked to #nextNode.
<p><strong>Precondition:</strong> (type >= NODE) & (type <= PRINTER);</p>
	 */
	/*
	public Node(byte type, String name, Node nextNode) {
		assert (type >= NODE) & (type <= PRINTER);
		type_ = type;
		name_ = name;
		nextNode_ = nextNode;
	}
*/
	public void logging(Writer report) throws IOException {
		report.write("\tNode '");
		report.write(name_);
		report.write("' passes packet on.\n");
		report.flush();
	}

	public void acceptBroadcastPackage(Writer report) throws IOException {
		report.write("\tNode '");
		report.write(name_);
		report.write("' accepts broadcase packet.\n");
	}
/*
	public void switchPrintTypeNode(StringBuffer buf, boolean t) {
		switch (type_) {
		case Node.NODE:
			buf.append(t ? "Node ": "<node>");
			buf.append(name_);
			buf.append(t ? " [Node]": "</node>");
			break;
		case Node.WORKSTATION:
			new WorkStation(name_).printOnSwitch(buf, t);
			break;
		case Node.PRINTER:
			new Printer(name_).printOnSwitch(buf, t);
			break;
		default:
			buf.append(t ? "(Unexpected)": "<unknown></unknown>");
			break;
		};
	}
*/
	private void printOnSwitch(StringBuffer buf, boolean isHtml) {
		buf.append(!isHtml ? "Node ": "<node>");
		buf.append(name_);
		buf.append(!isHtml ? " [Node]": "</node>");
	}
	
	public boolean atDestination(String destinationOrigin) {
		return ! destinationOrigin.equals(name_);
	}

	/**
	Write a printable representation of #receiver on the given #buf.
	<p><strong>Precondition:</strong> isInitialized();</p>
	 * @param network TODO
	 * @param buf TODO
	 */
	public void printOn (StringBuffer buf) {

		Node currentNode = this;
		do {
			//currentNode.switchPrintTypeNode(buf, true);
			checkTypeOfNode(currentNode, buf, false);
			buf.append(" -> ");
			currentNode = currentNode.nextNode_;
		} while (checkCurrentNode(currentNode));
		buf.append(" ... ");
	}

	private void checkTypeOfNode(Node currentNode, StringBuffer buf, boolean isHtml) {
		if(currentNode instanceof Workstation) {
			((Workstation) currentNode).printOnSwitch(buf, isHtml);
		} else if(currentNode instanceof Printer) {
			((Printer) currentNode).printOnSwitch(buf, isHtml);
		} else if(currentNode instanceof Node) {
			currentNode.printOnSwitch(buf, isHtml);
		} else {
			printUnknownOn(buf, isHtml);
		}
	}
	
	public void printUnknownOn(StringBuffer buf, boolean isHtml) {
		buf.append(isHtml ? "(Unexpected)": "<unknown></unknown>");
	}
	
	/**
	Write an XML representation of #receiver on the given #buf.
	<p><strong>Precondition:</strong> isInitialized();</p>
	 * @param network TODO
	 * @param buf TODO
	 */
	public void printXMLOn (StringBuffer buf) {
	
		Node currentNode = this;
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<network>");
		do {
			buf.append("\n\t");
			//switchPrintTypeNode(buf, false);
			//currentNode.printOnSwitch(buf, false);
			checkTypeOfNode(currentNode, buf, false);
			currentNode = currentNode.nextNode_;
		} while (checkCurrentNode(currentNode));
		buf.append("\n</network>");
	}

	/**
	Write a HTML representation of #receiver on the given #buf.
	<p><strong>Precondition:</strong> isInitialized();</p>
	 * @param network TODO
	 * @param buf TODO
	 */
	public void printHTMLOn (StringBuffer buf) {
	
		buf.append("<HTML>\n<HEAD>\n<TITLE>LAN Simulation</TITLE>\n</HEAD>\n<BODY>\n<H1>LAN SIMULATION</H1>");
		Node currentNode = this;
		buf.append("\n\n<UL>");
		do {
			buf.append("\n\t<LI> ");
			//currentNode.switchPrintTypeNode(buf, true);
			//currentNode.printOnSwitch(buf, true);
			checkTypeOfNode(currentNode, buf, true);
			buf.append(" </LI>");
			currentNode = currentNode.nextNode_;
		} while (checkCurrentNode(currentNode));
		buf.append("\n\t<LI>...</LI>\n</UL>\n\n</BODY>\n</HTML>\n");
	}
	
	private boolean checkCurrentNode(Node currentNode) {
		return currentNode != this;
	}
	
	public class Workstation extends Node{
		
		public Workstation(String name_) {
			//super(WORKSTATION, name_);
			super(name_);
		}
		
		public void printOnSwitch(StringBuffer buf, boolean isHtml) {
			buf.append(!isHtml ? "Workstation ": "<workstation>");
			buf.append(name_);
			buf.append(!isHtml ? " [Workstation]": "</workstation>");
		}
	}
	
	public class Printer extends Node{
		
		public Printer(String name_) {
			//super(PRINTER, name_);
			super(name_);
		}
		
		public void printOnSwitch(StringBuffer buf, boolean isHtml) {
			buf.append(!isHtml ? "Printer ": "<printer>");
			buf.append(name_);
			buf.append(!isHtml ? " [Printer]": "</printer>");
		}
	}
	
}