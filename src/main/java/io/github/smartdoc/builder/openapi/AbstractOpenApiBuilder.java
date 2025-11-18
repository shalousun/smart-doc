/*
 * smart-doc
 *
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

package io.github.smartdoc.builder.openapi;

import io.github.smartdoc.builder.DocBuilderTemplate;
import io.github.smartdoc.builder.ProjectDocConfigBuilder;
import io.github.smartdoc.constants.ComponentTypeEnum;
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.MediaType;
import io.github.smartdoc.constants.Methods;
import io.github.smartdoc.constants.ParamTypeConstants;
import io.github.smartdoc.factory.BuildTemplateFactory;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.ApiExceptionStatus;
import io.github.smartdoc.model.ApiMethodDoc;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.ApiReqParam;
import io.github.smartdoc.model.ApiSchema;
import io.github.smartdoc.model.DocMapping;
import io.github.smartdoc.model.TagDoc;
import io.github.smartdoc.model.openapi.OpenApiTag;
import io.github.smartdoc.template.IDocBuildTemplate;
import io.github.smartdoc.utils.DocUtil;
import io.github.smartdoc.utils.OpenApiSchemaUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static io.github.smartdoc.constants.DocGlobalConstants.OPENAPI_2_COMPONENT_KRY;
import static io.github.smartdoc.constants.DocGlobalConstants.OPENAPI_3_COMPONENT_KRY;

/**
 * abstract openapi builder
 *
 * @author xingzi Date 2022/10/12 18:49
 * @since 2.6.2
 */
public abstract class AbstractOpenApiBuilder {

	/**
	 * Logger for the class.
	 */
	private static final Logger logger = Logger.getLogger(AbstractOpenApiBuilder.class.getName());

	/**
	 * Component key
	 */
	private String componentKey;

	/**
	 * Get component key
	 * @return component key
	 */
	public String getComponentKey() {
		return componentKey;
	}

	/**
	 * Set component key
	 * @param componentKey component key
	 */
	public void setComponentKey(String componentKey) {
		this.componentKey = componentKey;
	}

	/**
	 * Get module name
	 * @return module name
	 */
	abstract String getModuleName();

	/**
	 * Create OpenAPI definition
	 * @param apiConfig Configuration of smart-doc
	 * @param apiSchema Project API schema
	 */
	abstract void openApiCreate(ApiConfig apiConfig, ApiSchema<ApiDoc> apiSchema);

	/**
	 * Build request
	 * @param apiConfig Configuration of smart-doc
	 * @param apiMethodDoc Data of method
	 * @param apiDoc singe api doc
	 * @param apiExceptionStatuses Exception status list
	 * @return Map of request urls
	 */
	abstract Map<String, Object> buildPathUrlsRequest(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc,
			List<ApiExceptionStatus> apiExceptionStatuses);

