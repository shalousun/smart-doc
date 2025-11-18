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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.smartdoc.constants.DocAnnotationConstants;
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.ParamTypeConstants;
import io.github.smartdoc.constants.TornaConstants;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDocDict;
import io.github.smartdoc.model.ApiErrorCode;
import io.github.smartdoc.model.ApiMethodDoc;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.ApiReqParam;
import io.github.smartdoc.model.BodyAdvice;
import io.github.smartdoc.model.DataDict;
import io.github.smartdoc.model.RpcJavaMethod;
import io.github.smartdoc.model.rpc.RpcApiDependency;
import io.github.smartdoc.model.torna.Apis;
import io.github.smartdoc.model.torna.CommonErrorCode;
import io.github.smartdoc.model.torna.DebugEnv;
import io.github.smartdoc.model.torna.HttpParam;
import io.github.smartdoc.model.torna.TornaApi;
import io.github.smartdoc.model.torna.TornaDic;
import io.github.smartdoc.model.torna.TornaRequestInfo;
import com.power.common.model.EnumDictionary;
import com.power.common.util.CollectionUtil;
import com.power.common.util.OkHttp3Util;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static io.github.smartdoc.constants.TornaConstants.ENUM_PUSH;
import static io.github.smartdoc.constants.TornaConstants.PUSH;

/**
 * Torna Util
 *
 * @author xingzi 2021/4/28 16:15
 **/
public class TornaUtil {

	/**
	 * private constructor
	 */
	private TornaUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Pushes documentation to the Torna API documentation platform.
	 * <p>
	 * This method decides whether to push all or part of the documentation based on the
	 * settings in apiConfig. If the Torna API or the configuration information is
	 * missing, the method will return directly.
	 * </p>
	 * @param tornaApi The API documentation object, containing information about all
	 * APIs.
	 * @param apiConfig The configuration information for the API, including whether to
	 * push all documentation and other settings.
	 * @param builder The Java project builder, used for constructing the project during
	 * the push process.
	 */
	public static void pushToTorna(TornaApi tornaApi, ApiConfig apiConfig, JavaProjectBuilder builder) {
		// Check whether the document needs to be pushed
		if (tornaApi == null || apiConfig == null) {
			return;
		}
		// Push all documents
		if (apiConfig.getApiUploadNums() == null) {
			pushToTornaAll(tornaApi, apiConfig, builder);
			return;
		}
		// Push part of documents if the upload number is not null
		List<Apis> tornaApis = tornaApi.getApis();
		if (tornaApis == null || tornaApis.isEmpty()) {
			return;
		}
		CollectionUtil.partition(tornaApis, apiConfig.getApiUploadNums()).forEach(apis -> {
			tornaApi.setApis(apis);
			pushToTornaAll(tornaApi, apiConfig, builder);
		});
	}

	/**
	 * Pushes all documentation information to the Torna platform.
	 * @param tornaApi The Torna API object, containing the API details to be pushed.
	 * @param apiConfig The API configuration object, containing the connection
	 * information for the Torna platform.
	 * @param builder The Java project builder object, used to construct the documentation
	 * information.
	 */
	private static void pushToTornaAll(TornaApi tornaApi, ApiConfig apiConfig, JavaProjectBuilder builder) {
		// Build push document information
		Map<String, String> requestJson = TornaConstants.buildParams(PUSH, new Gson().toJson(tornaApi), apiConfig);
		TornaUtil.printDebugInfo(apiConfig, null, requestJson, PUSH, true);
		// Push dictionary information
		Map<String, Object> dicMap = new HashMap<>(2);
		List<TornaDic> docDicts = TornaUtil.buildTornaDic(DocUtil.buildDictionary(apiConfig, builder));
		// Push dictionary information
		if (CollectionUtil.isNotEmpty(docDicts)) {
			dicMap.put("enums", docDicts);
			Map<String, String> dicRequestJson = TornaConstants.buildParams(ENUM_PUSH, new Gson().toJson(dicMap),
					apiConfig);
			String dicResponseMsg = OkHttp3Util.syncPostJson(apiConfig.getOpenUrl(), new Gson().toJson(dicRequestJson));
			TornaUtil.printDebugInfo(apiConfig, dicResponseMsg, dicRequestJson, ENUM_PUSH);
		}
		// Get the response result
		String responseMsg = OkHttp3Util.syncPostJson(apiConfig.getOpenUrl(), new Gson().toJson(requestJson));
		// Print the log of pushing documents to Torna
		TornaUtil.printDebugInfo(apiConfig, responseMsg, requestJson, PUSH);
	}

