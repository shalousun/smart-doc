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

import java.util.Arrays;
import java.util.List;

/**
 * @author yu3.sun on 2022/10/1
 */
public class MappingAnnotation {

	private String annotationName;

	private String annotationFullyName;

	private List<String> pathProps;

	private String producesProp;

	private String consumesProp;

	private String methodProp;

	private String methodType;

	private String paramsProp;

	private List<String> scope;

	public static MappingAnnotation builder() {
		return new MappingAnnotation();
	}

	public String getAnnotationName() {
		return annotationName;
	}

	public MappingAnnotation setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
		return this;
	}

	public List<String> getPathProps() {
		return pathProps;
	}

	public MappingAnnotation setPathProps(String... pathProp) {
		this.pathProps = Arrays.asList(pathProp);
		return this;
	}

	public String getAnnotationFullyName() {
		return annotationFullyName;
	}

	public MappingAnnotation setAnnotationFullyName(String annotationFullyName) {
		this.annotationFullyName = annotationFullyName;
		return this;
	}

	public String getConsumesProp() {
		return consumesProp;
	}

	public MappingAnnotation setConsumesProp(String consumesProp) {
		this.consumesProp = consumesProp;
		return this;
	}

	public String getProducesProp() {
		return producesProp;
	}

	public MappingAnnotation setProducesProp(String producesProp) {
		this.producesProp = producesProp;
		return this;
	}

	public String getMethodProp() {
		return methodProp;
	}

	public MappingAnnotation setMethodProp(String methodProp) {
		this.methodProp = methodProp;
		return this;
	}

	public String getMethodType() {
		return methodType;
	}

	public MappingAnnotation setMethodType(String methodType) {
		this.methodType = methodType;
		return this;
	}

	public String getParamsProp() {
		return paramsProp;
	}

	public MappingAnnotation setParamsProp(String paramsProp) {
		this.paramsProp = paramsProp;
		return this;
	}

	public List<String> getScope() {
		return scope;
	}

	public MappingAnnotation setScope(String... scope) {
		this.scope = Arrays.asList(scope);
		return this;
	}

}
