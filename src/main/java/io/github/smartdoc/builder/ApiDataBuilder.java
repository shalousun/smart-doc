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
package io.github.smartdoc.builder;

import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiAllData;
import io.github.smartdoc.model.ApiConfig;
import com.thoughtworks.qdox.JavaProjectBuilder;

/**
 * Build Api Data
 *
 * @author yu 2019/12/7.
 * @since 1.7.9
 */
public class ApiDataBuilder {

	/**
	 * private constructor
	 */
	private ApiDataBuilder() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Get list of ApiDoc
	 * @param config ApiConfig
	 * @return List of ApiDoc
	 */
	public static ApiAllData getApiData(ApiConfig config) {
		return getApiData(config, Boolean.FALSE);
	}

	/**
	 * Get list of ApiDoc
	 * @param config ApiConfig
	 * @return List of ApiDoc
	 */
	public static ApiAllData getApiDataTree(ApiConfig config) {
		return getApiData(config, Boolean.TRUE);
	}

	/**
	 * Retrieves API data based on the given configuration.
	 * @param config The API configuration object containing request parameters and data
	 * source information.
	 * @param toTree A flag indicating whether to convert the parameter data into a tree
	 * structure.
	 * @return An ApiAllData object containing all the API data information.
	 */
	private static ApiAllData getApiData(ApiConfig config, boolean toTree) {
		config.setParamsDataToTree(toTree);
		DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
		builderTemplate.checkAndInitForGetApiData(config);
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		return builderTemplate.getApiData(config, javaProjectBuilder);
	}

}
