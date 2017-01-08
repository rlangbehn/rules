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

import java.rmi.RemoteException;
import java.util.Map;

import javax.rules.ConfigurationException;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetCreateException;

import net.sourceforge.rules.provider.drools.RuleServiceProviderImpl;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public final class JSR94Util
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param ruleExecutionSetAST
	 * @param properties
	 * @return
	 * @throws ConfigurationException
	 * @throws RemoteException
	 * @throws RuleExecutionSetCreateException
	 */
	@SuppressWarnings("rawtypes")
	public static RuleExecutionSet createRuleExecutionSet(Object ruleExecutionSetAST, Map properties)
	throws ConfigurationException, RemoteException, RuleExecutionSetCreateException {
		
        RuleAdministrator ruleAdministrator = getRuleAdministrator();
        LocalRuleExecutionSetProvider lresp = ruleAdministrator.getLocalRuleExecutionSetProvider(properties);
		return lresp.createRuleExecutionSet(ruleExecutionSetAST, properties);
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 * @throws ConfigurationException
	 */
	public static RuleAdministrator getRuleAdministrator() throws ConfigurationException {
		RuleServiceProvider ruleServiceProvider = getRuleServiceProvider();
		return ruleServiceProvider.getRuleAdministrator();
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 * @throws ConfigurationException 
	 */
	public static RuleRuntime getRuleRuntime() throws ConfigurationException {
		RuleServiceProvider ruleServiceProvider = getRuleServiceProvider();
		return ruleServiceProvider.getRuleRuntime();
	}

	/**
	 * TODO
	 * 
	 * @return
	 * @throws ConfigurationException 
	 */
	public static RuleServiceProvider getRuleServiceProvider() throws ConfigurationException {
		String uri = RuleServiceProviderImpl.RULE_SERVICE_PROVIDER_URI;
		return RuleServiceProviderManager.getRuleServiceProvider(uri);
	}
	
	// Constructors ----------------------------------------------------------

	/**
	 * Private default ctor to prevent instantiation. 
	 */
	private JSR94Util() {
	}
	
	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
