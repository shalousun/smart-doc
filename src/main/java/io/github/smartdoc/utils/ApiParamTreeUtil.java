/*
 * Copyright (C) 2018-2025 smart-doc
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
import io.github.smartdoc.model.ApiMethodReqParam;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.ApiReqParam;
import com.power.common.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ApiParam Tree Util {@link ApiParam}
 *
 * @author yu 2020/8/8.
 */
public class ApiParamTreeUtil {

	/**
	 * private constructor
	 */
	private ApiParamTreeUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Converts a list of ApiParam objects into a tree structure.
	 * @param apiParamList The list of ApiParam objects to be converted.
	 * @return A list of ApiParam objects representing the tree structure.
	 */
	public static List<ApiParam> apiParamToTree(List<ApiParam> apiParamList) {
		if (CollectionUtil.isEmpty(apiParamList)) {
			return new ArrayList<>(0);
		}
		List<ApiParam> params = new ArrayList<>();
		// find root
		for (ApiParam apiParam : apiParamList) {
			// remove pre of field
			apiParam
				.setField(apiParam.getField().replaceAll(DocGlobalConstants.PARAM_PREFIX, "").replaceAll("&nbsp;", ""));
			// pid == 0
			if (apiParam.getPid() == 0) {
				params.add(apiParam);
			}
		}
		for (ApiParam apiParam : params) {
			// remove pre of field
			apiParam.setChildren(getChild(apiParam.getId(), apiParamList, 0));
		}
		return params;
	}

	/**
	 * find child
	 * @param id param id
	 * @param apiParamList List of ApiParam
	 * @param counter invoked counter
	 * @return List of ApiParam
	 */
	private static List<ApiParam> getChild(int id, List<ApiParam> apiParamList, int counter) {
		List<ApiParam> childList = new ArrayList<>();
		if (counter > 7) {
			return childList;
		}
		for (ApiParam param : apiParamList) {
			if (param.getPid() == id) {
				childList.add(param);
			}
		}
		counter++;
		for (ApiParam param : childList) {
			param.setChildren(getChild(param.getId(), apiParamList, counter));
		}
		if (childList.isEmpty()) {
			return new ArrayList<>(0);
		}
		return childList;
	}

	/**
	 * Constructs method request parameters based on the parameter list, query parameter
	 * map, and path parameter map. This method categorizes parameters into path, query,
	 * and body parameters, and treats all as query parameters if the request method type
	 * is GET or DELETE.
	 * @param paramList List of parameters containing all unconfigured parameter
	 * information.
	 * @param queryReqParamMap Mapping of configured query parameters.
	 * @param pathReqParamMap Mapping of configured path parameters.
	 * @param methodType The request method type, determining whether to treat all
	 * parameters as query parameters.
	 * @return An instance of ApiMethodReqParam built with categorized parameter
	 * information.
	 */
	public static ApiMethodReqParam buildMethodReqParam(List<ApiParam> paramList,
			final Map<String, ApiReqParam> queryReqParamMap, final Map<String, ApiReqParam> pathReqParamMap,
			String methodType) {
		List<ApiParam> pathParams = new ArrayList<>();
		List<ApiParam> queryParams = new ArrayList<>();
		List<ApiParam> bodyParams = new ArrayList<>();
		for (ApiParam param : paramList) {
			if (param.isPathParam()) {
				if (pathReqParamMap.containsKey(param.getField())) {
					param.setConfigParam(true).setValue(pathReqParamMap.get(param.getField()).getValue());
				}
				param.setId(pathParams.size() + 1);
				pathParams.add(param);
			}
			else if (param.isQueryParam() || Methods.GET.getValue().equals(methodType)) {
				if (queryReqParamMap.containsKey(param.getField())) {
					param.setConfigParam(true).setValue(queryReqParamMap.get(param.getField()).getValue());
				}
				param.setId(queryParams.size() + 1);
				queryParams.add(param);
			}
			else {
				param.setId(bodyParams.size() + 1);
				bodyParams.add(param);
			}
		}

		final Set<String> queryParamSet = queryParams.stream().map(ApiParam::getField).collect(Collectors.toSet());
		for (ApiReqParam value : queryReqParamMap.values()) {
			if (queryParamSet.contains(value.getName())) {
				continue;
			}
			final ApiParam apiParam = ApiReqParam.convertToApiParam(value)
				.setQueryParam(true)
				.setId(queryParams.size() + 1);
			queryParams.add(apiParam);
		}

		final Set<String> pathParamSet = pathParams.stream().map(ApiParam::getField).collect(Collectors.toSet());
		for (ApiReqParam value : pathReqParamMap.values()) {
			if (pathParamSet.contains(value.getName())) {
				continue;
			}
			final ApiParam apiParam = ApiReqParam.convertToApiParam(value)
				.setPathParam(true)
				.setId(pathParams.size() + 1);
			pathParams.add(apiParam);
		}

		return ApiMethodReqParam.builder()
			.setRequestParams(bodyParams)
			.setPathParams(pathParams)
			.setQueryParams(queryParams);
	}

}
