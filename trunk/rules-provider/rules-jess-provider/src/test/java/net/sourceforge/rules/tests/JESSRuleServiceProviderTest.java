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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.rules.RuleRuntime;
import javax.rules.RuleSession;
import javax.rules.StatelessRuleSession;

import junit.framework.TestCase;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class JESSRuleServiceProviderTest extends TestCase
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	/**
     * Creates a test case with the given name.
	 *
	 * @param name
	 */
	public JESSRuleServiceProviderTest(String name) {
		super(name);
	}

	// Public ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	public void testTestRuleset() throws Exception {
		
		List<String> expectedOutput = Arrays.asList(
				"ruleset 'test-ruleset' executed on: " +
				java.net.InetAddress.getLocalHost().getHostName()
		);
		
		runTest(
				"/net/sourceforge/rules/tests/test-ruleset.clp",
				"net.sourceforge.rules.tests/test-ruleset/1.0",
				Collections.EMPTY_LIST,
				expectedOutput
		);
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param bindUri
	 * @param properties
	 * @param sessionType
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected RuleSession createRuleSession(
			String bindUri,
			Map properties,
			int sessionType)
	throws Exception {
		
		RuleRuntime ruleRuntime = getRuleRuntime();
		assertNotNull("ruleRuntime shouldn't be null", ruleRuntime);
		
		RuleSession ruleSession = ruleRuntime.createRuleSession(
				bindUri,
				properties,
				sessionType
		);
		
		return ruleSession;
	}

	/**
	 * TODO
	 * 
	 * @return
	 * @throws Exception
	 */
	protected RuleRuntime getRuleRuntime() throws Exception {
		return JESSUtil.getRuleRuntime();
	}
	
	/**
	 * TODO
	 * 
	 * @param sourceUri
	 * @param bindUri
	 * @throws Exception
	 */
	protected void registerRuleExecutionSet(
			String sourceUri,
			String bindUri)
	throws Exception {
		JESSUtil.registerRuleExecutionSet(sourceUri, bindUri);
	}
	
	/**
	 * TODO
	 * 
	 * @param sourceUri
	 * @param bindUri
	 * @param inputObjects
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected void runTest(
			String sourceUri,
			String bindUri,
			List inputObjects,
			List expectedOutputObjects)
	throws Exception {
		runTest(sourceUri, bindUri, null, inputObjects, expectedOutputObjects);
	}
	
	/**
	 * TODO
	 * 
	 * @param sourceUri
	 * @param bindUri
	 * @param properties
	 * @param inputObjects
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected void runTest(
			String sourceUri,
			String bindUri,
			Map properties,
			List inputObjects,
			List expectedOutputObjects)
	throws Exception {
		
		registerRuleExecutionSet(sourceUri, bindUri);
		
		StatelessRuleSession ruleSession = (StatelessRuleSession)
		createRuleSession(
				bindUri,
				properties,
				RuleRuntime.STATELESS_SESSION_TYPE
		);
		assertNotNull("ruleSession shouldn't be null", ruleSession);
		
		List outputObjects = ruleSession.executeRules(
				inputObjects
		);
		assertNotNull("outputObjects shouldn't be null", outputObjects);
		assertEquals(expectedOutputObjects,	outputObjects);
	}
	
	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
