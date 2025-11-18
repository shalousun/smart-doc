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
package io.github.smartdoc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * the webSocket doc
 *
 * @author linwumingshi
 * @since 3.0.3
 */
public class WebSocketDoc extends ApiDoc {

	/**
	 * the subProtocol list of webSocket
	 * <p>
	 * Sec-WebSocket-Protocol: soap, wamp
	 */
	private String subProtocols;

	/**
	 * path params
	 */
	private List<ApiParam> pathParams;

	/**
	 * message param
	 */
	private List<ApiParam> messageParams;

	/**
	 * response param
	 */
	private List<List<ApiParam>> responseParams;

	/**
	 * webSocket url
	 */
	private String uri;

	/**
	 * webSocket deprecated
	 */
	private Boolean deprecated;

	public WebSocketDoc() {
		this.deprecated = false;
	}

	public String getSubProtocols() {
		return subProtocols;
	}

	public void setSubProtocols(String subProtocols) {
		this.subProtocols = subProtocols;
	}

	public List<ApiParam> getPathParams() {
		if (Objects.isNull(this.pathParams)) {
			return new ArrayList<>();
		}
		return pathParams;
	}

	public void setPathParams(List<ApiParam> pathParams) {
		this.pathParams = pathParams;
	}

	public List<ApiParam> getMessageParams() {
		if (messageParams == null) {
			return new ArrayList<>();
		}
		return messageParams;
	}

	public WebSocketDoc setMessageParams(List<ApiParam> messageParams) {
		this.messageParams = messageParams;
		return this;
	}

	public List<List<ApiParam>> getResponseParams() {
		return responseParams;
	}

	public WebSocketDoc setResponseParams(List<List<ApiParam>> responseParams) {
		this.responseParams = responseParams;
		return this;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Boolean getDeprecated() {
		return deprecated;
	}

	public void setDeprecated(Boolean deprecated) {
		this.deprecated = deprecated;
	}

}
