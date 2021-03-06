package org.radeox.macro;

/*
 * This file is part of "SnipSnap Radeox Rendering Engine".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://radeox.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
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
 * --LICENSE NOTICE--
 */

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.radeox.Messages;
import org.radeox.api.macro.MacroParameter;
import org.radeox.util.Encoder;

/*
 * Displays a mail to link. @author stephan @team sonicteam
 * 
 * @version $Id$
 */

public class MailToMacro extends LocalePreserved
{
	private static Logger log = LoggerFactory.getLogger(MailToMacro.class);

	private String[] paramDescription = { Messages.getString("MailToMacro.0") }; //$NON-NLS-1$

	public String getLocaleKey()
	{
		return "macro.mailto"; //$NON-NLS-1$
	}

	public String[] getParamDescription()
	{
		return paramDescription;
	}

	public void execute(Writer writer, MacroParameter params)
			throws IllegalArgumentException, IOException
	{

		if (params.getLength() == 1)
		{
			String mail = Encoder.escape(params.get("0")); //$NON-NLS-1$
			writer.write("<a href=\"mailto:" + mail + "\">" + mail + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return;
	}
}
