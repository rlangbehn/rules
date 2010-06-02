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
package net.sourceforge.rules.compiler.drools.main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import net.sourceforge.rules.compiler.drools.DroolsRulesCompiler;
import net.sourceforge.rules.compiler.drools.util.Context;
import net.sourceforge.rules.compiler.drools.util.IOUtil;
import net.sourceforge.rules.compiler.drools.util.Log;
import net.sourceforge.rules.compiler.drools.util.Options;

import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.drools.util.DroolsStreamUtils;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RulesCompiler
{
	// Constants -------------------------------------------------------------

	/**
	 * TODO
	 */
	public static final String DEFAULT_EXTENSION = ".pkg"; //$NON-NLS-1$

	/**
	 * TODO
	 */
	public static final String VERSION_INFO = "droolsc version 1.0.0"; //$NON-NLS-1$

	/**
	 * The context key for the compiler.
	 */
	private static final Context.Key<RulesCompiler> compilerKey =
		new Context.Key<RulesCompiler>();

	// Attributes ------------------------------------------------------------

	private Log log;
	private String compiler;
	private File destination;
	private String outputFileName;
	private boolean verbose;

	// Static ----------------------------------------------------------------

	/**
	 * TODO
	 *
	 * @param context
	 * @return
	 */
	public static RulesCompiler instance(Context context) {
		RulesCompiler instance = context.get(compilerKey);

		if (instance == null) {
			instance = new RulesCompiler(context);
		}

		return instance;
	}

	/**
	 * TODO
	 *
	 * @return
	 */
	public static String version() {
		return RulesCompiler.VERSION_INFO;
	}

	// Constructors ----------------------------------------------------------

	/**
	 * TODO
	 *
	 * @param context
	 */
	public RulesCompiler(Context context) {
		context.put(compilerKey, this);

		log = Log.instance(context);

		Options options = Options.instance(context);
		compiler = options.get("-compiler");
		String d = options.get("-d");

		if (d != null) {
			destination = new File(d);
		}
		
		outputFileName = options.get("-outputfile");
		verbose = (options.get("-verbose") != null);
	}

	// Public ----------------------------------------------------------------

	/**
	 * TODO
	 */
	public void close() {
		log.flush();
	}

	/**
	 * TODO
	 *
	 * @param filenames
	 */
	public void compile(List<String> fileNames) {
		
		KnowledgeBuilderConfiguration kbconfig = createKnowledgeBuilderConfiguration();
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbconfig);
		DecisionTableConfiguration dtconfig = KnowledgeBuilderFactory.newDecisionTableConfiguration();
		Resource resource = null;
		
		for (String fileName : fileNames) {
			
			resource = ResourceFactory.newFileResource(fileName);
			
			if (verbose) {
				printVerbose("processing.resource", resource);
			}
			
			try {
				if (fileName.endsWith(".brl")) {
					kbuilder.add(resource, ResourceType.BRL);
				} else if (fileName.endsWith(".csv")) {
					dtconfig.setInputType(DecisionTableInputType.CSV);
					kbuilder.add(resource, ResourceType.DTABLE, dtconfig);
				} else if (fileName.endsWith(".drl")) {
					kbuilder.add(resource, ResourceType.DRL);
				} else if (fileName.endsWith(".dsl")) {
					kbuilder.add(resource, ResourceType.DSL);
				} else if (fileName.endsWith(".dslr")) {
					kbuilder.add(resource, ResourceType.DSLR);
				} else if (fileName.endsWith(".rf")) {
					kbuilder.add(resource, ResourceType.DRF);
				} else if (fileName.endsWith(".rfm")) {
					kbuilder.add(resource, ResourceType.DRF);
				} else if (fileName.endsWith(".xls")) {
					dtconfig.setInputType(DecisionTableInputType.XLS);
					kbuilder.add(resource, ResourceType.DTABLE, dtconfig);
				} else if (fileName.endsWith(".xml")) {
					kbuilder.add(resource, ResourceType.XDRL);
				} else {
					log.warning(0, "warn.unknown.resource.type", resource);
					continue;
				}
				
			} catch (Exception e) {
				log.error(0, "error", e.getMessage());
			}
		}

		if (kbuilder.hasErrors()) {
			log(kbuilder.getErrors());
		} else {
			writeKnowledgePackages(kbuilder.getKnowledgePackages());
		}
	}

	/**
	 * TODO
	 *
	 * @return
	 */
	public String getCompiler() {
		return compiler;
	}

	/**
	 * The number of errors encountered so far.
	 */
	public int errorCount() {
		return log.nerrors;
	}

	/**
	 * The number of warnings encountered so far.
	 */
	public int warningCount() {
		return log.nwarnings;
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @return
	 */
	private KnowledgeBuilderConfiguration createKnowledgeBuilderConfiguration() {
		KnowledgeBuilderConfiguration kbconfig = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
/*		
		JavaDialectConfiguration javaConfig = (JavaDialectConfiguration)
		config.getDialectConfiguration("java");
		javaConfig.setJavaLanguageLevel("1.5");

		if ("eclipse".equalsIgnoreCase(getCompiler())) { //$NON-NLS-1$
			javaConfig.setCompiler(JavaDialectConfiguration.ECLIPSE);
		}	else if ("extEclipse".equalsIgnoreCase(getCompiler())) { //$NON-NLS-1$
			javaConfig.setCompiler(JavaDialectConfiguration.ECLIPSE);
		}	else if ("janino".equalsIgnoreCase(getCompiler())) { //$NON-NLS-1$
			javaConfig.setCompiler(JavaDialectConfiguration.JANINO);
		}	else if ("extJanino".equalsIgnoreCase(getCompiler())) { //$NON-NLS-1$
			javaConfig.setCompiler(JavaDialectConfiguration.JANINO);
		}
*/
		return kbconfig;
	}

	/**
	 * TODO
	 *
	 * @param destDir
	 * @param pkgName
	 * @param fileName
	 * @param extension
	 * @return
	 */
	private File createOutputFile(
			File destDir,
			String pkgName,
			String fileName,
			String extension)
	throws IOException {

		int startIndex = 0;
		int endIndex = pkgName.indexOf('.');

		while (endIndex >= startIndex) {
			destDir = new File(destDir, pkgName.substring(startIndex, endIndex));

			if (!destDir.exists()) {
				destDir.mkdir();
			}

			startIndex = endIndex + 1;
			endIndex = pkgName.indexOf('.', startIndex);
		}

		if (startIndex < pkgName.length()) {
			destDir = new File(destDir, pkgName.substring(startIndex));

			if (!destDir.exists()) {
				destDir.mkdir();
			}
		}
		
		return new File(destDir, fileName + '.' + extension);
	}

	/**
	 * TODO
	 * 
	 * @param errors
	 */
	private void log(KnowledgeBuilderErrors errors) {
		for (KnowledgeBuilderError error : errors) {
			log.error(0, "rules.error", error.getMessage());
		}
	}
	
    /**
     * 	Output for "-verbose" option.
     * 
     *  @param key The key to look up the correct internationalized string.
     *  @param arg An argument for substitution into the output string.
     */
    private void printVerbose(String key, Object arg) {
    	Log.printLines(log.noticeWriter, Log.getLocalizedString("verbose." + key, arg));
    }

	/**
	 * Emit a rules file for the given Package instance.
	 *
	 * @param pkg
	 */
	private void writePackage(Package pkg) {
		
		File destFile = null;

		try {
			if (outputFileName == null) {
				destFile = createOutputFile(
						destination,
						pkg.getName(),
						pkg.getName(),
						DroolsRulesCompiler.OUTPUT_FILE_ENDING
				);
			} else {
				destFile = createOutputFile(
						destination,
						pkg.getName(),
						outputFileName,
						DroolsRulesCompiler.OUTPUT_FILE_ENDING
				);
			}
			
			writePackage(destFile, pkg);
			
		} catch (IOException e) {
			log.error(0, "class.cant.write", outputFileName, e.getMessage());
		}
	}

	/**
	 * Emit a rules file for the given Package instance.
	 *
	 * @param file
	 * @param object
	 */
	private void writePackage(File file, Object object) {
		
		OutputStream out = null;

		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			DroolsStreamUtils.streamOut(out, object);
			out.close();
			out = null;
		} catch (IOException e) {
			log.error(0, "class.cant.write", file.getAbsolutePath(), e.getMessage());
		} finally {
			IOUtil.close(out);
		}
	}

	/**
	 * TODO
	 * 
	 * @param pkgs
	 */
	private void writeKnowledgePackages(Collection<KnowledgePackage> kpkgs) {
		for (KnowledgePackage kpkg : kpkgs) {
			if (kpkg instanceof KnowledgePackageImp) {
				writePackage(((KnowledgePackageImp)kpkg).pkg);
			}
		}
	}

	// Inner classes ---------------------------------------------------------
}
