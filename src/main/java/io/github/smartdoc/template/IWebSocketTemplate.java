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
import io.github.smartdoc.constants.DocAnnotationConstants;
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.DocTags;
import io.github.smartdoc.constants.JavaTypeConstants;
import io.github.smartdoc.handler.DefaultWebSocketRequestHandler;
import io.github.smartdoc.handler.IWebSocketRequestHandler;
import io.github.smartdoc.helper.ParamsBuildHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.WebSocketDoc;
import io.github.smartdoc.model.annotation.FrameworkAnnotations;
import io.github.smartdoc.model.annotation.ServerEndpointAnnotation;
import io.github.smartdoc.model.request.ServerEndpoint;
import io.github.smartdoc.utils.DocClassUtil;
import io.github.smartdoc.utils.DocUtil;
import io.github.smartdoc.utils.JavaClassUtil;
import com.power.common.util.StringUtil;
import com.power.common.util.ValidateUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * the WebSocket Template
 *
 * @author linwumingshi
 * @since 3.0.3
 */
public interface IWebSocketTemplate {

	/**
	 * param order AtomicInteger
	 */
	AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

	/**
	 * logger
	 */
	Logger log = Logger.getLogger(IWebSocketTemplate.class.getName());

	/**
	 * Processes the provided Java classes and generates WebSocket documentation.
	 * @param projectBuilder The project configuration builder.
	 * @param frameworkAnnotations The framework annotations to look for.
	 * @param webSocketRequestHandler The handler for processing WebSocket requests.
	 * @param candidateClasses The collection of Java classes to process.
	 * @return A list of WebSocketDoc containing the documentation for the WebSocket
	 * endpoints.
	 */
	default List<WebSocketDoc> processWebSocketData(ProjectDocConfigBuilder projectBuilder,
			FrameworkAnnotations frameworkAnnotations, IWebSocketRequestHandler webSocketRequestHandler,
			Collection<JavaClass> candidateClasses) {
		ApiConfig apiConfig = projectBuilder.getApiConfig();
		List<WebSocketDoc> apiDocList = new ArrayList<>();
		int maxOrder = 0;
		boolean setCustomOrder = false;
		// exclude class is ignore
		for (JavaClass javaClass : candidateClasses) {
			if (StringUtil.isNotEmpty(apiConfig.getPackageFilters())) {
				// from smart config
				if (!DocUtil.isMatch(apiConfig.getPackageFilters(), javaClass)) {
					continue;
				}
			}
			// exclude class is ignore
			if (StringUtil.isNotEmpty(apiConfig.getPackageExcludeFilters())) {
				if (DocUtil.isMatch(apiConfig.getPackageExcludeFilters(), javaClass)) {
					continue;
				}
			}
			// ignore tag is ignore
			if (Objects.nonNull(javaClass.getTagByName(DocTags.IGNORE))) {
				continue;
			}
			// if the class is websocket
			Optional<JavaAnnotation> optionalAnnotation = this.getOptionalWebSocketAnnotation(javaClass,
					frameworkAnnotations);
			if (!optionalAnnotation.isPresent()) {
				continue;
			}
			String strOrder = JavaClassUtil.getClassTagsValue(javaClass, DocTags.ORDER, Boolean.TRUE);
			int order = 0;
			if (ValidateUtil.isNonNegativeInteger(strOrder)) {
				setCustomOrder = true;
				order = Integer.parseInt(strOrder);
				maxOrder = Math.max(maxOrder, order);
			}
			WebSocketDoc webSocketDoc = this.buildEntryPointWebSocketDoc(javaClass, projectBuilder,
					webSocketRequestHandler, order, optionalAnnotation.get());
			if (Objects.nonNull(webSocketDoc)) {
				apiDocList.add(webSocketDoc);
			}
		}
		if (apiConfig.isSortByTitle()) {
			// sort by title
			Collections.sort(apiDocList);
		}
		else if (setCustomOrder) {
			ATOMIC_INTEGER.getAndAdd(maxOrder);
			// while set custom oder
			final List<WebSocketDoc> tempList = new ArrayList<>(apiDocList);
			tempList.forEach(p -> {
				if (p.getOrder() == 0) {
					p.setOrder(ATOMIC_INTEGER.getAndAdd(1));
				}
			});
			return tempList.stream().sorted(Comparator.comparing(WebSocketDoc::getOrder)).collect(Collectors.toList());
		}
		else {
			apiDocList.forEach(p -> p.setOrder(ATOMIC_INTEGER.getAndAdd(1)));
		}
		return apiDocList;
	}

