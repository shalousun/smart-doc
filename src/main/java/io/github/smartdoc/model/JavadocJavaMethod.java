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
package io.github.smartdoc.model;

import io.github.smartdoc.utils.ParamUtil;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;

import java.util.List;
import java.util.Map;

/**
 * java method.
 *
 * @author chenchuxin
 * @since 3.0.5
 */
public class JavadocJavaMethod implements IMethod {

	/**
	 * java method
	 */
	private JavaMethod javaMethod;

	/**
	 * methodId handled by md5
	 */
	private String methodId;

	/**
	 * method name
	 */
	private String name;

	/**
	 * method order
	 */
	private int order;

	/**
	 * method description
	 */
	private String desc;

	/**
	 * method definition
	 */
	private String methodDefinition;

	/**
	 * escape method definition
	 */
	private String escapeMethodDefinition;

	/**
	 * detailed introduction of the method
	 */
	private String detail;

	/**
	 * method describe
	 */
	private String throwsInfo;

	/**
	 * return class Info
	 */
	private String returnClassInfo;

	/**
	 * http request params
	 */
	private List<ApiParam> requestParams;

	/**
	 * http request author
	 */
	private String author;

	/**
	 * http response params
	 */
	private List<ApiParam> responseParams;

	/**
	 * method deprecated
	 */
	private boolean deprecated;

	private Map<String, JavaType> actualTypesMap;

	private String version;

	public String getVersion() {
		return version;
	}

	public JavadocJavaMethod setVersion(String version) {
		this.version = version;
		return this;
	}

	public static JavadocJavaMethod builder() {
		return new JavadocJavaMethod();
	}

	public JavaMethod getJavaMethod() {
		return javaMethod;
	}

	public JavadocJavaMethod setJavaMethod(JavaMethod javaMethod) {
		this.javaMethod = javaMethod;
		return this;
	}

	public String getMethodId() {
		return methodId;
	}

	public JavadocJavaMethod setMethodId(String methodId) {
		this.methodId = methodId;
		return this;
	}

	public String getName() {
		return name;
	}

	public JavadocJavaMethod setName(String name) {
		this.name = name;
		return this;
	}

	public int getOrder() {
		return order;
	}

	public JavadocJavaMethod setOrder(int order) {
		this.order = order;
		return this;
	}

	public String getDesc() {
		return desc;
	}

	public JavadocJavaMethod setDesc(String desc) {
		this.desc = desc;
		return this;
	}

	public String getDetail() {
		return detail;
	}

	public JavadocJavaMethod setDetail(String detail) {
		this.detail = detail;
		return this;
	}

	public String getThrowsInfo() {
		return throwsInfo;
	}

	public JavadocJavaMethod setThrowsInfo(String throwsInfo) {
		this.throwsInfo = throwsInfo;
		return this;
	}

	public String getReturnClassInfo() {
		return returnClassInfo;
	}

	public JavadocJavaMethod setReturnClassInfo(String returnClassInfo) {
		this.returnClassInfo = returnClassInfo;
		return this;
	}

	public String getAuthor() {
		return author;
	}

	public JavadocJavaMethod setAuthor(String author) {
		this.author = author;
		return this;
	}

	public List<ApiParam> getResponseParams() {
		return responseParams;
	}

	public JavadocJavaMethod setResponseParams(List<ApiParam> responseParams) {
		this.responseParams = responseParams;
		return this;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public JavadocJavaMethod setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
		return this;
	}

	public List<ApiParam> getRequestParams() {
		return requestParams;
	}

	public JavadocJavaMethod setRequestParams(List<ApiParam> requestParams) {
		this.requestParams = requestParams;
		return this;
	}

	public String getMethodDefinition() {
		return methodDefinition;
	}

	public JavadocJavaMethod setMethodDefinition(String methodDefinition) {
		this.methodDefinition = methodDefinition;
		return this;
	}

	public String getEscapeMethodDefinition() {
		return escapeMethodDefinition;
	}

	public JavadocJavaMethod setEscapeMethodDefinition(String escapeMethodDefinition) {
		this.escapeMethodDefinition = escapeMethodDefinition;
		return this;
	}

	public Map<String, JavaType> getActualTypesMap() {
		return actualTypesMap;
	}

	public JavadocJavaMethod setActualTypesMap(Map<String, JavaType> actualTypesMap) {
		this.actualTypesMap = actualTypesMap;
		return this;
	}

	@Override
	public JavaClass getDeclaringClass() {
		return this.javaMethod.getDeclaringClass();
	}

	@Override
	public String getMethodName() {
		return this.name;
	}

	@Override
	public List<String> getArgsClasses() {
		return ParamUtil.extractQualifiedName(this.requestParams);
	}

	@Override
	public List<String> getReturnClasses() {
		return ParamUtil.extractQualifiedName(this.responseParams);
	}

}
