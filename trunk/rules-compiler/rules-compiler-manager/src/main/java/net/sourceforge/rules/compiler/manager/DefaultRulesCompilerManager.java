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
package net.sourceforge.rules.compiler.manager;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.rules.compiler.RulesCompiler;
import net.sourceforge.rules.compiler.droolsc.DroolscRulesCompiler;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class DefaultRulesCompilerManager implements RulesCompilerManager
{
	/**
	 * TODO
	 */
	private static final RulesCompilerManager INSTANCE =
		new DefaultRulesCompilerManager();

	/**
	 * TODO
	 */
	private Map<String, RulesCompiler> rulesCompilers =
		new HashMap<String, RulesCompiler>();
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public static RulesCompilerManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Private default ctor to force the use of getInstance(). 
	 */
	private DefaultRulesCompilerManager() {
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.compiler.manager.RulesCompilerManager#getRulesCompiler(java.lang.String)
	 */
	public RulesCompiler getRulesCompiler(String rulesCompilerId)
	throws NoSuchRulesCompilerException {

		RulesCompiler rulesCompiler = rulesCompilers.get(rulesCompilerId);

		if (rulesCompiler == null) {
			rulesCompiler = createRulesCompiler(rulesCompilerId);
			rulesCompilers.put(rulesCompilerId, rulesCompiler);
		}
		
		return rulesCompiler;
	}
	
	/**
	 * TODO
	 * 
	 * @param rulesCompilerId
	 * @return
	 * @throws NoSuchRulesCompilerException
	 */
	protected RulesCompiler createRulesCompiler(String rulesCompilerId)
	throws NoSuchRulesCompilerException {

		if ("droolsc".equals(rulesCompilerId)) {
			return new DroolscRulesCompiler();
		} else {
			throw new NoSuchRulesCompilerException(rulesCompilerId);
		}
	}
}
