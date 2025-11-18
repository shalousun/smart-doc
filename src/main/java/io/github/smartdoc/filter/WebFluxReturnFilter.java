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
package io.github.smartdoc.filter;

import io.github.smartdoc.constants.JavaTypeConstants;
import io.github.smartdoc.model.ApiReturn;

/**
 * WebFlux Return Filter
 *
 * @author yu 2020/4/17.
 */
public class WebFluxReturnFilter implements ReturnTypeFilter {

	/**
	 * Flux
	 */
	private static final String FLUX = "reactor.core.publisher.Flux";

	@Override
	public ApiReturn doFilter(String fullyName) {
		// support web flux
		if (fullyName.startsWith(FLUX)) {
			ApiReturn apiReturn = new ApiReturn();
			// rewrite type name
			fullyName = fullyName.replace(FLUX, JavaTypeConstants.JAVA_LIST_FULLY);
			apiReturn.setGenericCanonicalName(fullyName);
			apiReturn.setSimpleName(JavaTypeConstants.JAVA_LIST_FULLY);
			return apiReturn;
		}
		return null;
	}

}
