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
import io.github.smartdoc.constants.DocAnnotationConstants;
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.JAXRSAnnotations;
import io.github.smartdoc.constants.JakartaJaxrsAnnotations;
import io.github.smartdoc.constants.JavaTypeConstants;
import io.github.smartdoc.constants.MediaType;
import io.github.smartdoc.model.request.JaxrsPathMapping;
import io.github.smartdoc.utils.DocUrlUtil;
import io.github.smartdoc.utils.DocUtil;
import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaMethod;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.smartdoc.constants.DocTags.DEPRECATED;
import static io.github.smartdoc.constants.DocTags.IGNORE;

/**
 * JAX-RS Path Handler This class is responsible for processing JAX-RS annotations on Java
 * methods and generating corresponding path mapping information.
 *
 * @author Zxq
 */
public class JaxrsPathHandler {

	/**
	 * Set of annotation names related to JAX-RS path handling.
	 */
	private static final Set<String> ANNOTATION_NAMES = Collections.unmodifiableSet(Stream
		.of(JakartaJaxrsAnnotations.JAXB_DELETE_FULLY, JakartaJaxrsAnnotations.JAX_PUT_FULLY,
				JakartaJaxrsAnnotations.JAX_GET_FULLY, JakartaJaxrsAnnotations.JAX_POST_FULLY,
				JakartaJaxrsAnnotations.JAX_PATCH_FULLY, JakartaJaxrsAnnotations.JAX_HEAD_FULLY,
				JAXRSAnnotations.JAXB_DELETE_FULLY, JAXRSAnnotations.JAX_PUT_FULLY, JAXRSAnnotations.JAX_GET_FULLY,
				JAXRSAnnotations.JAX_POST_FULLY, JAXRSAnnotations.JAXB_PATCH_FULLY, JAXRSAnnotations.JAXB_HEAD_FULLY)
		.collect(Collectors.toSet()));

	/**
	 * Map of constants used in the project.
	 */
	private Map<String, String> constantsMap;

	/**
	 * Handles JAX-RS annotations on a given method and generates path mapping
	 * information.
	 * @param projectBuilder The project documentation configuration builder
	 * @param baseUrl The base URL
	 * @param method The Java method object
	 * @param mediaType The media type
	 * @return A JaxrsPathMapping object containing the processed path information
	 */
	public JaxrsPathMapping handle(ProjectDocConfigBuilder projectBuilder, String baseUrl, JavaMethod method,
			String mediaType) {

		List<JavaAnnotation> annotations = method.getAnnotations();
		this.constantsMap = projectBuilder.getConstantsMap();
		String methodType = null;
		String shortUrl = "";
		String serverUrl = projectBuilder.getServerUrl();
		String contextPath = projectBuilder.getApiConfig().getPathPrefix();
		boolean deprecated = false;
		for (JavaAnnotation annotation : annotations) {
			String annotationName = annotation.getType().getFullyQualifiedName();
			// method level annotation will override class level annotation
			if (annotationName.equals(JakartaJaxrsAnnotations.JAX_CONSUMES_FULLY)
					|| annotationName.equals(JAXRSAnnotations.JAX_CONSUMES_FULLY)) {
				Object value = annotation.getNamedParameter(DocAnnotationConstants.VALUE_PROP);
				if (Objects.nonNull(value)) {
					mediaType = MediaType.valueOf(value.toString());
				}
			}
			// Deprecated annotation on method
			if (JavaTypeConstants.JAVA_DEPRECATED_FULLY.equals(annotationName)) {
				deprecated = true;
			}
			if (JakartaJaxrsAnnotations.JAX_PATH_FULLY.equals(annotationName)
					|| JakartaJaxrsAnnotations.JAX_PATH_PARAM_FULLY.equals(annotationName)
					|| JakartaJaxrsAnnotations.JAXB_REST_PATH_FULLY.equals(annotationName)
					|| JAXRSAnnotations.JAX_PATH_FULLY.equals(annotationName)
					|| JAXRSAnnotations.JAX_PATH_PARAM_FULLY.equals(annotationName)) {
				ClassLoader classLoader = projectBuilder.getApiConfig().getClassLoader();
				shortUrl = DocUtil.handleMappingValue(classLoader, annotation);
			}
			// annotationName like "Get" "Post", not "jakarta.ws.rs.Get"
			// "jakarta.ws.rs.Post"
			if (ANNOTATION_NAMES.stream().anyMatch(it -> it.contains(annotationName))) {
				methodType = annotation.getType().getName();
			}
		}
		// @deprecated tag on method
		if (Objects.nonNull(method.getTagByName(DEPRECATED))) {
			deprecated = true;
		}
		JaxrsPathMapping jaxrsPathMapping = getJaxbPathMapping(projectBuilder, baseUrl, method, shortUrl, serverUrl,
				contextPath);
		if (jaxrsPathMapping != null) {
			return jaxrsPathMapping.setDeprecated(deprecated).setMethodType(methodType).setMediaType(mediaType);
		}
		return null;
	}

	/**
	 * Generates JAX-RS path mapping based on provided parameters.
	 * @param projectBuilder The project documentation configuration builder
	 * @param baseUrl The base URL
	 * @param method The Java method object
	 * @param shortUrl The short URL
	 * @param serverUrl The server URL
	 * @param contextPath The context path
	 * @return A JaxrsPathMapping object containing the processed path information
	 */
	private JaxrsPathMapping getJaxbPathMapping(ProjectDocConfigBuilder projectBuilder, String baseUrl,
			JavaMethod method, String shortUrl, String serverUrl, String contextPath) {
		String url;
		if (Objects.nonNull(shortUrl)) {
			if (Objects.nonNull(method.getTagByName(IGNORE))) {
				return null;
			}
			shortUrl = StringUtil.removeQuotes(shortUrl);
			List<String> urls = DocUtil.split(shortUrl);
			url = String.join(DocGlobalConstants.PATH_DELIMITER, serverUrl, contextPath, baseUrl, shortUrl);
			shortUrl = String.join(DocGlobalConstants.PATH_DELIMITER, DocGlobalConstants.PATH_DELIMITER, contextPath,
					baseUrl, shortUrl);
			if (urls.size() > 1) {
				url = DocUrlUtil.getMvcUrls(serverUrl, contextPath + DocGlobalConstants.PATH_DELIMITER + baseUrl, urls);
				shortUrl = DocUrlUtil.getMvcUrls(DocGlobalConstants.EMPTY,
						contextPath + DocGlobalConstants.PATH_DELIMITER + baseUrl, urls);
			}
			for (Map.Entry<String, String> entry : constantsMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (url.contains(key)) {
					url = url.replace(key, value).replace("+", "");
				}
				if (shortUrl.contains(key)) {
					shortUrl = shortUrl.replace(key, value).replace("+", "");
				}
			}
			String urlSuffix = projectBuilder.getApiConfig().getUrlSuffix();
			url = UrlUtil.simplifyUrl(url);
			shortUrl = UrlUtil.simplifyUrl(shortUrl);
			if (StringUtil.isNotEmpty(urlSuffix)) {
				url += urlSuffix;
				shortUrl += urlSuffix;
			}
			return JaxrsPathMapping.builder().setUrl(StringUtil.trim(url)).setShortUrl(StringUtil.trim(shortUrl));
		}
		return null;
	}

}