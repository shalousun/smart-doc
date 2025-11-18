/*
 * Copyright (C) 2018-2024 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.github.smartdoc.function;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.smartdoc.utils.DocUtil;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * Function to escape XML content in Beetl templates.
 * <p>
 * For example, it replaces: - <code>" "</code> with <code> </code>
 *
 * @author yu 2021/6/26.
 */
public class WordXmlEscape implements Function {

	/*
	 * Represents a non-breaking space character (U+00A0) commonly used in Word documents.
	 * In HTML, this character is typically represented as "&nbsp;".
	 */
	private static final String UNICODE_NON_BREAK_SPACE = "\u00A0";

	private static final Map<String, String> XML_ESCAPE_MAP;

	static {
		XML_ESCAPE_MAP = new LinkedHashMap<>();
		XML_ESCAPE_MAP.put(" ", UNICODE_NON_BREAK_SPACE);
		XML_ESCAPE_MAP.put("&nbsp;&nbsp;", UNICODE_NON_BREAK_SPACE);
		XML_ESCAPE_MAP.put("&nbsp;", "");
		XML_ESCAPE_MAP.put("<br/>", "");
	}

	@Override
	public String call(Object[] params, Context ctx) {
		if (params == null || params.length == 0 || params[0] == null) {
			return "";
		}

		String xml = String.valueOf(params[0]);
		for (Map.Entry<String, String> entry : XML_ESCAPE_MAP.entrySet()) {
			xml = xml.replaceAll(entry.getKey(), entry.getValue());
		}

		return DocUtil.getEscapeAndCleanComment(xml);
	}

}
