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
package io.github.smartdoc.constants;

/**
 * Template Variable.
 *
 * @author yu 2019/9/21.
 */
public enum TemplateVariable {

	/**
	 * desc
	 */
	DESC("desc"),
	/**
	 * name
	 */
	NAME("name"),
	/**
	 * list
	 */
	LIST("list"),
	/**
	 * apiDocList
	 */
	API_DOC_LIST("apiDocList"),
	/**
	 * apiDocListOnlyHasDefaultGroup
	 */
	API_DOC_LIST_ONLY_HAS_DEFAULT_GROUP("apiDocListOnlyHasDefaultGroup"),
	/**
	 * errorCodeList
	 */
	ERROR_CODE_LIST("errorCodeList"),
	/**
	 * errorCodeListOrder
	 */
	ERROR_CODE_ORDER("errorCodeListOrder"),
	/**
	 * versionList
	 */
	VERSION_LIST("revisionLogList"),
	/**
	 * dependencyList
	 */
	DEPENDENCY_LIST("dependencyList"),
	/**
	 * homePage
	 */
	HOME_PAGE("homePage"),
	/**
	 * html
	 */
	HTML("html"),
	/**
	 * title
	 */
	TITLE("title"),
	/**
	 * style
	 */
	STYLE("style"),
	/**
	 * background
	 */
	BACKGROUND("background"),
	/**
	 * errorListTitle
	 */
	ERROR_LIST_TITLE("errorListTitle"),
	/**
	 * createTime
	 */
	CREATE_TIME("createTime"),
	/**
	 * projectName
	 */
	PROJECT_NAME("projectName"),
	/**
	 * language
	 */
	LANGUAGE("language"),
	/**
	 * dictList
	 */
	DICT_LIST("dictList"),
	/**
	 * dictListTitle
	 */
	DICT_LIST_TITLE("dictListTitle"),
	/**
	 * dictListOrder
	 */
	DICT_ORDER("dictListOrder"),
	/**
	 * version
	 */
	VERSION("version"),
	/**
	 * protocol
	 */
	PROTOCOL("protocol"),
	/**
	 * author
	 */
	AUTHOR("author"),
	/**
	 * uri
	 */
	URI("uri"),
	/**
	 * consumerConfigExample
	 */
	RPC_CONSUMER_CONFIG("consumerConfigExample"),
	/**
	 * requestExample
	 */
	REQUEST_EXAMPLE("isRequestExample"),
	/**
	 * responseExample
	 */
	RESPONSE_EXAMPLE("isResponseExample"),
	/**
	 * displayRequestParams
	 */
	DISPLAY_REQUEST_PARAMS("displayRequestParams"),
	/**
	 * displayResponseParams
	 */
	DISPLAY_RESPONSE_PARAMS("displayResponseParams"),
	/**
	 * respList
	 */
	RESPONSE_LIST("respList"),
	/**
	 * order
	 */
	ORDER("order"),
	/**
	 * indexAlias
	 */
	INDEX_ALIAS("alias"),
	/**
	 * directoryTree
	 */
	DIRECTORY_TREE("directoryTree"),
	/**
	 * highlightCssLink
	 */
	HIGH_LIGHT_CSS_LINK("highlightCssLink"),
	/**
	 * css_cdn
	 */
	CSS_CND("css_cdn"),
	/**
	 * webSocketDocList
	 */
	WEBSOCKET_DOC_LIST("webSocketDocList"),

	/**
	 * jmeterPrometheusListener
	 */
	JMETER_PROMETHEUS_LISTENER("jmeterPrometheusListener"),

	/**
	 * subProtocols
	 */
	SUB_PROTOCOLS("subProtocols"),

	/**
	 * deprecated
	 */
	DEPRECATED("deprecated"),

	/**
	 * webSocketPathParams
	 */
	WEBSOCKET_PATH_PARAMS("pathParams"),

	/**
	 * webSocketMessageParams
	 */
	WEBSOCKET_MESSAGE_PARAMS("messageParams"),

	/**
	 * webSocketResponseParams
	 */
	WEBSOCKET_RESPONSE_PARAMS("responseParams"),

	;

	/**
	 * variable
	 */
	private final String variable;

	/**
	 * constructor
	 * @param variable variable
	 */
	TemplateVariable(String variable) {
		this.variable = variable;
	}

	/**
	 * get variable
	 * @return variable
	 */
	public String getVariable() {
		return this.variable;
	}

}
