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
import io.github.smartdoc.utils.DocClassUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Box Return Filter
 *
 * @author yu 2020/4/17.
 */
public class BoxReturnFilter implements ReturnTypeFilter {

	/**
	 * Box Return Type
	 */
	private static final Set<String> TYPE_SET = new HashSet<>();

	static {
		TYPE_SET.add("java.util.concurrent.Callable");
		TYPE_SET.add("java.util.concurrent.Future");
		TYPE_SET.add("java.util.concurrent.CompletableFuture");
		TYPE_SET.add("org.springframework.web.context.request.async.DeferredResult");
		TYPE_SET.add("org.springframework.web.context.request.async.WebAsyncTask");
		TYPE_SET.add("reactor.core.publisher.Mono");
		TYPE_SET.add("org.springframework.http.ResponseEntity");
	}

	@Override
	public ApiReturn doFilter(String fullyName) {
		if (TYPE_SET.stream().anyMatch(fullyName::startsWith)) {
			ApiReturn apiReturn = new ApiReturn();
			if (fullyName.contains("<")) {
				String[] strings = DocClassUtil.getSimpleGicName(fullyName);
				String newFullName = strings[0];
				if (newFullName.contains("<")) {
					apiReturn.setGenericCanonicalName(newFullName);
					apiReturn.setSimpleName(newFullName.substring(0, newFullName.indexOf("<")));
				}
				else {
					apiReturn.setGenericCanonicalName(newFullName);
					apiReturn.setSimpleName(newFullName);
				}
			}
			else {
				// directly return Java Object
				apiReturn.setGenericCanonicalName(JavaTypeConstants.JAVA_OBJECT_FULLY);
				apiReturn.setSimpleName(JavaTypeConstants.JAVA_OBJECT_FULLY);
			}
			return apiReturn;
		}
		return null;
	}

}
