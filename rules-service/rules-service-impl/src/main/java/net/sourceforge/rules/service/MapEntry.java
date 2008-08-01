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
package net.sourceforge.rules.service;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class MapEntry
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	@XmlAttribute
	private Object key;
	
	/**
	 * TODO
	 */
	@XmlElement
	private Object value;
	
	// Constructors ----------------------------------------------------------

	/**
	 * Required default ctor for marshalling to work.
	 */
	public MapEntry() {}
	
	/**
	 * TODO
	 * 
	 * @param key
	 * @param value
	 */
	public MapEntry(Object key, Object value) {
		this.key = key;
		this.value = value;
	}
	
	// Public ----------------------------------------------------------------

	/**
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}
	
	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
