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

import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepositoryException;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public final class PackageItemUtil
{
	// Constants -------------------------------------------------------------

    /**
     * TODO 
     */
	// FIXME: this constant should move to class PackageItem
    public static final String BIND_URI_PROPERTY_NAME = "drools:bindURI";
    
	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

    /**
     * TODO
     * 
     * @param packageItem
     * @return
     * @throws RulesRepositoryException
     */
    public static String getBindUri(PackageItem packageItem) throws RulesRepositoryException {
    	return packageItem.getStringProperty(BIND_URI_PROPERTY_NAME);
    }

    /**
     * TODO
     * 
     * @param packageItem
     * @param bindUri
     * @throws RulesRepositoryException
     */
    public static void setBindUri(PackageItem packageItem, String bindUri) throws RulesRepositoryException {
    	packageItem.updateStringProperty(bindUri, BIND_URI_PROPERTY_NAME);
    }
    
	// Constructors ----------------------------------------------------------

    /**
     * Private default ctor to prevent instantiation. 
     */
	private PackageItemUtil() {
	}

	// Public ----------------------------------------------------------------
	
	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
