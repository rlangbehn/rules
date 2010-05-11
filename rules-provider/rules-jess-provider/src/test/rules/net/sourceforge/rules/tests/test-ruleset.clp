/*****************************************************************************
 * $Id: test-ruleset.clp 244 2008-08-04 18:47:35Z rlangbehn $
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
;(defmodule net.sourceforge.rules.tests)

(import java.net.InetAddress)

/**
 * TODO
 * 
 * @version $Revision: 244 $ $Date: 2008-08-04 20:47:35 +0200 (Mo, 04 Aug 2008) $
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
(defrule test-ruleset
	=>
    	(bind ?hostName ((call InetAddress getLocalHost) getHostName))
    	(add (new String (str-cat "ruleset 'test-ruleset' executed on: " ?hostName)))
)
