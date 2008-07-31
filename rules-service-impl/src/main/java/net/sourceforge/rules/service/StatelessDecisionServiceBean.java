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
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.rules.InvalidRuleSessionException;
import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleSessionCreateException;
import javax.rules.RuleSessionTypeUnsupportedException;
import javax.rules.StatelessRuleSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:rlangbehn@users.sourceforge.net">Rainer Langbehn</a>
 */
@Local({StatelessDecisionService.class})
@Remote({StatelessDecisionServiceRemote.class})
@Stateless(name="StatelessDecisionService")
@WebService(serviceName="StatelessDecisionService")
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
	private RuleRuntime ruleRuntime;

	// Static ----------------------------------------------------------------

	// Constructors ----------------------------------------------------------

	// StatelessDecisionService implementation -------------------------------

	/* (non-Javadoc)
	 * @see net.sourceforge.rules.service.StatelessDecisionService#decide(java.lang.String, java.util.Map, java.util.List)
	 */
	@WebMethod()
	@WebResult(name="outputObjects")
	public List<?> decide(
			@WebParam(name="bindUri")
			String bindUri,
			@WebParam(name="properties")
			Map<?, ?> properties,
			@WebParam(name="inputObjects")
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
			ruleRuntime.createRuleSession(bindUri, properties, sessionType);

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
			release(ruleSession);
		}

		return outputObjects;
	}

	// Public ----------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param ruleRuntime the ruleRuntime to set
	 */
	@Resource
	@WebMethod(exclude=true)
	public void setRuleRuntime(RuleRuntime ruleRuntime) {
		this.ruleRuntime = ruleRuntime;
	}

	// Package protected -----------------------------------------------------

	// Protected -------------------------------------------------------------

	// Private ---------------------------------------------------------------

	/**
	 * TODO
	 * 
	 * @param ruleSession
	 */
	private void release(StatelessRuleSession ruleSession) {
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
		}
	}

	// Inner classes ---------------------------------------------------------
}
