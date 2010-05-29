/*****************************************************************************
 * $Id: DefaultRulesCompilerManager.java 363 2008-08-17 20:58:32Z rlangbehn $
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

import org.codehaus.plexus.logging.AbstractLogEnabled;

import net.sourceforge.rules.compiler.RulesCompiler;

/**
 * This is the default <code>RulesCompilerManager</code> implementation.
 *
 * @plexus.component
 * 
 * @version $Revision: 363 $ $Date: 2008-08-17 22:58:32 +0200 (So, 17 Aug 2008) $
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class DefaultRulesCompilerManager
	extends AbstractLogEnabled
	implements RulesCompilerManager
{
	/**
	 * TODO
	 * 
	 * @plexus.requirement role="net.sourceforge.rules.compiler.RulesCompiler"
	 */
	private Map<String, RulesCompiler> rulesCompilers =
		new HashMap<String, RulesCompiler>();
	
	/* (non-Javadoc)
	 * @see net.sourceforge.rules.compiler.manager.RulesCompilerManager#getRulesCompiler(java.lang.String)
	 */
	public RulesCompiler getRulesCompiler(String rulesCompilerId)
	throws NoSuchRulesCompilerException {

		RulesCompiler rulesCompiler = rulesCompilers.get(rulesCompilerId);

		if (rulesCompiler == null) {
			throw new NoSuchRulesCompilerException(rulesCompilerId);
		}
		
		return rulesCompiler;
	}
}
