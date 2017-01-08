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
package net.sourceforge.rules.verifier;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class RulesVerifierConfiguration
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	private Set<String> excludes = new HashSet<>();
	private Set<String> includes = new HashSet<>();
	private File reportsDirectory;
	private File rulesDirectory;
	private boolean verbose;

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// Object overrides ------------------------------------------------------

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("[");
		sb.append("excludes=").append(excludes).append(", ");
		sb.append("includes=").append(includes).append(", ");
		sb.append("reportsDirectory=").append(reportsDirectory).append(", ");
		sb.append("rulesDirectory=").append(rulesDirectory).append(", ");
		sb.append("verbose=").append(verbose);
		sb.append("]");
		return sb.toString();
	}

	// Public ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param exclude
	 */
	public void addExclude(String exclude) {
		excludes.add(exclude);
	}
	
	/**
	 * TODO
	 * 
	 * @return the excludes
	 */
	public Set<String> getExcludes() {
		return Collections.unmodifiableSet(excludes);
	}

	/**
	 * TODO
	 * 
	 * @param include
	 */
	public void addInclude(String include) {
		includes.add(include);
	}
	
	/**
	 * TODO
	 * 
	 * @return the includes
	 */
	public Set<String> getIncludes() {
		return Collections.unmodifiableSet(includes);
	}

	/**
	 * TODO
	 * 
	 * @return the reportsDirectory
	 */
	public File getReportsDirectory() {
		return reportsDirectory;
	}

	/**
	 * TODO
	 * 
	 * @param reportsDirectory the reportsDirectory to set
	 */
	public void setReportsDirectory(File reportsDirectory) {
		this.reportsDirectory = reportsDirectory;
	}

	/**
	 * TODO
	 * 
	 * @return the rulesDirectory
	 */
	public File getRulesDirectory() {
		return rulesDirectory;
	}

	/**
	 * TODO
	 * 
	 * @param rulesDirectory the rulesDirectory to set
	 */
	public void setRulesDirectory(File rulesDirectory) {
		this.rulesDirectory = rulesDirectory;
	}

	/**
	 * TODO
	 * 
	 * @return the verbose
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * TODO
	 * 
	 * @param verbose the verbose to set
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