	/**
	 * response body
	 * @param apiMethodDoc ApiMethodDoc
	 * @param apiConfig ApiConfig
	 * @return Map of response body
	 */
	abstract Map<String, Object> buildResponsesBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc);

	/**
	 * Build request parameters
	 * @param apiMethodDoc API data for the method
	 * @return List of parameters
	 */
	abstract List<Map<String, Object>> buildParameters(ApiMethodDoc apiMethodDoc);

	/**
	 * Build request parameters
	 * @param apiParam ApiParam
	 * @param hasItems has items
	 * @return Map of request parameters
	 */
	abstract Map<String, Object> getStringParams(ApiParam apiParam, boolean hasItems);

	/**
	 * component schema
	 * @param apiSchema API schema
	 * @return component schema Map
	 */
	abstract public Map<String, Object> buildComponentsSchema(ApiSchema<ApiDoc> apiSchema);

	/**
	 * String component
	 */
	protected static final Map<String, String> STRING_COMPONENT = new HashMap<>();

	static {
		STRING_COMPONENT.put("type", "string");
		STRING_COMPONENT.put("format", "string");
	}

	/**
	 * Build openapi paths
	 * @param apiConfig Configuration of smart-doc
	 * @param apiSchema Project API schema
	 * @param tags tags
	 * @param isSwagger is swagger
	 * @return Map of paths
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> buildPaths(ApiConfig apiConfig, ApiSchema<ApiDoc> apiSchema, Set<OpenApiTag> tags,
			boolean isSwagger) {
		Map<String, Object> pathMap = new LinkedHashMap<>(500);

		List<ApiDoc> apiDocs = apiSchema.getApiDatas();
		for (ApiDoc apiDoc : apiDocs) {
			if (CollectionUtil.isEmpty(apiDoc.getList())) {
				continue;
			}
			for (ApiMethodDoc methodDoc : apiDoc.getList()) {
				String[] paths = methodDoc.getPath().split(";");
				for (String path : paths) {
					path = path.trim();
					if (StringUtil.isNotEmpty(apiConfig.getPathPrefix()) && isSwagger) {
						path = path.replace(apiConfig.getPathPrefix(), "");
					}
					Map<String, Object> request = this.buildPathUrls(apiConfig, methodDoc, methodDoc.getClazzDoc(),
							apiSchema.getApiExceptionStatuses());
					if (!pathMap.containsKey(path)) {
						pathMap.put(path, request);
					}
					else {
						Map<String, Object> oldRequest = (Map<String, Object>) pathMap.get(path);
						oldRequest.putAll(request);
					}
				}
			}
		}
		for (Map.Entry<String, TagDoc> docEntry : DocMapping.TAG_DOC.entrySet()) {
			tags.addAll(docEntry.getValue()
				.getClazzDocs()
				.stream()
				// optimize tag content for compatible to swagger
				.map(doc -> OpenApiTag.of(apiConfig.getOpenApiTagNameType(), doc))
				.collect(Collectors.toSet()));
		}
		return pathMap;
	}

	/**
	 * Build path urls
	 * @param apiConfig ApiConfig
	 * @param apiMethodDoc Method
	 * @param apiDoc ApiDoc
	 * @param apiExceptionStatuses Exception status list
	 * @return Map of path urls
	 */
	public Map<String, Object> buildPathUrls(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc,
			List<ApiExceptionStatus> apiExceptionStatuses) {
		Map<String, Object> request = new HashMap<>(16);
		request.put(apiMethodDoc.getType().toLowerCase(),
				this.buildPathUrlsRequest(apiConfig, apiMethodDoc, apiDoc, apiExceptionStatuses));
		return request;
	}

	/**
	 * Build content for responses and requestBody
	 * @param apiConfig ApiConfig
	 * @param apiMethodDoc ApiMethodDoc
	 * @param isRep is response
	 * @return Map of content
	 */
	public Map<String, Object> buildContent(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, boolean isRep) {
		Map<String, Object> content = new HashMap<>(16);
		String contentType = apiMethodDoc.getContentType();
		if (isRep) {
			contentType = "*/*";
		}
		content.put(contentType, this.buildContentBody(apiConfig, apiMethodDoc, isRep));
		return content;

	}

	/**
	 * Build data of content
	 * @param apiConfig ApiConfig
	 * @param apiMethodDoc ApiMethodDoc
	 * @param isRep is response
	 * @return Map of content
	 */
	public Map<String, Object> buildContentBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, boolean isRep) {
		Map<String, Object> content = new HashMap<>(16);
		if (Objects.nonNull(apiMethodDoc.getReturnSchema()) && isRep) {
			content.put("schema", apiMethodDoc.getReturnSchema());
		}
		else if (!isRep && Objects.nonNull(apiMethodDoc.getRequestSchema())) {
			content.put("schema", apiMethodDoc.getRequestSchema());
		}
		else {
			content.put("schema", this.buildBodySchema(apiMethodDoc, isRep));
		}

		if (OPENAPI_2_COMPONENT_KRY.equals(componentKey) && !isRep) {
			content.put("name", apiMethodDoc.getName());
		}
		if (OPENAPI_3_COMPONENT_KRY.equals(componentKey)
				&& (!isRep && apiConfig.isRequestExample() || (isRep && apiConfig.isResponseExample()))) {
			content.put("examples", buildBodyExample(apiMethodDoc, isRep));
		}
		return content;

	}

	/**
	 * Build schema of Body
	 * @param apiMethodDoc ApiMethodDoc
	 * @param isRep is response
	 * @return Map of schema
	 */
	public Map<String, Object> buildBodySchema(ApiMethodDoc apiMethodDoc, boolean isRep) {
		Map<String, Object> schema = new HashMap<>(10);
		Map<String, Object> innerScheme = new HashMap<>(10);
		// For response
		if (isRep) {
			String responseRef = componentKey
					+ OpenApiSchemaUtil.getClassNameFromParams(apiMethodDoc.getResponseParams());
			if (apiMethodDoc.getIsResponseArray() == 1) {
				schema.put("type", ParamTypeConstants.PARAM_TYPE_ARRAY);
				innerScheme.put("$ref", responseRef);
				schema.put("items", innerScheme);
			}
			else if (CollectionUtil.isNotEmpty(apiMethodDoc.getResponseParams())) {
				schema.put("$ref", responseRef);
			}
			return schema;
		}

		// for request
		String requestRef;
		String randomName = ComponentTypeEnum.getRandomName(ApiConfig.getInstance().getComponentType(), apiMethodDoc);
		if (Methods.POST.getValue().equals(apiMethodDoc.getType())
				&& (apiMethodDoc.getContentType().equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
						|| apiMethodDoc.getContentType().equals(MediaType.MULTIPART_FORM_DATA_VALUE))) {
			schema.put("type", ParamTypeConstants.PARAM_TYPE_OBJECT);
			Map<String, Object> propertiesAndRequiredMap = this.buildProperties(apiMethodDoc.getRequestParams(),
					new HashMap<>(), Boolean.FALSE);
			schema.put("properties", propertiesAndRequiredMap.get("properties"));
			schema.put("required", propertiesAndRequiredMap.get("required"));
			return schema;
		}
		else if (apiMethodDoc.getContentType().equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
			requestRef = componentKey + OpenApiSchemaUtil.getClassNameFromParams(apiMethodDoc.getQueryParams());
		}
		else {
			requestRef = componentKey + OpenApiSchemaUtil.getClassNameFromParams(apiMethodDoc.getRequestParams());
		}
		// remove special characters in url
		if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
			if (apiMethodDoc.getIsRequestArray() == 1) {
				schema.put("type", ParamTypeConstants.PARAM_TYPE_ARRAY);
				innerScheme.put("$ref", requestRef);
				schema.put("items", innerScheme);
			}
			else {
				schema.put("$ref", requestRef);
			}
		}
		return schema;
	}

	/**
	 * Build body example
	 * @param apiMethodDoc ApiMethodDoc
	 * @param isRep is response
	 * @return Map of content
	 */
	public static Map<String, Object> buildBodyExample(ApiMethodDoc apiMethodDoc, boolean isRep) {
		Map<String, Object> content = new HashMap<>(8);
		content.put("json", buildExampleData(apiMethodDoc, isRep));
		return content;

	}

	/**
	 * Build example data
	 * @param apiMethodDoc ApiMethodDoc
	 * @param isRep is response
	 * @return Map of content
	 */
	public static Map<String, Object> buildExampleData(ApiMethodDoc apiMethodDoc, boolean isRep) {
		Map<String, Object> content = new HashMap<>(8);
		content.put("summary", "test data");
		if (!isRep) {
			content.put("value",
					StringUtil.isEmpty(apiMethodDoc.getRequestExample().getJsonBody())
							? apiMethodDoc.getRequestExample().getExampleBody()
							: apiMethodDoc.getRequestExample().getJsonBody());
		}
		else {
			content.put("value", apiMethodDoc.getResponseUsage());
		}
		return content;

	}

	/**
	 * If it is a get request or @PathVariable set the request parameters
	 * @param apiParam Parameter information
	 * @return parameters schema
	 */
	public Map<String, Object> buildParametersSchema(ApiParam apiParam) {
		Map<String, Object> schema = new HashMap<>(10);
		String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType());
		// The values of openApiType are "file", "object", "array","string",
		// "integer","number"
		schema.put("type", openApiType);
		switch (openApiType) {
			case ParamTypeConstants.PARAM_TYPE_FILE:
				schema.put("format", "binary");
				schema.put("type", "string");
				break;
			case ParamTypeConstants.PARAM_TYPE_OBJECT:
				if ("enum".equals(apiParam.getType())) {
					schema.put("enum", apiParam.getEnumValues());
				}
				break;
			case ParamTypeConstants.PARAM_TYPE_ARRAY:
				if (CollectionUtil.isNotEmpty(apiParam.getEnumValues())) {
					schema.put("type", "string");
					schema.put("items", apiParam.getEnumValues());
				}
				else {
					schema.put("type", ParamTypeConstants.PARAM_TYPE_ARRAY);
					Map<String, String> map = new HashMap<>(4);
					map.put("type", "string");
					map.put("format", "string");
					schema.put("items", map);
				}
				break;
			default:
				// "string", "integer", "number"
				schema.put("format", apiParam.getType());
				if ("enum".equals(apiParam.getType())) {
					schema.put("enum", apiParam.getEnumValues());
				}
				break;
		}
		return schema;
	}

	/**
	 * If the header is included, set the request parameters
	 * @param header header
	 * @return parameters schema
	 */
	public static Map<String, Object> buildParametersSchema(ApiReqParam header) {
		Map<String, Object> schema = new HashMap<>(10);
		String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(header.getType());
		schema.put("type", openApiType);
		schema.put("format", openApiType);
		return schema;
	}

	/**
	 * build response
	 * @param apiMethodDoc ApiMethodDoc
	 * @param apiConfig ApiConfig
	 * @param apiExceptionStatuses apiExceptionStatuses
	 * @return response info
	 */
	public Map<String, Object> buildResponses(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc,
			List<ApiExceptionStatus> apiExceptionStatuses) {
		Map<String, Object> response = new LinkedHashMap<>(8);
		response.put("200", this.buildResponsesBody(apiConfig, apiMethodDoc));
		if (CollectionUtil.isNotEmpty(apiExceptionStatuses)) {
			for (ApiExceptionStatus apiExceptionStatus : apiExceptionStatuses) {
				response.put(apiExceptionStatus.getStatus(),
						this.buildExceptionResponsesBody(apiConfig, apiExceptionStatus));
			}
		}
		return response;
	}

	/**
	 * Build exception response body
	 * @param apiConfig ApiConfig
	 * @param apiExceptionStatus ApiExceptionStatus
	 * @return Map of content
	 */
	public Map<String, Object> buildExceptionResponsesBody(ApiConfig apiConfig, ApiExceptionStatus apiExceptionStatus) {
		Map<String, Object> responseBody = new HashMap<>(8);
		responseBody.put("description", apiExceptionStatus.getDesc());
		Map<String, Object> content = new HashMap<>(8);
		Map<String, Object> mediaTypeContent = new HashMap<>(8);
		Map<String, Object> schema = new HashMap<>(8);
		String responseRef = componentKey
				+ OpenApiSchemaUtil.getClassNameFromParams(apiExceptionStatus.getExceptionResponseParams());
		if (CollectionUtil.isNotEmpty(apiExceptionStatus.getExceptionResponseParams())) {
			schema.put("$ref", responseRef);
		}
		mediaTypeContent.put("schema", schema);
		if (OPENAPI_3_COMPONENT_KRY.equals(componentKey) && apiConfig.isResponseExample()) {
			Map<String, Object> json = new HashMap<>(8);
			Map<String, Object> jsonData = new HashMap<>(8);
			jsonData.put("summary", "response example");
			jsonData.put("value", apiExceptionStatus.getResponseUsage());
			json.put("json", jsonData);
			mediaTypeContent.put("examples", json);
		}
		content.put("*/*", mediaTypeContent);
		responseBody.put("content", content);
		return responseBody;
	}

	/**
	 * component schema properties
	 * @param apiParam list of ApiParam
	 * @param component component
	 * @param isResp is response
	 * @return properties
	 */
	public Map<String, Object> buildProperties(List<ApiParam> apiParam, Map<String, Object> component, boolean isResp) {
		Map<String, Object> properties = new HashMap<>();
		Map<String, Object> propertiesData = new LinkedHashMap<>();
		List<String> requiredList = new ArrayList<>();
		if (apiParam != null) {
			int paramsSize = apiParam.size();
			for (ApiParam param : apiParam) {
				if (param.isRequired()) {
					requiredList.add(param.getField());
				}
				if (ParamTypeConstants.PARAM_TYPE_MAP.equals(param.getType())
						&& StringUtil.isEmpty(param.getClassName())) {
					continue;
				}
				if (param.isQueryParam() || param.isPathParam()) {
					continue;
				}
				String field = param.getField();
				propertiesData.put(field, this.buildPropertiesData(param, component, isResp));
			}
			if (!propertiesData.isEmpty()) {
				properties.put("type", "object");
				properties.put("properties", propertiesData);
			}
			if (!CollectionUtil.isEmpty(requiredList)) {
				properties.put("required", requiredList);
			}
			return properties;
		}
		else {
			return new HashMap<>();
		}

	}

	/**
	 * component schema properties
	 * @param apiParam list of ApiParam
	 * @param component component
	 * @param isResp is response
	 * @return properties
	 */
	private Map<String, Object> buildPropertiesData(ApiParam apiParam, Map<String, Object> component, boolean isResp) {
		Map<String, Object> propertiesData = new HashMap<>(16);
		String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType());
		// array object file map
		propertiesData.put("description", apiParam.getDesc());
		if (StringUtil.isNotEmpty(apiParam.getValue())) {
			propertiesData.put("example", this.getExampleValueBasedOnTypeForApiParam(apiParam));
		}

		if (!"object".equals(openApiType)) {
			propertiesData.put("type", openApiType);
			propertiesData.put("format", "int16".equals(apiParam.getType()) ? "int32" : apiParam.getType());
			if ("enum".equals(apiParam.getType())) {
				propertiesData.put("enum", apiParam.getEnumValues());
			}
		}
		if ("map".equals(apiParam.getType())) {
			propertiesData.put("type", "object");
			propertiesData.put("description", apiParam.getDesc() + "(map data)");
		}
		if ("array".equals(apiParam.getType())) {
			propertiesData.put("type", "array");
			if (CollectionUtil.isNotEmpty(apiParam.getChildren())) {
				if (!apiParam.isSelfReferenceLoop()) {
					Map<String, Object> arrayRef = new HashMap<>(4);
					String childSchemaName = OpenApiSchemaUtil.getClassNameFromParams(apiParam.getChildren());
					if (childSchemaName.contains(OpenApiSchemaUtil.NO_BODY_PARAM)) {
						propertiesData.put("type", "object");
						propertiesData.put("description", apiParam.getDesc() + "(object)");
					}
					else {
						component.put(childSchemaName, this.buildProperties(apiParam.getChildren(), component, isResp));
						arrayRef.put("$ref", componentKey + childSchemaName);
						propertiesData.put("items", arrayRef);
					}
				}
			}
			else {
				Map<String, Object> arrayRef = new HashMap<>(4);
				arrayRef.put("type", "string");
				propertiesData.put("items", arrayRef);
			}
		}
		if ("file".equals(apiParam.getType())) {
			propertiesData.put("type", "string");
			propertiesData.put("format", "binary");
		}
		if ("object".equals(apiParam.getType())) {
			propertiesData.put("description", apiParam.getDesc() + "(object)");
			if (CollectionUtil.isNotEmpty(apiParam.getChildren())) {
				if (!apiParam.isSelfReferenceLoop()) {
					String childSchemaName = OpenApiSchemaUtil.getClassNameFromParams(apiParam.getChildren());
					if (childSchemaName.contains(OpenApiSchemaUtil.NO_BODY_PARAM)) {
						propertiesData.put("type", "object");
					}
					else {
						component.put(childSchemaName, this.buildProperties(apiParam.getChildren(), component, isResp));
						propertiesData.put("$ref", componentKey + childSchemaName);
					}
				}
			}
			else {
				propertiesData.put("type", "object");
			}
		}
		if (apiParam.getExtensions() != null && !apiParam.getExtensions().isEmpty()) {
			apiParam.getExtensions().forEach((key, value) -> propertiesData.put("x-" + key, value));
		}

		return propertiesData;
	}

	/**
	 * Builds component data for API documentation. This method iterates through all API
	 * documentation entries to extract request and response parameter information, and
	 * organizes them into OpenAPI component schemas.
	 * @param apiSchema The API documentation schema.
	 * @return Returns a map containing all component schemas.
	 */
	public Map<String, Object> buildComponentData(ApiSchema<ApiDoc> apiSchema) {
		Map<String, Object> component = new HashMap<>(16);
		component.put(DocGlobalConstants.DEFAULT_PRIMITIVE, STRING_COMPONENT);
		apiSchema.getApiDatas().forEach(entrypoint -> {
			List<ApiMethodDoc> apiMethodDocs = entrypoint.getList();
			apiMethodDocs.forEach(method -> {
				// request components
				String requestSchema = OpenApiSchemaUtil.getClassNameFromParams(method.getRequestParams());
				List<ApiParam> requestParams = method.getRequestParams();
				Map<String, Object> prop = this.buildProperties(requestParams, component, false);
				component.put(requestSchema, prop);
				// response components
				List<ApiParam> responseParams = method.getResponseParams();
				String responseSchemaName = OpenApiSchemaUtil.getClassNameFromParams(method.getResponseParams());
				component.put(responseSchemaName, this.buildProperties(responseParams, component, true));
			});
		});
		// Exception response components
		if (Objects.nonNull(apiSchema.getApiExceptionStatuses())) {
			apiSchema.getApiExceptionStatuses().forEach(e -> {
				List<ApiParam> responseParams = e.getExceptionResponseParams();
				String responseSchemaName = OpenApiSchemaUtil.getClassNameFromParams(e.getExceptionResponseParams());
				component.put(responseSchemaName, this.buildProperties(responseParams, component, true));
			});
		}
		component.remove(OpenApiSchemaUtil.NO_BODY_PARAM);
		return component;
	}

	/**
	 * Get a list of OpenAPI's document data
	 * @param config Configuration of smart-doc
	 * @param projectBuilder JavaDocBuilder of QDox
	 * @return List of OpenAPI's document data
	 */
	public ApiSchema<ApiDoc> getOpenApiDocs(ApiConfig config, JavaProjectBuilder projectBuilder) {
		config.setShowJavaType(false);
		DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
		builderTemplate.checkAndInit(config, Boolean.TRUE);
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, projectBuilder);
		config.setParamsDataToTree(true);
		IDocBuildTemplate<ApiDoc> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework(),
				config.getClassLoader());
		Objects.requireNonNull(docBuildTemplate, "doc build template is null");
		return docBuildTemplate.getApiData(configBuilder);
	}

	/**
	 * Get example value based on type for ApiParam
	 * @param apiParam ApiParam
	 * @return example value
	 */
	protected Object getExampleValueBasedOnTypeForApiParam(ApiParam apiParam) {
		return getValueByType(apiParam.getValue(), apiParam.getType());
	}

	/**
	 * Get example value based on type for ApiReqParam
	 * @param apiReqParam ApiReqParam
	 * @return example value
	 */
	protected Object getExampleValueBasedOnTypeForApiReqParam(ApiReqParam apiReqParam) {
		return getValueByType(apiReqParam.getValue(), apiReqParam.getType());
	}

	/**
	 * Get value by type
	 * @param originalValue original value
	 * @param type type
	 * @return value
	 */
	private Object getValueByType(String originalValue, String type) {
		if (StringUtil.isEmpty(originalValue) || StringUtil.isEmpty(type)) {
			return originalValue;
		}
		try {
			String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(type);
			switch (openApiType) {
				case "boolean":
					return Boolean.parseBoolean(originalValue);
				case "integer":
					return Integer.parseInt(originalValue);
				case "number":
					String javaTypeName = type.toLowerCase();
					switch (javaTypeName) {
						case "long":
						case "java.lang.long":
						case "int64":
							return Long.parseLong(originalValue);
						case "double":
						case "java.lang.double":
							return Double.parseDouble(originalValue);
						case "float":
						case "java.lang.float":
							return Float.parseFloat(originalValue);
						case "java.math.biginteger":
							return new BigInteger(originalValue);
						case "java.math.bigdecimal":
							return new BigDecimal(originalValue);
					}
					return originalValue;
			}
			return originalValue;
		}
		catch (Exception e) {
			// if there is an exception, return the original value
			logger
				.warning("Error parsing value: " + originalValue + " for type: " + type + ", error: " + e.getMessage());
			return originalValue;
		}
	}

}
