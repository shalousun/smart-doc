/*
 * smart-doc
 *
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

package io.github.smartdoc.utils;

import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.Methods;
import io.github.smartdoc.constants.ParamTypeConstants;
import io.github.smartdoc.model.ApiMethodDoc;
import io.github.smartdoc.model.ApiReqParam;
import io.github.smartdoc.model.FormData;
import io.github.smartdoc.model.request.ApiRequestExample;
import io.github.smartdoc.model.request.CurlRequest;
import com.power.common.util.UrlUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility class to handle setting example request bodies into API method documentation.
 *
 * @author yu 2024/05/27.
 * @since 3.0.5
 */
public class RequestExampleUtil {

	/**
	 * private constructor
	 */
	private RequestExampleUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Sets example data into the API method documentation.
	 * @param apiMethodDoc The API method documentation object to receive the example
	 * data.
	 * @param requestExample The API request example object containing a complete request
	 * example.
	 * @param pathParamsMap A mapping of path parameters for describing URL path
	 * variables.
	 * @param queryParamsMap A mapping of query parameters for describing URL query string
	 * parameters.
	 * @return The updated API request example with the example data set.
	 */
	public static ApiRequestExample setExampleBody(ApiMethodDoc apiMethodDoc, ApiRequestExample requestExample,
			Map<String, String> pathParamsMap, Map<String, String> queryParamsMap) {
		String[] split = apiMethodDoc.getPath().split(";");
		String path = split.length > 0 ? split[0] : null;
		if (path == null) {
			return requestExample;
		}

		// Determine the server URL and the content type
		String serverUrl = apiMethodDoc.getServerUrl();
		String contentType = apiMethodDoc.getContentType();
		List<ApiReqParam> reqHeaderList = apiMethodDoc.getRequestHeaders();

		String methodType = apiMethodDoc.getType();
		// Common URL construction
		String url = constructUrl(path, pathParamsMap, queryParamsMap, serverUrl);

		// Handle POST/PUT requests
		if (isPostOrPut(methodType)) {
			return handlePostPutRequest(requestExample, methodType, url, contentType, reqHeaderList);
		}
		// Handle GET/DELETE requests
		return handleGetDeleteRequest(requestExample, methodType, url, contentType, reqHeaderList);

	}

	/**
	 * Helper method to check if the HTTP method is either POST or PUT.
	 * @param methodType The HTTP method type (e.g., "POST", "PUT").
	 * @return True if the method type is POST or PUT, false otherwise.
	 */
	private static boolean isPostOrPut(String methodType) {
		return Methods.POST.getValue().equals(methodType) || Methods.PUT.getValue().equals(methodType);
	}

	/**
	 * Helper method to construct the URL for POST and PUT requests, including formatting
	 * path and query parameters.
	 * @param path The API method path (e.g., "/api/v1/resource").
	 * @param pathParamsMap A mapping of path parameters.
	 * @param queryParamsMap A mapping of query parameters.
	 * @param serverUrl The base URL of the server.
	 * @return A fully formatted URL with path and query parameters.
	 */
	private static String constructUrl(String path, Map<String, String> pathParamsMap,
			Map<String, String> queryParamsMap, String serverUrl) {
		// Format path with path parameters
		path = DocUtil.formatAndRemove(path, pathParamsMap);
		// Join query parameters to the path
		path = UrlUtil.urlJoin(path, queryParamsMap);
		// Simplify and return the full URL
		return UrlUtil.simplifyUrl(serverUrl + DocGlobalConstants.PATH_DELIMITER + path);
	}

	/**
	 * Helper method to group form data by type, separating file form data from regular
	 * form data.
	 * @param formDataList A list of form data entries.
	 * @return A map that groups form data by file type (Boolean key: true for files,
	 * false for regular form data).
	 */
	private static Map<Boolean, List<FormData>> groupFormDataByType(List<FormData> formDataList) {
		return formDataList.stream()
			.collect(Collectors.groupingBy(e -> Objects.equals(e.getType(), ParamTypeConstants.PARAM_TYPE_FILE)
					|| Objects.nonNull(e.getContentType())));
	}

	/**
	 * Handles GET or DELETE requests by constructing the curl request example. Since GET
	 * and DELETE requests typically do not have a request body, this method constructs
	 * the curl example without a body.
	 * @param requestExample The request example data containing the example details.
	 * @param methodType The HTTP method type (GET or DELETE).
	 * @param url The full URL for the request, including path and query parameters.
	 * @param contentType The content type for the request (e.g., "application/json").
	 * @param reqHeaderList The list of request headers.
	 * @return The updated request example with the curl request example set.
	 */
	private static ApiRequestExample handleGetDeleteRequest(ApiRequestExample requestExample, String methodType,
			String url, String contentType, List<ApiReqParam> reqHeaderList) {
		// No Request body is set for GET or DELETE requests (they typically do not
		// include a body)
		CurlRequest curlRequest = CurlRequest.builder()
			.setContentType(contentType)
			.setType(methodType)
			.setReqHeaders(reqHeaderList)
			.setUrl(url);

		// Convert the CurlRequest to a curl command string
		String exampleBody = CurlUtil.toCurl(curlRequest);

		return requestExample.setExampleBody(exampleBody).setUrl(url);
	}

	/**
	 * Handles POST or PUT requests by constructing the request body and setting the
	 * example.
	 * @param requestExample The request example data.
	 * @param methodType The HTTP method type (POST or PUT).
	 * @param url The full URL for the request.
	 * @param contentType The content type for the request.
	 * @param reqHeaderList The request headers.
	 * @return The updated request example with the request body set.
	 */
	private static ApiRequestExample handlePostPutRequest(ApiRequestExample requestExample, String methodType,
			String url, String contentType, List<ApiReqParam> reqHeaderList) {
		CurlRequest curlRequest;

		// If the request is JSON
		if (requestExample.isJson()) {
			curlRequest = CurlRequest.builder()
				.setBody(requestExample.getJsonBody())
				.setContentType(contentType)
				.setType(methodType)
				.setReqHeaders(reqHeaderList)
				.setUrl(url);
		}
		// If the request contains form data
		else {
			Map<Boolean, List<FormData>> formDataGroupMap = groupFormDataByType(requestExample.getFormDataList());
			List<FormData> fileFormDataList = formDataGroupMap.getOrDefault(Boolean.TRUE, new ArrayList<>());
			curlRequest = CurlRequest.builder()
				.setContentType(contentType)
				.setFileFormDataList(fileFormDataList)
				.setType(methodType)
				.setReqHeaders(reqHeaderList)
				.setUrl(url);

			// Process other form data if available
			List<FormData> formDataList = formDataGroupMap.getOrDefault(Boolean.FALSE, new ArrayList<>());
			if (!formDataList.isEmpty()) {
				final Map<String, String> formDataToMap = DocUtil.formDataToMap(formDataList);
				curlRequest.setBody(UrlUtil.urlJoin(DocGlobalConstants.EMPTY, formDataToMap)
					.replace("?", DocGlobalConstants.EMPTY));
			}
		}
		return requestExample.setExampleBody(CurlUtil.toCurl(curlRequest)).setUrl(url);
	}

}
