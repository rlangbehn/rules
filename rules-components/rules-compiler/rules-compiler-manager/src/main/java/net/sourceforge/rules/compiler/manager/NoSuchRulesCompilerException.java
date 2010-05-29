/*****************************************************************************
 * $Id: NoSuchRulesCompilerException.java 111 2008-07-20 19:03:01Z rlangbehn $
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
package net.sourceforge.rules.compiler.manager;

/**
 * TODO
 * 
 * @version $Revision: 111 $ $Date: 2008-07-20 21:03:01 +0200 (So, 20 Jul 2008) $
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class NoSuchRulesCompilerException extends Exception
{
	/**
	 * TODO
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * TODO
	 */
	private final String rulesCompilerId;

	/**
	 * TODO
	 * 
	 * @param compilerId
	 */
	public NoSuchRulesCompilerException(String rulesCompilerId) {
		super("No such rules compiler '" + rulesCompilerId + "'.");
		this.rulesCompilerId = rulesCompilerId;
	}

	/**
	 * @return the rulesCompilerId
	 */
	public String getRulesCompilerId() {
		return rulesCompilerId;
	}
}