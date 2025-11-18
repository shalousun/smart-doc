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
package io.github.smartdoc.template;

import io.github.smartdoc.builder.ProjectDocConfigBuilder;
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.DocTags;
import io.github.smartdoc.helper.ParamsBuildHelper;
import io.github.smartdoc.model.*;
import io.github.smartdoc.model.annotation.FrameworkAnnotations;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.*;
import io.github.smartdoc.utils.*;

import java.util.*;

import static io.github.smartdoc.constants.DocGlobalConstants.NO_COMMENTS_FOUND;

/**
 * Base Doc Build Template
 *
 * @author yu3.sun on 2022/10/2
 */
public interface IBaseDocBuildTemplate {

	/**
	 * Comment Resolve
	 * @param comment comment
	 * @return String
	 */
	default String paramCommentResolve(String comment) {
		if (StringUtil.isEmpty(comment)) {
			comment = NO_COMMENTS_FOUND;
		}
		else {
			if (comment.contains("|")) {
				comment = comment.substring(0, comment.indexOf("|"));
			}
		}
		return comment;
	}

	/**
	 * Build return api params
	 * @param docJavaMethod JavaMethod
	 * @param projectBuilder ProjectDocConfigBuilder
	 * @return List
	 */
	default List<ApiParam> buildReturnApiParams(DocJavaMethod docJavaMethod, ProjectDocConfigBuilder projectBuilder) {
		JavaMethod method = docJavaMethod.getJavaMethod();
		if (method.getReturns().isVoid() && Objects.isNull(projectBuilder.getApiConfig().getResponseBodyAdvice())) {
			return new ArrayList<>(0);
		}
		DocletTag downloadTag = method.getTagByName(DocTags.DOWNLOAD);
		if (Objects.nonNull(downloadTag)) {
			return new ArrayList<>(0);
		}
		String returnTypeGenericCanonicalName = method.getReturnType().getGenericCanonicalName();
		if (Objects.nonNull(projectBuilder.getApiConfig().getResponseBodyAdvice())
				&& Objects.isNull(method.getTagByName(DocTags.IGNORE_RESPONSE_BODY_ADVICE))) {
			String responseBodyAdvice = projectBuilder.getApiConfig().getResponseBodyAdvice().getClassName();
			if (!returnTypeGenericCanonicalName.startsWith(responseBodyAdvice)) {
				returnTypeGenericCanonicalName = responseBodyAdvice + "<" + returnTypeGenericCanonicalName + ">";
			}
		}
		Map<String, JavaType> actualTypesMap = docJavaMethod.getActualTypesMap();
		ApiReturn apiReturn = DocClassUtil.processReturnType(returnTypeGenericCanonicalName);
		String returnType = apiReturn.getGenericCanonicalName();
		if (Objects.nonNull(actualTypesMap)) {
			for (Map.Entry<String, JavaType> entry : actualTypesMap.entrySet()) {
				returnType = returnType.replace(entry.getKey(), entry.getValue().getCanonicalName());
			}
		}

		String typeName = apiReturn.getSimpleName();
		if (this.ignoreReturnObject(typeName, projectBuilder.getApiConfig().getIgnoreRequestParams())) {
			return new ArrayList<>(0);
		}
		if (JavaClassValidateUtil.isPrimitive(typeName)) {
			docJavaMethod.setReturnSchema(OpenApiSchemaUtil.primaryTypeSchema(typeName));
			return new ArrayList<>(0);
		}
		if (JavaClassValidateUtil.isCollection(typeName)) {
			if (returnType.contains("<")) {
				String gicName = returnType.substring(returnType.indexOf("<") + 1, returnType.lastIndexOf(">"));
				if (JavaClassValidateUtil.isPrimitive(gicName)) {
					docJavaMethod.setReturnSchema(OpenApiSchemaUtil.arrayTypeSchema(gicName));
					return new ArrayList<>(0);
				}
				return ParamsBuildHelper.buildParams(gicName, "", 0, null, Boolean.TRUE, new HashMap<>(16),
						projectBuilder, null, docJavaMethod.getJsonViewClasses(), 0, Boolean.FALSE, null);
			}
			else {
				return new ArrayList<>(0);
			}
		}
		if (JavaClassValidateUtil.isMap(typeName)) {
			String[] keyValue = DocClassUtil.getMapKeyValueType(returnType);
			if (keyValue.length == 0) {
				return new ArrayList<>(0);
			}
			return ParamsBuildHelper.buildParams(returnType, "", 0, null, Boolean.TRUE, new HashMap<>(16),
					projectBuilder, null, docJavaMethod.getJsonViewClasses(), 0, Boolean.FALSE, null);
		}
		if (StringUtil.isNotEmpty(returnType)) {
			return ParamsBuildHelper.buildParams(returnType, "", 0, null, Boolean.TRUE, new HashMap<>(16),
					projectBuilder, null, docJavaMethod.getJsonViewClasses(), 0, Boolean.FALSE, null);
		}
		return new ArrayList<>(0);
	}

