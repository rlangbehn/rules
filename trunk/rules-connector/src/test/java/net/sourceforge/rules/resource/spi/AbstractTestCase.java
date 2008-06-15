/*****************************************************************************
 * $Id: AbstractTestCase.java 679 2008-06-07 18:22:46Z  $
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

import junit.framework.TestCase;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:langbehn@netcologne.de">Rainer Langbehn</a>
 */
public abstract class AbstractTestCase extends TestCase
{
	/**
	 * TODO
	 */
	public static final String RULE_SERVICE_PROVIDER_CLASSNAME =
		"org.drools.jsr94.rules.RuleServiceProviderImpl";
	
	/**
	 * TODO
	 */
	public static final String RULE_SERVICE_PROVIDER_URI =
		"http://drools.org/";
	
	/**
	 * TODO
	 */
	protected RuleManagedConnectionFactory mcf;
	
	/**
	 * TODO
	 */
	protected RuleResourceAdapter ra;
	
	/**
	 * TODO
	 *
	 * @param name
	 */
	public AbstractTestCase(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		ra = new RuleResourceAdapter();
		ra.start(null);
		
		mcf = new RuleManagedConnectionFactory();
		mcf.setLogWriter(new PrintWriter(System.out));
		mcf.setRuleServiceProviderClassName(RULE_SERVICE_PROVIDER_CLASSNAME);
		mcf.setRuleServiceProviderUri(RULE_SERVICE_PROVIDER_URI);
		mcf.setResourceAdapter(ra);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		ra.stop();
		ra = null;
		
		super.tearDown();
	}
}
