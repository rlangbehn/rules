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

import java.io.PrintWriter;
import java.util.Map;

import javax.resource.spi.ConnectionManager;

import junit.framework.TestCase;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:langbehn@netcologne.de">Rainer Langbehn</a>
 */
public abstract class AbstractTestCase extends TestCase
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	protected RuleManagedConnectionFactory mcf;
	
	/**
	 * TODO
	 */
	protected RuleResourceAdapter ra;
	
	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	/**
     * Creates a test case with the given name.
	 *
	 * @param name
	 */
	public AbstractTestCase(String name) {
		super(name);
	}

	// TestCase overrides ----------------------------------------------------

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ra = new RuleResourceAdapter();
		ra.start(null);
		
		mcf = new RuleManagedConnectionFactory();
		mcf.setLogWriter(createLogWriter());
		mcf.setRuleServiceProviderClassName(
				getRuleServiceProviderClassName());
		mcf.setRuleServiceProviderUri(
				getRuleServiceProviderUri());
		mcf.setResourceAdapter(ra);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		ra.stop();
		ra = null;
		
		super.tearDown();
	}

	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @return
	 */
	protected ConnectionManager createConnectionManager() {
		return new TestConnectionManager();
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	protected PrintWriter createLogWriter() {
		return new PrintWriter(System.out, true);
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	protected abstract String getRuleServiceProviderClassName();
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	protected abstract String getRuleServiceProviderUri();

	/**
	 * TODO
	 * 
	 * @param sourceUri
	 * @param bindUri
	 * @param properties
	 * @throws Exception
	 */
	protected abstract void registerRuleExecutionSet(
			String sourceUri,
			String bindUri,
			Map properties)
	throws Exception;
	
	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
