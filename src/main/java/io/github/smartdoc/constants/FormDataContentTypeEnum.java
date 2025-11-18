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

/**
 * Enum representing Content-Type values for form-data in POST requests.
 *
 * @see <a href=
 * "https://swagger.io/docs/specification/describing-request-body/multipart-requests/">
 * Specifying Content-Type</a>
 * @author linwumingshi
 * @since 3.0.7
 */
public enum FormDataContentTypeEnum {

	/**
	 * application/json: Complex values or arrays of complex values.
	 */
	APPLICATION_JSON("application/json"),

	/**
	 * text/plain: Primitive values or arrays of primitive values.
	 */
	TEXT_PLAIN("text/plain"),

	/**
	 * application/octet-stream: Binary or base64 encoded strings.
	 */
	APPLICATION_OCTET_STREAM("application/octet-stream");

	/**
	 * The Content-Type value.
	 */
	private final String value;

	/**
	 * Constructor to set the Content-Type value.
	 * @param value the Content-Type value
	 */
	FormDataContentTypeEnum(String value) {
		this.value = value;
	}

	/**
	 * Returns the Content-Type value.
	 * @return the Content-Type value
	 */
	public String getValue() {
		return value;
	}

}