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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.resource.Referenceable;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.rules.RuleRuntime;
import javax.rules.RuleSession;
import javax.rules.StatelessRuleSession;
import javax.transaction.xa.XAResource;

import net.sourceforge.rules.tests.DroolsUtil;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:langbehn@netcologne.de">Rainer Langbehn</a>
 */
public class ConnectionFactoryTest extends AbstractTestCase
{
	// Constants -------------------------------------------------------------

    /**
     * Number of concurrent test cases to run, default is 10.
     */
    public static final int CONCURRENT_RUN_COUNT = 10;

    /**
     * Constant indicating whether concurrent test cases
     * should be performed, the default is true.
     */
    public static final boolean RUN_CONCURRENT_TESTS = true;

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	/**
     * Creates a test case with the given name.
	 *
	 * @param name
	 */
	public ConnectionFactoryTest(String name) {
		super(name);
	}

	// AbstractTestCase overrides --------------------------------------------

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.resource.spi.AbstractTestCase#getRuleServiceProviderClassName()
	 */
	@Override
	protected String getRuleServiceProviderClassName() {
		return DroolsUtil.RULE_SERVICE_PROVIDER_CLASSNAME;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.resource.spi.AbstractTestCase#getRuleServiceProviderUri()
	 */
	@Override
	protected String getRuleServiceProviderUri() {
		return DroolsUtil.RULE_SERVICE_PROVIDER_URI;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.resource.spi.AbstractTestCase#registerRuleExecutionSet(java.lang.String, java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void registerRuleExecutionSet(
			String sourceUri,
			String bindUri,
			Map properties)
	throws Exception {
		DroolsUtil.registerRuleExecutionSet(sourceUri, bindUri, properties);
	}

	// TestCase overrides ----------------------------------------------------

	// Public ----------------------------------------------------------------

	/**
	 * Test the connection factory allocation.
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testAllocation() throws Exception {

		System.out.println("\nTesting connection factory allocation...");
		
		// Create the connection manager
		ConnectionManager cm = createConnectionManager();
		assertNotNull("cm shouldn't be null", cm);
		
		// Create the connection factory
		Object cf = mcf.createConnectionFactory(cm);
		assertTrue(cf instanceof RuleRuntimeHandle);
		RuleRuntime ruleRuntime = (RuleRuntime)cf;
		
		String sourceUri = "/net/sourceforge/rules/tests/test-ruleset.rules";
		String bindUri = "net.sourceforge.rules.tests/test-ruleset/1.0";
		int sessionType = RuleRuntime.STATELESS_SESSION_TYPE; 
		Map properties = new HashMap();
		
		registerRuleExecutionSet(sourceUri, bindUri, properties);
		
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
	public void testMatching() throws Exception {
		
		System.out.println("\nTesting connection matching...");
		
		// Create connection request infos
		Map properties1 = new HashMap();
		properties1.put("key1", "value1");
		
		RuleConnectionRequestInfo cri1 = new RuleConnectionRequestInfo(
				"net.sourceforge.rules.tests/test-ruleset/1.0",
				properties1,
				RuleRuntime.STATELESS_SESSION_TYPE,
				"anonymous",
				null
		);
		
		Map properties2 = new HashMap();
		properties1.put("key2", "value2");
		
		RuleConnectionRequestInfo cri2 = new RuleConnectionRequestInfo(
				"net.sourceforge.rules.tests/test-ruleset/1.0",
				properties2,
				RuleRuntime.STATELESS_SESSION_TYPE,
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
	public void testSerializable() throws Exception {
		
		System.out.println("\nTesting if the connection factory is serializable...");
		
		// Create the connection factory
		Object cf = mcf.createConnectionFactory();
		assertNotNull("connectionFactory shouldn't be null", cf);
        assertTrue(cf instanceof Serializable);
        assertTrue(cf instanceof Referenceable);
	}

	/**
	 * Test if the connection supports transactions.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testTransactionSupport() throws Exception {
		
		System.out.println("\nTesting if the connection supports transactions...");
		
		// Create the connection manager
		ConnectionManager cm = createConnectionManager();
		assertNotNull("cm shouldn't be null", cm);

		// Create the connection factory
		Object cf = mcf.createConnectionFactory(cm);
		assertTrue(cf instanceof RuleRuntimeHandle);
		RuleRuntime ruleRuntime = (RuleRuntime)cf;
		
		String sourceUri = "/net/sourceforge/rules/tests/test-ruleset.rules";
		String bindUri = "net.sourceforge.rules.tests/test-ruleset/1.0";
		int sessionType = RuleRuntime.STATELESS_SESSION_TYPE; 
		Map properties = new HashMap();
		
		registerRuleExecutionSet(sourceUri, bindUri, properties);
		
		RuleSession ruleSession = ruleRuntime.createRuleSession(bindUri, properties, sessionType);
		assertTrue(ruleSession instanceof XAResource);
		ruleSession.release();
	}
	
	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
