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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Repository;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rules.admin.RuleExecutionSet;

import junit.framework.TestCase;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class JCRRuleExecutionSetRepositoryIntegrationTest extends TestCase
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
	public JCRRuleExecutionSetRepositoryIntegrationTest(String name) {
		super(name);
	}

	// TestCase overrides ----------------------------------------------------

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// Public ----------------------------------------------------------------

	/**
	 * Test method for {@link org.drools.jsr94.rules.repository.JCRRuleExecutionSetRepository#getRegistrations()}.
	 * 
	 * @throws Exception
	 */
	public final void testGetRegistrations() throws Exception {
		Repository repository = createRepository();
		assertNotNull("repository shouldn't be null", repository);
		
		JCRRuleExecutionSetRepository resRepository = new JCRRuleExecutionSetRepository();
		resRepository.setRepository(repository);
		
		List<String> registrations = resRepository.getRegistrations();
		assertNotNull("registrations shouldn't be null", registrations);
	}

	/**
	 * Test method for {@link org.drools.jsr94.rules.repository.JCRRuleExecutionSetRepository#getRuleExecutionSet(java.lang.String, java.util.Map)}.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public final void testGetRuleExecutionSet() throws Exception {
		Repository repository = createRepository();
		assertNotNull("repository shouldn't be null", repository);
		
		JCRRuleExecutionSetRepository resRepository = new JCRRuleExecutionSetRepository();
		resRepository.setRepository(repository);
		
		String bindUri = "org.drools.test/test-ruleset/1.0";
		Map properties = null;
		RuleExecutionSet ruleExecutionSet = resRepository.getRuleExecutionSet(bindUri, properties);
		assertNotNull("ruleExecutionSet shouldn't be null", ruleExecutionSet);
	}

	/**
	 * Test method for {@link org.drools.jsr94.rules.repository.JCRRuleExecutionSetRepository#registerRuleExecutionSet(java.lang.String, javax.rules.admin.RuleExecutionSet, java.util.Map)}.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public final void testRegisterRuleExecutionSet() throws Exception {
		Repository repository = createRepository();
		assertNotNull("repository shouldn't be null", repository);
		
		JCRRuleExecutionSetRepository resRepository = new JCRRuleExecutionSetRepository();
		resRepository.setRepository(repository);
		
		String sourceUri = "/net/sourceforge/rules/tests/test-ruleset.rules";
		String bindUri = "net.sourceforge.rules.tests/test-ruleset/1.0";
		Map properties = new HashMap();
		properties.put("javax.security.auth.login.name", "admin");
		properties.put("javax.security.auth.login.password", "rainer".toCharArray());
		
		//RuleExecutionSet ruleExecutionSet = (RuleExecutionSet)
		//DroolsPackageLoader.loadPackage(sourceUri);
		//assertNotNull("ruleExecutionSet shouldn't be null", ruleExecutionSet);
		
		//resRepository.registerRuleExecutionSet(bindUri, ruleExecutionSet, properties);
	}

	/**
	 * Test method for {@link org.drools.jsr94.rules.repository.JCRRuleExecutionSetRepository#unregisterRuleExecutionSet(java.lang.String, java.util.Map)}.
	 * 
	 * @throws Exception
	 */
	public final void testUnregisterRuleExecutionSet() throws Exception {
		Repository repository = createRepository();
		assertNotNull("repository shouldn't be null", repository);
		
		JCRRuleExecutionSetRepository resRepository = new JCRRuleExecutionSetRepository();
		resRepository.setRepository(repository);
		
		// TODO
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @return
	 * @throws Exception
	 */
	private Repository createRepository() throws Exception {
		Context ctx = new InitialContext();
		assertNotNull("ctx shouldn't be null", ctx);

		String jndiName = "java:/JCRSessionFactory";
		return (Repository)ctx.lookup(jndiName);
	}

	// Inner classes ---------------------------------------------------------
}
