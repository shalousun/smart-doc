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
import com.power.common.util.DateTimeUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.List;

/**
 * @author yu 2020/5/16.
 */
public class RpcMarkdownBuilder {

	/**
	 * private constructor
	 */
	private RpcMarkdownBuilder() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * @param config ApiConfig
	 */
	public static void buildApiDoc(ApiConfig config) {
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		buildApiDoc(config, javaProjectBuilder);
	}

	/**
	 * Only for smart-doc maven plugin and gradle plugin.
	 * @param apiConfig ApiConfig
	 * @param javaProjectBuilder ProjectDocConfigBuilder
	 */
	public static void buildApiDoc(ApiConfig apiConfig, JavaProjectBuilder javaProjectBuilder) {
		RpcDocBuilderTemplate builderTemplate = new RpcDocBuilderTemplate();
		List<RpcApiDoc> apiDocList = builderTemplate.getApiDoc(false, true, false, apiConfig, javaProjectBuilder);
		if (apiConfig.isAllInOne()) {
			String version = apiConfig.isCoverOld() ? "" : "-V" + DateTimeUtil.long2Str(System.currentTimeMillis(),
					DocGlobalConstants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
			String docName = builderTemplate.allInOneDocName(apiConfig, "rpc-all" + version,
					DocGlobalConstants.MARKDOWN_EXTENSION);
			builderTemplate.buildAllInOne(apiDocList, apiConfig, javaProjectBuilder,
					DocGlobalConstants.RPC_ALL_IN_ONE_MD_TPL, docName);
		}
		else {
			builderTemplate.buildApiDoc(apiDocList, apiConfig, DocGlobalConstants.RPC_API_DOC_MD_TPL,
					DocGlobalConstants.MARKDOWN_API_FILE_EXTENSION);
			builderTemplate.buildErrorCodeDoc(apiConfig, DocGlobalConstants.ERROR_CODE_LIST_MD_TPL,
					DocGlobalConstants.ERROR_CODE_LIST_MD, javaProjectBuilder);
		}
	}

}
