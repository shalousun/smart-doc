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
package io.github.smartdoc.handler;

import io.github.smartdoc.builder.ProjectDocConfigBuilder;
import io.github.smartdoc.constants.DocTags;
import io.github.smartdoc.constants.ParamTypeConstants;
import io.github.smartdoc.helper.ParamsBuildHelper;
import io.github.smartdoc.model.ApiReqParam;
import io.github.smartdoc.model.annotation.HeaderAnnotation;
import io.github.smartdoc.model.torna.EnumInfoAndValues;
import io.github.smartdoc.utils.DocClassUtil;
import io.github.smartdoc.utils.DocUtil;
import io.github.smartdoc.utils.JavaClassUtil;
import io.github.smartdoc.utils.JavaClassValidateUtil;
import io.github.smartdoc.utils.JavaFieldUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

/**
 * Header Handler
 *
 * @author yu3.sun on 2022/8/30
 */
public interface IHeaderHandler {

	/**
	 * Handle header
	 * @param method JavaMethod
	 * @param projectBuilder ProjectDocConfigBuilder
	 * @return {@code List<ApiReqParam>}
	 */
	@SuppressWarnings("unchecked")
	default List<ApiReqParam> handle(JavaMethod method, ProjectDocConfigBuilder projectBuilder) {
		List<ApiReqParam> mappingHeaders = new ArrayList<>();
		List<JavaAnnotation> annotations = method.getAnnotations();
		HeaderAnnotation headerAnnotation = getHeaderAnnotation();
		for (JavaAnnotation annotation : annotations) {
			String annotationName = annotation.getType().getValue();
			Object headersObject = annotation.getNamedParameter("headers");
			if (!isMapping(annotationName) || Objects.isNull(headersObject)) {
				continue;
			}
			String mappingHeader = StringUtil.removeQuotes(headersObject.toString());
			if (!mappingHeader.startsWith("[")) {
				processMappingHeaders(mappingHeader, mappingHeaders);
				continue;
			}
			List<String> headers = (LinkedList<String>) headersObject;
			for (String str : headers) {
				String header = StringUtil.removeQuotes(str);
				if (header.startsWith("!")) {
					continue;
				}
				processMappingHeaders(header, mappingHeaders);
			}
		}
		List<ApiReqParam> reqHeaders = new ArrayList<>();
		for (JavaParameter javaParameter : method.getParameters()) {
			List<JavaAnnotation> javaAnnotations = javaParameter.getAnnotations();
			String className = method.getDeclaringClass().getCanonicalName();
			String parameterName = javaParameter.getName();
			Map<String, String> paramCommentMap = DocUtil.getCommentsByTag(method, DocTags.PARAM, className);

			for (JavaAnnotation annotation : javaAnnotations) {
				String annotationName = annotation.getType().getValue();
				if (!headerAnnotation.getAnnotationName().equals(annotationName)) {
					continue;
				}
				ApiReqParam apiReqHeader = new ApiReqParam();
				apiReqHeader.setName(parameterName);
				apiReqHeader.setRequired(true);
				apiReqHeader.setDesc(DocUtil.paramCommentResolve(paramCommentMap.get(parameterName)));

				handleParamAnnotation(annotation, apiReqHeader, projectBuilder);
				handleParamTypeAndValue(javaParameter, apiReqHeader, projectBuilder, paramCommentMap);
				reqHeaders.add(apiReqHeader);
				break;

			}
		}
		return Stream.of(mappingHeaders, reqHeaders)
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toList());
	}

	/**
	 * handle Param
	 * @param javaParameter javaParameter
	 * @param apiReqHeader apiReqHeader
	 * @param builder builder
	 * @param paramCommentMap paramCommentMap
	 */
	default void handleParamTypeAndValue(JavaParameter javaParameter, ApiReqParam apiReqHeader,
			ProjectDocConfigBuilder builder, Map<String, String> paramCommentMap) {
		String fullyQualifiedName = javaParameter.getFullyQualifiedName();
		String genericFullyQualifiedName = javaParameter.getGenericFullyQualifiedName();
		String paramName = javaParameter.getName();
		JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(genericFullyQualifiedName);
		String simpleTypeName = javaParameter.getType().getValue();

		if (JavaClassValidateUtil.isCollection(fullyQualifiedName)
				|| JavaClassValidateUtil.isArray(fullyQualifiedName)) {
			String[] gicNameArr = DocClassUtil.getSimpleGicName(genericFullyQualifiedName);
			String gicName = gicNameArr[0];
			if (JavaClassValidateUtil.isArray(gicName)) {
				gicName = gicName.substring(0, gicName.indexOf("["));
			}
			// handle array and list mock value
			JavaClass gicJavaClass = builder.getJavaProjectBuilder().getClassByName(gicName);
			if (gicJavaClass.isEnum()) {
				String enumComment = ParamsBuildHelper.handleEnumComment(gicJavaClass, builder);
				apiReqHeader.setDesc(apiReqHeader.getDesc() + enumComment);
				apiReqHeader.setType(ParamTypeConstants.PARAM_TYPE_ARRAY);

				EnumInfoAndValues enumInfoAndValue = JavaClassUtil.getEnumInfoAndValue(gicJavaClass, builder,
						Boolean.FALSE);
				if (Objects.nonNull(enumInfoAndValue)) {
					String enumValue = StringUtil.removeDoubleQuotes(String.valueOf(enumInfoAndValue.getValue()));
					apiReqHeader.setValue(enumValue + "," + enumValue).setEnumInfoAndValues(enumInfoAndValue);
				}
			}
			else if (JavaClassValidateUtil.isPrimitive(gicName)) {
				String mockValue = JavaFieldUtil.createMockValue(paramCommentMap, paramName, gicName, gicName);
				if (StringUtil.isNotEmpty(mockValue) && !mockValue.contains(",")) {
					mockValue = StringUtils.join(mockValue, ",",
							JavaFieldUtil.createMockValue(paramCommentMap, paramName, gicName, gicName));
				}

				apiReqHeader.setType(ParamTypeConstants.PARAM_TYPE_ARRAY);
				apiReqHeader.setValue(mockValue);
			}
			else {
				apiReqHeader.setType(ParamTypeConstants.PARAM_TYPE_ARRAY);
			}
		}
		else if (JavaClassValidateUtil.isPrimitive(fullyQualifiedName)) {
			String mockValue = JavaFieldUtil.createMockValue(paramCommentMap, paramName,
					javaParameter.getFullyQualifiedName(), simpleTypeName);

			apiReqHeader.setType(DocClassUtil.processTypeNameForParams(simpleTypeName));
			apiReqHeader.setValue(mockValue);
		}
		// Handle if it is enum types
		else if (javaClass.isEnum()) {
			String enumComment = ParamsBuildHelper.handleEnumComment(javaClass, builder);
			apiReqHeader.setDesc(apiReqHeader.getDesc() + enumComment);
			apiReqHeader.setType(ParamTypeConstants.PARAM_TYPE_ENUM);

			EnumInfoAndValues enumInfoAndValue = JavaClassUtil.getEnumInfoAndValue(javaClass, builder, Boolean.FALSE);
			if (Objects.nonNull(enumInfoAndValue)) {
				String enumValue = StringUtil.removeDoubleQuotes(String.valueOf(enumInfoAndValue.getValue()));
				apiReqHeader.setValue(enumValue)
					.setEnumInfoAndValues(enumInfoAndValue)
					.setType(enumInfoAndValue.getType());
			}
		}
		else {
			apiReqHeader.setType(ParamTypeConstants.PARAM_TYPE_OBJECT);
		}
	}

	/**
	 * Handle annotation
	 * @param annotation annotation
	 * @param apiReqHeader apiReqHeader
	 * @param projectBuilder projectBuilder
	 */
	default void handleParamAnnotation(JavaAnnotation annotation, ApiReqParam apiReqHeader,
			ProjectDocConfigBuilder projectBuilder) {
		HeaderAnnotation headerAnnotation = getHeaderAnnotation();
		Map<String, String> constantsMap = projectBuilder.getConstantsMap();
		Map<String, Object> requestHeaderMap = annotation.getNamedParameterMap();
		if (requestHeaderMap != null && requestHeaderMap.size() > 0) {
			// Obtain header value
			if (requestHeaderMap.containsKey(headerAnnotation.getValueProp())) {
				ClassLoader classLoader = projectBuilder.getApiConfig().getClassLoader();
				String attrValue = DocUtil.handleRequestHeaderValue(classLoader, annotation);
				String constValue = ((String) requestHeaderMap.get(headerAnnotation.getValueProp())).replaceAll("\"",
						"");
				if (StringUtil.isEmpty(attrValue)) {
					Object value = constantsMap.get(constValue);
					if (value != null) {
						apiReqHeader.setName(value.toString());
					}
					else {
						apiReqHeader.setName(constValue);
					}
				}
				else {
					apiReqHeader.setName(attrValue);
				}
			}

			// Obtain header default value
			if (requestHeaderMap.containsKey(headerAnnotation.getDefaultValueProp())) {
				StringBuilder desc = new StringBuilder();
				String defaultValue = String.valueOf(requestHeaderMap.get(headerAnnotation.getDefaultValueProp()));
				desc.append("(defaultValue: ").append(StringUtil.removeQuotes(defaultValue)).append(")");
				apiReqHeader.setValue(StringUtil.removeQuotes(defaultValue));
				apiReqHeader.setDesc(apiReqHeader.getDesc() + desc);
			}

			if (requestHeaderMap.containsKey(headerAnnotation.getRequiredProp())) {
				apiReqHeader.setRequired(
						!Boolean.FALSE.toString().equals(requestHeaderMap.get(headerAnnotation.getRequiredProp())));
			}
		}
	}

	/**
	 * process mapping headers
	 * @param header header
	 * @param mappingHeaders mapping headers
	 */
	default void processMappingHeaders(String header, List<ApiReqParam> mappingHeaders) {
		if (header.contains("!=")) {
			String headerName = header.substring(0, header.indexOf("!"));
			ApiReqParam apiReqHeader = ApiReqParam.builder()
				.setName(headerName)
				.setRequired(true)
				.setValue(null)
				.setDesc("header condition")
				.setType("string");
			mappingHeaders.add(apiReqHeader);
		}
		else {
			String headerName;
			String headerValue = null;
			if (header.contains("=")) {
				int index = header.indexOf("=");
				headerName = header.substring(0, index);
				headerValue = header.substring(index + 1);
			}
			else {
				headerName = header;
			}
			ApiReqParam apiReqHeader = ApiReqParam.builder()
				.setName(headerName)
				.setRequired(true)
				.setValue(headerValue)
				.setDesc("header condition")
				.setType("string");
			mappingHeaders.add(apiReqHeader);
		}
	}

	/**
	 * check mapping annotation
	 * @param annotationName annotation name
	 * @return boolean
	 */
	boolean isMapping(String annotationName);

	/**
	 * Get framework annotation info
	 * @return Header annotation info
	 */
	HeaderAnnotation getHeaderAnnotation();

}
