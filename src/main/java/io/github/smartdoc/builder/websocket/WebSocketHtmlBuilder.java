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
package io.github.smartdoc.builder.websocket;

import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.WebSocketDoc;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.List;

/**
 * use to create websocket Html doc.
 *
 * @author linwumingshi
 * @since 3.0.7
 */
public class WebSocketHtmlBuilder {

	/**
	 * index.html
	 */
	private final static String INDEX_HTML = "websocket-index.html";

	/**
	 * private constructor
	 */
	private WebSocketHtmlBuilder() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * build websocket html doc.
	 * @param config ApiConfig
	 */
	public static void buildApiDoc(ApiConfig config) {
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		buildApiDoc(config, javaProjectBuilder);
	}

	/**
	 * Only for smart-doc maven plugin and gradle plugin.
	 * @param config ApiConfig
	 * @param javaProjectBuilder ProjectDocConfigBuilder
	 */
	public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		WebSocketDocBuilderTemplate webSocketDocBuilderTemplate = new WebSocketDocBuilderTemplate();
		List<WebSocketDoc> webSocketDocList = webSocketDocBuilderTemplate.getWebSocketApiDoc(Boolean.FALSE, config,
				javaProjectBuilder);

		if (null == webSocketDocList || webSocketDocList.isEmpty()) {
			return;
		}
		webSocketDocBuilderTemplate.copyJQueryAndCss(config);
		webSocketDocBuilderTemplate.buildWebSocketAllInOne(webSocketDocList, config, javaProjectBuilder,
				DocGlobalConstants.WEBSOCKET_ALL_IN_ONE_HTML_TPL, INDEX_HTML);
		webSocketDocBuilderTemplate.buildSearchJs(webSocketDocList, config, javaProjectBuilder,
				DocGlobalConstants.WEBSOCKET_ALL_IN_ONE_SEARCH_TPL, DocGlobalConstants.SEARCH_JS_OUT);

	}

}
