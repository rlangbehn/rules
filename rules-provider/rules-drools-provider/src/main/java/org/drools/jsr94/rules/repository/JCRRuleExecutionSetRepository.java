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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.rmi.RemoteException;
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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rules.ConfigurationException;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.Rule;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetCreateException;
import javax.rules.admin.RuleExecutionSetDeregistrationException;
import javax.rules.admin.RuleExecutionSetRegisterException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class JCRRuleExecutionSetRepository
	implements RuleExecutionSetRepository
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
	 * The <code>Log</code> instance for this class.
	 */
	private static final Log log = LogFactory.getLog(
			JCRRuleExecutionSetRepository.class);

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	// Attributes ------------------------------------------------------------

	/**
	 * TODO 
	 */
	private Repository repository;
	
    // Static ----------------------------------------------------------------
    
    // Constructors ----------------------------------------------------------
    
    // RuleExecutionSetRepository implementation -----------------------------
    
	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#getRegistrations()
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRegistrations()
	throws RuleExecutionSetRepositoryException {
		
		boolean traceEnabled = log.isTraceEnabled();

		if (traceEnabled) {
			log.trace("Retrieving rule execution set registrations");
		}
		
		RulesRepository rulesRepository = createRulesRepository(null);
        List<String> registrations = new ArrayList<String>();

		try {
			
	        for (Iterator it = rulesRepository.listPackages(); it.hasNext(); ) {
	        	PackageItem packageItem = (PackageItem)it.next();
	        	String bindUri = PackageItemUtil.getBindUri(packageItem);
	        	
				if (bindUri != null) {
		        	registrations.add(bindUri);
				}
	        }
	        
		} finally {
	        rulesRepository.logout();
		}
		
        if (traceEnabled) {
        	log.trace("Retrieved rule execution set registrations (" + registrations + ")");
        }
        
		return Collections.unmodifiableList(registrations);
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#getRuleExecutionSet(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public RuleExecutionSet getRuleExecutionSet(
			String bindUri,
			Map properties)
	throws RuleExecutionSetRepositoryException {

		if (bindUri == null) {
			String s = "Parameter 'bindUri' cannot be null";
			throw new IllegalArgumentException(s);
		}
		
		boolean traceEnabled = log.isTraceEnabled();

		if (traceEnabled) {
			log.trace("Retrieving rule execution set bound to " + bindUri);
		}
		
		BindUriParser parser = createBindUriParser(bindUri);
        String packageName = parser.getPackageName();
        
		RulesRepository rulesRepository = createRulesRepository(properties);
        Object pkg = null;

        try {
        	
            PackageItem packageItem = rulesRepository.loadPackage(packageName);
            boolean binaryUpToDate = PackageItemUtil.isBinaryUpToDate(packageItem);
            
            if (binaryUpToDate) {
            	byte[] data = packageItem.getCompiledPackageBytes();
            	ByteArrayInputStream in = new ByteArrayInputStream(data);

            	try {
            		ObjectInputStream ois = new ObjectInputStream(in);
            		pkg = ois.readObject();
            		ois.close();
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

        if (pkg != null) {
            ruleExecutionSet = createRuleExecutionSet(pkg, properties);
        }
        
        if (traceEnabled) {
        	log.trace("Retrieved rule execution set (" + ruleExecutionSet + ")");
        }
        
		return ruleExecutionSet;
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#registerRuleExecutionSet(java.lang.String, javax.rules.admin.RuleExecutionSet, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public void registerRuleExecutionSet(
			String bindUri,
			RuleExecutionSet ruleSet,
			Map properties)
	throws RuleExecutionSetRegisterException {
		
		if (bindUri == null) {
			String s = "Parameter 'bindUri' cannot be null";
			throw new IllegalArgumentException(s);
		}
		
        if (ruleSet == null) {
        	String s = "Parameter 'ruleSet' cannot be null";
            throw new IllegalArgumentException(s);
        }
        
		boolean traceEnabled = log.isTraceEnabled();

		if (traceEnabled) {
			log.trace("Registering rule execution set (" + ruleSet + ")");
		}
		
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
		String description = ruleSet.getDescription();
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
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
		try {
	        Object ruleExecutionSetAST = getRuleExecutionSetAST(ruleSet);
			ObjectOutputStream oos = new ObjectOutputStream(out);
	        oos.writeObject(ruleExecutionSetAST);
	        oos.flush();
	        oos.close();
		} catch (IOException e) {
			String s = "Error while serializing rule package";
			throw new RuleExecutionSetRegisterException(s, e);
		} catch (RuleExecutionSetRepositoryException e) {
			String s = "Error while serializing rule package";
			throw new RuleExecutionSetRegisterException(s, e);
		}

        packageItem.updateCompiledPackage(new ByteArrayInputStream(out.toByteArray()));
    	PackageItemUtil.setBindUri(packageItem, bindUri);
    	PackageItemUtil.setBinaryUpToDate(packageItem, true);
        List rules = ruleSet.getRules();
        
        for (Iterator it = rules.iterator(); it.hasNext(); ) {
        	Rule rule = (Rule)it.next();
        	packageItem.addAsset(rule.getName(), rule.getDescription());
        }

        rulesRepository.save();
        rulesRepository.logout();
        
        if (traceEnabled) {
        	log.trace("Successfully registered rule execution set (" + ruleSet + ")");
        }
	}

	/**
	 * TODO
	 * 
	 * @param ruleSet
	 * @return
	 * @throws RuleExecutionSetRepositoryException
	 */
	private Object getRuleExecutionSetAST(RuleExecutionSet ruleSet)
	throws RuleExecutionSetRepositoryException {

		Field field = null;
		Object pkg = null;
		
		try {
			field = ruleSet.getClass().getDeclaredField("pkg");
			field.setAccessible(true);
			pkg = field.get(ruleSet);
		} catch (SecurityException e) {
			String s = "Error while retrieving rule package";
			throw new RuleExecutionSetRepositoryException(s, e); 
		} catch (NoSuchFieldException e) {
			String s = "Error while retrieving rule package";
			throw new RuleExecutionSetRepositoryException(s, e); 
		} catch (IllegalArgumentException e) {
			String s = "Error while retrieving rule package";
			throw new RuleExecutionSetRepositoryException(s, e); 
		} catch (IllegalAccessException e) {
			String s = "Error while retrieving rule package";
			throw new RuleExecutionSetRepositoryException(s, e); 
		}
		
		return pkg;
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#unregisterRuleExecutionSet(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public void unregisterRuleExecutionSet(
			String bindUri,
			Map properties)
	throws RuleExecutionSetDeregistrationException {
		
		if (bindUri == null) {
			String s = "Parameter 'bindUri' cannot be null";
			throw new IllegalArgumentException(s);
		}
		
		boolean traceEnabled = log.isTraceEnabled();

		if (traceEnabled) {
			log.trace("Unregistering rule execution set bound to (" + bindUri + ")");
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
        	log.trace("Successfully unregistered rule execution set bound to (" + bindUri + ")");
        }
	}
	
    // Public ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param repository
	 */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
    // Package protected -----------------------------------------------------
    
    // Protected -------------------------------------------------------------
    
    // Private ---------------------------------------------------------------
	  
	/**
	 * TODO
	 * 
	 * @param bindUri
	 * @return
	 * @throws RuleExecutionSetRepositoryException
	 */
	private BindUriParser createBindUriParser(String bindUri)
	throws RuleExecutionSetRepositoryException {
		
		try {
			return new BindUriParser(bindUri);
		} catch (UnsupportedEncodingException e) {
			String s = "Error while parsing bind uri: " + bindUri;
			throw new RuleExecutionSetRepositoryException(s, e);
		}
	}

	/**
	 * TODO
	 * 
	 * @param properties
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Credentials createCredentials(Map properties) {
		
		Credentials credentials = null;
		
		if (properties != null) {
			String userName = (String)properties.get(USERNAME_PROPERTY_KEY);
			char[] password = (char[])properties.get(PASSWORD_PROPERTY_KEY);

			if (userName != null && password != null) {
				credentials = new SimpleCredentials(
						userName, password
				);
			}
		}
		
		return credentials;
	}

	/**
	 * TODO
	 * 
	 * @param ruleExecutionSetAST
	 * @param properties
	 * @return
	 * @throws RuleExecutionSetRepositoryException
	 */
	@SuppressWarnings("unchecked")
	private RuleExecutionSet createRuleExecutionSet(
			Object ruleExecutionSetAST,
			Map properties)
	throws RuleExecutionSetRepositoryException {
		
        LocalRuleExecutionSetProvider lresp = null;
        
        try {
            RuleAdministrator ra = JSR94Util.getRuleAdministrator();
            lresp = ra.getLocalRuleExecutionSetProvider(properties);
			return lresp.createRuleExecutionSet(ruleExecutionSetAST, properties);
		} catch (RuleExecutionSetCreateException e) {
			String s = "Error while creating rule execution set";
			throw new RuleExecutionSetRepositoryException(s, e);
		} catch (RemoteException e) {
			String s = "Error while creating rule execution set";
			throw new RuleExecutionSetRepositoryException(s, e);
		} catch (ConfigurationException e) {
			String s = "Error while retrieving rule administrator";
			throw new RuleExecutionSetRepositoryException(s, e);
		}
	}

	/**
	 * TODO
	 * 
	 * @param properties
	 * @return
	 * @throws RuleExecutionSetRepositoryException 
	 */
	@SuppressWarnings("unchecked")
	private RulesRepository createRulesRepository(Map properties)
	throws RuleExecutionSetRepositoryException {

		boolean traceEnabled = log.isTraceEnabled();

		if (traceEnabled) {
			StringBuilder sb = new StringBuilder();
			sb.append("Creating rules repository");
			sb.append("\n\tproperties: ").append(properties);
			log.trace(sb.toString());
		}
		
		Repository repository = null;
		
		try {
			repository = getRepository();
		} catch (NamingException e) {
			String s = "Error while looking up repository";
			throw new RuleExecutionSetRepositoryException(s, e);
		}
		
		if (traceEnabled) {
			log.trace("Using repository (" + repository + ")");
		}
		
		Credentials credentials = createCredentials(properties);
		
		if (traceEnabled) {
			log.trace("Created credentials (" + credentials + ")");
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
			log.trace("Created repository session (" + session + ")");
		}
		
		RulesRepository rulesRepository = new RulesRepository(session);
		
		if (traceEnabled) {
			log.trace("Created rules repository (" + rulesRepository + ")");
		}
		
		return rulesRepository;
	}

	/**
	 * TODO
	 * 
	 * @return
	 * @throws NamingException 
	 */
	private Repository getRepository() throws NamingException {
		if (repository == null) {
			Context ctx = new InitialContext();
			String jndiName = "java:/JCRSessionFactory";
			repository = (Repository)ctx.lookup(jndiName);
		}
		
		return repository;
	}
	
	// Inner classes ---------------------------------------------------------
}
