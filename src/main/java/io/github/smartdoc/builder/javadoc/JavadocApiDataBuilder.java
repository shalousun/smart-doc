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
package io.github.smartdoc.builder.javadoc;

import io.github.smartdoc.constants.FrameworkEnum;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.javadoc.JavadocApiAllData;
import com.thoughtworks.qdox.JavaProjectBuilder;

/**
 * Javadoc Api Data Builder
 *
 * @author chenchuxin
 * @since 3.0.5
 */
public class JavadocApiDataBuilder {

	/**
	 * private constructor
	 */
	private JavadocApiDataBuilder() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Get list of ApiDoc
	 * @param config JavadocApiAllData
	 * @return List of ApiDoc
	 */
	public static JavadocApiAllData getApiData(ApiConfig config) {
		config.setShowJavaType(true);
		config.setFramework(FrameworkEnum.JAVADOC.getFramework());
		JavadocDocBuilderTemplate builderTemplate = new JavadocDocBuilderTemplate();
		builderTemplate.checkAndInitForGetApiData(config);
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		builderTemplate.getApiData(config, javaProjectBuilder);
		return builderTemplate.getApiData(config, javaProjectBuilder);
	}

}
