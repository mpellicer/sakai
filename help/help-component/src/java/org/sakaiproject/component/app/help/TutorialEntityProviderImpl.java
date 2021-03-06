package org.sakaiproject.component.app.help;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.InvariantReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sakaiproject.api.app.help.TutorialEntityProvider;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RESTful;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.search.Search;
import org.sakaiproject.util.ResourceLoader;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorialEntityProviderImpl implements TutorialEntityProvider, AutoRegisterEntityProvider, RESTful{

	protected final Logger log = LoggerFactory.getLogger(getClass());
	private ResourceLoader msgs = new ResourceLoader("TutorialMessages");
	private org.sakaiproject.component.api.ServerConfigurationService serverConfigurationService;
	private static PropertiesConfiguration tutorialProps;

	private void initConfig() {
		
		URL url = getClass().getClassLoader().getResource("Tutorial.config"); 
		
		try {
			tutorialProps = new PropertiesConfiguration(); //must use blank constructor so it doesn't parse just yet (as it will split)
			tutorialProps.setReloadingStrategy(new InvariantReloadingStrategy());	//don't watch for reloads
			tutorialProps.setThrowExceptionOnMissing(false);	//throw exception if no prop
			tutorialProps.setDelimiterParsingDisabled(true); //don't split properties
			tutorialProps.load(url); //now load our file
		} catch (ConfigurationException e) {
			log.error(e.getClass() + ": " + e.getMessage());
			return;
		}
	}
	
	@Override
	public String getEntityPrefix() {
		return ENTTITY_PREFIX;
	}

	@Override
	public String createEntity(EntityReference ref, Object entity,
			Map<String, Object> params) {
		return null;
	}

	@Override
	public Object getSampleEntity() {
		return null;
	}

	@Override
	public void updateEntity(EntityReference ref, Object entity,
			Map<String, Object> params) {
	}

	@Override
	public Object getEntity(EntityReference ref) {
		if(tutorialProps == null){
			initConfig();
			if(tutorialProps == null){
				return null;
			}
		}
		String previousUrl = tutorialProps.getString(ref.getId() + ".previousUrl");
		String nextUrl = tutorialProps.getString(ref.getId() + ".nextUrl");
		String sakaiInstanceName = ServerConfigurationService.getString("ui.service", "Sakai");
		String selection = tutorialProps.getString(ref.getId() + ".selection");

		Map valuesMap = new HashMap<String, String>();
		valuesMap.put("selection", selection);
		valuesMap.put("title", msgs.getFormattedMessage(ref.getId() + ".title", sakaiInstanceName));
		valuesMap.put("dialog", tutorialProps.getString(ref.getId() + ".dialog"));
		valuesMap.put("positionTooltip", tutorialProps.getString(ref.getId() + ".positionTooltip"));
		valuesMap.put("positionTarget", tutorialProps.getString(ref.getId() + ".positionTarget"));
		valuesMap.put("fadeout", tutorialProps.getString(ref.getId() + ".fadeout"));
		valuesMap.put("previousUrl", previousUrl);
		valuesMap.put("nextUrl", nextUrl);
                	
		//build the body html:
		//String body = msgs.getString(ref.getId() + ".body");
                
                String body = msgs.getFormattedMessage(ref.getId() + ".body", sakaiInstanceName);
		
		//build footer html:
		String footerHtml = "<div class='tut-footer'>";
		if(previousUrl != null && !"".equals(previousUrl)){
			footerHtml += "<div class='tut-previous'><a href='#' class='qtipLinkButton' onclick='previousClicked=true;showTutorialPage(\"" + previousUrl + "\");'><i class='fa fa-arrow-left'></i>&nbsp;" + msgs.getString("previous") + "</a></div>";
		}
		if(nextUrl != null && !"".equals(nextUrl)){
			footerHtml += "<div class='tut-next'><a href='#' class='qtipLinkButton' onclick='showTutorialPage(\"" + nextUrl + "\");'>" + msgs.getString("next") + "&nbsp;<i class='fa fa-arrow-right'></i></a></div>";
		}else{
			footerHtml += "<a href='#' class='btn-primary tut-close' onclick='endTutorial(\"" + selection + "\");' >"+ msgs.getString("endTutorial") +"</a>";
		}
		footerHtml += "</div>";
		body += footerHtml;
		
		valuesMap.put("body", body);
		
		return valuesMap;
	}

	@Override
	public void deleteEntity(EntityReference ref, Map<String, Object> params) {
	}

	@Override
	public List<?> getEntities(EntityReference ref, Search search) {
		return null;
	}

	@Override
	public String[] getHandledOutputFormats() {
		return null;
	}

	@Override
	public String[] getHandledInputFormats() {
		return new String[] { Formats.HTML, Formats.XML, Formats.JSON };
	}


	public void setServerConfigurationService(org.sakaiproject.component.api.ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}

	public org.sakaiproject.component.api.ServerConfigurationService getServerConfigurationService() {
		return serverConfigurationService;
	}
}
