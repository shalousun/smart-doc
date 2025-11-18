/*
 * Copyright (C) 2018-2025 smart-doc
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
 * Enum representing the possible sources for generating tag names in OpenAPI
 * specifications. Each enum value defines a strategy for determining the value of
 * `tags[].name` in the OpenAPI document.
 *
 * <p>
 * Supported strategies include:
 * <ul>
 * <li>Using the class name of the controller</li>
 * <li>Using the controller's description</li>
 * <li>Using the controller's package name</li>
 * </ul>
 *
 * @author Lin
 * @version 3.1.1
 */
public enum OpenApiTagNameTypeEnum {

	/**
	 * Use the simple class name of the controller as the tag name.
	 * <p>
	 * This is the default strategy. The tag name will be derived from the controller
	 * class name without its package prefix.
	 * </p>
	 * <p>
	 * Example: For a controller class named `UserController`, the tag name will be
	 * `UserController`.
	 * </p>
	 */
	CLASS_NAME,

	/**
	 * Use the controller's description as the tag name.
	 * <p>
	 * The description is typically defined in the controller's documentation (e.g., via
	 * Javadoc or configuration). This allows for more descriptive and user-friendly tag
	 * names.
	 * </p>
	 * <p>
	 * Example: If the controller has a description `User Management API`, the tag name
	 * will be `User Management API`.
	 * </p>
	 */
	DESCRIPTION,

	/**
	 * Use the full package name of the controller as the tag name.
	 * <p>
	 * This strategy is useful when you want to organize tags by package structure,
	 * especially in large projects.
	 * </p>
	 * <p>
	 * Example: For a controller in package `com.example.controller.user`, the tag name
	 * will be `com.example.controller.user`.
	 * </p>
	 */
	PACKAGE_NAME;

}