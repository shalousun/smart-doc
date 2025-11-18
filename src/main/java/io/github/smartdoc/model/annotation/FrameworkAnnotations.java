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
package io.github.smartdoc.model.annotation;

import java.util.Map;

/**
 * A model class representing various framework annotations. Provides getter and setter
 * methods to access and modify the annotations.
 * <p>
 * This class includes annotations for entry points, headers, mappings, path variables,
 * request parameters, request bodies, request parts, exception advice, and server
 * endpoints.
 * </p>
 *
 * @author yu3.sun on 2022/10/1
 */
public class FrameworkAnnotations {

	/**
	 * Map of entry annotations.
	 */
	private Map<String, EntryAnnotation> entryAnnotations;

	/**
	 * Header annotation.
	 */
	private HeaderAnnotation headerAnnotation;

	/**
	 * Map of mapping annotations.
	 */
	private Map<String, MappingAnnotation> mappingAnnotations;

	/**
	 * Path variable annotation.
	 */
	private PathVariableAnnotation pathVariableAnnotation;

	/**
	 * Request parameter annotation.
	 */
	private RequestParamAnnotation requestParamAnnotation;

	/**
	 * Request body annotation.
	 */
	private RequestBodyAnnotation requestBodyAnnotation;

	/**
	 * Request part annotation.
	 */
	private RequestPartAnnotation requestPartAnnotation;

	/**
	 * Map of exception advice annotations.
	 */
	private Map<String, ExceptionAdviceAnnotation> exceptionAdviceAnnotations;

	/**
	 * WebSocket server endpoint annotation.
	 * <p>
	 * javax.websocket.server.ServerEndpoint jakarta.websocket.server.ServerEndpoint
	 * </p>
	 */
	private ServerEndpointAnnotation serverEndpointAnnotation;

	public static FrameworkAnnotations builder() {
		return new FrameworkAnnotations();
	}

	public Map<String, EntryAnnotation> getEntryAnnotations() {
		return entryAnnotations;
	}

	public FrameworkAnnotations setEntryAnnotations(Map<String, EntryAnnotation> entryAnnotations) {
		this.entryAnnotations = entryAnnotations;
		return this;
	}

	public HeaderAnnotation getHeaderAnnotation() {
		return headerAnnotation;
	}

	public FrameworkAnnotations setHeaderAnnotation(HeaderAnnotation headerAnnotation) {
		this.headerAnnotation = headerAnnotation;
		return this;
	}

	public Map<String, MappingAnnotation> getMappingAnnotations() {
		return mappingAnnotations;
	}

	public FrameworkAnnotations setMappingAnnotations(Map<String, MappingAnnotation> mappingAnnotation) {
		this.mappingAnnotations = mappingAnnotation;
		return this;
	}

	public PathVariableAnnotation getPathVariableAnnotation() {
		return pathVariableAnnotation;
	}

	public FrameworkAnnotations setPathVariableAnnotation(PathVariableAnnotation pathVariableAnnotation) {
		this.pathVariableAnnotation = pathVariableAnnotation;
		return this;
	}

	public RequestParamAnnotation getRequestParamAnnotation() {
		return requestParamAnnotation;
	}

	public FrameworkAnnotations setRequestParamAnnotation(RequestParamAnnotation requestParamAnnotation) {
		this.requestParamAnnotation = requestParamAnnotation;
		return this;
	}

	public RequestBodyAnnotation getRequestBodyAnnotation() {
		return requestBodyAnnotation;
	}

	public FrameworkAnnotations setRequestBodyAnnotation(RequestBodyAnnotation requestBodyAnnotation) {
		this.requestBodyAnnotation = requestBodyAnnotation;
		return this;
	}

	public RequestPartAnnotation getRequestPartAnnotation() {
		return requestPartAnnotation;
	}

	public FrameworkAnnotations setRequestPartAnnotation(RequestPartAnnotation requestPartAnnotation) {
		this.requestPartAnnotation = requestPartAnnotation;
		return this;
	}

	public ServerEndpointAnnotation getServerEndpointAnnotation() {
		return serverEndpointAnnotation;
	}

	public FrameworkAnnotations setServerEndpointAnnotation(ServerEndpointAnnotation serverEndpointAnnotation) {
		this.serverEndpointAnnotation = serverEndpointAnnotation;
		return this;
	}

	public Map<String, ExceptionAdviceAnnotation> getExceptionAdviceAnnotations() {
		return exceptionAdviceAnnotations;
	}

	public FrameworkAnnotations setExceptionAdviceAnnotations(
			Map<String, ExceptionAdviceAnnotation> exceptionAdviceAnnotations) {
		this.exceptionAdviceAnnotations = exceptionAdviceAnnotations;
		return this;
	}

}
