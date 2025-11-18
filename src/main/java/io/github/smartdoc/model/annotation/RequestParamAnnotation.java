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
 * @author yu3.sun on 2022/10/1
 */
public class RequestParamAnnotation {

	private String annotationName;

	private String annotationFullyName;

	private String defaultValueProp;

	private String requiredProp;

	public static RequestParamAnnotation builder() {
		return new RequestParamAnnotation();
	}

	public String getAnnotationName() {
		return annotationName;
	}

	public RequestParamAnnotation setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
		return this;
	}

	public String getAnnotationFullyName() {
		return annotationFullyName;
	}

	public RequestParamAnnotation setAnnotationFullyName(String annotationFullyName) {
		this.annotationFullyName = annotationFullyName;
		return this;
	}

	public String getDefaultValueProp() {
		return defaultValueProp;
	}

	public RequestParamAnnotation setDefaultValueProp(String defaultValueProp) {
		this.defaultValueProp = defaultValueProp;
		return this;
	}

	public String getRequiredProp() {
		return requiredProp;
	}

	public RequestParamAnnotation setRequiredProp(String requiredProp) {
		this.requiredProp = requiredProp;
		return this;
	}

}
