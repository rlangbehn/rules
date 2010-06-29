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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import net.sourceforge.rules.verifier.AbstractRulesVerifier;
import net.sourceforge.rules.verifier.RulesVerifierConfiguration;
import net.sourceforge.rules.verifier.RulesVerifierException;

import org.codehaus.plexus.util.DirectoryScanner;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.util.DroolsStreamUtils;
import org.drools.verifier.Verifier;

/**
 * TODO
 *
 * @plexus.component
 *   role="net.sourceforge.rules.verifier.RulesVerifier"
 *   role-hint="droolsv"
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class DroolsRulesVerifier extends AbstractRulesVerifier
{
	// Constants -------------------------------------------------------------

	/**
	 * TODO
	 */
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// AbstractRulesVerifier Overrides ---------------------------------------

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.verifier.RulesVerifier#verify(net.sourceforge.rules.verifier.RulesVerifierConfiguration)
	 */
	public void verify(RulesVerifierConfiguration configuration)
	throws RulesVerifierException {

		File outputDir = configuration.getOutputDirectory();
		
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		
		String[] fileNames = collectRulePackages(configuration);
		
		if (fileNames.length == 0) {
			return;
		}
		
        if ((getLogger() != null) && getLogger().isInfoEnabled()) {
        	getLogger().info(
        			"Verifying " + fileNames.length + " " +
        			"rules package" + (fileNames.length == 1 ? "" : "s" ) +
        			" to " + outputDir.getAbsolutePath()
        	);
        }

        Verifier verifier = new Verifier();
        
        for (String fileName : fileNames) {
        	File file = new File(configuration.getRulesDirectory(), fileName);
        	verifyPackage(verifier, file);
        }
	}

	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param config
	 * @return
	 */
	private String[] collectRulePackages(RulesVerifierConfiguration config) {

		DirectoryScanner ds = new DirectoryScanner();
		ds.setFollowSymlinks(true);
		ds.setBasedir(config.getRulesDirectory());

		Set<String> includes = config.getIncludes();
		
		if (includes.isEmpty()) {
			ds.setIncludes(EMPTY_STRING_ARRAY);
		} else {
			ds.setIncludes(includes.toArray(new String[includes.size()]));
		}
		
		Set<String> excludes = config.getExcludes();
		
		if (excludes.isEmpty()) {
			ds.setExcludes(EMPTY_STRING_ARRAY);
		} else {
			ds.setExcludes(excludes.toArray(new String[excludes.size()]));
		}

		ds.scan();
		
		return ds.getIncludedFiles();
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
			
			in.close();
			in = null;
			
		} catch (IOException e) {
			String s = "Error while deserializing rules package";
			throw new RulesVerifierException(s, e);
		} catch (ClassNotFoundException e) {
			String s = "Error while deserializing rules package";
			throw new RulesVerifierException(s, e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignored
				}
			}
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
	private void verifyPackage(Verifier verifier, File file)
	throws RulesVerifierException {

		Package pkg = readPackage(file);
		PackageDescr pkgDescr = new PackageDescr(pkg.getName());

		Rule[] rules = pkg.getRules();

		for (Rule rule : rules) {
			RuleDescr ruleDescr = new RuleDescr();
			
			//pkgDescr.addRule(ruleDescr);
		}
		
		verifier.addPackageDescr(pkgDescr);
		verifier.fireAnalysis();
	}

	// Inner classes ---------------------------------------------------------
}
