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
import java.util.Properties;

import org.drools.jsr94.rules.repository.RuleExecutionSetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * The <code>Logger</code> instance for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(
			RuleExecutionSetRepositoryLoader.class);

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
	
	/**
	 * TODO
	 */
	private static boolean traceEnabled = logger.isTraceEnabled();
	
    // Static ----------------------------------------------------------------

	/**
	 * Loads the <code>RuleExecutionSetRepository</code> using the
	 * algorithm described above.
	 * 
	 * @param defaultClassName the FQN of the default
	 * 	<code>RuleExecutionSetRepository</code> implementation class
	 * @return
	 */
	public static RuleExecutionSetRepository loadRuleExecutionSetRepository(
			String defaultClassName) {

		if (traceEnabled) {
			logger.trace("loadRuleExecutionSetRepository(" + defaultClassName + ")");
		}
		
		ClassLoader cL = ss.getContextClassLoader();

		if (cL == null) {
			cL = RuleExecutionSetRepositoryLoader.class.getClassLoader();
		}
		
		// Use the system property first
		Object repository = createRepositoryBySystemProperty(cL);
		
		// Use the properties file "drools.properties"
		if (repository == null) {
			repository = createRepositoryByPropertiesResource(cL);
		}

		// Use the Services API (as detailed in the JAR specification), if available, to determine the class name.
		if (repository == null) {
			repository = createRepositoryByServicesAPI(cL);
		}

		// Use the default factory implementation class.
		if (repository == null && defaultClassName != null) {
			repository = createRepository(cL, defaultClassName);
		}
		
		return (RuleExecutionSetRepository)repository;
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
	 * @param className
	 * @return
	 */
	private static Object createRepository(
			ClassLoader cL,
			String className) {

		if (traceEnabled) {
			logger.trace("createReposiory(" + cL + ", " + className + ")");
		}
		
		try {
			Class<?> clazz = cL.loadClass(className);
			return clazz.newInstance();
		} catch (Throwable t) {
			throw new IllegalStateException("Failed to load: " + className, t);
		}
	}

	/**
	 * TODO
	 * 
	 * @param cL
	 * @return
	 */
	private static Object createRepositoryByPropertiesResource(
			ClassLoader cL) {

		if (traceEnabled) {
			logger.trace("createRepositoryByPropertiesResource(" + cL + ")");
		}
		
		String propertyName = SERVICE_CLASS.getName();
		String resourceName = "drools.properties";
		InputStream in = cL.getResourceAsStream(resourceName);
		Object repository = null;

		if (in != null) {
			Properties properties = new Properties();

			try {
				properties.load(in);
			} catch (IOException e) {
				String s = "Failed to load resource " + resourceName;
				throw new IllegalStateException(s, e);
			} finally {
				close(in);
			}

			String className = properties.getProperty(propertyName);

			if (className != null) {
				repository = createRepository(cL, className);
			}
		}
		
		return repository;
	}

	/**
	 * TODO
	 * 
	 * @param cL
	 * @return
	 */
	private static Object createRepositoryByServicesAPI(
			ClassLoader cL) {
		
		if (traceEnabled) {
			logger.trace("createRepositoryByServicesAPI(" + cL + ")");
		}
		
		String propertyName = SERVICE_CLASS.getName();
		String resourceName = "META-INF/services/" + propertyName;
		InputStream in = ss.getResourceAsStream(cL, resourceName);
		Object repository = null;

		if (in != null) {
			BufferedReader reader = null;
			String className = null;
			
			try {
				reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				className = reader.readLine();
				
				if (className != null) {
					repository = createRepository(cL, className);
				}
				
			} catch (UnsupportedEncodingException e) {
				String s = "Failed to load " + propertyName + ": " + className;
				throw new IllegalStateException(s, e);
			} catch (IOException e) {
				String s = "Failed to load " + propertyName + ": " + className;
				throw new IllegalStateException(s, e);
			} finally {
				close(reader);
			}
		}
		
		return repository;
	}

	/**
	 * TODO
	 * 
	 * @param cL
	 * @return
	 */
	private static Object createRepositoryBySystemProperty(
			ClassLoader cL) {

		if (traceEnabled) {
			logger.trace("createRepositoryBySystemProperty(" + cL + ")");
		}
		
		String propertyName = SERVICE_CLASS.getName();
		Object repository = null;
		
		try {
			String className = ss.getSystemProperty(propertyName);
			
			if (className != null) {
				repository = createRepository(cL, className);
			}

		} catch (SecurityException e) {
			// empty on purpose
		}
		
		return repository;
	}

    // Constructors ----------------------------------------------------------
    
    // Public ----------------------------------------------------------------

    // Package protected -----------------------------------------------------
    
    // Protected -------------------------------------------------------------
    
    // Private ---------------------------------------------------------------
    
	// Inner classes ---------------------------------------------------------
}
