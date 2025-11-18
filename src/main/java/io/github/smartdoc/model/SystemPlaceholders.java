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

import org.apache.commons.lang3.StringUtils;

/**
 * @author xingzi Date 2022/9/25 14:52
 */
public class SystemPlaceholders {

	public static final String PLACEHOLDER_PREFIX = "${";

	/**
	 * Suffix for system property placeholders: "}".
	 */
	public static final String PLACEHOLDER_SUFFIX = "}";

	/**
	 * Value separator for system property placeholders: ":".
	 */
	public static final String VALUE_SEPARATOR = ":";

	public static final String SIMPLE_PREFIX = "{";

	private SystemPlaceholders() {

	}

	public static boolean hasSystemProperties(String url) {
		return !StringUtils.isBlank(url) && url.contains(PLACEHOLDER_PREFIX) && url.contains(PLACEHOLDER_SUFFIX)
				&& url.contains(VALUE_SEPARATOR);
	}

	public static String replaceSystemProperties(String url) {
		return null;
	}

}
