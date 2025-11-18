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
package io.github.smartdoc.constants;

import com.power.common.util.StringUtil;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Smart-doc Supported Framework.
 *
 * @author yu 2021/6/27.
 */
public enum FrameworkEnum {

	/**
	 * Apache Dubbo
	 */
	DUBBO("dubbo", "io.github.smartdoc.template.RpcDocBuildTemplate"),

	/**
	 * Javadoc
	 */
	JAVADOC("javadoc", "io.github.smartdoc.template.JavadocDocBuildTemplate"),

	/**
	 * Spring Framework
	 */
	SPRING("spring", "io.github.smartdoc.template.SpringBootDocBuildTemplate"),

	/**
	 * Solon
	 */
	SOLON("solon", "io.github.smartdoc.template.SolonDocBuildTemplate"),

	/**
	 * JAX-RS
	 */
	JAX_RS("JAX-RS", "io.github.smartdoc.template.JAXRSDocBuildTemplate"),

	/**
	 * grpc
	 */
	GRPC("grpc", "io.github.smartdoc.template.GRpcDocBuildTemplate"),

	;

	/**
	 * Framework name
	 */
	private final String framework;

	/**
	 * Framework IDocBuildTemplate implement
	 */
	private final String className;

	/**
	 * Constructor
	 * @param framework framework name
	 * @param className class name
	 */
	FrameworkEnum(String framework, String className) {
		this.framework = framework;
		this.className = className;
	}

	/**
	 * Get class name by framework.
	 * @param framework framework name
	 * @return class name
	 */
	public static String getClassNameByFramework(String framework) {
		String className = "";
		if (StringUtil.isEmpty(framework)) {
			return className;
		}
		for (FrameworkEnum frameworkEnum : FrameworkEnum.values()) {
			if (frameworkEnum.framework.equalsIgnoreCase(framework)) {
				className = frameworkEnum.className;
				break;
			}
		}
		return className;
	}

	/**
	 * Get all supported frameworks.
	 * @return all supported frameworks
	 */
	public static String allFramework() {
		return Arrays.stream(FrameworkEnum.values()).map(FrameworkEnum::getFramework).collect(Collectors.joining(","));
	}

	/**
	 * Get framework name.
	 * @return framework name
	 */
	public String getFramework() {
		return framework;
	}

	/**
	 * Get class name.
	 * @return class name
	 */
	public String getClassName() {
		return className;
	}

}
