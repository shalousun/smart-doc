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

import java.util.ArrayList;
import java.util.List;

/**
 * Solon Request Annotation Enum
 *
 * @author yu 2019/12/20.
 */
public enum SolonRequestAnnotationsEnum {

	/**
	 * Solon Request Annotation PathVar
	 */
	PATH_VAR("Path"),

	/**
	 * Solon Request Annotation PathVar full
	 */
	PATH_VAR_FULLY("org.noear.solon.annotation.Path"),

	/**
	 * Solon Request Annotation Param
	 */
	REQ_PARAM("Param"),

	/**
	 * Solon Request Annotation Param full
	 */
	REQ_PARAM_FULLY("org.noear.solon.annotation.Param"),

	/**
	 * Solon Request Annotation Body
	 */
	REQUEST_BODY("Body"),

	/**
	 * Solon Request Annotation Body full
	 */
	REQUEST_BODY_FULLY("org.noear.solon.annotation.Body"),

	/**
	 * Solon Request Annotation Header
	 */
	REQUEST_HERDER("Header"),

	/**
	 * Solon Request Annotation Header full
	 */
	REQUEST_HERDER_FULLY("org.noear.solon.annotation.Header"),;

	/**
	 * Solon Request Annotation value
	 */
	private final String value;

	SolonRequestAnnotationsEnum(String value) {
		this.value = value;
	}

	public static List<String> listMvcRequestAnnotations() {
		List<String> annotations = new ArrayList<>();
		for (SolonRequestAnnotationsEnum annotation : SolonRequestAnnotationsEnum.values()) {
			annotations.add(annotation.value);
		}
		return annotations;
	}

}