	/**
	 * Convert params data to tree
	 * @param apiMethodDoc ApiMethodDoc
	 */
	default void convertParamsDataToTree(ApiMethodDoc apiMethodDoc) {
		apiMethodDoc.setPathParams(ApiParamTreeUtil.apiParamToTree(apiMethodDoc.getPathParams()));
		apiMethodDoc.setQueryParams(ApiParamTreeUtil.apiParamToTree(apiMethodDoc.getQueryParams()));
		apiMethodDoc.setRequestParams(ApiParamTreeUtil.apiParamToTree(apiMethodDoc.getRequestParams()));
	}

	/**
	 * Retrieves and processes the list of parameters for a given Java method, applying
	 * various transformations and ignoring specified parameters.
	 * @param builder The project documentation configuration builder.
	 * @param docJavaMethod The documented Java method.
	 * @param frameworkAnnotations The framework annotations used to identify specific
	 * annotations.
	 * @return A list of processed {@link DocJavaParameter} objects.
	 */
	default List<DocJavaParameter> getJavaParameterList(ProjectDocConfigBuilder builder,
			final DocJavaMethod docJavaMethod, FrameworkAnnotations frameworkAnnotations) {
		JavaMethod javaMethod = docJavaMethod.getJavaMethod();
		Map<String, String> replacementMap = builder.getReplaceClassMap();
		Map<String, String> paramTagMap = docJavaMethod.getParamTagMap();
		List<JavaParameter> parameterList = javaMethod.getParameters();
		if (parameterList.isEmpty()) {
			return new ArrayList<>(0);
		}
		Set<String> ignoreSets = ignoreParamsSets(javaMethod);
		List<DocJavaParameter> apiJavaParameterList = new ArrayList<>(parameterList.size());
		Map<String, JavaType> actualTypesMap = docJavaMethod.getActualTypesMap();
		for (JavaParameter parameter : parameterList) {
			String paramName = parameter.getName();
			if (ignoreSets.contains(paramName)) {
				continue;
			}
			DocJavaParameter apiJavaParameter = new DocJavaParameter();
			apiJavaParameter.setJavaParameter(parameter);
			JavaType javaType = parameter.getType();
			if (Objects.nonNull(actualTypesMap) && Objects.nonNull(actualTypesMap.get(javaType.getCanonicalName()))) {
				javaType = actualTypesMap.get(javaType.getCanonicalName());
			}
			apiJavaParameter.setTypeValue(javaType.getValue());
			StringBuilder genericCanonicalName = new StringBuilder(javaType.getGenericCanonicalName());
			String fullyQualifiedName = javaType.getFullyQualifiedName();
			apiJavaParameter.setFullyQualifiedName(fullyQualifiedName);
			String genericFullyQualifiedName = javaType.getGenericFullyQualifiedName();
			String commentClass = paramTagMap.get(paramName);
			// ignore request params
			if (Objects.nonNull(commentClass) && commentClass.contains(DocTags.IGNORE)) {
				continue;
			}
			String rewriteClassName = getRewriteClassName(replacementMap, genericFullyQualifiedName, commentClass);
			// rewrite class
			if (JavaClassValidateUtil.isClassName(rewriteClassName)) {
				genericCanonicalName = new StringBuilder(rewriteClassName);
				genericFullyQualifiedName = DocClassUtil.getSimpleName(rewriteClassName);
			}
			if (JavaClassValidateUtil.isMvcIgnoreParams(genericCanonicalName.toString(),
					builder.getApiConfig().getIgnoreRequestParams())) {
				continue;
			}
			genericFullyQualifiedName = DocClassUtil.rewriteRequestParam(genericFullyQualifiedName);
			genericCanonicalName = new StringBuilder(DocClassUtil.rewriteRequestParam(genericCanonicalName.toString()));
			List<JavaAnnotation> annotations = parameter.getAnnotations();
			apiJavaParameter.setAnnotations(annotations);
			for (JavaAnnotation annotation : annotations) {
				String annotationName = annotation.getType().getValue();
				if (Objects.nonNull(frameworkAnnotations)
						&& frameworkAnnotations.getRequestBodyAnnotation().getAnnotationName().equals(annotationName)) {
					if (Objects.nonNull(builder.getApiConfig().getRequestBodyAdvice())
							&& Objects.isNull(javaMethod.getTagByName(DocTags.IGNORE_REQUEST_BODY_ADVICE))) {
						String requestBodyAdvice = builder.getApiConfig().getRequestBodyAdvice().getClassName();
						genericFullyQualifiedName = requestBodyAdvice;
						genericCanonicalName = new StringBuilder(requestBodyAdvice + "<" + genericCanonicalName + ">");
					}
				}
			}
			if (JavaClassValidateUtil.isCollection(genericFullyQualifiedName)
					|| JavaClassValidateUtil.isArray(genericFullyQualifiedName)) {
				if (JavaClassValidateUtil.isCollection(genericCanonicalName.toString())) {
					genericCanonicalName.append("<T>");
				}
			}
			apiJavaParameter.setGenericCanonicalName(genericCanonicalName.toString());
			apiJavaParameter.setGenericFullyQualifiedName(genericFullyQualifiedName);
			apiJavaParameterList.add(apiJavaParameter);
		}
		return apiJavaParameterList;
	}

