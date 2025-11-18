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

import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.factory.BuildTemplateFactory;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.template.IDocBuildTemplate;
import com.power.common.util.DateTimeUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.List;
import java.util.Objects;

/**
 * use to create Markdown doc
 *
 * @author yu 2019/09/20
 */
public class ApiDocBuilder {

	/**
	 * private constructor
	 */
	private ApiDocBuilder() {
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
	 * @param config ApiConfig
	 * @param javaProjectBuilder ProjectDocConfigBuilder
	 */
	public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
		builderTemplate.checkAndInit(config, Boolean.TRUE);
		config.setAdoc(false);
		config.setParamsDataToTree(false);
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
		IDocBuildTemplate<ApiDoc> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework(),
				config.getClassLoader());
		Objects.requireNonNull(docBuildTemplate, "doc build template is null");
		List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder).getApiDatas();
		if (config.isAllInOne()) {
			String version = config.isCoverOld() ? "" : "-V" + DateTimeUtil.long2Str(System.currentTimeMillis(),
					DocGlobalConstants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
			String docName = builderTemplate.allInOneDocName(config,
					"AllInOne" + version + DocGlobalConstants.MARKDOWN_EXTENSION,
					DocGlobalConstants.MARKDOWN_EXTENSION);
			apiDocList = docBuildTemplate.handleApiGroup(apiDocList, config);
			builderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder, DocGlobalConstants.ALL_IN_ONE_MD_TPL,
					docName);
		}
		else {
			builderTemplate.buildApiDoc(apiDocList, config, DocGlobalConstants.API_DOC_MD_TPL,
					DocGlobalConstants.MARKDOWN_API_FILE_EXTENSION);
			builderTemplate.buildErrorCodeDoc(config, DocGlobalConstants.ERROR_CODE_LIST_MD_TPL,
					DocGlobalConstants.ERROR_CODE_LIST_MD, javaProjectBuilder);
			builderTemplate.buildDirectoryDataDoc(config, javaProjectBuilder, DocGlobalConstants.DICT_LIST_MD_TPL,
					DocGlobalConstants.DICT_LIST_MD);
		}
	}

}
