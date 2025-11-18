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
 * spring mvc request annotation enum
 *
 * @author yu 2019/12/20.
 */
public enum SpringMvcRequestAnnotationsEnum {

	/**
	 * SpringMvc RequestAnnotation PathVariable
	 */
	PATH_VARIABLE("PathVariable"),

	/**
	 * SpringMvc RequestAnnotation PathVariable fully
	 */
	PATH_VARIABLE_FULLY("org.springframework.web.bind.annotation.PathVariable"),

	/**
	 * SpringMvc RequestAnnotation RequestParam
	 */
	REQ_PARAM("RequestParam"),

	/**
	 * SpringMvc RequestAnnotation RequestParam fully
	 */
	REQ_PARAM_FULLY("org.springframework.web.bind.annotation.RequestParam"),

	/**
	 * SpringMvc RequestAnnotation RequestBody
	 */
	REQUEST_BODY("RequestBody"),

	/**
	 * SpringMvc RequestAnnotation RequestBody fully
	 */
	REQUEST_BODY_FULLY("org.springframework.web.bind.annotation.RequestBody"),

	/**
	 * SpringMvc RequestAnnotation RequestHeader
	 */
	REQUEST_HERDER("RequestHeader"),

	/**
	 * SpringMvc RequestAnnotation RequestHeader fully
	 */
	REQUEST_HERDER_FULLY("org.springframework.web.bind.annotation.RequestHeader"),

	/**
	 * SpringMvc RequestAnnotation RequestPart
	 */
	REQUEST_PART("RequestPart"),

	/**
	 * SpringMvc RequestAnnotation RequestPart fully
	 */
	REQUEST_PART_FULLY("org.springframework.web.bind.annotation.RequestPart"),

	;

	/**
	 * SpringMvc RequestAnnotation value
	 */
	private final String value;

	/**
	 * SpringMvc RequestAnnotation constructor
	 * @param value SpringMvc RequestAnnotation value
	 */
	SpringMvcRequestAnnotationsEnum(String value) {
		this.value = value;
	}

	/**
	 * get SpringMvc RequestAnnotation list
	 * @return SpringMvc RequestAnnotation list
	 */
	public static List<String> listSpringMvcRequestAnnotations() {
		List<String> annotations = new ArrayList<>();
		for (SpringMvcRequestAnnotationsEnum annotation : SpringMvcRequestAnnotationsEnum.values()) {
			annotations.add(annotation.value);
		}
		return annotations;
	}

}
