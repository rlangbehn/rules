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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class Messages
{
    // Constants -------------------------------------------------------------
	
	/**
	 * TODO
	 */
	private static final String BUNDLE_NAME =
		"net.sourceforge.rules.resource.spi.messages"; //$NON-NLS-1$

	/**
	 * TODO
	 */
	private static final ResourceBundle RESOURCE_BUNDLE =
		ResourceBundle.getBundle(BUNDLE_NAME);

	// Attributes ------------------------------------------------------------

    // Static ----------------------------------------------------------------
	
	/**
	 * TODO
	 *
	 * @param key
	 * @return
	 */
	public static String getError(String key) {
		String s = "net.sourceforge.rules.resource.spi.error." + key; //$NON-NLS-1$
		return getString(s);
	}

	/**
	 * TODO
	 *
	 * @param key
	 * @param arguments
	 * @return
	 */
	public static String getError(String key, Object... arguments) {
		String s = "net.sourceforge.rules.resource.spi.error." + key; //$NON-NLS-1$
		return getString(s, arguments);
	}

	/**
	 * TODO
	 *
	 * @param key
	 * @return
	 */
	public static String getInfo(String key) {
		String s = "net.sourceforge.rules.resource.spi.info." + key; //$NON-NLS-1$
		return getString(s);
	}

	/**
	 * TODO
	 *
	 * @param key
	 * @param arguments
	 * @return
	 */
	public static String getInfo(String key, Object... arguments) {
		String s = "net.sourceforge.rules.resource.spi.info." + key; //$NON-NLS-1$
		return getString(s, arguments);
	}

	/**
	 * TODO
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * TODO
	 *
	 * @param key
	 * @param arguments
	 * @return
	 */
	public static String getString(String key, Object... arguments) {
		String theString = getString(key);
		return MessageFormat.format(theString, arguments);
	}
	
	/**
	 * TODO
	 *
	 * @param key
	 * @return
	 */
	public static String getWarning(String key) {
		String s = "net.sourceforge.rules.resource.spi.warning." + key; //$NON-NLS-1$
		return getString(s);
	}

	/**
	 * TODO
	 *
	 * @param key
	 * @param arguments
	 * @return
	 */
	public static String getWarning(String key, Object... arguments) {
		String s = "net.sourceforge.rules.resource.spi.warning." + key; //$NON-NLS-1$
		return getString(s, arguments);
	}

    // Constructors ----------------------------------------------------------
	
	/**
	 * Private default ctor to prevent instantiation.
	 */
	private Messages() {
	}
	
    // Public ----------------------------------------------------------------

    // Package protected -----------------------------------------------------

    // Protected -------------------------------------------------------------

    // Private ---------------------------------------------------------------

    // Inner classes ---------------------------------------------------------
}
