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
package io.github.smartdoc.utils;

import io.github.smartdoc.constants.HttpStatusEnum;

/**
 * HttpStatusUtil
 *
 * @author yu.sun on 2024/6/9
 * @since 3.0.5
 */
public class HttpStatusUtil {

	/**
	 * private constructor
	 */
	private HttpStatusUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Retrieves the corresponding HTTP status code as a string based on the input status
	 * string. This method maps status strings from the HttpStatus enum to their numeric
	 * HTTP status code equivalents.
	 * @param status The status string from the HttpStatus enum
	 * @return The HTTP status code as a string
	 */
	public static String getStatusCode(String status) {
		try {
			HttpStatusEnum httpStatusEnum = HttpStatusEnum.valueOf(status);
			return String.valueOf(httpStatusEnum.value());
		}
		catch (IllegalArgumentException e) {
			return String.valueOf(HttpStatusEnum.INTERNAL_SERVER_ERROR.value());
		}
	}

	/**
	 * Retrieves the description of an HTTP status based on the input status code string.
	 * This method translates numeric HTTP status codes into human-readable descriptions.
	 * @param statusCode The HTTP status code as a string
	 * @return The description of the HTTP status
	 */
	public static String getStatusDescription(String statusCode) {
		try {
			HttpStatusEnum httpStatusEnum = HttpStatusEnum.valueOf(statusCode);
			return String.valueOf(httpStatusEnum.getReasonPhrase());
		}
		catch (IllegalArgumentException e) {
			return HttpStatusEnum.INTERNAL_SERVER_ERROR.getReasonPhrase();
		}
	}

}
