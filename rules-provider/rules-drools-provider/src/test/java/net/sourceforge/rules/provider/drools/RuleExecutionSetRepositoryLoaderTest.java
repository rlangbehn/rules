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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.drools.jsr94.rules.repository.RuleExecutionSetRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RuleExecutionSetRepositoryLoaderTest
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		System.getProperties().remove(
				RuleExecutionSetRepository.class.getName()
		);
	}

	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		System.getProperties().remove(
				RuleExecutionSetRepository.class.getName()
		);
	}

	// Constructors ----------------------------------------------------------

	// Public ----------------------------------------------------------------

	/**
	 * Test method for loading a <code>RuleExecutionSetRepository</code>
	 * instance by default class name.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testLoadRuleExecutionSetRepositoryByDefaultClassName()
	throws Exception {
		
		String defaultClassName = RuleExecutionSetRepositoryLoadedByDefaultClassName.class.getName();
		RuleExecutionSetRepository repository = RuleRepositoryLoader.loadRuleExecutionSetRepository(defaultClassName);
		assertNotNull("repository shouldn't be null", repository);
		assertTrue(
				defaultClassName + " expected, but was " + repository.getClass().getName(),
				repository instanceof RuleExecutionSetRepositoryLoadedByDefaultClassName
		);
	}

	/**
	 * Test method for loading a <code>RuleExecutionSetRepository</code>
	 * instance by the 'drools.properties' resource.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testLoadRuleExecutionSetRepositoryByPropertiesResource()
	throws Exception {
		
		String expectedClassName = RuleExecutionSetRepositoryLoadedByPropertiesResource.class.getName();
		Properties properties = new Properties();
		properties.setProperty(
				RuleExecutionSetRepository.class.getName(),
				expectedClassName
		);

		ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File tmpDroolsDir = new File(tmpDir, "drools");
		File propertiesFile = new File(tmpDroolsDir, "drools.properties");
		OutputStream out = null;

		try {
			tmpDroolsDir.mkdir();

			out = new FileOutputStream(propertiesFile);
			properties.store(out, "For test purposes only");

			out.close();
			out = null;

			URL[] urls = new URL[] {tmpDroolsDir.toURI().toURL()};
			ClassLoader cL = URLClassLoader.newInstance(urls, savedClassLoader);
			Thread.currentThread().setContextClassLoader(cL);

			RuleExecutionSetRepository repository = RuleRepositoryLoader.loadRuleExecutionSetRepository(null);
			assertNotNull("repository shouldn't be null", repository);
			assertTrue(
					expectedClassName + " expected, but was " + repository.getClass().getName(),
					repository instanceof RuleExecutionSetRepositoryLoadedByPropertiesResource
			);

		} finally {
			Thread.currentThread().setContextClassLoader(savedClassLoader);
			close(out);
			delete(tmpDroolsDir);
		}
	}

	/**
	 * Test method for loading a <code>RuleExecutionSetRepository</code>
	 * instance by the services API.
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore("Currently deactivated")
	public final void testLoadRuleExecutionSetRepositoryByServicesAPI()
	throws Exception {

		String expectedClassName = RuleExecutionSetRepositoryLoadedByServicesAPI.class.getName();
		ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File tmpDroolsDir = new File(tmpDir, "drools");
		File servicesDir = new File(tmpDroolsDir, "META-INF/services/");
		File serviceFile = new File(servicesDir, RuleExecutionSetRepository.class.getName());
		Writer writer = null;

		try {
			servicesDir.mkdirs();

			writer = new OutputStreamWriter(new FileOutputStream(serviceFile), "UTF-8");
			writer.write(expectedClassName);

			writer.close();
			writer = null;

			URL[] urls = new URL[] {tmpDroolsDir.toURI().toURL()};
			ClassLoader cL = URLClassLoader.newInstance(urls, savedClassLoader);
			Thread.currentThread().setContextClassLoader(cL);

			RuleExecutionSetRepository repository = RuleRepositoryLoader.loadRuleExecutionSetRepository(null);
			assertNotNull("repository shouldn't be null", repository);
			assertTrue(
					expectedClassName + " expected, but was " + repository.getClass().getName(),
					repository instanceof RuleExecutionSetRepositoryLoadedByServicesAPI
			);

		} finally {
			Thread.currentThread().setContextClassLoader(savedClassLoader);
			close(writer);
			delete(tmpDroolsDir);
		}
	}

	/**
	 * Test method for loading a <code>RuleExecutionSetRepository</code>
	 * instance by system property.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testLoadRuleExecutionSetRepositoryBySystemProperty()
	throws Exception {

		String expectedClassName = RuleExecutionSetRepositoryLoadedBySystemProperty.class.getName();
		System.setProperty(
				RuleExecutionSetRepository.class.getName(),
				expectedClassName
		);

		try {
			RuleExecutionSetRepository repository = RuleRepositoryLoader.loadRuleExecutionSetRepository(null);
			assertNotNull("repository shouldn't be null", repository);
			assertTrue(
					expectedClassName + " expected, but was " + repository.getClass().getName(),
					repository instanceof RuleExecutionSetRepositoryLoadedBySystemProperty
			);
		} finally {
			System.getProperties().remove(RuleExecutionSetRepository.class.getName());
		}
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param closeable
	 */
	private void close(Closeable closeable) {
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
	 * @param file
	 * @return
	 */
	private boolean delete(File file) {
		if (file == null) {
			return false;
		}

		if (file.isDirectory()) {
			File[] files = file.listFiles();

			if (files != null) {
				for (File f : files) {
					delete(f);
				}
			}
		}

		try {
			return file.delete();
		} catch (Exception e) {
			return false;
		}
	}
	
	// Inner classes ---------------------------------------------------------
}
