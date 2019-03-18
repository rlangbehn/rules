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
package net.sourceforge.rules.resource.spi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.resource.Referenceable;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ResourceAdapter;
import javax.rules.ConfigurationException;
import javax.rules.RuleExecutionException;
import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.RuleSession;
import javax.rules.RuleSessionCreateException;
import javax.rules.RuleSessionTypeUnsupportedException;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetDeregistrationException;
import javax.rules.admin.RuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSetRegisterException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:langbehn@netcologne.de">Rainer Langbehn</a>
 */
public class ConnectionFactoryTest
{
	// Constants -------------------------------------------------------------

	private static final String RULE_SERVICE_PROVIDER_URI = "http://rules.sourceforge.net/provider/test";
	
	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private static RuleManagedConnectionFactory mcf;
	
	/**
	 * TODO
	 */
	private static ResourceAdapter ra;
	
	// Static ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		System.setProperty(
				"org.drools.jsr94.rules.repository.RuleExecutionSetRepository",
				"org.drools.jsr94.rules.repository.DefaultRuleExecutionSetRepository"
		);
		
		RuleServiceProviderManager.registerRuleServiceProvider(RULE_SERVICE_PROVIDER_URI, TestRuleServiceProviderImpl.class);
		
		ra = new RuleResourceAdapter();
		ra.start(null);
		
		mcf = new RuleManagedConnectionFactory();
		mcf.setRuleServiceProviderClassName(TestRuleServiceProviderImpl.class.getName());
		mcf.setRuleServiceProviderUri(RULE_SERVICE_PROVIDER_URI);
		mcf.setResourceAdapter(ra);
	}

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ra.stop();
		ra = null;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	private static ConnectionManager createConnectionManager() {
		return new TestConnectionManager();
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
	 * Test the connection factory allocation.
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAllocation() throws Exception {

		System.out.println("\nTesting connection factory allocation...");
		
		String sourceUri = "/net/sourceforge/rules/tests/net.sourceforge.rules.tests.pkg";
		String bindUri = "net.sourceforge.rules.tests/test-ruleset/1.0";
		int sessionType = RuleRuntime.STATELESS_SESSION_TYPE; 
		Map properties = new HashMap();
		
		registerRuleExecutionSet(sourceUri, bindUri, properties);
		
		// Create the connection manager
		ConnectionManager cm = createConnectionManager();
		assertNotNull("cm shouldn't be null", cm);
		
		// Create the connection factory
		Object cf = mcf.createConnectionFactory(cm);
		assertTrue(cf instanceof RuleRuntimeHandle);
		RuleRuntime ruleRuntime = (RuleRuntime)cf;
		
		RuleSession ruleSession = null;
		List inputObjects = new ArrayList();
		List outputObjects = null;

		try {
			ruleSession = ruleRuntime.createRuleSession(bindUri, properties, sessionType);
			assertTrue(ruleSession instanceof StatelessRuleSessionHandle);
			StatelessRuleSession slrs = (StatelessRuleSession)ruleSession;
			
			outputObjects = slrs.executeRules(inputObjects);
			assertNotNull("outputObjects shouldn't be null", outputObjects);

			ruleSession.release();
			ruleSession = null;

		} finally {
			if (ruleSession != null) {
				ruleSession.release();
			}
		}
	}

	/**
	 * Test the connection matching.
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testMatching() throws Exception {
		
		System.out.println("\nTesting connection matching...");
		
		String sourceUri = "/net/sourceforge/rules/tests/net.sourceforge.rules.tests.pkg";
		String bindUri = "net.sourceforge.rules.tests/test-ruleset/1.0";
		int sessionType = RuleRuntime.STATELESS_SESSION_TYPE; 
		Map properties = new HashMap();
		
		registerRuleExecutionSet(sourceUri, bindUri, properties);
		
		// Create connection request infos
		Map properties1 = new HashMap();
		properties1.put("key1", "value1");
		
		RuleConnectionRequestInfo cri1 = new RuleConnectionRequestInfo(
				bindUri,
				properties1,
				sessionType,
				"anonymous",
				null
		);
		
		Map properties2 = new HashMap();
		properties1.put("key2", "value2");
		
		RuleConnectionRequestInfo cri2 = new RuleConnectionRequestInfo(
				bindUri,
				properties2,
				sessionType,
				"anonymous",
				null
		);
		
        // Check if not same
        assertNotSame(cri1, cri2);

        // Allocate connections
        ManagedConnection mc1 = mcf.createManagedConnection(null, cri1);
        ManagedConnection mc2 = mcf.createManagedConnection(null, cri2);

        // Check if not same
        assertTrue(mc1 != mc2);

        // Create a sef of connections
        Set<ManagedConnection> connectionSet = new HashSet<ManagedConnection>();
        connectionSet.add(mc1);
        connectionSet.add(mc2);

        // Match the first connection
        ConnectionRequestInfo cri3 = new RuleConnectionRequestInfo(cri1);
        assertTrue((cri1 != cri3) && cri1.equals(cri3));
        ManagedConnection mc3 = mcf.matchManagedConnections(connectionSet, null, cri3);
        assertTrue(mc1 == mc3);

        // Match the second connection
        ConnectionRequestInfo cri4 = new RuleConnectionRequestInfo(cri2);
        assertTrue((cri2 != cri4) && cri2.equals(cri4));
        ManagedConnection mc4 = mcf.matchManagedConnections(connectionSet, null, cri4);
        assertTrue(mc2 == mc4);
	}
	
	/**
	 * Test if the connection factory is serializable.
	 *
	 * @throws Exception
	 */
	@Test
	public void testSerializable() throws Exception {
		
		System.out.println("\nTesting if the connection factory is serializable...");
		
		// Create the connection factory
		Object cf = mcf.createConnectionFactory();
		assertNotNull("connectionFactory shouldn't be null", cf);
        assertTrue(cf instanceof Serializable);
        assertTrue(cf instanceof Referenceable);
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

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
	
	// Inner classes ---------------------------------------------------------

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
