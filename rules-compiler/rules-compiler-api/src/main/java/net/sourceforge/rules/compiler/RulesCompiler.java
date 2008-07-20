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
package net.sourceforge.rules.compiler;

import java.util.List;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public interface RulesCompiler
{
	/**
	 * TODO
	 * 
	 * @param configuration
	 * @return
	 * @throws RulesCompilerException
	 */
	boolean canUpdateTarget(RulesCompilerConfiguration configuration)
	throws RulesCompilerException;
	
	/**
	 * TODO
	 * 
	 * @param configuration
	 * @return
	 * @throws RulesCompilerException
	 */
	List<RulesCompilerError> compile(RulesCompilerConfiguration configuration)
	throws RulesCompilerException;
	
	/**
	 * TODO
	 * 
	 * @param configuration
	 * @return
	 * @throws RulesCompilerException
	 */
	String[] createCommandLine(RulesCompilerConfiguration configuration)
	throws RulesCompilerException;
	
	/**
	 * TODO
	 * 
	 * @param configuration
	 * @return
	 * @throws RulesCompilerException
	 */
	String[] getInputFileEndings(RulesCompilerConfiguration configuration)
	throws RulesCompilerException;
	
	/**
	 * TODO
	 * 
	 * @param configuration
	 * @return
	 * @throws RulesCompilerException
	 */
	String getOutputFile(RulesCompilerConfiguration configuration)
	throws RulesCompilerException;
	
	/**
	 * TODO
	 * 
	 * @param configuration
	 * @return
	 * @throws RulesCompilerException
	 */
	String getOutputFileEnding(RulesCompilerConfiguration configuration)
	throws RulesCompilerException;
}
