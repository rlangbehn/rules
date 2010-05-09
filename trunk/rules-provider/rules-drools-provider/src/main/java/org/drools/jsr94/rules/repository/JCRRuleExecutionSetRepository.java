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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rules.admin.Rule;
import javax.rules.admin.RuleExecutionSet;
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
	// FIXME: this constant should move to class RulesRepository
	public static final String BINARY_UPTODATE_PROPERTY_NAME = "drools:binaryUpToDate";
	
    /**
     * TODO 
     */
	// FIXME: this constant should move to class RulesRepository
    public static final String BIND_URI_PROPERTY_NAME = "drools:bindURI";
    
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
    
	/**
	 * Required default ctor. 
	 */
	public JCRRuleExecutionSetRepository() {
	}

    // RuleExecutionSetRepository implementation -----------------------------
    
	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#getRegistrations()
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRegistrations()
	throws RuleExecutionSetRepositoryException {
		
        List<String> registrations = new ArrayList<String>();
		RulesRepository rulesRepository = null;
		
		try {
			rulesRepository = createRulesRepository(null);
		} catch (RepositoryException e) {
			String s = "Error while creating rules repository";
			throw new RuleExecutionSetRepositoryException(s, e);
		} catch (NamingException e) {
			String s = "Error while creating rules repository";
			throw new RuleExecutionSetRepositoryException(s, e);
		}
        
        for (Iterator it = rulesRepository.listPackages(); it.hasNext(); ) {
        	PackageItem packageItem = (PackageItem)it.next();
        	String bindUri = null;
        	
			try {
				bindUri = getStringProperty(packageItem, BIND_URI_PROPERTY_NAME);
			} catch (RepositoryException e) {
				String s = "Error while retrieving property: " + BIND_URI_PROPERTY_NAME;
				throw new RuleExecutionSetRepositoryException(s, e);
			}
			
			if (bindUri != null) {
	        	registrations.add(bindUri);
			}
        }
        
        rulesRepository.logout();
		return registrations;
	}

	/* (non-Javadoc)
	 * @see org.drools.jsr94.rules.repository.RuleExecutionSetRepository#getRuleExecutionSet(java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public RuleExecutionSet getRuleExecutionSet(
			String bindUri,
			Map properties)
	throws RuleExecutionSetRepositoryException {

		boolean traceEnabled = log.isTraceEnabled();
		
		if (bindUri == null) {
			String s = "bindUri cannot be null";
			throw new RuleExecutionSetRepositoryException(s);
		}
		
		RulesRepository rulesRepository = null;
		BindUriParser parser = null;
		
		try {
			rulesRepository = createRulesRepository(properties);
			
			if (traceEnabled) {
				log.trace("Created rules repository (" + rulesRepository + ")");
			}
			
		} catch (RepositoryException e) {
			String s = "Error while creating rules repository";
			throw new RuleExecutionSetRepositoryException(s, e);
		} catch (NamingException e) {
			String s = "Error while creating rules repository";
			throw new RuleExecutionSetRepositoryException(s, e);
		}
        
		try {
			parser = new BindUriParser(bindUri);
		} catch (UnsupportedEncodingException e) {
			String s = "Error while parsing bind uri: " + bindUri;
			throw new RuleExecutionSetRepositoryException(s, e);
		}
		
        String packageName = parser.getPackageName();
        PackageItem packageItem = rulesRepository.loadPackage(packageName);
        RuleExecutionSet ruleSet = null;
        boolean binaryUpToDate = false;
        
		try {
			binaryUpToDate = getBooleanProperty(
					packageItem,
					BINARY_UPTODATE_PROPERTY_NAME
			);
		} catch (RepositoryException e) {
			String s = "Error while retrieving property: " + BINARY_UPTODATE_PROPERTY_NAME;
			throw new RuleExecutionSetRepositoryException(s, e);
		}
		
        if (binaryUpToDate) {
        	byte[] data = packageItem.getCompiledPackageBytes();
        	ByteArrayInputStream in = new ByteArrayInputStream(data);
        	
        	try {
        		ObjectInputStream ois = new ObjectInputStream(in);
	        	ruleSet = (RuleExecutionSet)ois.readObject();
	        	ois.close();
			} catch (IOException e) {
				String s = "Error while deserializing rule execution set";
				throw new RuleExecutionSetRepositoryException(s, e);
			} catch (ClassNotFoundException e) {
				String s = "Error while deserializing rule execution set";
				throw new RuleExecutionSetRepositoryException(s, e);
			}
        }
        
        rulesRepository.logout();
		return ruleSet;
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
            throw new RuleExecutionSetRegisterException("bindUri cannot be null");
        }
        
        if (ruleSet == null) {
            throw new RuleExecutionSetRegisterException("ruleSet cannot be null");
        }
        
		RulesRepository rulesRepository = null;
		
		try {
			rulesRepository = createRulesRepository(properties);
		} catch (RepositoryException e) {
			String s = "Error while creating rules repository";
			throw new RuleExecutionSetRegisterException(s, e);
		} catch (NamingException e) {
			String s = "Error while creating rules repository";
			throw new RuleExecutionSetRegisterException(s, e);
		}
        
		String name = ruleSet.getName();
		String description = ruleSet.getDescription();
		
		if (rulesRepository.containsPackage(name)) {
			try {
				PackageItem packageItem = rulesRepository.loadPackage(name);
				packageItem.getNode().remove();
			} catch (RepositoryException e) {
				String s = "Error while removing package: " + name;
				throw new RuleExecutionSetRegisterException(s, e);
			}
		}
		
        PackageItem packageItem = rulesRepository.createPackage(name, description);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
		try {
			ObjectOutputStream oos = new ObjectOutputStream(out);
	        oos.writeObject(ruleSet);
	        oos.flush();
	        oos.close();
		} catch (IOException e) {
			String s = "Error while serializing rule execution set";
			throw new RuleExecutionSetRegisterException(s, e);
		}

        packageItem.updateCompiledPackage(new ByteArrayInputStream(out.toByteArray()));
        
        try {
			updateBooleanProperty(packageItem, BINARY_UPTODATE_PROPERTY_NAME, true);
		} catch (RepositoryException e) {
			String s = "Error while updating property: " + BINARY_UPTODATE_PROPERTY_NAME;
			throw new RuleExecutionSetRegisterException(s, e); 
		}

        List rules = ruleSet.getRules();
        
        for (Iterator it = rules.iterator(); it.hasNext(); ) {
        	Rule rule = (Rule)it.next();
        	packageItem.addAsset(rule.getName(), rule.getDescription());
        }

        try {
			updateStringProperty(packageItem, BIND_URI_PROPERTY_NAME, bindUri);
		} catch (RepositoryException e) {
			String s = "Error while updating property: " + BIND_URI_PROPERTY_NAME;
			throw new RuleExecutionSetRegisterException(s, e); 
		}
        
        rulesRepository.save();
        rulesRepository.logout();
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
            throw new RuleExecutionSetDeregistrationException("bindUri cannot be null");
        }
        
		RulesRepository rulesRepository = null;
		BindUriParser parser = null;
		
		try {
			rulesRepository = createRulesRepository(properties);
		} catch (RepositoryException e) {
			String s = "Error while creating rules repository";
			throw new RuleExecutionSetDeregistrationException(s, e);
		} catch (NamingException e) {
			String s = "Error while creating rules repository";
			throw new RuleExecutionSetDeregistrationException(s, e);
		}
        
		try {
			parser = new BindUriParser(bindUri);
		} catch (UnsupportedEncodingException e) {
			String s = "Error while parsing bind uri: " + bindUri;
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
	}
	
    // Public ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param repository
	 */
	// FIXME dependency injection only works for managed components
	@Resource(name="JCRSessionFactory")
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
    // Package protected -----------------------------------------------------
    
    // Protected -------------------------------------------------------------
    
    // Private ---------------------------------------------------------------
	  
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
	 * @param properties
	 * @return
	 * @throws RepositoryException 
	 * @throws NamingException 
	 */
	@SuppressWarnings("unchecked")
	private RulesRepository createRulesRepository(Map properties)
	throws RepositoryException, NamingException {

		boolean traceEnabled = log.isTraceEnabled();

		if (traceEnabled) {
			StringBuilder sb = new StringBuilder();
			sb.append("Creating rules repository");
			sb.append("\n\tproperties: ").append(properties);
			log.trace(sb.toString());
		}
		
		Repository repository = getRepository();
		
		if (traceEnabled) {
			log.trace("Using repository (" + repository + ")");
		}
		
		Credentials credentials = createCredentials(properties);
		
		if (traceEnabled) {
			log.trace("Created credentials (" + credentials + ")");
		}
		
		Session session = null;
		
		if (credentials == null) {
			session = repository.login();
		} else {
			session = repository.login(credentials);
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
	
	/**
	 * TODO
	 * 
	 * @param packageItem
	 * @param propertyName
	 * @return
	 * @throws RepositoryException 
	 */
	private boolean getBooleanProperty(
			PackageItem packageItem,
			String propertyName)
	throws RepositoryException {
		
		Node node = packageItem.getNode();
		boolean propertyValue = false;

		if (node.hasProperty(propertyName)) {
			Property property = node.getProperty(propertyName);
			propertyValue = property.getBoolean();
		}
		
		return propertyValue;
	}

	/**
	 * TODO
	 * 
	 * @param packageItem
	 * @param propertyName
	 * @return
	 * @throws RepositoryException 
	 */
	private String getStringProperty(
			PackageItem packageItem,
			String propertyName)
	throws RepositoryException {
		
		Node node = packageItem.getNode();
		String propertyValue = null;

		if (node.hasProperty(propertyName)) {
			Property property = node.getProperty(propertyName);
			propertyValue = property.getValue().getString();
		}
		
		return propertyValue;
	}

	/**
	 * TODO
	 * 
	 * @param packageItem
	 * @param propertyName
	 * @param propertyValue
	 * @throws RepositoryException
	 */
	private void updateBooleanProperty(
			PackageItem packageItem,
			String propertyName,
			boolean propertyValue)
	throws RepositoryException {
		
		Node node = packageItem.getNode();
		
		node.checkout();
		node.setProperty(propertyName, propertyValue);
		
		Calendar lastModified = Calendar.getInstance();
		node.setProperty(
				PackageItem.LAST_MODIFIED_PROPERTY_NAME,
				lastModified
		);
	}
	
	/**
	 * TODO
	 * 
	 * @param packageItem
	 * @param propertyName
	 * @param propertyValue
	 * @throws RepositoryException 
	 */
	private void updateStringProperty(
			PackageItem packageItem,
			String propertyName,
			String propertyValue)
	throws RepositoryException {
		
		Node node = packageItem.getNode();

		if (propertyValue == null) {
			return;
		}

		node.checkout();
		node.setProperty(propertyName, propertyValue);

		Calendar lastModified = Calendar.getInstance();
		node.setProperty(
				PackageItem.LAST_MODIFIED_PROPERTY_NAME,
				lastModified
		);
	}
	
	// Inner classes ---------------------------------------------------------
}
