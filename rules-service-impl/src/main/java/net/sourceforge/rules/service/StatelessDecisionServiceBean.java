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

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.rules.InvalidRuleSessionException;
import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleSessionCreateException;
import javax.rules.RuleSessionTypeUnsupportedException;
import javax.rules.StatelessRuleSession;

import net.sourceforge.rules.service.DecisionServiceException;
import net.sourceforge.rules.service.StatelessDecisionService;
import net.sourceforge.rules.service.StatelessDecisionServiceRemote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
@Stateless(name="StatelessDecisionService")
@Local({StatelessDecisionService.class})
@Remote({StatelessDecisionServiceRemote.class})
//@WebService(serviceName="StatelessDecisionService")
public class StatelessDecisionServiceBean implements StatelessDecisionServiceRemote
{
	// Constants -------------------------------------------------------------

	/**
	 * The <code>Log</code> instance for this class.
	 */
	private static final Log log = LogFactory.getLog(
			StatelessDecisionServiceBean.class);

	/**
	 * TODO
	 */
	private static final long serialVersionUID = 1L;

	// Attributes ------------------------------------------------------------

	/**
	 * TODO
	 */
	@Resource(mappedName="java:/RuleSessionFactory")
	private RuleRuntime ruleRuntime;

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// StatelessDecisionService implementation -------------------------------

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.service.StatelessDecisionService#decide(java.lang.String, java.util.Map, java.util.List)
	 */
	public List<?> decide(
			String bindUri,
			Map<?, ?> properties,
			List<?> inputObjects)
	throws DecisionServiceException {

		if (log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder("Executing RuleExecutionSet");
			sb.append("\n\tBindURI     : ").append(bindUri);
			sb.append("\n\tProperties  : ").append(properties);
			sb.append("\n\tInputObjects: ").append(inputObjects);
			log.debug(sb.toString());
		}

		int sessionType = RuleRuntime.STATELESS_SESSION_TYPE;
		StatelessRuleSession ruleSession = null;
		List<?> outputObjects = null;

		try {
			ruleSession = (StatelessRuleSession)
			ruleRuntime.createRuleSession(bindUri, null, sessionType);

			outputObjects = ruleSession.executeRules(
					inputObjects,
					new OutputObjectsOnlyObjectFilter(inputObjects)
			);

			ruleSession.release();
			ruleSession = null;

		} catch (RuleSessionTypeUnsupportedException e) {
			String s = "Error while creating rule session";
			throw new DecisionServiceException(s, e);
		} catch (RuleSessionCreateException e) {
			String s = "Error while creating rule session";
			throw new DecisionServiceException(s, e);
		} catch (RuleExecutionSetNotFoundException e) {
			String s = "Error while creating rule session";
			throw new DecisionServiceException(s, e);
		} catch (RemoteException e) {
			String s = "Error while executing rules";
			throw new DecisionServiceException(s, e);
		} catch (InvalidRuleSessionException e) {
			String s = "Error while executing rules";
			throw new DecisionServiceException(s, e);
		} finally {
			if (ruleSession != null) {
				try {
					ruleSession.release();
				} catch (InvalidRuleSessionException e) {
					String s = "Error while releasing rule session";
					log.warn(s, e);
				} catch (RemoteException e) {
					String s = "Error while releasing rule session";
					log.warn(s, e);
				}
				ruleSession = null;
			}
		}

		return outputObjects;
	}

	// Public ----------------------------------------------------------------

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	// Inner classes ---------------------------------------------------------
}