	/**
	 * Retrieves the rewritten class name based on the provided map, full type name, and
	 * comment class.
	 * @param replacementMap The map containing replacements for class names.
	 * @param fullTypeName The fully qualified type name.
	 * @param commentClass The comment associated with the class, if any.
	 * @return The rewritten class name or the original class name if no valid rewrite is
	 * found.
	 */
	default String getRewriteClassName(Map<String, String> replacementMap, String fullTypeName, String commentClass) {
		String rewriteClassName;
		if (Objects.nonNull(commentClass) && !DocGlobalConstants.NO_COMMENTS_FOUND.equals(commentClass)) {
			String[] comments = commentClass.split("\\|");
			if (comments.length < 1) {
				return replacementMap.get(fullTypeName);
			}
			rewriteClassName = comments[comments.length - 1];
			if (JavaClassValidateUtil.isClassName(rewriteClassName)) {
				return rewriteClassName;
			}
		}
		return replacementMap.get(fullTypeName);
	}

	/**
	 * Retrieves a set of parameter names to be ignored based on the `@ignoreParams` tag
	 * in the method's documentation.
	 * @param method The Java method to inspect for the ignore parameters tag.
	 * @return A set of parameter names that should be ignored.
	 */
	default Set<String> ignoreParamsSets(JavaMethod method) {
		Set<String> ignoreSets = new HashSet<>();
		DocletTag ignoreParam = method.getTagByName(DocTags.IGNORE_PARAMS);
		if (Objects.nonNull(ignoreParam)) {
			String[] igParams = ignoreParam.getValue().split(" ");
			Collections.addAll(ignoreSets, igParams);
		}
		return ignoreSets;
	}

	/**
	 * Retrieves the simplified return type of a Java method, handling generic types and
	 * array notations.
	 * @param javaMethod The Java method whose return type needs to be processed.
	 * @param actualTypesMap A map containing actual type mappings.
	 * @return The simplified return type as a string.
	 */
	default String getMethodReturnType(JavaMethod javaMethod, Map<String, JavaType> actualTypesMap) {
		JavaType returnType = javaMethod.getReturnType();
		String simpleReturn = this.replaceTypeName(returnType.getCanonicalName(), actualTypesMap, Boolean.TRUE);
		String returnClass = this.replaceTypeName(returnType.getGenericCanonicalName(), actualTypesMap, Boolean.TRUE);
		returnClass = returnClass.replace(simpleReturn, JavaClassUtil.getClassSimpleName(simpleReturn));
		String[] arrays = DocClassUtil.getSimpleGicName(returnClass);
		for (String str : arrays) {
			if (str.contains("[")) {
				str = str.substring(0, str.indexOf("["));
			}
			String[] generics = str.split("[<,]");
			for (String generic : generics) {
				if (generic.contains("extends")) {
					String className = generic.substring(generic.lastIndexOf(" ") + 1);
					returnClass = returnClass.replace(className, JavaClassUtil.getClassSimpleName(className));
				}
				if (generic.length() != 1 && !generic.contains("extends")) {
					returnClass = returnClass.replaceAll(generic, JavaClassUtil.getClassSimpleName(generic));
				}

			}
		}
		return returnClass;
	}

	/**
	 * Replaces type names in the given string based on the provided map of actual types.
	 * @param type The type name to be replaced.
	 * @param actualTypesMap A map containing the actual types to be used for replacement.
	 * @param simple A flag indicating whether to use simple names for replacement.
	 * @return The type name after replacement.
	 */
	default String replaceTypeName(String type, Map<String, JavaType> actualTypesMap, boolean simple) {
		if (Objects.isNull(actualTypesMap)) {
			return type;
		}
		for (Map.Entry<String, JavaType> entry : actualTypesMap.entrySet()) {
			if (type.contains(entry.getKey())) {
				if (simple) {
					return type.replace(entry.getKey(), entry.getValue().getGenericValue());
				}
				else {
					return type.replace(entry.getKey(), entry.getValue().getGenericFullyQualifiedName());
				}
			}
		}
		return type;
	}

	/**
	 * Determines whether the return object should be ignored based on its type name and a
	 * list of ignored parameters.
	 * @param typeName The name of the type to check.
	 * @param ignoreParams A list of parameter names that should be ignored.
	 * @return true if the return object should be ignored; false otherwise.
	 */
	boolean ignoreReturnObject(String typeName, List<String> ignoreParams);

}