	/**
	 * build websocket doc
	 * @param javaClass JavaClass
	 * @param projectBuilder ProjectDocConfigBuilder
	 * @param webSocketRequestHandler WebSocketRequestHandler
	 * @param order order
	 * @param serverEndpointAnnotation ServerEndpointAnnotation
	 * @return WebSocketDoc
	 */
	default WebSocketDoc buildEntryPointWebSocketDoc(final JavaClass javaClass, ProjectDocConfigBuilder projectBuilder,
			IWebSocketRequestHandler webSocketRequestHandler, int order, JavaAnnotation serverEndpointAnnotation) {

		ApiConfig apiConfig = projectBuilder.getApiConfig();

		webSocketRequestHandler = Objects.isNull(webSocketRequestHandler) ? DefaultWebSocketRequestHandler.getInstance()
				: webSocketRequestHandler;
		ServerEndpoint serverEndpoint = webSocketRequestHandler.handleServerEndpoint(projectBuilder, javaClass,
				serverEndpointAnnotation);

		WebSocketDoc webSocketDoc = new WebSocketDoc();
		// if it does not have subProtocols
		if (!serverEndpoint.getSubProtocols().isEmpty()) {
			webSocketDoc.setSubProtocols(String.join(",", serverEndpoint.getSubProtocols()));
		}
		// populate websocket params
		this.populateWebSocketParams(projectBuilder, webSocketDoc, javaClass, serverEndpoint);

		// build websocket doc
		webSocketDoc.setName(javaClass.getName());
		webSocketDoc.setUri(replaceHttpPrefixToWebSocketPrefix(apiConfig.getServerUrl()) + serverEndpoint.getUrl());
		webSocketDoc.setPackageName(javaClass.getPackage().getName());
		webSocketDoc.setDesc(DocUtil.getEscapeAndCleanComment(javaClass.getComment()));
		webSocketDoc.setAuthor(JavaClassUtil.getClassTagsValue(javaClass, DocTags.AUTHOR, Boolean.TRUE));
		webSocketDoc.setOrder(order);
		boolean isDeprecated = Objects.nonNull(javaClass.getTagByName(DocTags.DEPRECATED)) || javaClass.getAnnotations()
			.stream()
			.anyMatch(i -> JavaTypeConstants.JAVA_DEPRECATED_FULLY.equals(i.getType().getGenericFullyQualifiedName()));
		webSocketDoc.setDeprecated(isDeprecated);
		return webSocketDoc;
	}

	/**
	 * Get WebSocket annotations from a JavaClass based on FrameworkAnnotations.
	 * @param javaClass The JavaClass to retrieve annotations from.
	 * @param frameworkAnnotations The FrameworkAnnotations containing specific framework
	 * annotation information.
	 * @return An Optional JavaAnnotation containing the WebSocket annotation, or
	 * Optional.empty() if not found.
	 */
	default Optional<JavaAnnotation> getOptionalWebSocketAnnotation(JavaClass javaClass,
			FrameworkAnnotations frameworkAnnotations) {
		// Check for null inputs
		if (Objects.isNull(frameworkAnnotations) || Objects.isNull(javaClass)
				|| Objects.isNull(frameworkAnnotations.getServerEndpointAnnotation())) {
			return Optional.empty();
		}

		ServerEndpointAnnotation serverEndpointAnnotation = frameworkAnnotations.getServerEndpointAnnotation();

		// Filter and find the WebSocket annotation
		return javaClass.getAnnotations()
			.stream()
			.filter(annotation -> Objects.equals(serverEndpointAnnotation.getAnnotationName(),
					annotation.getType().getName()))
			.findFirst();
	}

