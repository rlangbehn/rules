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
package net.sourceforge.rules.verifier.drools;

import java.io.File;

import net.sourceforge.rules.verifier.RulesVerifier;
import net.sourceforge.rules.verifier.RulesVerifierConfiguration;

import org.junit.Test;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class DroolsRulesVerifierTest
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// Public ----------------------------------------------------------------

	/**
	 * Test method for {@link net.sourceforge.rules.verifier.drools.DroolsRulesVerifier#verify(net.sourceforge.rules.verifier.RulesVerifierConfiguration)}.
	 */
	@Test
	public final void testVerify() throws Exception {

		RulesVerifier verifier = new DroolsRulesVerifier();
		
		RulesVerifierConfiguration config = new RulesVerifierConfiguration();
		
		File outputDirectory = new File("target/verifier-reports");
		config.setOutputDirectory(outputDirectory);
		
		File rulesDirectory = new File("target/test-classes");
		config.setRulesDirectory(rulesDirectory);
		
		config.addInclude("**/*.drl");
		config.setVerbose(true);
		
		verifier.verify(config);
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
