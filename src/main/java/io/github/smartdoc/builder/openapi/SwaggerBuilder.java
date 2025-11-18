/*
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
package io.github.smartdoc.builder.openapi;

import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.MediaType;
import io.github.smartdoc.constants.ParamTypeConstants;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.ApiExceptionStatus;
import io.github.smartdoc.model.ApiGroup;
import io.github.smartdoc.model.ApiMethodDoc;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.ApiReqParam;
import io.github.smartdoc.model.ApiSchema;
import io.github.smartdoc.model.openapi.OpenApiTag;
import io.github.smartdoc.utils.DocUtil;
import io.github.smartdoc.utils.JsonUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Swagger Builder
 *
 * @author xingzi Date 2022/9/17 15:16
 * @since 2.6.2
 */
public class SwaggerBuilder extends AbstractOpenApiBuilder {

	/**
	 * Singleton
	 */
	private static final SwaggerBuilder INSTANCE = new SwaggerBuilder();

	/**
	 * private constructor
	 */
	private SwaggerBuilder() {
	}

	/**
	 * For unit testing
	 * @param config Configuration of smart-doc
	 */
	public static void buildOpenApi(ApiConfig config) {
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		buildOpenApi(config, javaProjectBuilder);
	}

	/**
	 * Only for smart-doc maven plugin and gradle plugin.
	 * @param config Configuration of smart-doc
	 * @param projectBuilder JavaDocBuilder of QDox
	 */
	public static void buildOpenApi(ApiConfig config, JavaProjectBuilder projectBuilder) {
		ApiSchema<ApiDoc> apiSchema = INSTANCE.getOpenApiDocs(config, projectBuilder);
		INSTANCE.openApiCreate(config, apiSchema);
	}

	@Override
	public String getModuleName() {
		return DocGlobalConstants.OPENAPI_2_COMPONENT_KRY;
	}

	/**
	 * Build OpenApi
	 * @param config Configuration of smart-doc
	 */
	@Override
	public void openApiCreate(ApiConfig config, ApiSchema<ApiDoc> apiSchema) {
		this.setComponentKey(getModuleName());
		Map<String, Object> json = new HashMap<>(8);
		json.put("swagger", "2.0");
		json.put("info", buildInfo(config));
		json.put("host", config.getServerUrl() == null ? "127.0.0.1" : config.getServerUrl());
		json.put("basePath", StringUtils.isNotBlank(config.getPathPrefix()) ? config.getPathPrefix()
				: DocGlobalConstants.PATH_DELIMITER);
		Set<OpenApiTag> tags = new HashSet<>();
		json.put("tags", tags);
		json.put("paths", buildPaths(config, apiSchema, tags, true));
		json.put("definitions", buildComponentsSchema(apiSchema));

		String filePath = config.getOutPath();
		filePath = filePath + DocGlobalConstants.OPEN_API_JSON;
		String data = JsonUtil.toPrettyJson(json);
		FileUtil.nioWriteFile(data, filePath);
	}

	/**
	 * Build openapi info
	 * @param apiConfig Configuration of smart-doc
	 */
	private static Map<String, Object> buildInfo(ApiConfig apiConfig) {
		Map<String, Object> infoMap = new HashMap<>(8);
		infoMap.put("title", apiConfig.getProjectName() == null ? "Project Name is Null." : apiConfig.getProjectName());
		infoMap.put("version", "1.0.0");
		return infoMap;
	}

	/**
	 * Build Servers
	 * @param config Configuration of smart-doc
	 */
	@Deprecated
	private static List<Map<String, String>> buildTags(ApiConfig config) {
		List<Map<String, String>> tagList = new ArrayList<>();
		Map<String, String> tagMap;
		List<ApiGroup> groups = config.getGroups();
		for (ApiGroup group : groups) {
			tagMap = new HashMap<>(4);
			tagMap.put("name", group.getName());
			tagMap.put("description", group.getApis());
			tagList.add(tagMap);
		}
		return tagList;
	}

	/**
	 * Build request
	 * @param apiConfig Configuration of smart-doc
	 * @param apiMethodDoc ApiMethodDoc
	 * @param apiDoc apiDoc
	 */
	public Map<String, Object> buildPathUrlsRequest(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc,
			List<ApiExceptionStatus> apiExceptionStatuses) {
		Map<String, Object> request = new HashMap<>(20);
		request.put("summary", apiMethodDoc.getDesc());
		request.put("description", apiMethodDoc.getDetail());
		request.put("tags",
				Stream.of(apiDoc.getName(), apiDoc.getDesc(), apiMethodDoc.getGroup())
					.filter(StringUtil::isNotEmpty)
					.toArray(String[]::new));
		List<Map<String, Object>> parameters = this.buildParameters(apiMethodDoc);
		// requestBody
		if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
			Map<String, Object> parameter = new HashMap<>(16);
			parameter.put("in", "body");
			parameter.putAll(this.buildContentBody(apiConfig, apiMethodDoc, false));
			parameters.add(parameter);
		}
		if (this.hasFile(parameters)) {
			List<String> formData = new ArrayList<>();
			formData.add(MediaType.MULTIPART_FORM_DATA);
			request.put("consumes", formData);
		}
		request.put("parameters", parameters);
		request.put("responses", this.buildResponses(apiConfig, apiMethodDoc, apiExceptionStatuses));
		request.put("deprecated", apiMethodDoc.isDeprecated());
		String operationId = apiMethodDoc.getUrl().replace(apiMethodDoc.getServerUrl(), "");
		// make sure operationId is unique and can be used as a method name
		request.put("operationId",
				apiMethodDoc.getName() + "_" + apiMethodDoc.getMethodId() + "UsingOn" + apiMethodDoc.getType());