	/**
	 * Sets up the debugging environment.
	 * <p>
	 * This method determines if a debugging environment exists based on the provided
	 * {@code ApiConfig} configuration, and sets up the debugging environment for
	 * {@code TornaApi} if it exists.
	 * </p>
	 * @param apiConfig The API configuration, containing debugging environment name and
	 * URL information.
	 * @param tornaApi The TornaApi instance to set the debugging environment for.
	 * @return Returns true if a debugging environment exists; otherwise, returns false.
	 */
	public static boolean setDebugEnv(ApiConfig apiConfig, TornaApi tornaApi) {
		// Check if the debugging environment name and URL are set
		boolean hasDebugEnv = StringUtils.isNotBlank(apiConfig.getDebugEnvName())
				&& StringUtils.isNotBlank(apiConfig.getDebugEnvUrl());

		// Set up the test environment
		List<DebugEnv> debugEnvs = new ArrayList<>();

		// If a debugging environment exists, create a corresponding DebugEnv object and
		// add it to the list
		if (hasDebugEnv) {
			DebugEnv debugEnv = new DebugEnv();
			debugEnv.setName(apiConfig.getDebugEnvName());
			debugEnv.setUrl(apiConfig.getDebugEnvUrl());
			debugEnvs.add(debugEnv);
		}

		// Set the debugging environment list to the TornaApi instance
		tornaApi.setDebugEnvs(debugEnvs);

		// Return whether a debugging environment exists
		return hasDebugEnv;
	}

	/**
	 * Prints debug information. This method is used to print configuration details,
	 * request specifics, and response information during debugging.
	 * @param apiConfig The API configuration object containing OpenUrl, appToken, etc.
	 * @param responseMsg The response message, typically a JSON-formatted string.
	 * @param requestJson The request JSON object in key-value pairs.
	 * @param category The category of the request or response for classifying debug
	 * information.
	 */
	public static void printDebugInfo(ApiConfig apiConfig, String responseMsg, Map<String, String> requestJson,
			String category) {
		printDebugInfo(apiConfig, responseMsg, requestJson, category, false);
	}

	/**
	 * Prints debug information with mode support.
	 * @param apiConfig The API configuration object containing OpenUrl, appToken, etc.
	 * @param responseMsg The response message, null for pre-request mode.
	 * @param requestJson The request JSON object in key-value pairs.
	 * @param category The category of the request or response for classifying debug
	 * information.
	 * @param isPreRequest true for pre-request mode, false for post-request mode.
	 */
	public static void printDebugInfo(ApiConfig apiConfig, String responseMsg, Map<String, String> requestJson,
			String category, boolean isPreRequest) {
		if (apiConfig.isTornaDebug()) {
			String sb = "Configuration information : \n" + "OpenUrl: " + apiConfig.getOpenUrl() + "\n" + "appToken: "
					+ apiConfig.getAppToken() + "\n";
			System.out.println(sb);
			if (isPreRequest) {
				TornaRequestInfo info = new TornaRequestInfo().of()
					.setCategory(category)
					.setRequestInfo(requestJson)
					.setResponseInfo(responseMsg);
				System.out.println(info.buildRequestInfo());
			}
			else {
				try {
					JsonElement element = JsonParser.parseString(responseMsg);
					TornaRequestInfo info = new TornaRequestInfo().of()
						.setCategory(category)
						.setCode(element.getAsJsonObject().get(TornaConstants.CODE).getAsString())
						.setMessage(element.getAsJsonObject().get(TornaConstants.MESSAGE).getAsString())
						.setRequestInfo(requestJson)
						.setResponseInfo(responseMsg);
					System.out.println(info.buildInfo());
				}
				catch (Exception e) {
					System.out.println("Response Error : \n" + responseMsg);
				}
			}
		}
	}

