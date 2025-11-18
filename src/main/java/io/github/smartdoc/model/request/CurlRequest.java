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
package io.github.smartdoc.model.request;

import io.github.smartdoc.model.ApiReqParam;
import io.github.smartdoc.model.FormData;

import java.util.List;

/**
 * curl request
 *
 * @author yu 2020/12/21.
 */
public class CurlRequest {

	/**
	 * Http method
	 */
	private String type;

	/**
	 * Request headers
	 */
	private List<ApiReqParam> reqHeaders;

	/**
	 * Request file form data
	 */
	private List<FormData> fileFormDataList;

	/**
	 * Request url
	 */
	private String url;

	/**
	 * Request body
	 */
	private String body;

	/**
	 * Content-Type
	 */
	private String contentType;

	public static CurlRequest builder() {
		return new CurlRequest();
	}

	public List<FormData> getFileFormDataList() {
		return fileFormDataList;
	}

	public CurlRequest setFileFormDataList(List<FormData> fileFormDataList) {
		this.fileFormDataList = fileFormDataList;
		return this;
	}

	public String getType() {
		return type;
	}

	public CurlRequest setType(String type) {
		this.type = type;
		return this;
	}

	public List<ApiReqParam> getReqHeaders() {
		return reqHeaders;
	}

	public CurlRequest setReqHeaders(List<ApiReqParam> reqHeaders) {
		this.reqHeaders = reqHeaders;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public CurlRequest setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getBody() {
		return body;
	}

	public CurlRequest setBody(String body) {
		this.body = body;
		return this;
	}

	public String getContentType() {
		return contentType;
	}

	public CurlRequest setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

}