		return request;
	}

	/**
	 * Check if the parameter contains a file
	 * @param parameters list of parameters
	 * @return true if it contains a file
	 */
	private boolean hasFile(List<Map<String, Object>> parameters) {
		for (Map<String, Object> param : parameters) {
			if (DocGlobalConstants.SWAGGER_FILE_TAG.equals(param.get("in"))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * response body
	 * @param apiMethodDoc ApiMethodDoc
	 */
	@Override
	public Map<String, Object> buildResponsesBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
		Map<String, Object> responseBody = new HashMap<>(10);
		responseBody.put("description", "OK");
		if (CollectionUtil.isNotEmpty(apiMethodDoc.getResponseParams())
				|| Objects.nonNull(apiMethodDoc.getReturnSchema())) {
			responseBody.putAll(this.buildContentBody(apiConfig, apiMethodDoc, true));
		}
		return responseBody;
	}

	@Override
	public List<Map<String, Object>> buildParameters(ApiMethodDoc apiMethodDoc) {
		{
			Map<String, Object> parameters;
			List<Map<String, Object>> parametersList = new ArrayList<>();
			// Handling path parameters
			for (ApiParam apiParam : apiMethodDoc.getPathParams()) {
				parameters = this.getStringParams(apiParam, false);
				parameters.put("type", DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType()));
				parameters.put("in", "path");
				parametersList.add(parameters);
			}
			for (ApiParam apiParam : apiMethodDoc.getQueryParams()) {
				if (apiParam.getType().equals(ParamTypeConstants.PARAM_TYPE_ARRAY) || apiParam.isHasItems()) {
					parameters = this.getStringParams(apiParam, false);
					parameters.put("type", ParamTypeConstants.PARAM_TYPE_ARRAY);
					parameters.put("items", this.getStringParams(apiParam, true));
					parametersList.add(parameters);
				}
				else {
					parameters = this.getStringParams(apiParam, false);
					parameters.put("type", DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType()));
					parametersList.add(parameters);
				}
			}
			// with headers
			if (!CollectionUtil.isEmpty(apiMethodDoc.getRequestHeaders())) {
				for (ApiReqParam header : apiMethodDoc.getRequestHeaders()) {
					parameters = new HashMap<>(20);
					parameters.put("name", header.getName());
					parameters.put("type", DocUtil.javaTypeToOpenApiTypeConvert(header.getType()));
					parameters.put("description", header.getDesc());
					parameters.put("required", header.isRequired());
					parameters.put("example", getExampleValueBasedOnTypeForApiReqParam(header));
					parameters.put("schema", buildParametersSchema(header));
					parameters.put("in", "header");
					parametersList.add(parameters);
				}
			}
			return parametersList;
		}
	}

	@Override
	public Map<String, Object> getStringParams(ApiParam apiParam, boolean hasItems) {
		Map<String, Object> parameters;
		parameters = new HashMap<>(20);
		if (!hasItems) {
			if ("file".equalsIgnoreCase(apiParam.getType())) {
				parameters.put("in", DocGlobalConstants.SWAGGER_FILE_TAG);
			}
			else {
				parameters.put("in", "query");
			}
			parameters.put("name", apiParam.getField());
			parameters.put("description", apiParam.getDesc());
			parameters.put("required", apiParam.isRequired());
			parameters.put("type", apiParam.getType());
		}
		else {
			if (ParamTypeConstants.PARAM_TYPE_OBJECT.equals(apiParam.getType())
					|| (ParamTypeConstants.PARAM_TYPE_ARRAY.equals(apiParam.getType()) && apiParam.isHasItems())) {
				parameters.put("type", "object(complex POJO please use @RequestBody)");
			}
			else {
				String desc = apiParam.getDesc();
				if (desc.contains(ParamTypeConstants.PARAM_TYPE_FILE)) {
					parameters.put("type", ParamTypeConstants.PARAM_TYPE_FILE);
				}
				else if (desc.contains("string")) {
					parameters.put("type", "string");
				}
				else {
					parameters.put("type", "integer");
				}
			}
		}
		return parameters;
	}

	@Override
	public Map<String, Object> buildComponentsSchema(ApiSchema<ApiDoc> apiSchema) {
		return this.buildComponentData(apiSchema);
	}

}
