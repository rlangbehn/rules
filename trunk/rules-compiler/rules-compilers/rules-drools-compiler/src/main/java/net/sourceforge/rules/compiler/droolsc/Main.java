/*****************************************************************************
 * $Id: Main.java 111 2008-07-20 19:03:01Z rlangbehn $
 *
 * Copyright 2008, The Rules Framework Development Team, and individual
 * contributors as indicated by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ****************************************************************************/
package net.sourceforge.rules.compiler.droolsc;

import java.io.PrintWriter;

/**
 * The main program for the command-line compiler droolsc.
 * 
 * @version $Revision: 111 $ $Date: 2008-07-20 21:03:01 +0200 (So, 20 Jul 2008) $
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class Main
{
	static {
		ClassLoader loader = Main.class.getClassLoader();
		
		if (loader != null) {
			loader.setPackageAssertionStatus("net.sourceforge.rules.compiler.droolsc", true); //$NON-NLS-1$
		}
	}
	
	/**
	 * Command line interface.
	 *
	 * @param args The command line parameters.
	 */
	public static void main(String[] args) {
		System.exit(compile(args));
	}

	/**
	 * Programmatic interface.
	 *
	 * @param args The command line arguments that would normally be
	 * 	passed to the droolsc program as described in the man page.
	 * @return an integer equivalent to the exit value from invoking
	 * 	droolsc, see the man page for details.
	 */
	public static int compile(String[] args) {
		net.sourceforge.rules.compiler.droolsc.main.Main compiler =
			new net.sourceforge.rules.compiler.droolsc.main.Main("droolsc"); //$NON-NLS-1$
		return compiler.compile(args);
	}

	/**
	 * Programmatic interface.
	 *
	 * @param args The command line arguments that would normally be
	 * 	passed to the droolsc program as described in the man page.
	 * @param out PrintWriter to which the compiler's diagnostic
	 * 	output is directed.
	 * @return an integer equivalent to the exit value from invoking
	 * 	droolsc, see the man page for details.
	 */
	public static int compile(String[] args, PrintWriter out) {
		net.sourceforge.rules.compiler.droolsc.main.Main compiler =
			new net.sourceforge.rules.compiler.droolsc.main.Main("droolsc", out); //$NON-NLS-1$
		return compiler.compile(args);
	}
}
