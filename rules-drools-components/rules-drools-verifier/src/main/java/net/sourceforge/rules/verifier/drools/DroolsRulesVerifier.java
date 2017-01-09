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
package net.sourceforge.rules.verifier.drools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.plexus.util.DirectoryScanner;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierError;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.VerifierReportWriter;
import org.drools.verifier.report.VerifierReportWriterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.rules.verifier.AbstractRulesVerifier;
import net.sourceforge.rules.verifier.RulesVerifierConfiguration;
import net.sourceforge.rules.verifier.RulesVerifierException;
import net.sourceforge.rules.verifier.RulesVerifierMessage;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
@Named
@Singleton
public class DroolsRulesVerifier extends AbstractRulesVerifier
{
	// Constants -------------------------------------------------------------

	/**
	 * List of supported drools resource types.
	 */
	public static final String[] INPUT_FILE_ENDINGS = new String[] {"drl"};

    private static final Logger LOG = LoggerFactory.getLogger(DroolsRulesVerifier.class);

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// RulesVerifier Overrides -----------------------------------------------

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.verifier.RulesVerifier#verify(net.sourceforge.rules.verifier.RulesVerifierConfiguration)
	 */
	@Override
	public List<RulesVerifierMessage> verify(RulesVerifierConfiguration configuration) throws RulesVerifierException {

		LOG.info("verify(" + configuration + ")");
		
		File reportsDir = configuration.getReportsDirectory();
		
		if (!reportsDir.exists()) {
			reportsDir.mkdirs();
		}
		
		Set<File> files = collectRuleFiles(configuration);
		
		if (files.isEmpty()) {
			return Collections.emptyList();
		}

		if (LOG.isInfoEnabled()) {
        	LOG.info("Verifying " + files.size() + " " + "rules file" + (files.size() == 1 ? "" : "s" ) + " to " + reportsDir.getAbsolutePath());
		}
		
        List<RulesVerifierMessage> messages = new ArrayList<>();
        Verifier verifier = createVerifier();

        try {
            
            for (File file : files) {
            	messages.addAll(verify(verifier, file));
            }
            
            verifier.fireAnalysis();

            write(reportsDir, verifier.getResult());
        	
        } finally {
        	verifier.dispose();
        }
        
        return messages;
	}

	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	private Set<File> collectRuleFiles(RulesVerifierConfiguration config) {

		DirectoryScanner ds = new DirectoryScanner();
		ds.setFollowSymlinks(true);
		ds.setBasedir(config.getRulesDirectory());

		Set<String> includes = config.getIncludes();
		
		if (includes != null && !includes.isEmpty()) {
			ds.setIncludes(includes.toArray(new String[includes.size()]));
        } else {
        	String[] inclStrs = new String[INPUT_FILE_ENDINGS.length];
        	int i = 0;
        	
        	for (String inputFileEnding : INPUT_FILE_ENDINGS) {
        		inclStrs[i++] = "**/*." + inputFileEnding;
        	}
        	
            ds.setIncludes(inclStrs);
		}
		
		Set<String> excludes = config.getExcludes();
		
		if (excludes != null && !excludes.isEmpty()) {
			ds.setExcludes(excludes.toArray(new String[excludes.size()]));
		}

		ds.scan();

		String[] fileNames = ds.getIncludedFiles();
		Set<File> files = new HashSet<>();

		for (String fileName : fileNames) {
        	File file = new File(config.getRulesDirectory(), fileName);
        	files.add(file);
		}
		
		return files;
	}
	
	private Verifier createVerifier() {
		
		String key = "drools.dialect.java.compiler";
		String value = System.getProperty(key);
		
		if (value == null || value.isEmpty()) {
			System.setProperty(key, "JANINO");
		}
		
        VerifierBuilder verifierBuilder = VerifierBuilderFactory.newVerifierBuilder();
        //VerifierConfiguration verifierConfiguration = verifierBuilder.newVerifierConfiguration();
		// To avoid wrong class format error with jre 8 (see issue DROOLS-329)
        //verifierConfiguration.setProperty("drools.dialect.java.compiler", "JANINO");
        
        return verifierBuilder.newVerifier();
	}

	private List<RulesVerifierMessage> verify(Verifier verifier, File file) throws RulesVerifierException {
		
		Resource resource = ResourceFactory.newFileResource(file);
		verifier.addResourcesToVerify(resource, ResourceType.DRL);
        List<RulesVerifierMessage> messages;

		if (!verifier.hasErrors()) {
			messages = Collections.emptyList();
		} else {
			
			List<VerifierError> errors = verifier.getErrors();
			messages = new ArrayList<>();
			
			for (VerifierError error : errors) {
				messages.add(new RulesVerifierMessage(error.getMessage(), true));
			}
		}
		
		return messages;
	}

	private void write(File reportsDir, VerifierReport result) throws RulesVerifierException {
        
		VerifierReportWriter reportWriter = VerifierReportWriterFactory.newXMLReportWriter();
        File resultFile = new File(reportsDir, "verifier-result.xml");

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(resultFile))) {
			reportWriter.writeReport(out, result);
		} catch (IOException e) {
			String s = "Error while writing verifier report";
			throw new RulesVerifierException(s, e);
		}
	}

	// Inner classes ---------------------------------------------------------
}
