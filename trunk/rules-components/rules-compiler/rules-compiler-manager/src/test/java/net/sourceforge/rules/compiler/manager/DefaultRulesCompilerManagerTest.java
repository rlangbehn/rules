/*****************************************************************************
 * $Id: DefaultRulesCompilerManagerTest.java 232 2008-08-03 22:55:28Z rlangbehn $
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

import org.codehaus.plexus.PlexusTestCase;

/**
 * TODO
 * 
 * @version $Revision: 232 $ $Date: 2008-08-04 00:55:28 +0200 (Mo, 04 Aug 2008) $
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class DefaultRulesCompilerManagerTest extends PlexusTestCase
{
	/**
	 * Test method for {@link net.sourceforge.rules.compiler.manager.DefaultRulesCompilerManager#getRulesCompiler(java.lang.String)}.
	 * 
	 * @throws Exception
	 */
	public void testGetRulesCompiler() throws Exception {
		
		RulesCompilerManager rulesCompilerManager = (RulesCompilerManager)
		lookup(RulesCompilerManager.ROLE);

		try {
			rulesCompilerManager.getRulesCompiler("foo");
			fail("NoSuchRulesCompilerException expected");
		} catch (NoSuchRulesCompilerException e) {
			// ignored
		}
	}
}
