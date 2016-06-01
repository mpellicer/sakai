/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.portal.charon.site;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.portal.api.Portal;
import org.sakaiproject.portal.api.PortalSiteHelper;
import org.sakaiproject.portal.api.SiteNeighbourhoodService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.user.api.PreferencesService;

import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.component.cover.ComponentManager;

import org.sakaiproject.util.Web;

/**
 * @author ieb
 */
public class MoreSiteViewImpl extends AbstractSiteViewImpl
{
	private static final Log LOG = LogFactory.getLog(MoreSiteViewImpl.class);

        /** messages. */
        private static ResourceLoader rb = new ResourceLoader("sitenav");
	private CourseManagementService courseManagementService = (CourseManagementService) ComponentManager.get(CourseManagementService.class);

	/**
	 * @param siteHelper
	 * @param request
	 * @param session
	 * @param currentSiteId
	 * @param siteService
	 * @param serverConfigurationService
	 * @param preferencesService
	 */
	public MoreSiteViewImpl(PortalSiteHelper siteHelper, SiteNeighbourhoodService siteNeighbourhoodService, HttpServletRequest request,
							Session session, String currentSiteId, SiteService siteService,
							ServerConfigurationService serverConfigurationService,
							PreferencesService preferencesService)
	{
		super(siteHelper, siteNeighbourhoodService, request, session, currentSiteId, null, siteService,
				serverConfigurationService, preferencesService);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.portal.api.SiteView#getRenderContextObject()
	 */
	public Object getRenderContextObject()
	{
		// Get the list of sites in the right order,
		// My WorkSpace will be the first in the list

		// if public workgroup/gateway site is not included, add to list
		boolean siteFound = false;
		for (int i = 0; i < mySites.size(); i++)
		{
			if (((Site) mySites.get(i)).getId().equals(currentSiteId))
			{
				siteFound = true;
			}
		}

		try
		{
			if (!siteFound)
			{
				mySites.add(siteService.getSite(currentSiteId));
			}
		}
		catch (IdUnusedException e)
		{

		} // ignore

		// we allow one site in the drawer - that is OK
		moreSites = new ArrayList<Site>();
		
		processMySites();

		String profileToolId = serverConfigurationService.getString("portal.profiletool","sakai.profile2");
		String preferencesToolId = serverConfigurationService.getString("portal.preferencestool","sakai.preferences");
		String worksiteToolId = serverConfigurationService.getString("portal.worksitetool","sakai.sitesetup");

 		String profileToolUrl = null;
 		String worksiteToolUrl = null;
 		String prefsToolUrl = null;
 		String mrphs_profileToolUrl = null;
 		String mrphs_worksiteToolUrl = null;
 		String mrphs_prefsToolUrl = null;
 		String mrphs_worksiteUrl = null;
        if ( myWorkspaceSiteId != null ) {
            for (Iterator iSi = mySites.iterator(); iSi.hasNext();) {
                Site s = (Site) iSi.next();
                if (myWorkspaceSiteId.equals(s.getId()) ) {
                    mrphs_worksiteUrl = Web.returnUrl(request, "/site/" + Web.escapeUrl(siteHelper.getSiteEffectiveId(s)));
                    List pages = siteHelper.getPermittedPagesInOrder(s);
                    for (Iterator iPg = pages.iterator(); iPg.hasNext();) {
                        SitePage p = (SitePage) iPg.next();
                        List<ToolConfiguration> pTools = p.getTools();
                        Iterator iPt = pTools.iterator();
                        while (iPt.hasNext()) {
                            ToolConfiguration placement = (ToolConfiguration) iPt.next();
                            if ( profileToolId.equals(placement.getToolId()) ) {
                                profileToolUrl = Web.returnUrl(request, "/site/" + Web.escapeUrl(siteHelper.getSiteEffectiveId(s)) + "/page/" + Web.escapeUrl(p.getId()));
                                mrphs_profileToolUrl = Web.returnUrl(request, "/site/" + Web.escapeUrl(siteHelper.getSiteEffectiveId(s)) + "/tool-reset/" + Web.escapeUrl(placement.getId()));
                            } else if ( preferencesToolId.equals(placement.getToolId()) ) {
                                prefsToolUrl = Web.returnUrl(request, "/site/" + Web.escapeUrl(siteHelper.getSiteEffectiveId(s)) + "/page/" + Web.escapeUrl(p.getId()));
                                mrphs_prefsToolUrl = Web.returnUrl(request, "/site/" + Web.escapeUrl(siteHelper.getSiteEffectiveId(s)) + "/tool-reset/" + Web.escapeUrl(placement.getId()));
                            } else if ( worksiteToolId.equals(placement.getToolId()) ) {
                                worksiteToolUrl = Web.returnUrl(request, "/site/" + Web.escapeUrl(siteHelper.getSiteEffectiveId(s)) + "/page/" + Web.escapeUrl(p.getId()));
                                mrphs_worksiteToolUrl = Web.returnUrl(request, "/site/" + Web.escapeUrl(siteHelper.getSiteEffectiveId(s)) + "/tool-reset/" + Web.escapeUrl(placement.getId()));
                            }
                        }
                    }
                }
            }
        }

		if ( mrphs_worksiteUrl != null ) {
			renderContextMap.put("mrphs_worksiteUrl", mrphs_worksiteUrl);
        }
		if ( profileToolUrl != null ) {
			renderContextMap.put("profileToolUrl", profileToolUrl);
			renderContextMap.put("mrphs_profileToolUrl", mrphs_profileToolUrl);
		}
		if ( prefsToolUrl != null ) {
			renderContextMap.put("prefsToolUrl", prefsToolUrl);
			renderContextMap.put("mrphs_prefsToolUrl", mrphs_prefsToolUrl);
		}
		if ( worksiteToolUrl != null ) {
			renderContextMap.put("worksiteToolUrl", worksiteToolUrl);
			renderContextMap.put("mrphs_worksiteToolUrl", mrphs_worksiteToolUrl);
		}
		if (serverConfigurationService.getBoolean("portal.use.tutorial", true)) {
			renderContextMap.put("tutorial", true);
		} else {
			renderContextMap.put("tutorial", false);
		}
		List<Map> l = siteHelper.convertSitesToMaps(request, mySites, prefix,
				currentSiteId, myWorkspaceSiteId,
				/* includeSummary */false, /* expandSite */false,
				/* resetTools */"true".equalsIgnoreCase(serverConfigurationService
						.getString(Portal.CONFIG_AUTO_RESET)),
				/* doPages */true, /* toolContextPath */null, loggedIn);

		renderContextMap.put("tabsSites", l);

		boolean displayActive = serverConfigurationService.getBoolean("portal.always.display.active_sites",false);
		//If we don't always want to display it anyway, check to see if we need to display it
		if (!displayActive) {
				displayActive=Boolean.valueOf(moreSites.size() > 0);
		}

		renderContextMap.put("tabsMoreSitesShow", displayActive);

		// more dropdown
		if (moreSites.size() > 0)
		{
			List<Map> m = siteHelper.convertSitesToMaps(request, moreSites, prefix,
					currentSiteId, myWorkspaceSiteId,
					/* includeSummary */false, /* expandSite */ false,
					/* resetTools */"true".equalsIgnoreCase(serverConfigurationService
							.getString(Portal.CONFIG_AUTO_RESET)),
					/* doPages */true, /* toolContextPath */null, loggedIn);

			renderContextMap.put("tabsMoreSites", m);
		}

		return renderContextMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.portal.api.SiteView#processMySites()
	 */
	protected void processMySites()
	{
		List<Site> allSites = new ArrayList<Site>();
		allSites.addAll(mySites);
		allSites.addAll(moreSites);
		// get Sections
		Map<String, List<Site>> termsToSites = new HashMap<String, List<Site>>();
		Map<String, List> tabsMoreTerms = new TreeMap<String, List>();
		for (int i = 0; i < allSites.size(); i++)
		{
			Site site = allSites.get(i);
			ResourceProperties siteProperties = site.getProperties();

			String type = site.getType();
			String term = null;

			if (isCourseType(type))
			{
				term = siteProperties.getProperty("term");
				if(null==term) {
					term = rb.getString("moresite_unknown_term");
				}

			}
			else if (isProjectType(type))
			{
				term = rb.getString("moresite_projects");
			}
			else if ("portfolio".equals(type))
			{
				term = rb.getString("moresite_portfolios");
			}
			else if ("admin".equals(type))
			{
				term = rb.getString("moresite_administration");
			}
			else
			{
				term = rb.getString("moresite_other");
			}

			List<Site> currentList = termsToSites.get(term);
			if (currentList == null)
			{
				currentList = new ArrayList<Site>();
				termsToSites.put(term, currentList);
			}
			currentList.add(site);

			}

		// now loop through each section and convert the Lists to maps
		for (Map.Entry<String, List<Site>> entry : termsToSites.entrySet())
		{
			List<Site> currentList = entry.getValue();
			List<Map> temp = siteHelper.convertSitesToMaps(request, currentList, prefix,
					currentSiteId, myWorkspaceSiteId,
					/* includeSummary */false, /* expandSite */false,
					/* resetTools */"true".equalsIgnoreCase(serverConfigurationService
							.getString(Portal.CONFIG_AUTO_RESET)),
					/* doPages */true, /* toolContextPath */null, loggedIn);

			tabsMoreTerms.put(entry.getKey(), temp);

		}

		String[] termOrder = serverConfigurationService
				.getStrings("portal.term.order");
		List<String> tabsMoreSortedTermList = new ArrayList<String>();

		// Order term column headers according to order specified in
		// portal.term.order
		// Filter out terms for which user is not a member of any sites
		
		// SAK-19464 - Set tab order
		// Property portal.term.order 
		// Course sites (sorted in order by getAcademicSessions START_DATE ASC)
		// Rest of terms in alphabetic order
		if (termOrder != null)
		{
			for (int i = 0; i < termOrder.length; i++)
			{

				if (tabsMoreTerms.containsKey(termOrder[i]))
				{

					tabsMoreSortedTermList.add(termOrder[i]);

				}

			}
		}
		

		if (courseManagementService != null) {
			Collection<AcademicSession> sessions = courseManagementService.getAcademicSessions();
			for (AcademicSession s: sessions) {
				String title = s.getTitle();
				if (tabsMoreTerms.containsKey(title)) {
					if (!tabsMoreSortedTermList.contains(title)) {
						tabsMoreSortedTermList.add(title);
					}
				}
			}
		}

		for (String term: tabsMoreTerms.keySet())
		{
			if (!tabsMoreSortedTermList.contains(term))
			{
				tabsMoreSortedTermList.add(term);
			}
		}

		SitePanesArrangement sitesByPane = arrangeSitesIntoPanes(tabsMoreTerms);
		renderContextMap.put("tabsMoreTermsLeftPane", sitesByPane.sitesInLeftPane);
		renderContextMap.put("tabsMoreTermsRightPane", sitesByPane.sitesInRightPane);
		renderContextMap.put("tabsMoreSortedTermList", tabsMoreSortedTermList);

		if (myWorkspaceSite != null)
		{
			renderContextMap.put("myWorksite", siteHelper.convertSiteToMap(request,
					myWorkspaceSite, prefix, currentSiteId, myWorkspaceSiteId, false,
					false, "true".equals(serverConfigurationService
							.getString(Portal.CONFIG_AUTO_RESET)), false, null,
					loggedIn));
		}

	}

	private static class SitePanesArrangement {
		public Map<String, List> sitesInLeftPane = new TreeMap<String, List>();
		public Map<String, List> sitesInRightPane = new TreeMap<String, List>();
	}

	private SitePanesArrangement arrangeSitesIntoPanes(Map<String, List> tabsMoreTerms) {
		SitePanesArrangement result = new SitePanesArrangement();

		for (String term : tabsMoreTerms.keySet()) {
			result.sitesInLeftPane.put(term, new ArrayList());
			result.sitesInRightPane.put(term, new ArrayList());

			for (Map site : (List<Map>)tabsMoreTerms.get(term)) {
				if (isCourseType((String)site.get("siteType"))) {
					result.sitesInLeftPane.get(term).add(site);
				} else {
					result.sitesInRightPane.get(term).add(site);
				}
			}
		}

		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.portal.api.SiteView#isEmpty()
	 */
	public boolean isEmpty()
	{
		return mySites.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.portal.api.SiteView#setPrefix(java.lang.String)
	 */
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.portal.api.SiteView#setToolContextPath(java.lang.String)
	 */
	public void setToolContextPath(String toolContextPath)
	{
		this.toolContextPath = toolContextPath;

	}
	
	/**
	 * read the site Type definition from configuration files
	 */
	public List<String> getSiteTypeStrings(String type)
	{
		String[] siteTypes = serverConfigurationService.getStrings(type + "SiteType");
		if (siteTypes == null || siteTypes.length == 0)
		{
			siteTypes = new String[] {type};
		}
		return Arrays.asList(siteTypes);
	}

	private boolean isCourseType(String type)
	{
		List<String> courseSiteTypes = getSiteTypeStrings("course");
		if (courseSiteTypes.contains(type)) return true;
		else return false;
	}

	private boolean isProjectType(String type)
	{
		List<String> projectSiteTypes = getSiteTypeStrings("project");
		if (projectSiteTypes.contains(type)) return true;
		else return false;
	}

}
