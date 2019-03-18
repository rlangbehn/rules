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
package net.sourceforge.rules.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.rules.ConfigurationException;
import javax.rules.RuleExecutionException;
import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.RuleSession;
import javax.rules.RuleSessionCreateException;
import javax.rules.RuleSessionTypeUnsupportedException;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetDeregistrationException;
import javax.rules.admin.RuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSetRegisterException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test cases for the <code>StatelessDecisionService</code> implementation.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class StatelessDecisionServiceBeanTest
{
	private static final String RULE_SERVICE_PROVIDER_URI = "http://rules.sourceforge.net/provider/test";
	
	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpRuleServiceProvider() throws Exception {
		
		System.setProperty(
				"org.drools.jsr94.rules.repository.RuleExecutionSetRepository",
				"org.drools.jsr94.rules.repository.DefaultRuleExecutionSetRepository"
		);
		
		RuleServiceProviderManager.registerRuleServiceProvider(RULE_SERVICE_PROVIDER_URI, TestRuleServiceProviderImpl.class);
	}

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownRuleServiceProvider() throws Exception {
		// currently empty on purpose
	}

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTestRuleset() throws Exception {
		
		List<String> expectedOutput = Arrays.asList(
				"ruleset 'test-ruleset' executed on: " +
				java.net.InetAddress.getLocalHost().getHostName()
		);
		
		runTest(
				"/net/sourceforge/rules/tests/net.sourceforge.rules.tests.pkg",
				"net.sourceforge.rules.tests/test-ruleset/1.0",
				Collections.emptyList(),
				expectedOutput
		);
	}

	private StatelessDecisionService createDecisionService() throws Exception {
		
		RuleRuntime ruleRuntime = getRuleRuntime();
		assertNotNull("ruleRuntime shouldn't be null", ruleRuntime);
		
		StatelessDecisionServiceBean decisionService = new StatelessDecisionServiceBean();
		decisionService.setRuleRuntime(ruleRuntime);
		
		return decisionService;
	}

	private RuleRuntime getRuleRuntime() throws Exception {
		
		RuleServiceProvider ruleServiceProvider = RuleServiceProviderManager.getRuleServiceProvider(RULE_SERVICE_PROVIDER_URI);
		assertNotNull("ruleServiceProvider shouldn't be null", ruleServiceProvider);
		
		return ruleServiceProvider.getRuleRuntime();
	}
	
	private void registerRuleExecutionSet(String sourceUri, String bindUri, Map<?, ?> properties) throws Exception {

		Object pkg = null;
		
		RuleServiceProvider ruleServiceProvider = RuleServiceProviderManager.getRuleServiceProvider(RULE_SERVICE_PROVIDER_URI);
		assertNotNull("ruleServiceProvider shouldn't be null", ruleServiceProvider);

		RuleAdministrator ruleAdministrator = ruleServiceProvider.getRuleAdministrator();
		assertNotNull("ruleAdministrator shouldn't be null", ruleAdministrator);
		
		LocalRuleExecutionSetProvider localRuleExecutionSetProvider = ruleAdministrator.getLocalRuleExecutionSetProvider(properties);
		RuleExecutionSet ruleExecutionSet = localRuleExecutionSetProvider.createRuleExecutionSet(pkg, properties);
		
		ruleAdministrator.registerRuleExecutionSet(bindUri, ruleExecutionSet, properties);
	}
	
	private void runTest(String sourceUri, String bindUri, List<?> inputObjects, List<?> expectedOutputObjects) throws Exception {
		runTest(sourceUri, bindUri, null, inputObjects, expectedOutputObjects);
	}
	
	private void runTest(String sourceUri, String bindUri, Map<?, ?> properties, List<?> inputObjects, List<?> expectedOutputObjects) throws Exception {
		
		registerRuleExecutionSet(sourceUri, bindUri, properties);
		
		StatelessDecisionService decisionService = createDecisionService();
		assertNotNull("decisionService shouldn't be null", decisionService);
		
		List<?> outputObjects = decisionService.decide(bindUri, properties, inputObjects);
		
		assertNotNull("outputObjects shouldn't be null", outputObjects);
		assertEquals(expectedOutputObjects,	outputObjects);
	}

	public interface RuleExecutionSetRepository {
		
	    /**
	     * Retrieves a <code>List</code> of the URIs that currently have
	     * <code>RuleExecutionSet</code>s associated with them.
	     * 
	     * An empty list is returned if there are no associations.
	     * 
	     * @return a <code>List</code> of the URIs that currently have
	     *         <code>RuleExecutionSet</code>s associated with them.
	     * @throws RuleExecutionException
	     */
	    List<String> getRegistrations() throws RuleExecutionException;

	    /**
	     * Get the <code>RuleExecutionSet</code> bound to this URI, or return
	     * <code>null</code>.
	     * 
	     * @param bindUri
	     *            the URI associated with the wanted
	     *            <code>RuleExecutionSet</code>.
	     * @param properties
	     * 
	     * @return the <code>RuleExecutionSet</code> bound to the given URI.
	     * @throws RuleExecutionException
	     */
	    RuleExecutionSet getRuleExecutionSet(String bindUri, Map properties) throws RuleExecutionException;

	    /**
	     * Register a <code>RuleExecutionSet</code> under the given URI.
	     * 
	     * @param bindUri the URI to associate with the <code>RuleExecutionSet</code>.
	     * @param ruleSet the <code>RuleExecutionSet</code> to associate with the URI
	     * @param properties
	     * 
	     * @throws RuleExecutionSetRegisterException
	     *             if an error occurred that prevented registration (i.e. if
	     *             bindUri or ruleSet are <code>null</code>)
	     */
	    void registerRuleExecutionSet(String bindUri, RuleExecutionSet ruleSet, Map properties) throws RuleExecutionSetRegisterException;

	    /**
	     * Unregister a <code>RuleExecutionSet</code> from the given URI.
	     * 
	     * @param bindUri the URI to disassociate with the <code>RuleExecutionSet</code>.
	     * @param properties
	     * @throws RuleExecutionSetDeregistrationException
	     *             if an error occurred that prevented deregistration
	     */
	    void unregisterRuleExecutionSet(String bindUri, Map properties) throws RuleExecutionSetDeregistrationException;
	}
	
	public class TestRuleAdministratorImpl implements RuleAdministrator {

		private RuleExecutionSetRepository ruleExecutionSetRepository;
		
		TestRuleAdministratorImpl(RuleExecutionSetRepository ruleExecutionSetRepository) {
			this.ruleExecutionSetRepository = ruleExecutionSetRepository;
		}

		@Override
		public void deregisterRuleExecutionSet(String bindUri, Map properties) throws RuleExecutionSetDeregistrationException, RemoteException {
			ruleExecutionSetRepository.unregisterRuleExecutionSet(bindUri, properties);
		}

		@Override
		public LocalRuleExecutionSetProvider getLocalRuleExecutionSetProvider(Map properties) throws RemoteException {
			return null;
		}

		@Override
		public RuleExecutionSetProvider getRuleExecutionSetProvider(Map properties) throws RemoteException {
			return null;
		}

		@Override
		public void registerRuleExecutionSet(String bindUri, RuleExecutionSet set, Map properties) throws RuleExecutionSetRegisterException, RemoteException {
			ruleExecutionSetRepository.registerRuleExecutionSet(bindUri, set, properties);
		}
	}

	public class TestRuleExecutionSetRepositoryImpl implements RuleExecutionSetRepository {

		private Map<String, RuleExecutionSet> repository = new HashMap<>();
		
		@Override
		public List<String> getRegistrations() throws RuleExecutionException {
			List<String> registrations = new ArrayList<>(repository.keySet());
			return Collections.unmodifiableList(registrations);
		}

		@Override
		public RuleExecutionSet getRuleExecutionSet(String bindUri, Map properties) throws RuleExecutionException {
			return repository.get(bindUri);
		}

		@Override
		public void registerRuleExecutionSet(String bindUri, RuleExecutionSet ruleSet, Map properties) throws RuleExecutionSetRegisterException {
			repository.put(bindUri, ruleSet);
		}

		@Override
		public void unregisterRuleExecutionSet(String bindUri, Map properties) throws RuleExecutionSetDeregistrationException {
			repository.remove(bindUri);
		}
	}
	
	public class TestRuleRuntimeImpl implements RuleRuntime {

		private RuleExecutionSetRepository ruleExecutionSetRepository;
		
		TestRuleRuntimeImpl(RuleExecutionSetRepository ruleExecutionSetRepository) {
			this.ruleExecutionSetRepository = ruleExecutionSetRepository;
		}

		@Override
		public RuleSession createRuleSession(String uri, Map properties, int ruleSessionType) throws RuleSessionTypeUnsupportedException, RuleSessionCreateException, RuleExecutionSetNotFoundException, RemoteException {
			return null;
		}

		@Override
		public List getRegistrations() throws RemoteException {
			try {
				return ruleExecutionSetRepository.getRegistrations();
			} catch (RuleExecutionException e) {
	            String s = "Error while retrieving list of registrations";
	            throw new RuntimeException(s, e);
			}
		}
	}
	
	public class TestRuleServiceProviderImpl extends RuleServiceProvider {

		private RuleAdministrator ruleAdministrator;
		private RuleExecutionSetRepository ruleExecutionSetRepository;
		private RuleRuntime ruleRuntime;
		
		@Override
		public RuleAdministrator getRuleAdministrator() throws ConfigurationException {
			
			if (ruleAdministrator == null) {
				ruleAdministrator = new TestRuleAdministratorImpl(getRuleExecutionSetRepository());
			}
			
			return ruleAdministrator;
		}

		@Override
		public RuleRuntime getRuleRuntime() throws ConfigurationException {
			
			if (ruleRuntime == null) {
				ruleRuntime = new TestRuleRuntimeImpl(getRuleExecutionSetRepository());
			}
			
			return ruleRuntime;
		}
		
		private RuleExecutionSetRepository getRuleExecutionSetRepository() {
			
			if (ruleExecutionSetRepository == null) {
				ruleExecutionSetRepository = new TestRuleExecutionSetRepositoryImpl();
			}
			
			return ruleExecutionSetRepository;
		}
	}
}