	/**
	 * build apis
	 * @param apiMethodDocs apiMethodDocs
	 * @param hasDebugEnv has debug environment
	 * @return List of Api
	 */
	public static List<Apis> buildApis(List<ApiMethodDoc> apiMethodDocs, boolean hasDebugEnv) {
		// Parameter list
		List<Apis> apis = new ArrayList<>();
		Apis methodApi;
		// Iterative classification interface
		for (ApiMethodDoc apiMethodDoc : apiMethodDocs) {
			methodApi = new Apis();
			methodApi.setIsFolder(TornaConstants.NO);
			methodApi.setName(apiMethodDoc.getDesc());
			methodApi.setUrl(
					hasDebugEnv ? subFirstUrlOrPath(apiMethodDoc.getPath()) : subFirstUrlOrPath(apiMethodDoc.getUrl()));
			methodApi.setHttpMethod(apiMethodDoc.getType());
			methodApi.setContentType(apiMethodDoc.getContentType());
			methodApi.setDescription(apiMethodDoc.getDetail());
			methodApi.setIsShow(TornaConstants.YES);
			methodApi.setAuthor(apiMethodDoc.getAuthor());
			methodApi.setOrderIndex(apiMethodDoc.getOrder());
			methodApi.setVersion(apiMethodDoc.getVersion());

			methodApi.setHeaderParams(buildHerder(apiMethodDoc.getRequestHeaders()));
			methodApi.setResponseParams(buildParams(apiMethodDoc.getResponseParams()));
			methodApi.setIsRequestArray(apiMethodDoc.getIsRequestArray());
			methodApi.setIsResponseArray(apiMethodDoc.getIsResponseArray());
			methodApi.setRequestArrayType(apiMethodDoc.getRequestArrayType());
			methodApi.setResponseArrayType(apiMethodDoc.getResponseArrayType());
			methodApi.setDeprecated(apiMethodDoc.isDeprecated() ? DocAnnotationConstants.DEPRECATED : null);
			// Path Param
			if (CollectionUtil.isNotEmpty(apiMethodDoc.getPathParams())) {
				methodApi.setPathParams(buildParams(apiMethodDoc.getPathParams()));
			}
			// Query Param
			if (CollectionUtil.isNotEmpty(apiMethodDoc.getQueryParams())) {
				methodApi.setQueryParams(buildParams(apiMethodDoc.getQueryParams()));
			}
			// Body Param
			if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
				methodApi.setRequestParams(buildParams(apiMethodDoc.getRequestParams()));
			}
			apis.add(methodApi);
		}
		return apis;
	}

	/**
	 * build dubbo apis
	 * @param apiMethodDocs apiMethodDocs
	 * @return List of Api
	 */
	public static List<Apis> buildDubboApis(List<RpcJavaMethod> apiMethodDocs) {
		// Parameter list
		List<Apis> apis = new ArrayList<>();
		Apis methodApi;
		// Iterative classification interface
		for (RpcJavaMethod apiMethodDoc : apiMethodDocs) {
			methodApi = new Apis();
			methodApi.setIsFolder(TornaConstants.NO);
			methodApi.setName(apiMethodDoc.getDesc());
			methodApi.setDescription(apiMethodDoc.getDetail());
			methodApi.setIsShow(TornaConstants.YES);
			methodApi.setAuthor(apiMethodDoc.getAuthor());
			methodApi.setUrl(apiMethodDoc.getMethodDefinition());
			methodApi.setResponseParams(buildParams(apiMethodDoc.getResponseParams()));
			methodApi.setOrderIndex(apiMethodDoc.getOrder());
			methodApi.setDeprecated(apiMethodDoc.isDeprecated() ? DocAnnotationConstants.DEPRECATED : null);
			methodApi.setDubboMethod(apiMethodDoc.getJavaMethod().getDeclarationSignature(false));
			// Json
			if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
				methodApi.setRequestParams(buildParams(apiMethodDoc.getRequestParams()));
			}
			apis.add(methodApi);
		}
		return apis;
	}

	/**
	 * build request header
	 * @param apiReqParams Request header parameter list
	 * @return List of HttpParam
	 */
	public static List<HttpParam> buildHerder(List<ApiReqParam> apiReqParams) {
		HttpParam httpParam;
		List<HttpParam> headers = new ArrayList<>();
		for (ApiReqParam header : apiReqParams) {
			httpParam = new HttpParam();
			httpParam.setName(header.getName());
			httpParam.setType(header.getType());
			httpParam.setRequired(header.isRequired() ? TornaConstants.YES : TornaConstants.NO);
			httpParam.setExample(StringUtil.removeQuotes(header.getValue()));
			httpParam.setEnumInfo(header.getEnumInfo());
			if (StringUtil.isNotEmpty(header.getSince())
					&& !DocGlobalConstants.DEFAULT_VERSION.equals(header.getSince())) {
				httpParam.setDescription(header.getDesc() + "@since " + header.getSince());
			}
			else {
				httpParam.setDescription(header.getDesc());
			}
			headers.add(httpParam);
		}
		return headers;
	}

	/**
	 * build request response params
	 * @param apiParams Param list
	 * @return List of HttpParam
	 */
	public static List<HttpParam> buildParams(List<ApiParam> apiParams) {
		HttpParam httpParam;
		List<HttpParam> bodies = new ArrayList<>();
		for (ApiParam apiParam : apiParams) {
			httpParam = new HttpParam();
			httpParam.setName(apiParam.getField());
			httpParam.setOrderIndex(apiParam.getId());
			httpParam.setMaxLength(apiParam.getMaxLength());
			String type = apiParam.getType();
			if (Objects.equals(type, ParamTypeConstants.PARAM_TYPE_FILE) && apiParam.isHasItems()) {
				type = TornaConstants.PARAM_TYPE_FILE_ARRAY;
			}

			httpParam.setType(type);
			httpParam.setVersion(apiParam.getVersion());
			httpParam.setRequired(apiParam.isRequired() ? TornaConstants.YES : TornaConstants.NO);
			httpParam.setExample(StringUtil.removeQuotes(apiParam.getValue()));
			httpParam.setDescription(DocUtil.replaceNewLineToHtmlBr(apiParam.getDesc()));
			httpParam.setEnumInfo(apiParam.getEnumInfo());
			if (apiParam.getChildren() != null) {
				httpParam.setChildren(buildParams(apiParam.getChildren()));
			}
			bodies.add(httpParam);
		}
		return bodies;
	}

	/**
	 * Builds a string representation of the dependencies.
	 * <p>
	 * This method takes a list of RpcApiDependency objects, converts each dependency to a
	 * string format, and concatenates them into a single string, with each dependency
	 * separated by a newline.
	 * @param dependencies A list of RpcApiDependency objects representing the
	 * dependencies.
	 * @return A string containing all the dependencies formatted and concatenated.
	 */
	public static String buildDependencies(List<RpcApiDependency> dependencies) {
		StringBuilder s = new StringBuilder();
		if (CollectionUtil.isNotEmpty(dependencies)) {
			for (RpcApiDependency r : dependencies) {
				s.append(r.toString()).append("\n\n");
			}
		}
		return s.toString();
	}

	/**
	 * Builds a list of error codes.
	 * @param config the API configuration object containing error code information
	 * @param javaProjectBuilder the Java project builder for accessing project resources
	 * @return a list of {@link CommonErrorCode} objects representing all API error codes
	 */
	public static List<CommonErrorCode> buildErrorCode(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		List<CommonErrorCode> commonErrorCodes = new ArrayList<>();
		CommonErrorCode commonErrorCode;
		List<ApiErrorCode> errorCodes = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
		if (CollectionUtil.isNotEmpty(errorCodes)) {
			for (EnumDictionary code : errorCodes) {
				commonErrorCode = new CommonErrorCode();
				commonErrorCode.setCode(code.getValue());
				// commonErrorCode.setSolution(code.getDesc());
				commonErrorCode.setMsg(DocUtil.replaceNewLineToHtmlBr(code.getDesc()));
				commonErrorCodes.add(commonErrorCode);
			}
		}
		return commonErrorCodes;
	}

	/**
	 * Builds a list of TornaDic objects from a list of ApiDocDict objects.
	 * <p>
	 * This method is primarily used to transform the original list of ApiDocDict objects
	 * into a list of TornaDic objects suitable for frontend display. It sets the name and
	 * description of TornaDic objects and calls other methods to construct dropdown
	 * items.
	 * </p>
	 * @param apiDocDicts The original list of ApiDocDict objects containing titles,
	 * descriptions, and data dictionaries.
	 * @return A list of TornaDic objects representing the transformed data dictionaries.
	 */
	public static List<TornaDic> buildTornaDic(List<ApiDocDict> apiDocDicts) {
		List<TornaDic> tornaDicArrayList = new ArrayList<>();
		TornaDic tornaDic;
		if (CollectionUtil.isNotEmpty(apiDocDicts)) {
			for (ApiDocDict doc : apiDocDicts) {
				tornaDic = new TornaDic();
				tornaDic.setName(doc.getTitle())
					.setDescription(DocUtil.replaceNewLineToHtmlBr(doc.getDescription()))
					.setItems(buildTornaDicItems(doc.getDataDictList()));
				tornaDicArrayList.add(tornaDic);
			}
		}
		return tornaDicArrayList;
	}

	/**
	 * Builds a list of TornaDic objects from a list of ApiDocDict objects.
	 * <p>
	 * This method is primarily used to transform the original list of ApiDocDict objects
	 * into a list of TornaDic objects suitable for frontend display. It sets the name and
	 * description of TornaDic objects and calls other methods to construct dropdown
	 * items.
	 * </p>
	 * @param enumDictionaries The original list of ApiDocDict objects containing titles,
	 * descriptions, and data dictionaries.
	 * @return A list of TornaDic objects for displaying API documentation information on
	 * the frontend.
	 */
	private static List<HttpParam> buildTornaDicItems(List<DataDict> enumDictionaries) {
		List<HttpParam> apis = new ArrayList<>();
		HttpParam api;
		if (CollectionUtil.isNotEmpty(enumDictionaries)) {
			for (EnumDictionary d : enumDictionaries) {
				api = new HttpParam();
				api.setName(d.getName());
				api.setType(d.getType());
				api.setValue(d.getValue());
				api.setDescription(d.getDesc());
				apis.add(api);
			}
		}
		return apis;
	}

	/**
	 * Set torna tags
	 * @param method method
	 * @param apiMethodDoc apiMethodDoc
	 * @param apiConfig apiConfig
	 */
	public static void setTornaArrayTags(JavaMethod method, ApiMethodDoc apiMethodDoc, ApiConfig apiConfig) {
		String returnTypeName = method.getReturnType().getCanonicalName();
		apiMethodDoc.setIsRequestArray(0);
		apiMethodDoc.setIsResponseArray(0);
		String responseBodyAdviceClassName = Optional.ofNullable(apiConfig)
			.map(ApiConfig::getResponseBodyAdvice)
			.map(BodyAdvice::getClassName)
			.orElse(StringUtil.EMPTY);
		String realReturnTypeName = StringUtil.isEmpty(responseBodyAdviceClassName) ? returnTypeName
				: responseBodyAdviceClassName;
		boolean respArray = JavaClassValidateUtil.isCollection(realReturnTypeName)
				|| JavaClassValidateUtil.isArray(realReturnTypeName);
		// response
		if (respArray) {
			apiMethodDoc.setIsResponseArray(1);
			String className = getType(method.getReturnType().getGenericCanonicalName());
			String arrayType = JavaClassValidateUtil.isPrimitive(className) ? className
					: ParamTypeConstants.PARAM_TYPE_OBJECT;
			apiMethodDoc.setResponseArrayType(arrayType);
		}
		// request
		if (CollectionUtil.isNotEmpty(method.getParameters())) {
			String requestBodyAdviceClassName = Optional.ofNullable(apiConfig)
				.map(ApiConfig::getRequestBodyAdvice)
				.map(BodyAdvice::getClassName)
				.orElse(StringUtil.EMPTY);
			for (JavaParameter param : method.getParameters()) {
				String typeName = param.getType().getCanonicalName();
				String realTypeName = StringUtil.isEmpty(requestBodyAdviceClassName) ? typeName
						: requestBodyAdviceClassName;
				boolean reqArray = JavaClassValidateUtil.isCollection(realTypeName)
						|| JavaClassValidateUtil.isArray(realTypeName);
				if (reqArray) {
					apiMethodDoc.setIsRequestArray(1);
					String className = getType(param.getType().getGenericCanonicalName());
					String arrayType = JavaClassValidateUtil.isPrimitive(className) ? className
							: ParamTypeConstants.PARAM_TYPE_OBJECT;
					apiMethodDoc.setRequestArrayType(arrayType);
					break;
				}
			}
		}

	}

	/**
	 * Gets the type of elements in an array based on a schema map.
	 * <p>
	 * This method parses a parameter schema to determine the type of elements in an
	 * array. It checks if the provided schema map is non-null and represents an array
	 * type. If so, it extracts the element type from the schema and returns the
	 * corresponding Java type name for primitive types or a generic object type for
	 * non-primitive types.
	 * @param schemaMap The schema map describing the array's properties, including its
	 * type.
	 * @return The type of elements in the array, either a primitive type name or an
	 * object type identifier.
	 */
	@SuppressWarnings("unchecked")
	private static String getArrayType(Map<String, Object> schemaMap) {
		String arrayType = null;
		if (Objects.nonNull(schemaMap) && Objects.equals(ParamTypeConstants.PARAM_TYPE_ARRAY, schemaMap.get("type"))) {
			Object innerScheme = schemaMap.get("items");
			if (Objects.nonNull(innerScheme)) {
				Map<String, Object> innerSchemeMap = (Map<String, Object>) innerScheme;
				String type = (String) innerSchemeMap.get("type");
				if (StringUtil.isNotEmpty(type)) {
					String className = getType(type);
					arrayType = JavaClassValidateUtil.isPrimitive(className) ? className
							: ParamTypeConstants.PARAM_TYPE_OBJECT;
				}
			}
		}
		return arrayType;
	}

	/**
	 * Extracts the generic type from a given type name.
	 * <p>
	 * This method is primarily used to parse a type name, removing array indicators and
	 * package names, and returns a simplified generic type name. If the type name
	 * contains generic information, it extracts the generic part as the type name. If the
	 * type name contains array indicators, it removes them. Finally, it returns a
	 * simplified generic type name; if there is no generic information, it returns a
	 * simplified version of the original type name.
	 * @param typeName The type name, which may contain generic information and array
	 * indicators.
	 * @return A simplified generic type name.
	 */
	private static String getType(String typeName) {
		String gicType;
		// get generic type
		if (typeName.contains("<")) {
			gicType = typeName.substring(typeName.indexOf("<") + 1, typeName.lastIndexOf(">"));
		}
		else {
			gicType = typeName;
		}
		if (gicType.contains("[")) {
			gicType = gicType.substring(0, gicType.indexOf("["));
		}
		return gicType.substring(gicType.lastIndexOf(".") + 1).toLowerCase();
	}

	/**
	 * Extracts and returns the first part of the given URL or path. If the URL contains a
	 * special separator, only the first part is returned; otherwise, the original URL is
	 * returned.
	 * @param url The URL or path to be processed.
	 * @return The first part after processing or the original URL.
	 */
	private static String subFirstUrlOrPath(String url) {
		if (StringUtil.isEmpty(url)) {
			return StringUtil.EMPTY;
		}
		if (!url.contains(DocGlobalConstants.MULTI_URL_SEPARATOR)) {
			return url;
		}
		String[] split = StringUtil.split(url, DocGlobalConstants.MULTI_URL_SEPARATOR);
		return split[0];
	}

}
