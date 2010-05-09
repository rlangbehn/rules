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

import javax.rules.ConfigurationException;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;

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
	 * @param uri
	 * @return
	 * @throws ConfigurationException 
	 */
	public static RuleRuntime getRuleRuntime(String uri)
	throws ConfigurationException {
		
		RuleServiceProvider ruleServiceProvider = getRuleServiceProvider(uri);
		return ruleServiceProvider.getRuleRuntime();
	}

	/**
	 * TODO
	 * 
	 * @param uri
	 * @return
	 * @throws ConfigurationException 
	 */
	public static RuleServiceProvider getRuleServiceProvider(String uri)
	throws ConfigurationException {
		
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