	/**
	 * Populates the WebSocketDoc object with both path and message parameters from the
	 * provided JavaClass and ServerEndpoint.
	 * @param projectBuilder The project configuration builder.
	 * @param webSocketDoc The WebSocketDoc object to be populated with parameters.
	 * @param javaClass The JavaClass containing the WebSocket endpoint.
	 * @param serverEndpoint The ServerEndpoint containing the URL and parameters.
	 */
	default void populateWebSocketParams(ProjectDocConfigBuilder projectBuilder, WebSocketDoc webSocketDoc,
			final JavaClass javaClass, ServerEndpoint serverEndpoint) {
		List<ApiParam> pathParams = new ArrayList<>();
		String url = serverEndpoint.getUrl();
		Set<String> pathParamsSet = extractPathParams(url);

		Map<String, JavaParameter> parameterMap = new HashMap<>(16);

		Map<String, String> commentsByTag = new HashMap<>(16);
		// @OnMessage Method flag
		boolean onMessageMethod = false;

		for (JavaMethod javaMethod : javaClass.getMethods()) {
			List<JavaAnnotation> annotations = javaMethod.getAnnotations();
			List<JavaParameter> parameters = javaMethod.getParameters();
			commentsByTag = DocUtil.getCommentsByTag(javaMethod, DocTags.PARAM, javaClass.getName());

			// if the method does not have @OnOpen
			boolean hasOnOpenAnnotation = annotations.stream()
				.anyMatch(annotation -> DocAnnotationConstants.ON_OPEN.equals(annotation.getType().getName()));
			if (hasOnOpenAnnotation) {
				// Collect parameters annotated with @PathParam
				for (JavaParameter parameter : parameters) {
					for (JavaAnnotation annotation : parameter.getAnnotations()) {
						if (DocAnnotationConstants.PATH_PARAM.equals(annotation.getType().getName())) {
							parameterMap.put(parameter.getName(), parameter);
						}
					}
				}
				continue;
			}
			// if the method does not have @OnMessage
			boolean hasOnMessageAnnotation = annotations.stream()
				.anyMatch(annotation -> DocAnnotationConstants.ON_MESSAGE.equals(annotation.getType().getName()));
			if (hasOnMessageAnnotation) {
				if (onMessageMethod) {
					log.warning("@OnMessage can only on one method");
				}
				else {
					onMessageMethod = true;
					if (!parameters.isEmpty() && parameters.size() > 1) {
						log.warning("@OnMessage method can only have one parameter");
					}
					JavaParameter first = parameters.get(0);
					List<ApiParam> apiParams = ParamsBuildHelper.buildParams(first.getFullyQualifiedName(), "", 0,
							"false", false, new HashMap<>(16), projectBuilder, new HashSet<>(16), new HashSet<>(16), 0,
							false, new AtomicInteger(0));
					webSocketDoc.setMessageParams(apiParams);
				}
			}

		}

		// Reorder path parameters according to the order in pathParamsSet
		for (String item : pathParamsSet) {
			ApiParam pathApiParam = ApiParam.of()
				.setId(0)
				.setField(item)
				.setType("string")
				.setDesc(item)
				.setVersion(commentsByTag.getOrDefault(DocTags.SINCE, DocGlobalConstants.DEFAULT_VERSION))
				.setRequired(true);
			JavaParameter javaParameter = parameterMap.get(item);
			if (Objects.nonNull(javaParameter)) {
				pathApiParam
					.setType(DocClassUtil
						.processTypeNameForParams(javaParameter.getType().getGenericFullyQualifiedName()))
					.setDesc(commentsByTag.get(javaParameter.getName()));
			}
			pathParams.add(pathApiParam);
		}
		webSocketDoc.setPathParams(pathParams);

		this.setResponseParam(projectBuilder, webSocketDoc, serverEndpoint);
	}

	/**
	 * Sets the response parameters for the WebSocketDoc object based on the provided
	 * ServerEndpoint.
	 * @param projectBuilder The project configuration builder.
	 * @param webSocketDoc The WebSocketDoc object to be populated with response
	 * parameters.
	 * @param serverEndpoint The ServerEndpoint containing the URL and parameters.
	 */
	default void setResponseParam(ProjectDocConfigBuilder projectBuilder, WebSocketDoc webSocketDoc,
			ServerEndpoint serverEndpoint) {
		if (serverEndpoint.getEncoders().isEmpty()) {
			return;
		}
		List<List<ApiParam>> result = new ArrayList<>();
		// Get the encoder class
		for (String encoder : serverEndpoint.getEncoders()) {
			JavaClass encoderClass = projectBuilder.getClassByName(encoder);
			if (Objects.nonNull(encoderClass)) {
				Optional<JavaType> first = encoderClass.getImplements()
					.stream()
					.filter(i -> "jakarta.websocket.Encoder$Text".equals(i.getBinaryName())
							|| "java.websocket.Encoder$Text".equals(i.getBinaryName()))
					.findFirst();
				if (first.isPresent()) {
					JavaType javaType = first.get();
					if (javaType instanceof DefaultJavaParameterizedType) {
						DefaultJavaParameterizedType type = (DefaultJavaParameterizedType) javaType;
						List<JavaType> actualTypeArguments = type.getActualTypeArguments();
						if (Objects.nonNull(actualTypeArguments) && !actualTypeArguments.isEmpty()) {
							JavaType firstResultClass = actualTypeArguments.get(0);
							List<ApiParam> apiParams = ParamsBuildHelper.buildParams(firstResultClass.getBinaryName(),
									"", 0, "false", true, new HashMap<>(16), projectBuilder, new HashSet<>(16),
									new HashSet<>(16), 0, false, new AtomicInteger(0));
							result.add(apiParams);
						}
					}
				}
			}
		}
		webSocketDoc.setResponseParams(result);
	}

	/**
	 * Replaces the HTTP prefix with a WebSocket prefix in a given URL.
	 * @param url The URL to modify.
	 * @return The modified URL with a WebSocket prefix.
	 */
	static String replaceHttpPrefixToWebSocketPrefix(String url) {
		return url.replace("http", "ws");
	}

	/**
	 * Extracts the path parameters from a URL.
	 * @param url The URL containing path parameters.
	 * @return A set of extracted path parameter names.
	 */
	static Set<String> extractPathParams(String url) {
		Set<String> pathParams = new LinkedHashSet<>();
		String regex = "\\{(\\w+)}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url);

		while (matcher.find()) {
			pathParams.add(matcher.group(1));
		}

		return pathParams;
	}

}
