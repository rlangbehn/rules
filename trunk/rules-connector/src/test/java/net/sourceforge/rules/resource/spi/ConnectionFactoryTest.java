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

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.resource.Referenceable;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ResourceAdapter;
import javax.rules.RuleRuntime;
import javax.rules.RuleSession;
import javax.rules.StatelessRuleSession;
import javax.transaction.xa.XAResource;

import net.sourceforge.rules.tests.DroolsUtil;

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
 * @version $Revision$ $Date$
 * @author <a href="mailto:langbehn@netcologne.de">Rainer Langbehn</a>
 */
public class ConnectionFactoryTest
{
	// Constants -------------------------------------------------------------

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
				RuleExecutionSetRepository.class.getName(),
				DefaultRuleExecutionSetRepository.class.getName()
		);
		
		DroolsUtil.registerRuleServiceProvider();
		
		ra = createResourceAdapter();
		ra.start(createBootstrapContext());
		
		mcf = new RuleManagedConnectionFactory();
		mcf.setLogWriter(createLogWriter());
		mcf.setRuleServiceProviderClassName(DroolsUtil.RULE_SERVICE_PROVIDER_CLASSNAME);
		mcf.setRuleServiceProviderUri(DroolsUtil.RULE_SERVICE_PROVIDER_URI);
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
	private static BootstrapContext createBootstrapContext() {
		return null;
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	private static ConnectionManager createConnectionManager() {
		return new TestConnectionManager();
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	private static PrintWriter createLogWriter() {
		return new PrintWriter(System.out, true);
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	private static ResourceAdapter createResourceAdapter() {
		return new RuleResourceAdapter();
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
		
		String sourceUri = "/net/sourceforge/rules/tests/net.sourceforge.rules.tests.res";
		String bindUri = "net.sourceforge.rules.tests/test-ruleset/1.0";
		int sessionType = RuleRuntime.STATELESS_SESSION_TYPE; 
		Map properties = new HashMap();
		
		DroolsUtil.registerRuleExecutionSet(sourceUri, bindUri, properties);
		
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
		
		String sourceUri = "/net/sourceforge/rules/tests/net.sourceforge.rules.tests.res";
		String bindUri = "net.sourceforge.rules.tests/test-ruleset/1.0";
		int sessionType = RuleRuntime.STATELESS_SESSION_TYPE; 
		Map properties = new HashMap();
		
		DroolsUtil.registerRuleExecutionSet(sourceUri, bindUri, properties);
		
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

	// Inner classes ---------------------------------------------------------
}
