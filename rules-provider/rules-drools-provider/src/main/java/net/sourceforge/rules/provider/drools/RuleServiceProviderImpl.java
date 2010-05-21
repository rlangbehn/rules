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
package net.sourceforge.rules.provider.drools;

import javax.rules.ConfigurationException;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.admin.RuleAdministrator;

import org.drools.jsr94.rules.RuleRuntimeImpl;
import org.drools.jsr94.rules.admin.RuleAdministratorImpl;
import org.drools.jsr94.rules.repository.RuleExecutionSetRepository;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleServiceProviderImpl extends RuleServiceProvider
{
	// Constants -------------------------------------------------------------

	/**
	 * TODO
	 */
	public static final String RULE_SERVICE_PROVIDER_URI =
		"http://rules.sourceforge.net/provider/drools";
	
	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private RuleExecutionSetRepository repository;
	
	/**
	 * TODO
	 */
	private RuleAdministrator ruleAdministrator;
	
	/**
	 * TODO
	 */
	private RuleRuntime ruleRuntime;
	
    // Static ----------------------------------------------------------------

	static {
		try {
			RuleServiceProviderManager.registerRuleServiceProvider(
					RULE_SERVICE_PROVIDER_URI,
					RuleServiceProviderImpl.class
			);
		} catch (ConfigurationException e) {
			String s = "Error while registering rule service provider " + RULE_SERVICE_PROVIDER_URI;
			throw new RuntimeException(s, e);
		}
	}
	
    // Constructors ----------------------------------------------------------
    
    // RuleServiceProvider Overrides -----------------------------------------

	/* (non-Javadoc)
	 * @see javax.rules.RuleServiceProvider#getRuleAdministrator()
	 */
	@Override
	public synchronized RuleAdministrator getRuleAdministrator()
	throws ConfigurationException {

		if (ruleAdministrator == null) {
			ruleAdministrator = new RuleAdministratorImpl(getRepository());
		}
		
		return ruleAdministrator;
	}

	/* (non-Javadoc)
	 * @see javax.rules.RuleServiceProvider#getRuleRuntime()
	 */
	@Override
	public synchronized RuleRuntime getRuleRuntime()
	throws ConfigurationException {

		if (ruleRuntime == null) {
			ruleRuntime = new RuleRuntimeImpl(getRepository());
		}
		
		return ruleRuntime;
	}
	
    // Public ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @return
	 */
	public synchronized RuleExecutionSetRepository getRepository() {
		
		if (repository == null) {
			repository = createRuleExecutionSetRepository();
		}
		
		return repository;
	}
	
    // Package protected -----------------------------------------------------
    
    // Protected -------------------------------------------------------------
    
    // Private ---------------------------------------------------------------
    
    /**
     * TODO
     * 
     * @return
     */
    private RuleExecutionSetRepository createRuleExecutionSetRepository() {
    	String defaultFactoryName = "org.drools.jsr94.rules.repository.DefaultRuleExecutionSetRepository";
    	return RuleExecutionSetRepositoryLoader.loadRuleExecutionSetRepository(defaultFactoryName);
    }
    
	// Inner classes ---------------------------------------------------------
}
