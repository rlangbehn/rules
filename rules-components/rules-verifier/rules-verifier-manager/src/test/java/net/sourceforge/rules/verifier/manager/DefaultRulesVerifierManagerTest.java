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
package net.sourceforge.rules.verifier.manager;

import org.codehaus.plexus.PlexusTestCase;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class DefaultRulesVerifierManagerTest extends PlexusTestCase
{
	/**
	 * Test method for {@link net.sourceforge.rules.verifier.manager.DefaultRulesVerifierManager#getRulesVerifier(java.lang.String)}.
	 * 
	 * @throws Exception
	 */
	public void testGetRulesVerifier() throws Exception {
		
		RulesVerifierManager rulesVerifierManager = (RulesVerifierManager)
		lookup(RulesVerifierManager.ROLE);

		try {
			rulesVerifierManager.getRulesVerifier("foo");
			fail("NoSuchRulesVerifierException expected");
		} catch (NoSuchRulesVerifierException e) {
			// ignored
		}
	}
}
