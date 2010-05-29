/*****************************************************************************
 * $Id: RulesCompilerManager.java 194 2008-08-02 19:06:16Z rlangbehn $
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

import net.sourceforge.rules.compiler.RulesCompiler;

/**
 * TODO
 * 
 * @version $Revision: 194 $ $Date: 2008-08-02 21:06:16 +0200 (Sa, 02 Aug 2008) $
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public interface RulesCompilerManager
{
	String ROLE = RulesCompilerManager.class.getName();
	
	/**
	 * TODO
	 * 
	 * @param rulesCompilerId
	 * @return
	 * @throws NoSuchRulesCompilerException
	 */
	RulesCompiler getRulesCompiler(String rulesCompilerId)
	throws NoSuchRulesCompilerException;
}