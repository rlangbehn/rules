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

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnectionMetaData;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleManagedConnectionMetaData implements ManagedConnectionMetaData
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private RuleManagedConnection mc;

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------
	
	/**
	 * TODO
	 * 
	 * @param mc
	 */
	public RuleManagedConnectionMetaData(RuleManagedConnection mc) {
		this.mc = mc;
	}

	// ManagedConnectionMetaData implementation ------------------------------

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnectionMetaData#getEISProductName()
	 */
	public String getEISProductName() throws ResourceException {
		return "TODO";
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnectionMetaData#getEISProductVersion()
	 */
	public String getEISProductVersion() throws ResourceException {
		return "TODO";
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnectionMetaData#getMaxConnections()
	 */
	public int getMaxConnections() throws ResourceException {
		return Integer.MAX_VALUE;
	}

	/* (non-Javadoc)
	 * @see javax.resource.spi.ManagedConnectionMetaData#getUserName()
	 */
	public String getUserName() throws ResourceException {
		return "TODO";
	}
	
	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
