/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2008 Etudes, Inc.
 * 
 * Portions completed before September 1, 2008
 * Copyright (c) 2007, 2008 The Regents of the University of Michigan & Foothill College, ETUDES Project
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
 **********************************************************************************/

package org.etudes.ambrosia.impl;

import java.io.PrintWriter;

import org.etudes.ambrosia.api.Context;
import org.etudes.ambrosia.api.Divider;
import org.w3c.dom.Element;

/**
 * UiDivider implements Divider.
 */
public class UiDivider extends UiComponent implements Divider
{
	/**
	 * Public no-arg constructor.
	 */
	public UiDivider()
	{
	}

	/**
	 * Construct from a dom element.
	 * 
	 * @param service
	 *        The UiService.
	 * @param xml
	 *        The dom element.
	 */
	protected UiDivider(UiServiceImpl service, Element xml)
	{
		// component stuff
		super(service, xml);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean render(Context context, Object focus)
	{
		// included?
		if (!isIncluded(context, focus)) return false;

		PrintWriter response = context.getResponseWriter();

		response.println("<span class=\"ambrosiaDivider\">&nbsp;</span>");

		return true;
	}
}
