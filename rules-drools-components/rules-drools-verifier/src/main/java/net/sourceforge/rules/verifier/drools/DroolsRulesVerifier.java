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
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.rules.verifier.AbstractRulesVerifier;
import net.sourceforge.rules.verifier.RulesVerifier;
import net.sourceforge.rules.verifier.RulesVerifierConfiguration;
import net.sourceforge.rules.verifier.RulesVerifierMessage;
import net.sourceforge.rules.verifier.RulesVerifierException;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.DirectoryScanner;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierError;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.VerifierReportWriter;
import org.drools.verifier.report.VerifierReportWriterFactory;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
@Component(hint = "drools-verifier", role = RulesVerifier.class)
public class DroolsRulesVerifier extends AbstractRulesVerifier
{
	// Constants -------------------------------------------------------------

	/**
	 * List of supported drools resource types.
	 */
	public static final String[] INPUT_FILE_ENDINGS = new String[] {
		"drl"
	};

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// AbstractRulesVerifier Overrides ---------------------------------------

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.verifier.RulesVerifier#verify(net.sourceforge.rules.verifier.RulesVerifierConfiguration)
	 */
	public List<RulesVerifierMessage> verify(RulesVerifierConfiguration configuration)
	throws RulesVerifierException {

		File reportsDir = configuration.getReportsDirectory();
		
		if (!reportsDir.exists()) {
			reportsDir.mkdirs();
		}
		
		Set<File> files = collectRuleFiles(configuration);
		
		if (files.size() == 0) {
			return Collections.emptyList();
		}
		
        if ((getLogger() != null) && getLogger().isInfoEnabled()) {
			getLogger().info("verify(" + configuration + ")");
        	getLogger().info(
        			"Verifying " + files.size() + " " +
        			"rules file" + (files.size() == 1 ? "" : "s" ) +
        			" to " + reportsDir.getAbsolutePath()
        	);
        }

        List<RulesVerifierMessage> messages = new ArrayList<RulesVerifierMessage>();
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
	 * @param config
	 * @return
	 */
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
		Set<File> files = new HashSet<File>();

		for (String fileName : fileNames) {
        	File file = new File(config.getRulesDirectory(), fileName);
        	files.add(file);
		}
		
		return files;
	}
	
	private Verifier createVerifier() {
        VerifierBuilder builder = VerifierBuilderFactory.newVerifierBuilder();
        return builder.newVerifier();
	}

	/**
	 * TODO
	 * 
	 * @param verifier 
	 * @param file
	 * @throws RulesVerifierException 
	 */
	private List<RulesVerifierMessage> verify(Verifier verifier, File file) throws RulesVerifierException {
		
		Resource resource = ResourceFactory.newFileResource(file);
		verifier.addResourcesToVerify(resource, ResourceType.DRL);
        List<RulesVerifierMessage> messages;

		if (!verifier.hasErrors()) {
			messages = Collections.emptyList();
		} else {
			
			List<VerifierError> errors = verifier.getErrors();
			messages = new ArrayList<RulesVerifierMessage>();
			
			for (VerifierError error : errors) {
				messages.add(new RulesVerifierMessage(error.getMessage(), true));
			}
		}
		
		return messages;
	}

	/**
	 * TODO
	 * 
	 * @param reportsDir
	 * @param result
	 * @throws RulesVerifierException 
	 */
	private void write(File reportsDir, VerifierReport result)
	throws RulesVerifierException {
        
		VerifierReportWriter reportWriter = VerifierReportWriterFactory.newXMLReportWriter();
        File resultFile = new File(reportsDir, "verifier-result.xml");
        OutputStream out = null;

        try {
        	out = new BufferedOutputStream(new FileOutputStream(resultFile));
			reportWriter.writeReport(out, result);
		} catch (FileNotFoundException e) {
			String s = "Error while writing verifier report";
			throw new RulesVerifierException(s, e);
		} catch (IOException e) {
			String s = "Error while writing verifier report";
			throw new RulesVerifierException(s, e);
		} finally {
			close(out);
		}
	}

	// Inner classes ---------------------------------------------------------
}
