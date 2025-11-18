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
import com.power.common.util.DateTimeUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.List;

/**
 * use to create websocket Markdown doc.
 *
 * @author linwumingshi
 * @since 3.0.3
 */
public class WebSocketMarkdownBuilder {

	/**
	 * private constructor
	 */
	private WebSocketMarkdownBuilder() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * build websocket Markdown doc.
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

		if (config.isAllInOne()) {
			String version = config.isCoverOld() ? "" : "-V" + DateTimeUtil.long2Str(System.currentTimeMillis(),
					DocGlobalConstants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
			String docName = webSocketDocBuilderTemplate.allInOneDocName(config, "webSocket-all" + version,
					DocGlobalConstants.MARKDOWN_EXTENSION);
			webSocketDocBuilderTemplate.buildWebSocketAllInOne(webSocketDocList, config, javaProjectBuilder,
					DocGlobalConstants.WEBSOCKET_ALL_IN_ONE_MD_TPL, docName);
		}
		else {
			webSocketDocBuilderTemplate.buildWebSocketApiDoc(webSocketDocList, config,
					DocGlobalConstants.WEBSOCKET_MD_TPL, DocGlobalConstants.MARKDOWN_API_FILE_EXTENSION);

			webSocketDocBuilderTemplate.buildErrorCodeDoc(config, DocGlobalConstants.ERROR_CODE_LIST_MD_TPL,
					DocGlobalConstants.ERROR_CODE_LIST_MD, javaProjectBuilder);
		}
	}

}
