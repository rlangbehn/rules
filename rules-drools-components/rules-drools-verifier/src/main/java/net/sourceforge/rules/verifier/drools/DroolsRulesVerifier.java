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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.rules.verifier.AbstractRulesVerifier;
import net.sourceforge.rules.verifier.RulesVerifier;
import net.sourceforge.rules.verifier.RulesVerifierConfiguration;
import net.sourceforge.rules.verifier.RulesVerifierException;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.DirectoryScanner;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.util.DroolsStreamUtils;
import org.drools.verifier.Verifier;
import org.drools.verifier.dao.VerifierResult;
import org.drools.verifier.report.ReportModeller;

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
	public void verify(RulesVerifierConfiguration configuration)
	throws RulesVerifierException {

		File reportsDir = configuration.getReportsDirectory();
		
		if (!reportsDir.exists()) {
			reportsDir.mkdirs();
		}
		
		Set<File> files = collectRuleFiles(configuration);
		
		if (files.size() == 0) {
			return;
		}
		
        if ((getLogger() != null) && getLogger().isInfoEnabled()) {
			getLogger().info("verify(" + configuration + ")");
        	getLogger().info(
        			"Verifying " + files.size() + " " +
        			"rules file" + (files.size() == 1 ? "" : "s" ) +
        			" to " + reportsDir.getAbsolutePath()
        	);
        }

        Verifier verifier = new Verifier();
        
        for (File file : files) {
        	verify(verifier, file);
        }
        
        verifier.fireAnalysis();

        write(reportsDir, verifier.getResult());
        
        //verifier.writeComponentsHTML(reportsDir.getAbsolutePath() + File.separator);
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
	
	/**
	 * TODO
	 * 
	 * @param file
	 * @return
	 * @throws RulesVerifierException 
	 */
	private Package readPackage(File file) throws RulesVerifierException {
		
		InputStream in = null;
		Object ast = null;
		
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			ast = DroolsStreamUtils.streamIn(in);
		} catch (IOException e) {
			String s = "Error while deserializing rules package";
			throw new RulesVerifierException(s, e);
		} catch (ClassNotFoundException e) {
			String s = "Error while deserializing rules package";
			throw new RulesVerifierException(s, e);
		} finally {
			close(in);
		}
		
		return (Package)ast;
	}

	/**
	 * TODO
	 * 
	 * @param verifier 
	 * @param file
	 * @throws RulesVerifierException 
	 */
	private void verify(Verifier verifier, File file)
	throws RulesVerifierException {

		DrlParser parser = new DrlParser();
		PackageDescr pkgDescr = null;
		Reader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			pkgDescr = parser.parse(reader);
			verifier.addPackageDescr(pkgDescr);
		} catch (FileNotFoundException e) {
			String s = "Error while reading rules file " + file;
			throw new RulesVerifierException(s, e);
		} catch (DroolsParserException e) {
			String s = "Error while parsing rules file " + file;
			throw new RulesVerifierException(s, e);
		} finally {
			close(reader);
		}
	}

	/**
	 * TODO
	 * 
	 * @param reportsDir
	 * @param result
	 * @throws RulesVerifierException 
	 */
	private void write(File reportsDir, VerifierResult result)
	throws RulesVerifierException {
        
        File resultFile = new File(reportsDir, "verifier-result.xml");
        Writer writer = null;

        try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile), "utf-8"));
			writer.write(ReportModeller.writeXML(result));
		} catch (FileNotFoundException e) {
			String s = "Error while writing verifier report";
			throw new RulesVerifierException(s, e);
		} catch (UnsupportedEncodingException e) {
			String s = "Error while writing verifier report";
			throw new RulesVerifierException(s, e);
		} catch (IOException e) {
			String s = "Error while writing verifier report";
			throw new RulesVerifierException(s, e);
		} finally {
			close(writer);
		}
	}

	// Inner classes ---------------------------------------------------------
}
