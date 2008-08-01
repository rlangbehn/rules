/*****************************************************************************
 * $Id: MapAdapter.java 166 2008-08-01 14:28:40Z rlangbehn $
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * TODO
 * 
 * @version $Revision: 166 $ $Date: 2008-08-01 16:28:40 +0200 (Fr, 01 Aug 2008) $
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
@SuppressWarnings("unchecked")
public class MapAdapter extends XmlAdapter<MapEntry[], Map>
{
	// Constants -------------------------------------------------------------

	// Attributes ------------------------------------------------------------

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// XmlAdapter overrides --------------------------------------------------

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public MapEntry[] marshal(Map map) throws Exception {
		MapEntry[] entries = new MapEntry[map.size()];
		Set entrySet = map.entrySet();
		int i = 0;
		
		for (Iterator it = entrySet.iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry)it.next();
			entries[i++] = new MapEntry(entry.getKey(), entry.getValue());
		}
		
		return entries;
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Map unmarshal(MapEntry[] entries) throws Exception {
		Map map = new HashMap();
		
		for (MapEntry entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}
		
		return map;
	}

	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
