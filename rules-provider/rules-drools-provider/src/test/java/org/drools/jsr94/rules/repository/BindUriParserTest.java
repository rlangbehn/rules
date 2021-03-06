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
package org.drools.jsr94.rules.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class BindUriParserTest
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// Public ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testPackageName_RuleExecutionSetName_RuleExecutionSetVersion()
	throws Exception {
		
		String bindUri = "net.sourceforge.rules.tests/test-ruleset/1.0";
		BindUriParser parser = new BindUriParser(bindUri);
		
		String packageName = parser.getPackageName();
		assertNotNull("packageName shouldn't be null", packageName);
		assertEquals("net.sourceforge.rules.tests", packageName);

		String packageVersion = parser.getPackageVersion();
		assertNull("packageVersion should be null", packageVersion);
		
		String ruleExecutionSetName = parser.getRuleExecutionSetName();
		assertNotNull("ruleExecutionSetName shouldn't be null", ruleExecutionSetName);
		assertEquals("test-ruleset", ruleExecutionSetName);
		
		String ruleExecutionSetVersion = parser.getRuleExecutionSetVersion();
		assertNotNull("ruleExecutionSetVersion shouldn't be null", ruleExecutionSetVersion);
		assertEquals("1.0", ruleExecutionSetVersion);
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
