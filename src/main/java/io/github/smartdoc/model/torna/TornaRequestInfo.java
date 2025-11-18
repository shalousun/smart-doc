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
package io.github.smartdoc.model.torna;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import io.github.smartdoc.constants.TornaConstants;

/**
 * Print the log of pushing documents to Torna
 *
 * @author xingzi 2021/3/20 22:11
 **/
public class TornaRequestInfo {

	private String code;

	private String message;

	private Object requestInfo;

	private String responseInfo;

	private String category;

	public String getCategory() {
		return category;
	}

	public TornaRequestInfo setCategory(String category) {
		this.category = category;
		return this;
	}

	public TornaRequestInfo of() {
		return this;
	}

	public String getCode() {
		return code;
	}

	public TornaRequestInfo setCode(String code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public TornaRequestInfo setMessage(String message) {
		this.message = message;
		return this;
	}

	public Object getRequestInfo() {
		return requestInfo;
	}

	public TornaRequestInfo setRequestInfo(Object requestInfo) {
		this.requestInfo = requestInfo;
		return this;
	}

	public Object getResponseInfo() {
		return responseInfo;
	}

	public TornaRequestInfo setResponseInfo(String responseInfo) {
		this.responseInfo = responseInfo;
		return this;
	}

	/**
	 * Builds a full log string including both request and response data.
	 * @return Formatted log string with request and response details
	 */
	public String buildInfo() {
		return buildLogContent(true, "=============== REQUEST & RESPONSE LOG END ===============");
	}

	/**
	 * Builds a log string containing only request data (no response).
	 * @return Formatted log string with request details only
	 */
	public String buildRequestInfo() {
		return buildLogContent(false, "==================== REQUEST LOG END ====================");
	}

	/**
	 * Shared method to construct log content dynamically.
	 * @param includeResponse Whether to include response data
	 * @param closingMarker Custom closing boundary marker
	 */
	private String buildLogContent(boolean includeResponse, String closingMarker) {
		StringBuilder sb = new StringBuilder().append("==================== PUSH LOG START ====================\n")
			.append("API: ")
			.append(category)
			.append("\n")
			.append("Request Param: \n")
			.append(TornaConstants.GSON.toJson(requestInfo))
			.append("\n");

		if (includeResponse) {
			sb.append("Response: \n").append(TornaConstants.GSON.fromJson(responseInfo, HashMap.class)).append("\n");
		}

		sb.append(closingMarker).append("\n"); // Custom closing marker

		try {
			return URLDecoder.decode(sb.toString(), StandardCharsets.UTF_8.name());
		}
		catch (UnsupportedEncodingException e) {
			return ""; // In production, log this error (e.g., via SLF4J)
		}
	}

}
