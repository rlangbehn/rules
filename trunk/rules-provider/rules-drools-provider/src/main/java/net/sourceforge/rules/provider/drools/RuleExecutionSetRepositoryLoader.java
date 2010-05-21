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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.drools.jsr94.rules.repository.RuleExecutionSetRepository;

/**
 * Load the <code>RuleExecutionSetRepository</code> using the following algorithm.
 * 
 * 1. If a system property with the name org.drools.jsr94.rules.repository.RuleExecutionSetRepository
 * is defined, then its value is used as the name of the implementation class.
 * 
 * 2. If the drools.properties file exists in the classpath and it is readable by the 
 * java.util.Properties.load(InputStream) method and it contains an entry whose key is 
 * org.drools.jsr94.rules.repository.RuleExecutionSetRepository, then the value of that
 * entry is used as the name of the implementation class.
 * 
 * 3. If a resource with the name of META-INF/services/org.drools.jsr94.rules.repository.RuleExecutionSetRepository exists,
 * then its first line, if present, is used as the UTF-8 encoded name of the implementation class.
 * 
 * 4. Finally, a default implementation class name, if provided, is used.
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public abstract class RuleExecutionSetRepositoryLoader
{
	// Constants -------------------------------------------------------------

	/**
	 * TODO
	 */
	private static final Class<RuleExecutionSetRepository> SERVICE_CLASS =
		RuleExecutionSetRepository.class;
	
	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private static SecuritySupport ss = new SecuritySupport();
	
    // Static ----------------------------------------------------------------

	/**
	 * Loads the <code>RuleExecutionSetRepository</code> using the
	 * algorithm described above.
	 * 
	 * @param defaultFactoryName the className of the default
	 * 	<code>RuleExecutionSetRepository</code> implementation
	 * @return
	 */
	public static RuleExecutionSetRepository loadRuleExecutionSetRepository(
			String defaultFactoryName) {

		ClassLoader cL = ss.getContextClassLoader();

		if (cL == null) {
			cL = RuleExecutionSetRepositoryLoader.class.getClassLoader();
		}
		
		String propertyName = SERVICE_CLASS.getName();
		String factoryName = null;
		Object factory = null;
		
		// Use the system property first
		try {
			factoryName = ss.getSystemProperty(propertyName);
			
			if (factoryName != null) {
				factory = createFactory(cL, factoryName);
			}

		} catch (Throwable t) {
			// empty on purpose
		}
		
		// Use the properties file "drools.properties"
		if (factory == null) {
			// TODO
		}

		// Use the Services API (as detailed in the JAR specification), if available, to determine the classname.
		if (factory == null) {
			String fileName = "META-INF/services/" + propertyName;
			InputStream in = cL.getResourceAsStream(fileName);

			if (in != null) {
				BufferedReader reader = null;
				
				try {
					reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
					factoryName = reader.readLine();
					
					if (factoryName != null) {
						factory = createFactory(cL, factoryName);
					}
					
				} catch (UnsupportedEncodingException e) {
					throw new IllegalStateException("Failed to load " + propertyName + ": " + factoryName, e);
				} catch (IOException e) {
					throw new IllegalStateException("Failed to load " + propertyName + ": " + factoryName, e);
				} finally {
					close(reader);
				}
			}
		}

		// Use the default factory implementation class.
		if (factory == null && defaultFactoryName != null) {
			factory = createFactory(cL, defaultFactoryName);
		}
		
		return (RuleExecutionSetRepository)factory;
	}

	/**
	 * TODO
	 * 
	 * @param closeable
	 */
	private static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// ignored
			}
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param cL
	 * @param factoryName
	 * @return
	 */
	private static Object createFactory(ClassLoader cL, String factoryName) {
		try {
			Class<?> factoryClass = cL.loadClass(factoryName);
			return factoryClass.newInstance();
		} catch (Throwable t) {
			throw new IllegalStateException("Failed to load: " + factoryName, t);
		}
	}

    // Constructors ----------------------------------------------------------
    
    // Public ----------------------------------------------------------------

    // Package protected -----------------------------------------------------
    
    // Protected -------------------------------------------------------------
    
    // Private ---------------------------------------------------------------
    
	// Inner classes ---------------------------------------------------------
}
