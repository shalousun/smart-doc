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

import io.github.smartdoc.utils.DocUtil;

import org.beetl.core.Context;
import org.beetl.core.Function;

import java.util.HashMap;
import java.util.Map;

/**
 * Function to escape HTML content in Beetl templates.
 * <p>
 * This class provides a method to replace specific HTML characters with their
 * corresponding escape sequences and clean up comments.
 * <p>
 * For example, it replaces:
 * <p>
 * - <code>"</code> with <code>&amp;quot;</code>
 *
 * @author yu
 * @since 2021/6/26
 */
public class HtmlEscape implements Function {

	/**
	 * Map to hold HTML escape sequences.
	 */
	private static final Map<String, String> HTML_ESCAPE_MAP;

	static {
		HTML_ESCAPE_MAP = new HashMap<>();
		HTML_ESCAPE_MAP.put("<p>", "");
		HTML_ESCAPE_MAP.put("</p>", " ");
	}

	@Override
	public String call(Object[] params, Context ctx) {
		if (params == null || params.length == 0 || params[0] == null) {
			return "";
		}
		String html = String.valueOf(params[0]);
		for (Map.Entry<String, String> entry : HTML_ESCAPE_MAP.entrySet()) {
			html = html.replace(entry.getKey(), entry.getValue());
		}

		html = DocUtil.getEscapeAndCleanComment(html);
		return DocUtil.replaceNewLineToHtmlBr(html);
	}

}
