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
package net.sourceforge.rules.compiler.drools.util;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * 
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
public class Context
{
	/**
	 * The client creates an instance of this class for each key.
	 */
	public static class Key<T> {
		// note: we inherit identity equality from Object
	}

	/**
	 * The client can register a factory for lazy creation of the
	 * instance.
	 */
	public static interface Factory<T> {
		T make();
	};

	/**
	 * The underlying map storing the data.
	 * We maintain the invariant that this table contains only
	 * mappings of the form
	 * Key<T> -> T or Key<T> -> Factory<T>
	 */
	@SuppressWarnings("unchecked")
	private Map<Key, Object> ht = new HashMap<Key, Object>();

	/**
	 * TODO
	 */
	public Context() {
		super();
	}

	/**
	 * Set the factory for the key in this context.
	 */
	public <T> void put(Key<T> key, Factory<T> fac) {
		Object old = ht.put(key, fac);

		if (old != null) {
			throw new AssertionError("duplicate context value"); //$NON-NLS-1$
		}
	}

	/**
	 * Set the value for the key in this context.
	 */
	public <T> void put(Key<T> key, T data) {
		if (data instanceof Factory) {
			throw new AssertionError("T extends Context.Factory"); //$NON-NLS-1$
		}

		Object old = ht.put(key, data);

		if (old != null && !(old instanceof Factory) && old != data) {
			throw new AssertionError("duplicate context value"); //$NON-NLS-1$
		}
	}

	/**
	 * Get the value for the key in this context.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Key<T> key) {
		Object o = ht.get(key);

		if (o instanceof Factory) {
			Factory fac = (Factory)o;
			o = fac.make();

			if (o instanceof Factory) {
				throw new AssertionError("T extends Context.Factory"); //$NON-NLS-1$
			}

			assert ht.get(key) == o;
		}

		/* The following cast can't fail unless there was
		 * cheating elsewhere, because of the invariant on ht.
		 * Since we found a key of type Key<T>, the value must
		 * be of type T.
		 */
		return (T)o; // NOTE: unchecked cast unavoidable here
	}
}
