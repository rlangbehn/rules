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
package net.sourceforge.rules.provider.drools;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
class SecuritySupport
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

    // Static ----------------------------------------------------------------

    // Constructors ----------------------------------------------------------
    
    // Public ----------------------------------------------------------------

    // Package protected -----------------------------------------------------
    
	/**
	 * TODO
	 * 
	 * @return
	 */
	ClassLoader getContextClassLoader() {
		return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
			public ClassLoader run() {
				ClassLoader cL = null;
				cL = Thread.currentThread().getContextClassLoader();

				if (cL == null) {
					cL = ClassLoader.getSystemClassLoader();
				}

				return cL;
			}
		});
	}

	/**
	 * TODO
	 * 
	 * @param cL
	 * @param name
	 * @return
	 */
	InputStream getResourceAsStream(final ClassLoader cL, final String name) {
		return AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
			public InputStream run() {
				InputStream in = null;
				
				if (cL == null) {
					in = ClassLoader.getSystemResourceAsStream(name);
				} else {
					in = cL.getResourceAsStream(name);
				}
				
				return in;
			}
		});
	}
	
	/**
	 * TODO
	 * 
	 * @param key
	 * @return
	 */
	String getSystemProperty(final String key) {
		return AccessController.doPrivileged(new PrivilegedAction<String>() {
			public String run() {
				return System.getProperty(key);
			}
		});
	}
    
    // Protected -------------------------------------------------------------
    
    // Private ---------------------------------------------------------------
    
	// Inner classes ---------------------------------------------------------
}
