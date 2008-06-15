/*****************************************************************************
 * $Id: ConnectionFactoryTest.java 679 2008-06-07 18:22:46Z  $
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

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.resource.Referenceable;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleSession;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:langbehn@netcologne.de">Rainer Langbehn</a>
 */
public class ConnectionFactoryTest extends AbstractTestCase
{
	/**
	 * TODO
	 *
	 * @param name
	 */
	public ConnectionFactoryTest(String name) {
		super(name);
	}

	/**
	 * Test the connection factory allocation.
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testAllocation() throws Exception {
		Object cf = mcf.createConnectionFactory();
		assertTrue(cf instanceof RuleRuntimeHandle);
		RuleRuntime ruleRuntime = (RuleRuntime)cf;
		
		String fileName = "/org/drools/test/test-ruleset.drl";
		String bindUri = "org.drools.test/test-ruleset/1.0";
		registerRuleExecutionSet(fileName, bindUri);
		
		int sessionType = RuleRuntime.STATELESS_SESSION_TYPE; 
		RuleSession ruleSession = null;
		List inputObjects = new ArrayList();
		List outputObjects = null;

		try {
			ruleSession = ruleRuntime.createRuleSession(bindUri, null, sessionType);
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
		// Create connection request infos
		Map properties1 = new HashMap();
		properties1.put("key1", "value1");
		RuleConnectionRequestInfo cri1 = new RuleConnectionRequestInfo(
				"org.drools.test/test-ruleset/1.0",
				properties1,
				RuleRuntime.STATELESS_SESSION_TYPE
		);
		
		Map properties2 = new HashMap();
		properties1.put("key2", "value2");
		RuleConnectionRequestInfo cri2 = new RuleConnectionRequestInfo(
				"org.drools.test/test-ruleset/1.0",
				properties2,
				RuleRuntime.STATELESS_SESSION_TYPE
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
		Object cf = mcf.createConnectionFactory();
		assertNotNull("connectionFactory shouldn't be null", cf);
        assertTrue(cf instanceof Serializable);
        assertTrue(cf instanceof Referenceable);
	}
	
	/**
	 * TODO
	 * 
	 * @param fileName
	 * @param bindUri
	 * @throws Exception
	 */
	private void registerRuleExecutionSet(String fileName, String bindUri)
	throws Exception {
		
		RuleServiceProvider ruleServiceProvider = mcf.getRuleServiceProvider();
		assertNotNull("ruleServiceProvider shouldn't be null", ruleServiceProvider);
		
		RuleAdministrator ruleAdministrator = ruleServiceProvider.getRuleAdministrator();
		assertNotNull("ruleAdministrator shouldn't be null", ruleAdministrator);
		
		LocalRuleExecutionSetProvider ruleExecutionSetProvider = ruleAdministrator.getLocalRuleExecutionSetProvider(null);
		assertNotNull("ruleExecutionSetProvider shouldn't be null", ruleExecutionSetProvider);
		
		InputStream in = getClass().getResourceAsStream(fileName);
		assertNotNull("Testresource '" + fileName + "' not found", in);
		
		RuleExecutionSet ruleExecutionSet = ruleExecutionSetProvider.createRuleExecutionSet(in, null);
		assertNotNull("ruleExecutionSet shouldn't be null", ruleExecutionSet);
		
		ruleAdministrator.registerRuleExecutionSet(bindUri, ruleExecutionSet, null);
	}
}
