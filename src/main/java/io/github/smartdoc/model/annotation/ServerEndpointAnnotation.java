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

/**
 * Spring `javax.websocket.server.ServerEndpoint` info
 *
 * @author linwumingshi
 * @since 3.0.2
 */
public class ServerEndpointAnnotation {

	/**
	 * the name of annotation
	 */
	private String annotationName;

	/**
	 * the fullyName of annotation
	 */
	private String annotationFullyName;

	/**
	 * builder ServerEndpointAnnotation
	 * @return ServerEndpointAnnotation
	 */
	public static ServerEndpointAnnotation builder() {
		return new ServerEndpointAnnotation();
	}

	public String getAnnotationName() {
		return annotationName;
	}

	public String getAnnotationFullyName() {
		return annotationFullyName;
	}

	public ServerEndpointAnnotation setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
		return this;
	}

	public ServerEndpointAnnotation setAnnotationFullyName(String annotationFullyName) {
		this.annotationFullyName = annotationFullyName;
		return this;
	}

}
