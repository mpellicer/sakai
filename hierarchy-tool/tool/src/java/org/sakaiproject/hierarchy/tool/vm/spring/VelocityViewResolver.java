/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 *  This code was basaed on the Same Code in the SpringFramework project,
 * hence the above license. It was unbound from the SpringFramework code to remove
 * classloader problems associated with inheritance
 */

package org.sakaiproject.hierarchy.tool.vm.spring;

import java.util.Locale;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * Convenience subclass of UrlBasedViewResolver that supports VelocityView (i.e.
 * Velocity templates) and custom subclasses of it.
 * <p>
 * The view class for all views generated by this resolver can be specified via
 * <code>setViewClass</code>. See UrlBasedViewResolver's javadoc for details.
 * <p>
 * <b>Note:</b> When chaining ViewResolvers, a VelocityViewResolver always
 * needs to be last, as it will attempt to resolve any view name, no matter
 * whether the underlying resource actually exists.
 * 
 * @author Juergen Hoeller
 * @since 13.12.2003
 * @see #setViewClass
 * @see #setPrefix
 * @see #setSuffix
 * @see #setRequestContextAttribute
 * @see #setExposeSpringMacroHelpers
 * @see #setVelocityFormatterAttribute
 * @see #setDateToolAttribute
 * @see #setNumberToolAttribute
 * @see VelocityView
 */
public class VelocityViewResolver extends CachingViewResolver implements
		ViewResolver
{

	private String requestContextAttribute = null;

	private String velocityFormatterAttribute = null;

	private boolean exposeRequestAttributes = false;

	private boolean exposeSessionAttributes = false;

	private boolean exposeSpringMacroHelpers = false;

	private boolean allowRequestOverride = false;

	private boolean allowSessionOverride = false;

	private Map attributesMap = null;

	private String prefix = "";

	private String suffix = ".vm";

	private String contentType = "text/html";
	
	private VelocityConfig velocityConfig = null;

	public View resolveViewName(String viewName, Locale locale)
			throws Exception
	{
		return super.createView(viewName, locale);
	}

	protected VelocityView buildView(String viewName) throws Exception
	{
		VelocityView view = new VelocityView();
		VelocityEngine ve = velocityConfig.getVelocityEngine();
		view.setServletContext(getServletContext());
		view.setVelocityEngine(ve);
		view.setUrl(getPrefix() + viewName + getSuffix());
		String contentType = getContentType();
		if (contentType != null)
		{
			view.setContentType(contentType);
		}
		view.setRequestContextAttribute(getRequestContextAttribute());
		view.setAttributesMap(getAttributesMap());
		view.setVelocityFormatterAttribute(this.velocityFormatterAttribute);
		view.setExposeRequestAttributes(this.exposeRequestAttributes);
		view.setExposeSessionAttributes(this.exposeSessionAttributes);
		view.setExposeSpringMacroHelpers(this.exposeSpringMacroHelpers);
		view.setAllowRequestOverride(this.allowRequestOverride);
		view.setAllowSessionOverride(this.allowSessionOverride);
		return view;
	}

	protected View loadView(String viewName, Locale locale) throws Exception
	{
		return buildView(viewName);
	}

	public boolean isAllowRequestOverride()
	{
		return allowRequestOverride;
	}

	public void setAllowRequestOverride(boolean allowRequestOverride)
	{
		this.allowRequestOverride = allowRequestOverride;
	}

	public boolean isAllowSessionOverride()
	{
		return allowSessionOverride;
	}

	public void setAllowSessionOverride(boolean allowSessionOverride)
	{
		this.allowSessionOverride = allowSessionOverride;
	}

	public Map getAttributesMap()
	{
		return attributesMap;
	}

	public void setAttributesMap(Map attributesMap)
	{
		this.attributesMap = attributesMap;
	}

	public String getContentType()
	{
		return contentType;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public boolean isExposeRequestAttributes()
	{
		return exposeRequestAttributes;
	}

	public void setExposeRequestAttributes(boolean exposeRequestAttributes)
	{
		this.exposeRequestAttributes = exposeRequestAttributes;
	}

	public boolean isExposeSessionAttributes()
	{
		return exposeSessionAttributes;
	}

	public void setExposeSessionAttributes(boolean exposeSessionAttributes)
	{
		this.exposeSessionAttributes = exposeSessionAttributes;
	}

	public boolean isExposeSpringMacroHelpers()
	{
		return exposeSpringMacroHelpers;
	}

	public void setExposeSpringMacroHelpers(boolean exposeSpringMacroHelpers)
	{
		this.exposeSpringMacroHelpers = exposeSpringMacroHelpers;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getRequestContextAttribute()
	{
		return requestContextAttribute;
	}

	public void setRequestContextAttribute(String requestContextAttribute)
	{
		this.requestContextAttribute = requestContextAttribute;
	}

	public String getSuffix()
	{
		return suffix;
	}

	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}

	public String getVelocityFormatterAttribute()
	{
		return velocityFormatterAttribute;
	}

	public void setVelocityFormatterAttribute(String velocityFormatterAttribute)
	{
		this.velocityFormatterAttribute = velocityFormatterAttribute;
	}
	
	public void setVelocityConfig(VelocityConfig velocityConfig) {
		this.velocityConfig = velocityConfig;
	}
	public VelocityConfig getVelocityConfig() {
		return velocityConfig;
	}

}
