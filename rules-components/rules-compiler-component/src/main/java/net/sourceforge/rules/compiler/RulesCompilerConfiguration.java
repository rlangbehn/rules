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
package net.sourceforge.rules.compiler;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
@SuppressWarnings("unchecked")
public class RulesCompilerConfiguration
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	private String outputLocation;

	private List<String> classpathEntries = new LinkedList<>();

	private boolean debugRulesCompiler;
	
	private Set<String> sourceFiles = new HashSet<>();

	private List<String> sourceLocations = new LinkedList<>();

	private Set<String> includes = new HashSet<>();

	private Set<String> excludes = new HashSet<>();

	private boolean debug;

	private String debugLevel;

	private boolean showWarnings = true;

	private boolean showDeprecation;

	private String sourceVersion;

	private String targetVersion;

	private String sourceEncoding;

	private LinkedHashMap customCompilerArguments = new LinkedHashMap();

	private boolean fork;

	private boolean optimize;

	private String meminitial;

	private String maxmem;

	private String executable;

	private File workingDirectory;

	private String compilerVersion;

	private boolean verbose = false;

	/**
	 * A build temporary directory, eg target/.
	 *
	 * Used by the compiler implementation to put temporary files.
	 */
	private File buildDirectory;

	/**
	 * Used to control the name of the output file when compiling a set of
	 * sources to a single file.
	 */
	private String outputFileName;

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// Object overrides ------------------------------------------------------

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RulesCompilerConfiguration [outputLocation=");
		builder.append(outputLocation);
		builder.append(", classpathEntries=");
		builder.append(classpathEntries);
		builder.append(", debugRulesCompiler=");
		builder.append(debugRulesCompiler);
		builder.append(", sourceFiles=");
		builder.append(sourceFiles);
		builder.append(", sourceLocations=");
		builder.append(sourceLocations);
		builder.append(", includes=");
		builder.append(includes);
		builder.append(", excludes=");
		builder.append(excludes);
		builder.append(", debug=");
		builder.append(debug);
		builder.append(", debugLevel=");
		builder.append(debugLevel);
		builder.append(", showWarnings=");
		builder.append(showWarnings);
		builder.append(", showDeprecation=");
		builder.append(showDeprecation);
		builder.append(", sourceVersion=");
		builder.append(sourceVersion);
		builder.append(", targetVersion=");
		builder.append(targetVersion);
		builder.append(", sourceEncoding=");
		builder.append(sourceEncoding);
		builder.append(", customCompilerArguments=");
		builder.append(customCompilerArguments);
		builder.append(", fork=");
		builder.append(fork);
		builder.append(", optimize=");
		builder.append(optimize);
		builder.append(", meminitial=");
		builder.append(meminitial);
		builder.append(", maxmem=");
		builder.append(maxmem);
		builder.append(", executable=");
		builder.append(executable);
		builder.append(", workingDirectory=");
		builder.append(workingDirectory);
		builder.append(", compilerVersion=");
		builder.append(compilerVersion);
		builder.append(", verbose=");
		builder.append(verbose);
		builder.append(", buildDirectory=");
		builder.append(buildDirectory);
		builder.append(", outputFileName=");
		builder.append(outputFileName);
		builder.append("]");
		return builder.toString();
	}

	// Public ----------------------------------------------------------------

	public void setOutputLocation( String outputLocation )
	{
		this.outputLocation = outputLocation;
	}

	public String getOutputLocation()
	{
		return outputLocation;
	}

	public void addClasspathEntry( String classpathEntry )
	{
		classpathEntries.add( classpathEntry );
	}

	public void setClasspathEntries( List<String> classpathEntries )
	{
		if ( classpathEntries == null )
		{
			this.classpathEntries = Collections.EMPTY_LIST;
		}
		else
		{
			this.classpathEntries = new LinkedList<>( classpathEntries );
		}
	}

	public List<String> getClasspathEntries()
	{
		return Collections.unmodifiableList( classpathEntries );
	}

	public void setDebugRulesCompiler(boolean debugRulesCompiler)
	{
		this.debugRulesCompiler = debugRulesCompiler;
	}

	public boolean isDebugRulesCompiler()
	{
		return debugRulesCompiler;
	}

	public void setSourceFiles( Set<String> sourceFiles )
	{
		if ( sourceFiles == null )
		{
			this.sourceFiles = Collections.emptySet();
		}
		else
		{
			this.sourceFiles = new HashSet<>( sourceFiles );
		}
	}

	public Set<String> getSourceFiles()
	{
		return sourceFiles;
	}

	public void addSourceLocation( String sourceLocation )
	{
		sourceLocations.add( sourceLocation );
	}

	public void setSourceLocations( List<String> sourceLocations )
	{
		if ( sourceLocations == null )
		{
			this.sourceLocations = Collections.emptyList();
		}
		else
		{
			this.sourceLocations = new LinkedList<>( sourceLocations );
		}
	}

	public List<String> getSourceLocations()
	{
		return Collections.unmodifiableList( sourceLocations );
	}

	public void addInclude( String include )
	{
		includes.add( include );
	}

	public void setIncludes( Set<String> includes )
	{
		if ( includes == null )
		{
			this.includes = Collections.emptySet();
		}
		else
		{
			this.includes = new HashSet<>( includes );
		}
	}

	public Set<String> getIncludes()
	{
		return Collections.unmodifiableSet( includes );
	}

	public void addExclude( String exclude )
	{
		excludes.add( exclude );
	}

	public void setExcludes( Set<String> excludes )
	{
		if ( excludes == null )
		{
			this.excludes = Collections.emptySet();
		}
		else
		{
			this.excludes = new HashSet<>( excludes );
		}
	}

	public Set<String> getExcludes()
	{
		return Collections.unmodifiableSet( excludes );
	}

	public void setDebug( boolean debug )
	{
		this.debug = debug;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebugLevel( String debugLevel )
	{
		this.debugLevel = debugLevel;
	}

	public String getDebugLevel()
	{
		return debugLevel;
	}

	public void setShowWarnings( boolean showWarnings )
	{
		this.showWarnings = showWarnings;
	}

	public boolean isShowWarnings()
	{
		return showWarnings;
	}

	public boolean isShowDeprecation()
	{
		return showDeprecation;
	}

	public void setShowDeprecation( boolean showDeprecation )
	{
		this.showDeprecation = showDeprecation;
	}

	public String getSourceVersion()
	{
		return sourceVersion;
	}

	public void setSourceVersion( String sourceVersion )
	{
		this.sourceVersion = sourceVersion;
	}

	public String getTargetVersion()
	{
		return targetVersion;
	}

	public void setTargetVersion( String targetVersion )
	{
		this.targetVersion = targetVersion;
	}

	public String getSourceEncoding()
	{
		return sourceEncoding;
	}

	public void setSourceEncoding( String sourceEncoding )
	{
		this.sourceEncoding = sourceEncoding;
	}

	public void addCompilerCustomArgument( String customArgument, String value )
	{
		customCompilerArguments.put( customArgument, value );
	}

	public LinkedHashMap getCustomCompilerArguments()
	{
		return new LinkedHashMap( customCompilerArguments );
	}

	public void setCustomCompilerArguments( LinkedHashMap customCompilerArguments )
	{
		if ( customCompilerArguments == null )
		{
			this.customCompilerArguments = new LinkedHashMap();
		}
		else
		{
			this.customCompilerArguments = customCompilerArguments;
		}
	}

	public boolean isFork()
	{
		return fork;
	}

	public void setFork( boolean fork )
	{
		this.fork = fork;
	}

	public String getMeminitial()
	{
		return meminitial;
	}

	public void setMeminitial( String meminitial )
	{
		this.meminitial = meminitial;
	}

	public String getMaxmem() {
		return maxmem;
	}

	public void setMaxmem(String maxmem) {
		this.maxmem = maxmem;
	}

	public String getExecutable() {
		return executable;
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public File getBuildDirectory() {
		return buildDirectory;
	}

	public void setBuildDirectory(File buildDirectory) {
		this.buildDirectory = buildDirectory;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public boolean isOptimize() {
		return optimize;
	}

	public void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}

	public String getCompilerVersion() {
		return compilerVersion;
	}

	public void setCompilerVersion(String compilerVersion) {
		this.compilerVersion = compilerVersion;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose)	{
		this.verbose = verbose;
	}
	
	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
