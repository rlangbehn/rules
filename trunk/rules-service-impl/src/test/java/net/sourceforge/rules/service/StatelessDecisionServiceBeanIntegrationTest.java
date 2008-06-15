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

import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.clarkware.junitperf.ConstantTimer;
import com.clarkware.junitperf.LoadTest;
import com.clarkware.junitperf.TestFactory;
import com.clarkware.junitperf.Timer;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class StatelessDecisionServiceBeanIntegrationTest extends TestCase
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

    /**
     * TODO
     */
    private StatelessDecisionService decisionService;
    
	// Static ----------------------------------------------------------------

	/**
     * CLI interface for this test suite.
	 *
	 * @param args the CLI arguments
	 */
	public static void main(String[] args) {
        junit.textui.TestRunner.run(createTestSuite(args));
	}

	/**
     * Create the test suite.
	 *
	 * @param args the CLI arguments
	 * @return
	 */
    public static Test createTestSuite(String[] args) {
        TestSuite testSuite = new TestSuite();

        if (RUN_CONCURRENT_TESTS == false) {
            testSuite.addTestSuite(StatelessDecisionServiceBeanIntegrationTest.class);
        } else {
            int userCount = CONCURRENT_RUN_COUNT;
            int iterations = 1;

            testSuite.addTest(createLoadTest(userCount, iterations));
        }

        return testSuite;
	}

    /**
     * TODO
     *
     * @param userCount
     * @param iterations
     * @return
     */
    protected static Test createLoadTest(int userCount, int iterations) {
        Timer timer = new ConstantTimer(500);
        Test factory = new TestFactory(StatelessDecisionServiceBeanIntegrationTest.class);
        return new LoadTest(factory, userCount, iterations, timer);
    }

	// Constructors ----------------------------------------------------------

	/**
     * Creates a test case with the given name.
	 *
	 * @param name
	 */
	public StatelessDecisionServiceBeanIntegrationTest(String name) {
		super(name);
	}

	// TestCase overrides ----------------------------------------------------

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		Context ctx = new InitialContext();
		assertNotNull("ctx shouldn't be null", ctx);

		String jndiName = "rules-ear-1.0-SNAPSHOT/StatelessDecisionService/remote";
		decisionService = (StatelessDecisionService)ctx.lookup(jndiName);
		assertNotNull("decisionService shouldn't be null", decisionService);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		decisionService = null;

		super.tearDown();
	}

	/**
	 * Test method for {@link net.sourceforge.rules.service.StatelessDecisionServiceBean#decide(java.lang.String, java.util.Map, java.util.List)}.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public final void testDecide() throws Exception {
		String bindUri = "TODO";
		List inputObjects = new ArrayList();
		
		List outputObjects = decisionService.decide(bindUri, null, inputObjects);
		assertNotNull("outputObjects shouldn't be null", outputObjects);
	}
}
