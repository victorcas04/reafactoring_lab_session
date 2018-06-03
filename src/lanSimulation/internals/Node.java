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

	/**
    Holds the name of the Node.
	 */
	public String name_;
	/**
    Holds the next Node in the token ring architecture.
    @see lanSimulation.internals.Node
	 */
	public Node nextNode_;
	
	/**
	 * Constructor por defecto para nodos específicos de tipo Worlstation y Printer
	 */
	
	public Node() {
		
	}
	
	/**
	 * Constructor por defecto para nodos genéricos
	 * @param name nombre del nodo
	 */
	
	public Node(String name) {
		name_ = name;
	}

	/**
	 * Guarda información relativa al nodo actual (mensaje recibido)
	 * @param report donde se guarda la info
	 * @throws IOException
	 */
	
	public void logging(Writer report) throws IOException {
		report.write("\tNode '");
		report.write(name_);
		report.write("' passes packet on.\n");
		report.flush();
	}

	/**
	 * Guarda información relativa al nodo actual (mensaje de broadcast)
	 * @param report donde se guarda la info
	 * @throws IOException
	 */
	
	public void acceptBroadcastPackage(Writer report) throws IOException {
		report.write("\tNode '");
		report.write(name_);
		report.write("' accepts broadcase packet.\n");
	}

	/**
	 * Imprime información relativa al nodo genérico
	 * @param buf string donde se guarda la info
	 * @param isHtml nos permite distinguir el tipo de salida que va a tener en función de si es o no html
	 */
	
	private void printInfoNode(StringBuffer buf, boolean isHtml) {
		buf.append(!isHtml ? "Node ": "<node>");
		buf.append(name_);
		buf.append(!isHtml ? " [Node]": "</node>");
	}
	
	/**
	 * Nos indica si el paquete ha llegado ya al nodo destino
	 * @param destinationOrigin nodo destino
	 * @return true/false si ha llegado o no
	 */
	public boolean atDestination(String destinationOrigin) {
		return ! destinationOrigin.equals(name_);
	}

	/**
	Write a printable representation of #receiver on the given #buf.
	<p><strong>Precondition:</strong> isInitialized();</p>
	 * @param buf string donde se guarda la salida
	 */
	public void printOn (StringBuffer buf) {

		Node currentNode = this;
		do {
			checkTypeOfNode(currentNode, buf, false);
			buf.append(" -> ");
			currentNode = currentNode.nextNode_;
		} while (checkCurrentNode(currentNode));
		buf.append(" ... ");
	}

	/**
	 * Checkea el tipo del nodo actual, para mostrar información específica del tipo que corresponda
	 * @param currentNode nodo actual
	 * @param buf string donde se guarda la salida
	 * @param isHtml nos permite distinguir la sintaxis de la salida en función de si es httml o no
	 */
	
	private void checkTypeOfNode(Node currentNode, StringBuffer buf, boolean isHtml) {
		if(currentNode instanceof Workstation) {
			((Workstation) currentNode).printInfoWorkstation(buf, isHtml);
		} else if(currentNode instanceof Printer) {
			((Printer) currentNode).printInfoPrinter(buf, isHtml);
		} else if(currentNode instanceof Node) {
			currentNode.printInfoNode(buf, isHtml);
		} else {
			printUnknownOn(buf, isHtml);
		}
	}
	
	/**
	 * Caso de imprimir por la salida cuando el nodo no es de los tipos especificados
	 * @param buf string donde se guarda
	 * @param isHtml nos permite distinguir la sintaxis de la salida en función de si es httml o no
	 */
	
	public void printUnknownOn(StringBuffer buf, boolean isHtml) {
		buf.append(isHtml ? "(Unexpected)": "<unknown></unknown>");
	}
	
	/**
	Write an XML representation of #receiver on the given #buf.
	<p><strong>Precondition:</strong> isInitialized();</p>
	 * @param buf string donde se guarda la salida
	 */
	public void printXMLOn (StringBuffer buf) {
	
		Node currentNode = this;
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<network>");
		do {
			buf.append("\n\t");
			checkTypeOfNode(currentNode, buf, false);
			currentNode = currentNode.nextNode_;
		} while (checkCurrentNode(currentNode));
		buf.append("\n</network>");
	}

	/**
	Write a HTML representation of #receiver on the given #buf.
	<p><strong>Precondition:</strong> isInitialized();</p>
	 * @param buf string donde se guarda la salida
	 */
	public void printHTMLOn (StringBuffer buf) {
	
		buf.append("<HTML>\n<HEAD>\n<TITLE>LAN Simulation</TITLE>\n</HEAD>\n<BODY>\n<H1>LAN SIMULATION</H1>");
		Node currentNode = this;
		buf.append("\n\n<UL>");
		do {
			buf.append("\n\t<LI> ");
			checkTypeOfNode(currentNode, buf, true);
			buf.append(" </LI>");
			currentNode = currentNode.nextNode_;
		} while (checkCurrentNode(currentNode));
		buf.append("\n\t<LI>...</LI>\n</UL>\n\n</BODY>\n</HTML>\n");
	}
	
	/**
	 * Comprueba que el nodo actual no sea el que le pasamos como parametro
	 * @param currentNode nodo de parametro
	 * @return true/false si son el mismo o no
	 */
	
	private boolean checkCurrentNode(Node currentNode) {
		return currentNode != this;
	}
	
	/**
	 * Subclase Workstation que hereda de Node
	 * @author Victor de Castro Hurtado
	 *
	 */
	
	public class Workstation extends Node{
			
		/**
		 * Constructor por defecto para nodos de tipo Workstation
		 * @param name_ nombre del nodo
		 */
		
		public Workstation(String name_) {
			super(name_);
		}
		
		/**
		 * Imprime información relativa al nodo de tipo Workstation
		 * @param buf string donde se guarda la info
		 * @param isHtml nos permite distinguir el tipo de salida que va a tener en función de si es o no html
		 */
		
		public void printInfoWorkstation(StringBuffer buf, boolean isHtml) {
			buf.append(!isHtml ? "Workstation ": "<workstation>");
			buf.append(name_);
			buf.append(!isHtml ? " [Workstation]": "</workstation>");
		}
	}
	
	/**
	 * Subclase Printer que hereda de Node
	 * @author Victor de Castro Hurtado
	 */
	
	public class Printer extends Node{
				
		/**
		 * Constructor por defecto para nodos de tipo Printer
		 * @param name nombre del nodo
		 */
		
		public Printer(String name_) {
			super(name_);
		}
		
		/**
		 * Imprime información relativa al nodo de tipo Printer
		 * @param buf string donde se guarda la info
		 * @param isHtml nos permite distinguir el tipo de salida que va a tener en función de si es o no html
		 */
		
		public void printInfoPrinter(StringBuffer buf, boolean isHtml) {
			buf.append(!isHtml ? "Printer ": "<printer>");
			buf.append(name_);
			buf.append(!isHtml ? " [Printer]": "</printer>");
		}
	}
	
}