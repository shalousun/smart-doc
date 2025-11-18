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
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.function.RequestMappingFunc;
import io.github.smartdoc.model.annotation.FrameworkAnnotations;
import io.github.smartdoc.model.request.RequestMapping;
import io.github.smartdoc.utils.DocUrlUtil;
import io.github.smartdoc.utils.DocUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RequestMapping Handler Interface Responsible for handling and formatting controller
 * request mapping information.
 *
 * @author yu3.sun on 2022/10/1
 */
public interface IRequestMappingHandler {

	/**
	 * Formats the request mapping data. Generates and formats the URL and short URL for
	 * the given request mapping object.
	 * @param projectBuilder Project documentation configuration builder containing
	 * project-related configurations
	 * @param controllerBaseUrl Base URL of the controller
	 * @param requestMapping RequestMapping object to be formatted
	 * @return Formatted RequestMapping object
	 */
	default RequestMapping formatMappingData(ProjectDocConfigBuilder projectBuilder, String controllerBaseUrl,
			RequestMapping requestMapping) {
		String shortUrl = requestMapping.getShortUrl();
		if (Objects.nonNull(shortUrl)) {
			String serverUrl = projectBuilder.getServerUrl();
			String contextPath = projectBuilder.getApiConfig().getPathPrefix();
			shortUrl = StringUtil.removeQuotes(shortUrl);
			String url = DocUrlUtil.getMvcUrls(serverUrl,
					contextPath + DocGlobalConstants.PATH_DELIMITER + controllerBaseUrl, shortUrl);
			shortUrl = DocUrlUtil.getMvcUrls(DocGlobalConstants.EMPTY,
					contextPath + DocGlobalConstants.PATH_DELIMITER + controllerBaseUrl, shortUrl);
			String urlSuffix = projectBuilder.getApiConfig().getUrlSuffix();
			if (StringUtil.isEmpty(urlSuffix)) {
				urlSuffix = StringUtil.EMPTY;
			}
			url = UrlUtil.simplifyUrl(StringUtil.trim(url)) + urlSuffix;
			shortUrl = UrlUtil.simplifyUrl(StringUtil.trim(shortUrl)) + urlSuffix;
			url = DocUtil.formatPathUrl(url);
			shortUrl = DocUtil.formatPathUrl(shortUrl);
			requestMapping.setUrl(url).setShortUrl(shortUrl);
			return requestMapping;
		}
		return requestMapping;
	}

	/**
	 * Retrieves all annotations from a method, including those inherited from interfaces.
	 * @param method The JavaMethod object for which annotations are to be retrieved
	 * @return A list of JavaAnnotation objects representing the annotations on the method
	 */
	default List<JavaAnnotation> getAnnotations(JavaMethod method) {
		List<JavaAnnotation> annotations = new ArrayList<>();
		// Add interface method annotations
		List<JavaClass> interfaces = method.getDeclaringClass().getInterfaces();
		if (CollectionUtil.isNotEmpty(interfaces)) {
			for (JavaClass interfaceClass : interfaces) {
				JavaMethod interfaceMethod = interfaceClass.getMethod(method.getName(), method.getParameterTypes(),
						method.isVarArgs());
				if (interfaceMethod != null) {
					// Can be overridden by implement class
					annotations.addAll(interfaceMethod.getAnnotations());
				}
			}
		}
		annotations.addAll(method.getAnnotations());
		return annotations;
	}

	/**
	 * Handles the request mapping for a given method.
	 * @param projectBuilder Project documentation configuration builder
	 * @param controllerBaseUrl Base URL of the controller
	 * @param method The JavaMethod object representing the method to handle
	 * @param frameworkAnnotations Framework annotations related to the method
	 * @param requestMappingFunc Function to process request mappings
	 * @return The processed RequestMapping object
	 */
	RequestMapping handle(ProjectDocConfigBuilder projectBuilder, String controllerBaseUrl, JavaMethod method,
			FrameworkAnnotations frameworkAnnotations, RequestMappingFunc requestMappingFunc);

}
