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

	private Set<String> excludes = Collections.emptySet();

	private Set<String> includes = Collections.emptySet();

	private boolean verbose;

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// Public ----------------------------------------------------------------

	/**
	 * @return the excludes
	 */
	public Set<String> getExcludes() {
		return Collections.unmodifiableSet(excludes);
	}

	/**
	 * @param excludes the excludes to set
	 */
	public void setExcludes(Set<String> excludes) {
		if (excludes == null) {
			this.excludes = Collections.emptySet();
		} else {
			this.excludes = new HashSet<String>(excludes);
		}
	}

	/**
	 * @return the includes
	 */
	public Set<String> getIncludes() {
		return Collections.unmodifiableSet(includes);
	}

	/**
	 * @param includes the includes to set
	 */
	public void setIncludes(Set<String> includes) {
		if (includes == null) {
			this.includes = Collections.emptySet();
		} else {
			this.includes = new HashSet<String>(includes);
		}
	}

	/**
	 * @return the verbose
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
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
