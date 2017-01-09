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
package net.sourceforge.rules.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

import net.sourceforge.rules.provider.drools.RuleServiceProviderImpl;

import org.drools.core.util.DroolsStreamUtils;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public final class DroolsUtil
{
	// Constants -------------------------------------------------------------

	/**
	 * TODO
	 */
	public static final String RULE_SERVICE_PROVIDER_CLASSNAME = RuleServiceProviderImpl.class.getName();
	
	/**
	 * TODO
	 */
	public static final String RULE_SERVICE_PROVIDER_URI = RuleServiceProviderImpl.RULE_SERVICE_PROVIDER_URI;
	
	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param sourceUri
	 * @param bindUri
	 * @param properties
	 * @return
	 * @throws Exception
	 */
	public static RuleExecutionSet createRuleExecutionSet(String sourceUri, String bindUri, Map<?, ?> properties) throws Exception {
		
		Object pkg = loadPackage(sourceUri);
		
		RuleAdministrator ra = getRuleAdministrator();
		
		LocalRuleExecutionSetProvider resp = ra.getLocalRuleExecutionSetProvider(properties);
		
		return resp.createRuleExecutionSet(pkg, properties);
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 * @throws Exception
	 */
	public static RuleAdministrator getRuleAdministrator() throws Exception {
		RuleServiceProvider rsp = getRuleServiceProvider();
		return rsp.getRuleAdministrator();
	}

	/**
	 * TODO
	 * 
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	public static RuleAdministrator getRuleAdministrator(String uri) throws Exception {
		RuleServiceProvider rsp = getRuleServiceProvider(uri);
		return rsp.getRuleAdministrator();
	}

	/**
	 * TODO
	 * 
	 * @return
	 * @throws Exception
	 */
	public static RuleRuntime getRuleRuntime() throws Exception {
		RuleServiceProvider rsp = getRuleServiceProvider();
		return rsp.getRuleRuntime();
	}
	
	/**
	 * TODO
	 * 
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	public static RuleRuntime getRuleRuntime(String uri) throws Exception {
		RuleServiceProvider rsp = getRuleServiceProvider(uri);
		return rsp.getRuleRuntime();
	}
	
	/**
	 * TODO
	 *
	 * @return
	 * @throws Exception
	 */
	public static RuleServiceProvider getRuleServiceProvider() throws Exception {
		return getRuleServiceProvider(RULE_SERVICE_PROVIDER_URI);
	}
	
	/**
	 * TODO
	 *
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	public static RuleServiceProvider getRuleServiceProvider(String uri) throws Exception {
		return RuleServiceProviderManager.getRuleServiceProvider(uri);
	}
	
	/**
	 * TODO
	 * 
	 * @param sourceUri
	 * @param bindUri
	 * @throws Exception
	 */
	public static void registerRuleExecutionSet(String sourceUri, String bindUri, Map<?, ?> properties) throws Exception {
		
		Object pkg = loadPackage(sourceUri);
		
		RuleAdministrator ra = getRuleAdministrator();
		
		LocalRuleExecutionSetProvider resp = ra.getLocalRuleExecutionSetProvider(properties);
		RuleExecutionSet ruleExecutionSet = resp.createRuleExecutionSet(pkg, properties);
		
		ra.registerRuleExecutionSet(bindUri, ruleExecutionSet, properties);
	}

	/**
	 * TODO
	 * 
	 * @return
	 * @throws Exception
	 */
	public static void registerRuleServiceProvider() throws Exception {

		RuleServiceProvider rsp = null;
		
		try {
			rsp = getRuleServiceProvider();
		} catch (Exception e) {
			// empty on purpose
		}
		
		if (rsp == null) {
			Class.forName(RULE_SERVICE_PROVIDER_CLASSNAME);
		}
	}

	/**
	 * TODO
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private static Object loadPackage(String fileName) throws Exception {
		return loadPackage(DroolsUtil.class, fileName);
	}
	
	/**
	 * TODO
	 *
	 * @param clazz
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static Object loadPackage(Class<?> clazz, String fileName) throws Exception {

		InputStream in = clazz.getResourceAsStream(fileName);
		
		if (in == null) {
			throw new Exception("Testresource '" + fileName + "' not found");
		}

		try {
			return DroolsStreamUtils.streamIn(in);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignored
				}
			}
		}
	}
	
	static {
		try {
			registerRuleServiceProvider();
		} catch (Exception e) {
			String s = "Error while registering rule service provider";
			throw new RuntimeException(s, e);
		}
	}
	
	// Constructors ----------------------------------------------------------

    /**
     * Private default ctor to prevent instantiation. 
     */
    private DroolsUtil() {
    }

	// Public ----------------------------------------------------------------
	
	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
