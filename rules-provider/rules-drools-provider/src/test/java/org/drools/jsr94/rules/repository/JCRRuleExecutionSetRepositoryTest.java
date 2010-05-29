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

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.rules.admin.RuleExecutionSet;

import net.sourceforge.rules.tests.DroolsUtil;

import org.drools.repository.JCRRepositoryConfigurator;
import org.drools.repository.JCRRepositoryConfiguratorImpl;
import org.drools.repository.JackrabbitRepositoryConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class JCRRuleExecutionSetRepositoryTest
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private static Repository repository;

	// Static ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		repository = createRepository();
		DroolsUtil.registerRuleServiceProvider();
	}

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		repository = null;
	}

	/**
	 * TODO
	 * 
	 * @return
	 * @throws Exception
	 */
	private static Repository createRepository() throws Exception {
		
		JCRRepositoryConfigurator configurator = new JackrabbitRepositoryConfigurator();
		Repository repository = configurator.getJCRRepository("C:\\Daten\\drools\\5.0.1");
		Credentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
		Session session = repository.login(credentials);
		
		try {
			configurator.setupRulesRepository(session);
		} finally {
			session.logout();
		}
		
		return repository;
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
	 * Test method for {@link org.drools.jsr94.rules.repository.JCRRuleExecutionSetRepository#getRegistrations()}.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testGetRegistrations() throws Exception {
		
		JCRRepositoryConfiguratorImpl repositoryConfigurator =
			(JCRRepositoryConfiguratorImpl)JCRRepositoryConfiguratorImpl.getInstance();
		repositoryConfigurator.setRepository(repository);
		
		JCRRuleExecutionSetRepository resRepository = new JCRRuleExecutionSetRepository();
		
		List<String> registrations = resRepository.getRegistrations();
		assertNotNull("registrations shouldn't be null", registrations);
	}

	/**
	 * Test method for {@link org.drools.jsr94.rules.repository.JCRRuleExecutionSetRepository#getRuleExecutionSet(java.lang.String, java.util.Map)}.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testGetRuleExecutionSet() throws Exception {
		
		JCRRepositoryConfiguratorImpl repositoryConfigurator =
			(JCRRepositoryConfiguratorImpl)JCRRepositoryConfiguratorImpl.getInstance();
		repositoryConfigurator.setRepository(repository);
		
		JCRRuleExecutionSetRepository resRepository = new JCRRuleExecutionSetRepository();

		String bindUri = "net.sourceforge.rules.tests/test-ruleset/1.0";
		Map properties = new HashMap();
		RuleExecutionSet ruleExecutionSet = resRepository.getRuleExecutionSet(bindUri, properties);
		assertNotNull("ruleExecutionSet shouldn't be null", ruleExecutionSet);
/*		
		bindUri = "mortgages/mortgages/2";
		properties = new HashMap();
		ruleExecutionSet = resRepository.getRuleExecutionSet(bindUri, properties);
		assertNotNull("ruleExecutionSet shouldn't be null", ruleExecutionSet);
*/		
	}

	/**
	 * Test method for {@link org.drools.jsr94.rules.repository.JCRRuleExecutionSetRepository#registerRuleExecutionSet(java.lang.String, javax.rules.admin.RuleExecutionSet, java.util.Map)}.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testRegisterRuleExecutionSet() throws Exception {
		
		JCRRepositoryConfiguratorImpl repositoryConfigurator =
			(JCRRepositoryConfiguratorImpl)JCRRepositoryConfiguratorImpl.getInstance();
		repositoryConfigurator.setRepository(repository);
		
		JCRRuleExecutionSetRepository resRepository = new JCRRuleExecutionSetRepository();
		
		String sourceUri = "/net/sourceforge/rules/tests/net.sourceforge.rules.tests.pkg";
		String bindUri = "net.sourceforge.rules.tests/test-ruleset/1.0";
		Map properties = new HashMap();
		properties.put("javax.security.auth.login.name", "admin");
		properties.put("javax.security.auth.login.password", "admin".toCharArray());
		
		RuleExecutionSet ruleExecutionSet = DroolsUtil.createRuleExecutionSet(
				sourceUri,
				bindUri,
				properties
		);
		assertNotNull("ruleExecutionSet shouldn't be null", ruleExecutionSet);
		
		resRepository.registerRuleExecutionSet(bindUri, ruleExecutionSet, properties);
	}

	/**
	 * Test method for {@link org.drools.jsr94.rules.repository.JCRRuleExecutionSetRepository#unregisterRuleExecutionSet(java.lang.String, java.util.Map)}.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testUnregisterRuleExecutionSet() throws Exception {
		
		JCRRepositoryConfiguratorImpl repositoryConfigurator =
			(JCRRepositoryConfiguratorImpl)JCRRepositoryConfiguratorImpl.getInstance();
		repositoryConfigurator.setRepository(repository);
		
		JCRRuleExecutionSetRepository resRepository = new JCRRuleExecutionSetRepository();

		Map properties = new HashMap();
		properties.put("javax.security.auth.login.name", "admin");
		properties.put("javax.security.auth.login.password", "admin".toCharArray());
		
		resRepository.unregisterRuleExecutionSet(
				"org.drools.test/test-ruleset/1.0",
				properties
		);
/*		
		resRepository.unregisterRuleExecutionSet(
				"net.sourceforge.rules.tests/test-ruleset/1.0",
				properties
		);
*/		
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
