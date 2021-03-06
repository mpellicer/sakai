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

package edu.amc.sakai.user;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.detail.ValueEncryptionUtilities;


/**
 * Extension for {@link SimpleLdapAttributeMapper}.
 * Adds logic to include encrypted CandidateId and additional info into the sakai user object.
 *
 */
public class SimpleLdapCandidateAttributeMapper extends SimpleLdapAttributeMapper {
	
	private static final String USER_PROP_CANDIDATE_ID = "candidateID";
	private static final String USER_PROP_ADDITIONAL_INFO = "additionalInfo";
	private static final String EMPTY = "";

	/** Class-specific logger */
	private static Log M_log = LogFactory.getLog(SimpleLdapCandidateAttributeMapper.class);

	private ValueEncryptionUtilities encryption;
	private int candidateIdLength;
	private int additionalInfoLength;

	public void setEncryption(ValueEncryptionUtilities encryption) {
		this.encryption = encryption;
	}

	public void setCandidateIdLength(int candidateIdLength) {
		this.candidateIdLength = candidateIdLength;
	}

	public void setAdditionalInfoLength(int additionalInfoLength) {
		this.additionalInfoLength = additionalInfoLength;
	}

	/**
	 * Completes configuration of this instance.
	 * 
	 * <p>Initializes internal mappings to a copy of 
	 * {@link AttributeMappingConstants#DEFAULT_ATTR_MAPPINGS} if 
	 * the current map is empty. Initializes user 
	 * type mapping strategy to a 
	 * {@link EmptyStringUserTypeMapper} if no strategy
	 * has been specified.
	 * </p>
	 * 
	 * <p>This defaulting enables UDP config 
	 * forward-compatibility.</p>
	 * 
	 */
	public void init() {
		
		if ( M_log.isDebugEnabled() ) {
			M_log.debug("init()");
		}

		if ( getAttributeMappings() == null || getAttributeMappings().isEmpty() ) {
			if ( M_log.isDebugEnabled() ) {
				M_log.debug("init(): creating default attribute mappings");
			}
			setAttributeMappings(AttributeMappingConstants.CANDIDATE_ATTR_MAPPINGS);
		}
		 
		if ( getUserTypeMapper() == null ) {
			setUserTypeMapper(new EmptyStringUserTypeMapper());
			if ( M_log.isDebugEnabled() ) {
				M_log.debug("init(): created default user type mapper [mapper = " + 
						getUserTypeMapper() + "]");
			}
		}
		if ( getValueMappings() == null ) {
			setValueMappings(Collections.EMPTY_MAP);
			if ( M_log.isDebugEnabled() ) {
				M_log.debug("init(): created default value mapper [mapper = " +
						getValueMappings() + "]");
			}
		} else {
			// Check we have good value mappings and throw any out that aren't (warning user).
			Iterator<Entry<String, MessageFormat>> iterator = getValueMappings().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, MessageFormat> entry = iterator.next();
				if (entry.getValue().getFormats().length != 1) {
					iterator.remove();
					M_log.warn(String.format("Removed value mapping as it didn't have one format: %s -> %s",
							entry.getKey(), entry.getValue().toPattern()));
				}
			}
		}
	}
	
	/**
	 * Caches the given Map reference and takes a 
	 * snapshot of the values therein for future
	 * use by {@link #getSearchResultAttributes()}. (using super method)
	 * 
	 * @see #getAttributeMappings()
	 */
	public void setAttributeMappings(Map<String,String> attributeMappings)
	{
		if ( attributeMappings == null || attributeMappings.isEmpty() ) {
			super.setAttributeMappings(AttributeMappingConstants.CANDIDATE_ATTR_MAPPINGS);
		} else {
			super.setAttributeMappings(attributeMappings);
		}
	}

	/**
	 * Straightforward {@link LdapUserData} to 
	 * {@link org.sakaiproject.user.api.UserEdit} field-to-field mapping, including
	 * properties.
	 */
	public void mapUserDataOntoUserEdit(LdapUserData userData, UserEdit userEdit) {
		Properties userDataProperties = userData.getProperties();
		ResourceProperties userEditProperties = userEdit.getProperties();
		if(userDataProperties != null) {
			Object o_prop = userDataProperties.get(AttributeMappingConstants.CANDIDATE_ID_ATTR_MAPPING_KEY);
			if(o_prop != null) {
				if(o_prop instanceof String) {
					userEditProperties.addProperty(USER_PROP_CANDIDATE_ID, encryption.encrypt((String)o_prop, candidateIdLength));
				} else if(o_prop instanceof List) {
					Set<String> propertySet = new HashSet<String>();
					//remove duplicate values
					for(String value : (List<String>)o_prop) {
						propertySet.add(value);
					}
					//add candidate ID, if there is only one value
					if(propertySet.size() == 1) {
						for(String value : propertySet) {
							userEditProperties.addProperty(USER_PROP_CANDIDATE_ID, encryption.encrypt(value, candidateIdLength));
						}
					}
				}
				userDataProperties.remove(AttributeMappingConstants.CANDIDATE_ID_ATTR_MAPPING_KEY);
			}
			
			o_prop = userDataProperties.get(AttributeMappingConstants.ADDITIONAL_INFO_ATTR_MAPPING_KEY);
			if(o_prop != null) {
				if(o_prop instanceof String) {
					userEditProperties.addPropertyToList(USER_PROP_ADDITIONAL_INFO, encryption.encrypt((String)o_prop, additionalInfoLength));
				} else if(o_prop instanceof List) {
					List<String> propertyList = new ArrayList<String>();
					//remove duplicate values (maintain order)
					for(String value : (List<String>)o_prop) {
						if(!propertyList.contains(value))
							propertyList.add(value);
					}
					for(String value : propertyList) {
						userEditProperties.addPropertyToList(USER_PROP_ADDITIONAL_INFO, encryption.encrypt(value, additionalInfoLength));
					}
				}
				userDataProperties.remove(AttributeMappingConstants.ADDITIONAL_INFO_ATTR_MAPPING_KEY);
			}
			// Make sure we have a value for the encrypted properties.
			if (StringUtils.isEmpty(userEditProperties.getProperty(USER_PROP_CANDIDATE_ID))) {
				userEditProperties.addProperty(USER_PROP_CANDIDATE_ID, encryption.encrypt(EMPTY, candidateIdLength));
			}
			if (userEditProperties.getPropertyList(USER_PROP_ADDITIONAL_INFO)!= null &&
					userEditProperties.getPropertyList(USER_PROP_ADDITIONAL_INFO).isEmpty()) {
				userEditProperties.addPropertyToList(USER_PROP_ADDITIONAL_INFO, encryption.encrypt(EMPTY, candidateIdLength));
			}

		super.mapUserDataOntoUserEdit(userData, userEdit);

		}

	}
}
