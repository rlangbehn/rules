/*****************************************************************************
 * $Id$
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
package net.sourceforge.rules.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.drools.rule.Package;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public final class DroolsPackageLoader
{
    // Constants -------------------------------------------------------------

    // Attributes ------------------------------------------------------------

    // Static ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static Package loadPackage(String fileName)
	throws Exception {
		return loadPackage(DroolsPackageLoader.class, fileName);
	}
	
	/**
	 * TODO
	 *
	 * @param clazz
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Package loadPackage(Class clazz, String fileName)
	throws Exception {

		InputStream in = clazz.getResourceAsStream(fileName);
		Object ast = createRuleExecutionSetAst(in);
		
        if (!(ast instanceof Package)) {
        	throw new Exception("Read AST object must be an org.drools.rule.Package. Was " + ast.getClass());
        }
        
		return ((Package)ast);
	}
	
	/**
	 * TODO
	 * 
	 * @param rules
	 * @return
	 * @throws Exception
	 */
	private static Object createRuleExecutionSetAst(InputStream rules)
	throws Exception {

		ObjectInputStream in = null;
		Object ruleExecutionSetAst = null;

		try {
			in = new ObjectInputStream(rules);
			ruleExecutionSetAst = in.readObject();
			in.close();
			in = null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignored
				}
			}
		}

		return ruleExecutionSetAst;
	}
	
    // Constructors ----------------------------------------------------------

	/**
     * Private default ctor to prevent instantiation. 
	 */
	private DroolsPackageLoader() {
	}

    // Public ----------------------------------------------------------------

    // Package protected -----------------------------------------------------

    // Protected -------------------------------------------------------------

    // Private ---------------------------------------------------------------

    // Inner classes ---------------------------------------------------------
}
