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
package io.github.smartdoc.builder.rpc;

import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.rpc.RpcApiDoc;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.List;

/**
 * RPC HTML builder
 *
 * @author yu 2020/5/17.
 * @since 1.8.7
 */
public class RpcHtmlBuilder {

	/**
	 * private constructor
	 */
	private RpcHtmlBuilder() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * build controller api
	 * @param config config
	 */
	public static void buildApiDoc(ApiConfig config) {
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		buildApiDoc(config, javaProjectBuilder);
	}

	/**
	 * Only for smart-doc maven plugin and gradle plugin.
	 * @param config ApiConfig
	 * @param javaProjectBuilder ProjectDocConfigBuilder
	 */
	public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		RpcDocBuilderTemplate builderTemplate = new RpcDocBuilderTemplate();
		List<RpcApiDoc> apiDocList = builderTemplate.getApiDoc(false, true, false, config, javaProjectBuilder);
		builderTemplate.copyJQueryAndCss(config);
		String INDEX_HTML = "rpc-index.html";
		builderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder,
				DocGlobalConstants.RPC_ALL_IN_ONE_HTML_TPL, INDEX_HTML);
		builderTemplate.buildSearchJs(apiDocList, config, javaProjectBuilder,
				DocGlobalConstants.RPC_ALL_IN_ONE_SEARCH_TPL, DocGlobalConstants.SEARCH_JS_OUT);
	}

}
