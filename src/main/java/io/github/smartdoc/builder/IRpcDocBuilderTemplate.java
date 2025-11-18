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
import io.github.smartdoc.constants.TemplateVariable;
import io.github.smartdoc.constants.TornaConstants;
import io.github.smartdoc.factory.BuildTemplateFactory;
import io.github.smartdoc.model.AbstractRpcApiDoc;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDocDict;
import io.github.smartdoc.model.ApiErrorCode;
import io.github.smartdoc.model.rpc.RpcApiAllData;
import io.github.smartdoc.model.rpc.RpcApiDoc;
import io.github.smartdoc.template.IDocBuildTemplate;
import io.github.smartdoc.utils.BeetlTemplateUtil;
import io.github.smartdoc.utils.DocUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.FileUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.beetl.core.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * rpc api doc builder template.
 *
 * @param <T> AbstractRpcApiDoc
 * @author linwumingshi
 * @since 3.0.7
 */
public interface IRpcDocBuilderTemplate<T extends AbstractRpcApiDoc<?>> extends IBaseDocBuilderTemplate<T> {

	/**
	 * Add dependency title
	 */
	String DEPENDENCY_TITLE = "Add dependency";

	/**
	 * Create empty api doc.
	 * @return empty api doc
	 */
	T createEmptyApiDoc();

	/**
	 * Generate api documentation for all controllers.
	 * @param apiDocList list of api doc
	 * @param config api config
	 * @param template template
	 * @param fileExtension file extension
	 */
	default void buildApiDoc(List<T> apiDocList, ApiConfig config, String template, String fileExtension) {
		FileUtil.mkdirs(config.getOutPath());
		for (T rpcDoc : apiDocList) {
			Template mapper = BeetlTemplateUtil.getByName(template);
			mapper.binding(TemplateVariable.DESC.getVariable(), rpcDoc.getDesc());
			mapper.binding(TemplateVariable.NAME.getVariable(), rpcDoc.getName());
			mapper.binding(TemplateVariable.LIST.getVariable(), rpcDoc.getList());
			mapper.binding(TemplateVariable.AUTHOR.getVariable(), rpcDoc.getAuthor());
			mapper.binding(TemplateVariable.PROTOCOL.getVariable(), rpcDoc.getProtocol());
			mapper.binding(TemplateVariable.VERSION.getVariable(), rpcDoc.getVersion());
			mapper.binding(TemplateVariable.URI.getVariable(), rpcDoc.getUri());
			this.writeApiDocFile(mapper, config, rpcDoc, fileExtension);
		}
	}

	/**
	 * Write rpc api doc file.
	 * @param mapper template
	 * @param config api config
	 * @param rpcDoc api doc
	 * @param fileExtension file extension
	 */
	default void writeApiDocFile(Template mapper, ApiConfig config, T rpcDoc, String fileExtension) {
		FileUtil.nioWriteFile(mapper.render(),
				config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + rpcDoc.getShortName() + fileExtension);
	}

	/**
	 * Merge all api doc into one document.
	 * @param apiDocList list data of Api doc
	 * @param config api config
	 * @param javaProjectBuilder JavaProjectBuilder
	 * @param template template
	 * @param outPutFileName output file
	 */
	default void buildAllInOne(List<T> apiDocList, ApiConfig config, JavaProjectBuilder javaProjectBuilder,
			String template, String outPutFileName) {
		String outPath = config.getOutPath();
		String rpcConfig = config.getRpcConsumerConfig();
		String rpcConfigConfigContent = null;
		if (Objects.nonNull(rpcConfig)) {
			rpcConfigConfigContent = FileUtil.getFileContent(rpcConfig);
		}
		FileUtil.mkdirs(outPath);
		Template tpl = BeetlTemplateUtil.getByName(template);
		tpl.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
		tpl.binding(TemplateVariable.DEPENDENCY_LIST.getVariable(), config.getRpcApiDependencies());
		tpl.binding(TemplateVariable.RPC_CONSUMER_CONFIG.getVariable(), rpcConfigConfigContent);
		// binding common variable
		this.bindingCommonVariable(config, javaProjectBuilder, tpl, apiDocList.isEmpty());
		FileUtil.nioWriteFile(tpl.render(), outPath + DocGlobalConstants.FILE_SEPARATOR + outPutFileName);
	}

