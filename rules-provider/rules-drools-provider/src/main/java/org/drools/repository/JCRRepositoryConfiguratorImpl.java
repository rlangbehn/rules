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
package org.drools.repository;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class JCRRepositoryConfiguratorImpl implements JCRRepositoryConfigurator
{
	// Constants -------------------------------------------------------------

	/**
	 * TODO
	 */
	public static final String DEFAULT_REPOSITORY_REF_JNDI_NAME =
		"JCRSessionFactory";

	/**
	 * TODO
	 */
	private static final JCRRepositoryConfigurator INSTANCE =
		new JCRRepositoryConfiguratorImpl();
	
	// Attributes ------------------------------------------------------------

	/**
	 * TODO 
	 */
	private Repository repository;
	
    // Static ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @return
	 */
	public static JCRRepositoryConfigurator getInstance() {
		return INSTANCE;
	}
	
    // Constructors ----------------------------------------------------------

	/**
	 * TODO
	 */
	public JCRRepositoryConfiguratorImpl() {
	}
	
    // JCRRepositoryConfigurator implementation ------------------------------
    
	/* (non-Javadoc)
	 * @see org.drools.repository.JCRRepositoryConfigurator#getJCRRepository(java.lang.String)
	 */
	@Override
	public Repository getJCRRepository(String repositoryRootDirectory) throws RulesRepositoryException {
		return getRepository();
	}

	/* (non-Javadoc)
	 * @see org.drools.repository.JCRRepositoryConfigurator#setupRulesRepository(javax.jcr.Session)
	 */
	@Override
	public void setupRulesRepository(Session session) throws RulesRepositoryException {
	}
	
    // Public ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @return
	 * @throws RulesRepositoryException
	 */
	public Repository getRepository() throws RulesRepositoryException {
		
		if (repository == null) {
			try {
				repository = lookupRepository(DEFAULT_REPOSITORY_REF_JNDI_NAME);
				System.out.println("Looked up JCR repository (" + repository + ")");
			} catch (NamingException e) {
				String s = "Error while looking up repository";
				throw new RulesRepositoryException(s, e);
			}
		}
		
		return repository;
	}
	
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
	 * @param jndiName
	 * @return
	 * @throws NamingException 
	 */
	private Repository lookupRepository(String jndiName) throws NamingException {

		System.out.println("Looking up JCR repository bound to " + jndiName);
		
		Context ctx = new InitialContext();
		return (Repository)ctx.lookup("java:comp/env/" + jndiName);
	}
	
	// Inner classes ---------------------------------------------------------
}
