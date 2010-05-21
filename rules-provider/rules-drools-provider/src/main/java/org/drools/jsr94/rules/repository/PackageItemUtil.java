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

import java.lang.reflect.Method;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

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
	public static final String BINARY_UPTODATE_PROPERTY_NAME =
		"drools:binaryUpToDate";
	
    /**
     * TODO 
     */
	// FIXME: this constant should move to class PackageItem
    public static final String BIND_URI_PROPERTY_NAME =
    	"drools:bindURI";
    
	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

    /**
     * TODO
     * 
     * @param packageItem
     * @return
     * @throws RulesRepositoryException
     */
    @SuppressWarnings("unchecked")
	public static boolean isBinaryUpToDate(PackageItem packageItem)
    throws RulesRepositoryException {

    	String methodName = "isBinaryUpToDate";
    	Class clazz = packageItem.getClass();
		Method method = null;
		Object value = null;
    	
		try {
			method = clazz.getMethod(methodName);
		} catch (Exception e) {
			// empty on purpose
		}

		if (method == null) {
	    	return getBooleanProperty(packageItem, BINARY_UPTODATE_PROPERTY_NAME);
		} else {
			try {
				value = method.invoke(packageItem);
				return ((Boolean)value).booleanValue();
			} catch (Exception e) {
		    	return getBooleanProperty(packageItem, BINARY_UPTODATE_PROPERTY_NAME);
			}
		}
    }

    /**
     * TODO
     * 
     * @param packageItem
     * @param status
     * @throws RulesRepositoryException
     */
    @SuppressWarnings("unchecked")
	public static void updateBinaryUpToDate(
    		PackageItem packageItem,
    		boolean status)
    throws RulesRepositoryException {
    	
    	String methodName = "updateBinaryUpToDate";
    	Class clazz = packageItem.getClass();
		Method method = null;
    	
		try {
			method = clazz.getMethod(methodName, boolean.class);
		} catch (Exception e) {
			// empty on purpose
		}

		if (method == null) {
	    	updateBooleanProperty(
	    			packageItem,
	    			BINARY_UPTODATE_PROPERTY_NAME,
	    			status
	    	);
		} else {
			try {
				method.invoke(packageItem, status);
			} catch (Exception e) {
		    	updateBooleanProperty(
		    			packageItem,
		    			BINARY_UPTODATE_PROPERTY_NAME,
		    			status
		    	);
			}
		}
    }
    
    /**
     * TODO
     * 
     * @param packageItem
     * @return
     * @throws RulesRepositoryException
     */
    public static String getBindUri(PackageItem packageItem)
    throws RulesRepositoryException {
    	return getStringProperty(packageItem, BIND_URI_PROPERTY_NAME);
    }

    /**
     * TODO
     * 
     * @param packageItem
     * @param bindUri
     * @throws RulesRepositoryException
     */
    public static void setBindUri(PackageItem packageItem, String bindUri)
    throws RulesRepositoryException {
    	updateStringProperty(
    			packageItem,
    			BIND_URI_PROPERTY_NAME,
    			bindUri
    	);
    }
    
	/**
	 * TODO
	 * 
	 * @param packageItem
	 * @param propertyName
	 * @return
	 * @throws RulesRepositoryException 
	 */
	public static boolean getBooleanProperty(
			PackageItem packageItem,
			String propertyName)
	throws RulesRepositoryException {
		
		try {
			
			Node node = packageItem.getNode();
			boolean propertyValue = false;

			if (node.hasProperty(propertyName)) {
				Property property = node.getProperty(propertyName);
				propertyValue = property.getBoolean();
			}
			
			return propertyValue;
			
		} catch (RepositoryException e) {
			throw new RulesRepositoryException(e);
		}
	}

	/**
	 * TODO
	 * 
	 * @param packageItem
	 * @param propertyName
	 * @return
	 * @throws RulesRepositoryException 
	 */
	public static String getStringProperty(
			PackageItem packageItem,
			String propertyName)
	throws RulesRepositoryException {

		try {
			
			Node node = packageItem.getNode();
			String propertyValue = null;

			if (node.hasProperty(propertyName)) {
				Property property = node.getProperty(propertyName);
				propertyValue = property.getValue().getString();
			}
			
			return propertyValue;
			
		} catch (RepositoryException e) {
			throw new RulesRepositoryException(e);
		}
	}

	/**
	 * TODO
	 * 
	 * @param packageItem
	 * @param propertyName
	 * @param propertyValue
	 * @throws RulesRepositoryException
	 */
	public static void updateBooleanProperty(
			PackageItem packageItem,
			String propertyName,
			boolean propertyValue)
	throws RulesRepositoryException {

		try {
			
			Node node = packageItem.getNode();
			
			node.checkout();
			node.setProperty(propertyName, propertyValue);
			
			Calendar lastModified = Calendar.getInstance();
			node.setProperty(
					PackageItem.LAST_MODIFIED_PROPERTY_NAME,
					lastModified
			);
			
		} catch (RepositoryException e) {
			throw new RulesRepositoryException(e);
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param packageItem
	 * @param propertyName
	 * @param propertyValue
	 * @throws RulesRepositoryException 
	 */
	public static void updateStringProperty(
			PackageItem packageItem,
			String propertyName,
			String propertyValue)
	throws RulesRepositoryException {

		try {
			
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
			
		} catch (RepositoryException e) {
			throw new RulesRepositoryException(e);
		}
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
