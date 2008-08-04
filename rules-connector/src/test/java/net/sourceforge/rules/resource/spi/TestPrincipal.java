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
package net.sourceforge.rules.resource.spi;

import java.security.Principal;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class TestPrincipal implements Principal
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	private String name;

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------
	
	/**
	 * TODO
	 * 
	 * @param name
	 */
	public TestPrincipal(String name) {
		this.name = name;
	}

	// Principal implementation ----------------------------------------------

	/* (non-Javadoc)
	 * @see java.security.Principal#getName()
	 */
	public String getName() {
		return name;
	}

	// Object overrides ------------------------------------------------------

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!(o instanceof TestPrincipal)) return false;
		
		TestPrincipal other = (TestPrincipal)o;
		return equals(name, other.name);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hashCode(name);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("TestPrincipal");
		sb.append("\n\tname: ").append(name);
		return sb.toString();
	}

	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

    /**
     * TODO
     * 
     * @param o1
     * @param o2
     * @return
     */
    private boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }
    
    /**
     * TODO
     * 
     * @param o
     * @return
     */
    private int hashCode(Object o) {
    	return o == null ? 0 : o.hashCode();
    }
    
	// Inner classes ---------------------------------------------------------
}
