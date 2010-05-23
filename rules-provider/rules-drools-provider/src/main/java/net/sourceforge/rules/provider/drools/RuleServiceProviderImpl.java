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
import org.drools.jsr94.rules.repository.DefaultRuleExecutionSetRepository;
import org.drools.jsr94.rules.repository.RuleExecutionSetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public static final String DEFAULT_RULE_REPOSITORY_CLASS_NAME =
		DefaultRuleExecutionSetRepository.class.getName();
	
	/**
	 * The <code>Logger</code> instance for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(
			RuleServiceProviderImpl.class);

	/**
	 * TODO
	 */
	public static final String RULE_SERVICE_PROVIDER_URI =
		"http://rules.sourceforge.net/provider/drools";
	
	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private static boolean traceEnabled = logger.isTraceEnabled();
	
	/**
	 * TODO
	 */
	private RuleExecutionSetRepository ruleRepository;
	
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

		if (traceEnabled) {
			logger.trace("getRuleAdministrator()");
		}
		
		if (ruleAdministrator == null) {
			ruleAdministrator = new RuleAdministratorImpl(
					getRuleRepository()
			);
		}
		
		return ruleAdministrator;
	}

	/* (non-Javadoc)
	 * @see javax.rules.RuleServiceProvider#getRuleRuntime()
	 */
	@Override
	public synchronized RuleRuntime getRuleRuntime()
	throws ConfigurationException {

		if (traceEnabled) {
			logger.trace("getRuleRuntime()");
		}
		
		if (ruleRuntime == null) {
			ruleRuntime = new RuleRuntimeImpl(
					getRuleRepository()
			);
		}
		
		return ruleRuntime;
	}
	
    // Public ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @return
	 */
	public synchronized RuleExecutionSetRepository getRuleRepository() {

		if (traceEnabled) {
			logger.trace("getRuleRepository()");
		}
		
		if (ruleRepository == null) {
			ruleRepository = createRuleRepository();
		}
		
		return ruleRepository;
	}
	
    // Package protected -----------------------------------------------------
    
    // Protected -------------------------------------------------------------
    
    // Private ---------------------------------------------------------------
    
    /**
     * TODO
     * 
     * @return
     */
    private RuleExecutionSetRepository createRuleRepository() {
    	
    	if (traceEnabled) {
    		logger.trace("createRuleRepository()");
    	}
    	
    	RuleExecutionSetRepository repository = null;
    	repository = RuleRepositoryLoader.loadRuleExecutionSetRepository(
    			DEFAULT_RULE_REPOSITORY_CLASS_NAME
    	);
    	
    	if (traceEnabled) {
    		logger.trace("Created rule execution set repository " + repository);
    	}
    	
    	return repository;
    }
    
	// Inner classes ---------------------------------------------------------
}
