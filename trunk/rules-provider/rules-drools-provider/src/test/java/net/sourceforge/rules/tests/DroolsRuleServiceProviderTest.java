/*****************************************************************************
 * $Id: DroolsRuleServiceProviderTest.java 258 2008-08-07 12:17:47Z rlangbehn $
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.rules.RuleRuntime;
import javax.rules.RuleSession;
import javax.rules.StatelessRuleSession;

import org.drools.jsr94.rules.repository.DefaultRuleExecutionSetRepository;
import org.drools.jsr94.rules.repository.RuleExecutionSetRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO
 * 
 * @version $Revision: 258 $ $Date: 2008-08-07 14:17:47 +0200 (Do, 07 Aug 2008) $
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class DroolsRuleServiceProviderTest
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		System.setProperty(
				RuleExecutionSetRepository.class.getName(),
				DefaultRuleExecutionSetRepository.class.getName()
		);
		
		DroolsUtil.registerRuleServiceProvider();
	}

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	// Constructors ----------------------------------------------------------

	// Public ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testTestRuleset() throws Exception {
		
		List<String> expectedOutput = Arrays.asList(
				"ruleset 'test-ruleset' executed on: " +
				java.net.InetAddress.getLocalHost().getHostName()
		);
		
		runTest(
				"/net/sourceforge/rules/tests/net.sourceforge.rules.tests.res",
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
		return DroolsUtil.getRuleRuntime();
	}
	
	/**
	 * TODO
	 * 
	 * @param sourceUri
	 * @param bindUri
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected void registerRuleExecutionSet(
			String sourceUri,
			String bindUri,
			Map properties)
	throws Exception {
		DroolsUtil.registerRuleExecutionSet(sourceUri, bindUri, properties);
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
		
		registerRuleExecutionSet(sourceUri, bindUri, properties);
		
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
