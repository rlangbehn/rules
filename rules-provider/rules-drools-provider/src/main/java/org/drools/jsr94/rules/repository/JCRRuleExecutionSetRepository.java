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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.rules.ConfigurationException;
import javax.rules.admin.Rule;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetCreateException;
import javax.rules.admin.RuleExecutionSetDeregistrationException;
import javax.rules.admin.RuleExecutionSetRegisterException;

import org.drools.jsr94.rules.admin.RuleExecutionSetImpl;
import org.drools.repository.JCRRepositoryConfiguratorImpl;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.rule.Package;
import org.drools.core.util.DroolsStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class JCRRuleExecutionSetRepository implements RuleExecutionSetRepository
{
	// Constants -------------------------------------------------------------

	/**
	 * TODO
	 */
	private static final String USERNAME_PROPERTY_KEY =
		"javax.security.auth.login.name";
	
	/**
	 * TODO
	 */
	private static final String PASSWORD_PROPERTY_KEY =
		"javax.security.auth.login.password";
	
	/**
	 * The <code>Logger</code> instance for this class.
	 */
	private static final Logger lOG = LoggerFactory.getLogger(JCRRuleExecutionSetRepository.class);

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	// Attributes ------------------------------------------------------------

    // Static ----------------------------------------------------------------
    
    // Constructors ----------------------------------------------------------
    
    // RuleExecutionSetRepository implementation -----------------------------
    
	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#getRegistrations()
	 */
	@Override
	public List<String> getRegistrations() throws RuleExecutionSetRepositoryException {
		
		boolean traceEnabled = lOG.isTraceEnabled();

		if (traceEnabled) {
			lOG.trace("Retrieving rule execution set registrations");
		}
		
		RulesRepository rulesRepository = createRulesRepository(null);
        List<String> registrations = new ArrayList<>();

		try {
			
	        for (Iterator it = rulesRepository.listPackages(); it.hasNext(); ) {
	        	PackageItem packageItem = (PackageItem)it.next();
	        	String bindUri = PackageItemUtil.getBindUri(packageItem);
	            //boolean binaryUpToDate = PackageItemUtil.isBinaryUpToDate(packageItem);
	        	
				if (bindUri != null) {
		        	registrations.add(bindUri);
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append(packageItem.getName());
					sb.append("/");
					sb.append(packageItem.getName());
					sb.append("/");
					sb.append(packageItem.getVersionNumber());
					registrations.add(sb.toString());
				}
	        }
	        
		} finally {
	        rulesRepository.logout();
		}
		
        if (traceEnabled) {
        	lOG.trace("Retrieved rule execution set registrations (" + registrations + ")");
        }
        
		return Collections.unmodifiableList(registrations);
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#getRuleExecutionSet(java.lang.String, java.util.Map)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public RuleExecutionSet getRuleExecutionSet(String bindUri, Map properties) throws RuleExecutionSetRepositoryException {

		if (bindUri == null) {
			throw new IllegalArgumentException("Parameter 'bindUri' cannot be null");
		}
		
		boolean traceEnabled = lOG.isTraceEnabled();

		if (traceEnabled) {
			lOG.trace("Retrieving rule execution set bound to " + bindUri);
		}
		
		BindUriParser parser = createBindUriParser(bindUri);
        String packageName = parser.getPackageName();
        
		RulesRepository rulesRepository = createRulesRepository(properties);
        Object ruleExecutionSetAST = null;

        try {
        	
            PackageItem packageItem = rulesRepository.loadPackage(packageName);

            if (packageItem.isBinaryUpToDate()) {
            	byte[] data = packageItem.getCompiledPackageBytes();

            	try {
            		ruleExecutionSetAST = DroolsStreamUtils.streamIn(data);
            	} catch (IOException e) {
            		String s = "Error while deserializing rule package";
            		throw new RuleExecutionSetRepositoryException(s, e);
            	} catch (ClassNotFoundException e) {
            		String s = "Error while deserializing rule package";
            		throw new RuleExecutionSetRepositoryException(s, e);
            	}
            }
            
        } finally {
            rulesRepository.logout();
        }
        
        RuleExecutionSet ruleExecutionSet = null;

        if (ruleExecutionSetAST != null) {
            try {
				ruleExecutionSet = JSR94Util.createRuleExecutionSet(
						ruleExecutionSetAST,
						properties
				);
			} catch (RuleExecutionSetCreateException e) {
				String s = "Error while creating rule execution set";
				throw new RuleExecutionSetRepositoryException(s, e);
			} catch (ConfigurationException e) {
				String s = "Error while creating rule execution set";
				throw new RuleExecutionSetRepositoryException(s, e);
			} catch (RemoteException e) {
				String s = "Error while creating rule execution set";
				throw new RuleExecutionSetRepositoryException(s, e);
			}
        }
        
        if (traceEnabled) {
        	lOG.trace("Retrieved rule execution set (" + ruleExecutionSet + ")");
        }
        
		return ruleExecutionSet;
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#registerRuleExecutionSet(java.lang.String, javax.rules.admin.RuleExecutionSet, java.util.Map)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void registerRuleExecutionSet(String bindUri, RuleExecutionSet ruleExecutionSet, Map properties) throws RuleExecutionSetRegisterException {
		
		if (bindUri == null) {
			throw new IllegalArgumentException("Parameter 'bindUri' cannot be null");
		}
		
        if (ruleExecutionSet == null) {
            throw new IllegalArgumentException("Parameter 'ruleExecutionSet' cannot be null");
        }
        
		boolean traceEnabled = lOG.isTraceEnabled();

		if (traceEnabled) {
			lOG.trace("Registering rule execution set (" + ruleExecutionSet + ")");
		}

		if (!(ruleExecutionSet instanceof RuleExecutionSetImpl)) {
			String s = "Don't know how to handle rule execution set (" + ruleExecutionSet + ")";
			throw new RuleExecutionSetRegisterException(s);
		}
		
		RuleExecutionSetImpl resi = (RuleExecutionSetImpl)ruleExecutionSet;
		RulesRepository rulesRepository = null;
		BindUriParser parser = null;
		
		try {
			parser = new BindUriParser(bindUri);
		} catch (UnsupportedEncodingException e) {
			String s = "Error while parsing bind uri: " + bindUri;
			throw new RuleExecutionSetRegisterException(s, e);
		}
		
		try {
			rulesRepository = createRulesRepository(properties);
		} catch (RuleExecutionSetRepositoryException e) {
			String s = "Error while creating rules repository";
			throw new RuleExecutionSetRegisterException(s, e);
		}
		
        String packageName = parser.getPackageName();
		String description = resi.getDescription();
		PackageItem packageItem = null;
		
		if (rulesRepository.containsPackage(packageName)) {
			try {
				packageItem = rulesRepository.loadPackage(packageName);
				packageItem.getNode().remove();
			} catch (RepositoryException e) {
				String s = "Error while removing package: " + packageName;
				throw new RuleExecutionSetRegisterException(s, e);
			}
		}
		
        packageItem = rulesRepository.createPackage(packageName, description);
        byte[] data = null;
        Package pkg = null;
        
		try {
	        pkg = getPackage(resi);
	        data = DroolsStreamUtils.streamOut(pkg);
		} catch (IOException e) {
			String s = "Error while serializing rule package";
			throw new RuleExecutionSetRegisterException(s, e);
		} catch (RuleExecutionSetRepositoryException e) {
			String s = "Error while serializing rule package";
			throw new RuleExecutionSetRegisterException(s, e);
		}

        packageItem.updateCompiledPackage(new ByteArrayInputStream(data));
        packageItem.updateBinaryUpToDate(true);
    	PackageItemUtil.setBindUri(packageItem, bindUri);
        List rules = ruleExecutionSet.getRules();
        
        for (Iterator it = rules.iterator(); it.hasNext(); ) {
        	Rule rule = (Rule)it.next();
        	// TODO retrieve category from rule properties
        	String initialCategory = null;
        	
        	packageItem.addAsset(
        			rule.getName(),
        			rule.getDescription(),
        			initialCategory,
        			"drl"
        	);
        }

        rulesRepository.save();
        rulesRepository.logout();
        
        if (traceEnabled) {
        	lOG.trace("Successfully registered rule execution set (" + ruleExecutionSet + ")");
        }
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#unregisterRuleExecutionSet(java.lang.String, java.util.Map)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void unregisterRuleExecutionSet(String bindUri, Map properties) throws RuleExecutionSetDeregistrationException {
		
		if (bindUri == null) {
			throw new IllegalArgumentException("Parameter 'bindUri' cannot be null");
		}
		
		boolean traceEnabled = lOG.isTraceEnabled();

		if (traceEnabled) {
			lOG.trace("Unregistering rule execution set bound to (" + bindUri + ")");
		}
		
		RulesRepository rulesRepository = null;
		BindUriParser parser = null;
		
		try {
			parser = new BindUriParser(bindUri);
		} catch (UnsupportedEncodingException e) {
			String s = "Error while parsing bind uri: " + bindUri;
			throw new RuleExecutionSetDeregistrationException(s, e);
		}
		
		try {
			rulesRepository = createRulesRepository(properties);
		} catch (RuleExecutionSetRepositoryException e) {
			String s = "Error while creating rules repository";
			throw new RuleExecutionSetDeregistrationException(s, e);
		}
		
        String packageName = parser.getPackageName();
        
		if (rulesRepository.containsPackage(packageName)) {
			try {
				PackageItem packageItem = rulesRepository.loadPackage(packageName);
				packageItem.getNode().remove();
			} catch (RepositoryException e) {
				String s = "Error while removing package: " + packageName;
				throw new RuleExecutionSetDeregistrationException(s, e);
			}
		}
        
        rulesRepository.save();
        rulesRepository.logout();
        
        if (traceEnabled) {
        	lOG.trace("Successfully unregistered rule execution set bound to (" + bindUri + ")");
        }
	}
	
    // Public ----------------------------------------------------------------

    // Package protected -----------------------------------------------------
    
    // Protected -------------------------------------------------------------
    
    // Private ---------------------------------------------------------------
	  
	private BindUriParser createBindUriParser(String bindUri) throws RuleExecutionSetRepositoryException {
		
		try {
			return new BindUriParser(bindUri);
		} catch (UnsupportedEncodingException e) {
			String s = "Error while parsing bind uri: " + bindUri;
			throw new RuleExecutionSetRepositoryException(s, e);
		}
	}

	@SuppressWarnings("rawtypes")
	private Credentials createCredentials(Map properties) {
		
		Credentials credentials = null;
		
		if (properties != null) {
			String userName = (String)properties.get(USERNAME_PROPERTY_KEY);
			char[] password = (char[])properties.get(PASSWORD_PROPERTY_KEY);

			if (userName != null && password != null) {
				credentials = new SimpleCredentials(userName, password);
			}
		}
		
		return credentials;
	}

	@SuppressWarnings("rawtypes")
	private RulesRepository createRulesRepository(Map properties) throws RuleExecutionSetRepositoryException {

		boolean traceEnabled = lOG.isTraceEnabled();

		if (traceEnabled) {
			StringBuilder sb = new StringBuilder();
			sb.append("Creating rules repository");
			sb.append("\n\tproperties: ").append(properties);
			lOG.trace(sb.toString());
		}

		Repository repository = JCRRepositoryConfiguratorImpl.getInstance().getJCRRepository(null);
		
		if (traceEnabled) {
			lOG.trace("Using repository (" + repository + ")");
		}
		
		Credentials credentials = createCredentials(properties);
		
		if (traceEnabled) {
			lOG.trace("Created credentials (" + credentials + ")");
		}
		
		Session session = null;

		try {
			if (credentials == null) {
				session = repository.login();
			} else {
				session = repository.login(credentials);
			}
		} catch (RepositoryException e) {
			String s = "Error while creating session";
			throw new RuleExecutionSetRepositoryException(s, e);
		}
		
		if (traceEnabled) {
			lOG.trace("Created repository session (" + session + ")");
		}
		
		RulesRepository rulesRepository = new RulesRepository(session);
		
		if (traceEnabled) {
			lOG.trace("Created rules repository (" + rulesRepository + ")");
		}
		
		return rulesRepository;
	}

	private Package getPackage(final RuleExecutionSetImpl resi) throws RuleExecutionSetRepositoryException {

		try {
			return AccessController.doPrivileged(
					new PrivilegedExceptionAction<Package>() {
						public Package run() throws Exception {
							Field field = resi.getClass().getDeclaredField("pkg");
							field.setAccessible(true);
							return (Package)field.get(resi);
						}
					}
			);
		} catch (PrivilegedActionException e) {
			String s = "Error while retrieving rule package from rule execution set (" + resi + ")";
			throw new RuleExecutionSetRepositoryException(s, e.getException()); 
		}
	}

	// Inner classes ---------------------------------------------------------
}
