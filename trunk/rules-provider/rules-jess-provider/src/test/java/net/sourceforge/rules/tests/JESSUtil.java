/*****************************************************************************
 * $Id: JESSUtil.java 244 2008-08-04 18:47:35Z rlangbehn $
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
package net.sourceforge.rules.tests;

import java.io.InputStream;

import javax.rules.ConfigurationException;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

/**
 * TODO
 * 
 * @version $Revision: 244 $ $Date: 2008-08-04 20:47:35 +0200 (Mo, 04 Aug 2008) $
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public final class JESSUtil
{
	// Constants -------------------------------------------------------------

	/**
	 * TODO
	 */
	public static final String RULE_SERVICE_PROVIDER_CLASSNAME =
		"jess.jsr94.RuleServiceProviderImpl";
	
	/**
	 * TODO
	 */
	public static final String RULE_SERVICE_PROVIDER_URI =
		"jess.jsr94";
	
	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @return
	 * @throws Exception
	 */
	public static RuleRuntime getRuleRuntime() throws Exception {
		RuleServiceProvider ruleServiceProvider = getRuleServiceProvider();
		return ruleServiceProvider.getRuleRuntime();
	}
	
	/**
	 * TODO
	 * 
	 * @param sourceUri
	 * @param bindUri
	 * @throws Exception
	 */
	public static void registerRuleExecutionSet(
			String sourceUri,
			String bindUri)
	throws Exception {
		
		InputStream in = JESSUtil.class.getResourceAsStream(sourceUri);
		RuleAdministrator ruleAdministrator = getRuleAdministrator();
		LocalRuleExecutionSetProvider ruleExecutionSetProvider =
			ruleAdministrator.getLocalRuleExecutionSetProvider(null);
		RuleExecutionSet ruleExecutionSet =
			ruleExecutionSetProvider.createRuleExecutionSet(in, null);
		ruleAdministrator.registerRuleExecutionSet(
				bindUri, ruleExecutionSet, null);
	}

	/**
	 * TODO
	 * 
	 * @return
	 * @throws Exception
	 */
	private static RuleAdministrator getRuleAdministrator()
	throws Exception {
		RuleServiceProvider ruleServiceProvider = getRuleServiceProvider();
		return ruleServiceProvider.getRuleAdministrator();
	}

	/**
	 * TODO
	 * 
	 * @return
	 * @throws Exception
	 */
	private static RuleServiceProvider getRuleServiceProvider()
	throws Exception {
		return RuleServiceProviderManager.getRuleServiceProvider(
				RULE_SERVICE_PROVIDER_URI
		);
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 * @throws Exception
	 */
	private static void registerRuleServiceProvider() throws Exception {

		ClassLoader cL = Thread.currentThread().getContextClassLoader();
		Class<?> clazz;

		try {
			clazz = cL.loadClass(RULE_SERVICE_PROVIDER_CLASSNAME);
		} catch (ClassNotFoundException e) {
			String s = "Error while loading rule service provider class";
			throw new Exception(s, e);
		}

		try {
			RuleServiceProviderManager.registerRuleServiceProvider(
					RULE_SERVICE_PROVIDER_URI, clazz, cL
			);
		} catch (ConfigurationException e) {
			String s = "Error while registering rule service provider";
			throw new Exception(s, e);
		}
	}

	static {
		try {
			registerRuleServiceProvider();
		} catch (Exception e) {
			String s = "Error while registering drools rule service provider";
			throw new RuntimeException(s, e);
		}
	}
	
	// Constructors ----------------------------------------------------------

    /**
     * Private default ctor to prevent instantiation. 
     */
    private JESSUtil() {
    }

	// Public ----------------------------------------------------------------
	
	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
