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

import io.github.smartdoc.constants.TornaConstants;
import io.github.smartdoc.factory.BuildTemplateFactory;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.ApiSchema;
import io.github.smartdoc.model.torna.Apis;
import io.github.smartdoc.model.torna.TornaApi;
import io.github.smartdoc.template.IDocBuildTemplate;
import io.github.smartdoc.utils.TornaUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.smartdoc.constants.TornaConstants.DEFAULT_GROUP_CODE;

/**
 * Rest Api Torna Builder
 *
 * @author xingzi 2021/2/2 18:05
 **/
public class TornaBuilder {

	/**
	 * private constructor
	 */
	private TornaBuilder() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * build controller api,for unit testing
	 * @param config config
	 */
	public static void buildApiDoc(ApiConfig config) {
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		buildApiDoc(config, javaProjectBuilder);
	}

	/**
	 * Only for smart-doc Maven plugin and Gradle plugin.
	 * @param config ApiConfig
	 * @param javaProjectBuilder JavaProjectBuilder
	 */
	public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		config.setParamsDataToTree(true);
		List<ApiDoc> apiDocList = generateApiDocs(config, javaProjectBuilder);
		buildTorna(apiDocList, config, javaProjectBuilder);
	}

	/**
	 * build torna Data
	 * @param apiDocs apiData
	 * @param apiConfig ApiConfig
	 * @param builder JavaProjectBuilder
	 */
	public static void buildTorna(List<ApiDoc> apiDocs, ApiConfig apiConfig, JavaProjectBuilder builder) {
		// Convert ApiDoc to TornaApi
		TornaApi tornaApi = convertToTornaApi(apiDocs, apiConfig, builder);
		// Push to torna
		TornaUtil.pushToTorna(tornaApi, apiConfig, builder);
	}

	/**
	 * Generate Torna API.
	 * @param config ApiConfig
	 * @return TornaApi
	 */
	public static TornaApi getTornaApi(ApiConfig config) {
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		List<ApiDoc> apiDocs = generateApiDocs(config, javaProjectBuilder);
		return convertToTornaApi(apiDocs, config, javaProjectBuilder);
	}

	/**
	 * Convert List of ApiDoc to TornaApi
	 * @param apiDocs apiData
	 * @param apiConfig ApiConfig
	 * @param builder JavaProjectBuilder
	 * @return TornaApi
	 */
	private static TornaApi convertToTornaApi(List<ApiDoc> apiDocs, ApiConfig apiConfig, JavaProjectBuilder builder) {
		TornaApi tornaApi = new TornaApi();
		tornaApi.setAuthor(apiConfig.getAuthor());
		tornaApi.setIsReplace(BooleanUtils.toInteger(apiConfig.getReplace()));
		Apis api;
		List<Apis> groupApiList = new ArrayList<>();
		// Convert ApiDoc to Apis
		for (ApiDoc groupApi : apiDocs) {
			List<Apis> apisList = new ArrayList<>();
			List<ApiDoc> childrenApiDocs = groupApi.getChildrenApiDocs();
			for (ApiDoc a : childrenApiDocs) {
				api = new Apis();
				api.setName(StringUtils.isBlank(a.getDesc()) ? a.getName() : a.getDesc());
				api.setItems(TornaUtil.buildApis(a.getList(), TornaUtil.setDebugEnv(apiConfig, tornaApi)));
				api.setIsFolder(TornaConstants.YES);
				api.setAuthor(a.getAuthor());
				api.setDescription(a.getDetail());
				api.setOrderIndex(a.getOrder());
				apisList.add(api);
			}
			api = new Apis();
			api.setName(StringUtils.isBlank(groupApi.getDesc()) ? groupApi.getName() : groupApi.getDesc());
			api.setDescription(groupApi.getDetail());
			api.setAuthor(tornaApi.getAuthor());
			api.setOrderIndex(groupApi.getOrder());
			api.setIsFolder(TornaConstants.YES);
			api.setItems(apisList);
			groupApiList.add(api);

		}
		tornaApi.setCommonErrorCodes(TornaUtil.buildErrorCode(apiConfig, builder));
		// delete default group when only default group
		tornaApi.setApis(groupApiList.size() == 1 && DEFAULT_GROUP_CODE.equals(groupApiList.get(0).getName())
				? groupApiList.get(0).getItems() : groupApiList);
		return tornaApi;
	}

	/**
	 * Generate API docs using the provided configuration and builder.
	 * @param config ApiConfig
	 * @param javaProjectBuilder JavaProjectBuilder
	 * @return List of ApiDoc
	 */
	private static List<ApiDoc> generateApiDocs(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
		builderTemplate.checkAndInit(config, Boolean.FALSE);
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
		IDocBuildTemplate<ApiDoc> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework(),
				config.getClassLoader());
		Objects.requireNonNull(docBuildTemplate, "doc build template is null");
		ApiSchema<ApiDoc> apiSchema = docBuildTemplate.getApiData(configBuilder);
		return docBuildTemplate.handleApiGroup(apiSchema.getApiDatas(), config);
	}

}
