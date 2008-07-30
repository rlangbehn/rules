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

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements the JCA ResourceAdapter system contract.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleResourceAdapter implements ResourceAdapter
{
	// Constants -------------------------------------------------------------

	/**
	 * The <code>Log</code> instance for this class.
	 */
	private static final Log log = LogFactory.getLog(RuleResourceAdapter.class);

	// Attributes ------------------------------------------------------------

	/**
	 * The <code>BootstrapContext</code> we are associated with.
	 */
	private BootstrapContext bootstrapContext;
	
	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------
	
	// ResourceAdapter implementation ----------------------------------------

	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapter#endpointActivation(javax.resource.spi.endpoint.MessageEndpointFactory, javax.resource.spi.ActivationSpec)
	 */
	public void endpointActivation(
			MessageEndpointFactory mef,
			ActivationSpec as)
	throws ResourceException {
		log.trace("RuleResourceAdapter.endpointActivation() called"); //$NON-NLS-1$
		throw new NotSupportedException();
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapter#endpointDeactivation(javax.resource.spi.endpoint.MessageEndpointFactory, javax.resource.spi.ActivationSpec)
	 */
	public void endpointDeactivation(
			MessageEndpointFactory mef,
			ActivationSpec as) {
		log.trace("RuleResourceAdapter.endpointDeactivation() called"); //$NON-NLS-1$
		// empty on purpose
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapter#getXAResources(javax.resource.spi.ActivationSpec[])
	 */
	public XAResource[] getXAResources(ActivationSpec[] activationSpecs)
	throws ResourceException {
		log.trace("RuleResourceAdapter.getXAResources() called"); //$NON-NLS-1$
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapter#start(javax.resource.spi.BootstrapContext)
	 */
	public void start(BootstrapContext bootstrapContext)
	throws ResourceAdapterInternalException {
		log.info("start"); //$NON-NLS-1$
		this.bootstrapContext = bootstrapContext;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapter#stop()
	 */
	public void stop() {
		log.info("stop"); //$NON-NLS-1$
		this.bootstrapContext = null;
	}

	// Public ----------------------------------------------------------------

	/**
	 * Returns the <code>BootstrapContext</code> we are associated with.
	 * 
	 * @return the bootstrapContext
	 */
	public BootstrapContext getBootstrapContext() {
		return bootstrapContext;
	}
	
	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