	/**
	 * Merge all api doc into one document.
	 * @param apiDocList list data of Api doc
	 * @param config api config
	 * @param javaProjectBuilder JavaProjectBuilder
	 * @param template template
	 * @param outPutFileName output file
	 * @return beetl Template
	 */
	default Template buildAllInOneWord(List<RpcApiDoc> apiDocList, ApiConfig config,
			JavaProjectBuilder javaProjectBuilder, String template, String outPutFileName) {
		String outPath = config.getOutPath();
		String rpcConfig = config.getRpcConsumerConfig();
		String rpcConfigConfigContent = null;
		if (Objects.nonNull(rpcConfig)) {
			rpcConfigConfigContent = FileUtil.getFileContent(rpcConfig);
		}
		FileUtil.mkdirs(outPath);
		Template tpl = BeetlTemplateUtil.getByName(template);
		tpl.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
		tpl.binding(TemplateVariable.DEPENDENCY_LIST.getVariable(), config.getRpcApiDependencies());
		tpl.binding(TemplateVariable.RPC_CONSUMER_CONFIG.getVariable(), rpcConfigConfigContent);
		// binding common variable
		this.bindingCommonVariable(config, javaProjectBuilder, tpl, apiDocList.isEmpty());
		this.setDirectoryLanguageVariable(config, tpl);
		boolean onlyHasDefaultGroup = apiDocList.stream()
			.allMatch(doc -> Objects.equals(TornaConstants.DEFAULT_GROUP_CODE, doc.getGroup()));

		tpl.binding(TemplateVariable.API_DOC_LIST_ONLY_HAS_DEFAULT_GROUP.getVariable(), onlyHasDefaultGroup);
		return tpl;
	}

	/**
	 * Build search js.
	 * @param apiDocList list data of Api doc
	 * @param config api config
	 * @param javaProjectBuilder projectBuilder
	 * @param template template
	 * @param outPutFileName output file
	 */
	default void buildSearchJs(List<T> apiDocList, ApiConfig config, JavaProjectBuilder javaProjectBuilder,
			String template, String outPutFileName) {
		List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
		Template tpl = BeetlTemplateUtil.getByName(template);
		// directory tree
		List<T> apiDocs = new ArrayList<>();
		T apiDoc = this.createEmptyApiDoc();
		apiDoc.setAlias(DEPENDENCY_TITLE);
		apiDoc.setOrder(1);
		apiDoc.setDesc(DEPENDENCY_TITLE);
		apiDoc.setList(new ArrayList<>(0));
		apiDocs.add(apiDoc);
		for (T apiDoc1 : apiDocList) {
			apiDoc1.setOrder(apiDocs.size() + 1);
			apiDocs.add(apiDoc1);
		}
		Map<String, String> titleMap = this.setDirectoryLanguageVariable(config, tpl);
		if (CollectionUtil.isNotEmpty(errorCodeList)) {
			T apiDoc1 = this.createEmptyApiDoc();
			apiDoc1.setOrder(apiDocs.size() + 1);
			apiDoc1.setDesc(titleMap.get(TemplateVariable.ERROR_LIST_TITLE.getVariable()));
			apiDoc1.setList(new ArrayList<>(0));
			apiDocs.add(apiDoc1);
		}

		// set dict list
		List<ApiDocDict> apiDocDictList = DocUtil.buildDictionary(config, javaProjectBuilder);
		tpl.binding(TemplateVariable.DICT_LIST.getVariable(), apiDocDictList);
		tpl.binding(TemplateVariable.DIRECTORY_TREE.getVariable(), apiDocs);
		FileUtil.nioWriteFile(tpl.render(), config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + outPutFileName);
	}

	/**
	 * get all api data
	 * @param config ApiConfig
	 * @param javaProjectBuilder JavaProjectBuilder
	 * @return ApiAllData
	 */
	default RpcApiAllData<T> getApiData(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		RpcApiAllData<T> apiAllData = new RpcApiAllData<>();
		apiAllData.setProjectId(DocUtil.generateId(config.getProjectName()));
		apiAllData.setLanguage(config.getLanguage().getCode());
		apiAllData.setProjectName(config.getProjectName());
		apiAllData.setApiDocList(this.listOfApiData(config, javaProjectBuilder));
		apiAllData.setErrorCodeList(DocUtil.errorCodeDictToList(config, javaProjectBuilder));
		apiAllData.setRevisionLogs(config.getRevisionLogs());
		apiAllData.setApiDocDictList(DocUtil.buildDictionary(config, javaProjectBuilder));
		apiAllData.setDependencyList(config.getRpcApiDependencies());
		return apiAllData;
	}

	/**
	 * get all api data.
	 * @param config ApiConfig
	 * @param javaProjectBuilder JavaProjectBuilder
	 * @return ApiAllData
	 */
	default List<T> listOfApiData(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		this.checkAndInitForGetApiData(config);
		config.setMd5EncryptedHtmlName(true);
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
		IDocBuildTemplate<T> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework(),
				config.getClassLoader());
		Objects.requireNonNull(docBuildTemplate, "doc build template is null");
		return docBuildTemplate.getApiData(configBuilder).getApiDatas();
	}

}
