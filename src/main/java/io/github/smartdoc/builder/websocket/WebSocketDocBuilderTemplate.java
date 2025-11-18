/*
 *
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
package io.github.smartdoc.builder.websocket;

import io.github.smartdoc.builder.IBaseDocBuilderTemplate;
import io.github.smartdoc.builder.ProjectDocConfigBuilder;
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.HighlightStyle;
import io.github.smartdoc.constants.TemplateVariable;
import io.github.smartdoc.factory.BuildTemplateFactory;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDocDict;
import io.github.smartdoc.model.ApiErrorCode;
import io.github.smartdoc.model.WebSocketDoc;
import io.github.smartdoc.template.IWebSocketDocBuildTemplate;
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
 * WebSocket doc builder template.
 *
 * @author linwumingshi
 * @since 3.0.7
 */
public class WebSocketDocBuilderTemplate implements IBaseDocBuilderTemplate<WebSocketDoc> {

	/**
	 * get all websocket api data.
	 * @param isAsciidoc is Asciidoc
	 * @param config ApiConfig
	 * @param javaProjectBuilder JavaProjectBuilder
	 * @return ApiAllData
	 */
	public List<WebSocketDoc> getWebSocketApiDoc(boolean isAsciidoc, ApiConfig config,
			JavaProjectBuilder javaProjectBuilder) {
		config.setAdoc(isAsciidoc);
		this.checkAndInit(config, Boolean.TRUE);
		config.setParamsDataToTree(false);
		config
			.setOutPath(config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + DocGlobalConstants.WEBSOCKET_OUT_DIR);
		return this.getWebSocketApiDoc(config, javaProjectBuilder);

	}

	/**
	 * get all websocket api data.
	 * @param config ApiConfig
	 * @param javaProjectBuilder JavaProjectBuilder
	 * @return ApiAllData
	 */
	public List<WebSocketDoc> getWebSocketApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		config.setShowJavaType(false);
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
		IWebSocketDocBuildTemplate<WebSocketDoc> webSocketDocBuildTemplate = BuildTemplateFactory
			.getWebSocketDocBuildTemplate(config.getFramework(), config.getClassLoader());
		Objects.requireNonNull(webSocketDocBuildTemplate, "doc build websocket template is null");
		return webSocketDocBuildTemplate.getWebSocketData(configBuilder);
	}

	/**
	 * Merge all websocket api doc into one document.
	 * @param webSocketDocList list data of Api doc
	 * @param config api config
	 * @param javaProjectBuilder JavaProjectBuilder
	 * @param template template
	 * @param outPutFileName output file
	 */
	public void buildWebSocketAllInOne(List<WebSocketDoc> webSocketDocList, ApiConfig config,
			JavaProjectBuilder javaProjectBuilder, String template, String outPutFileName) {
		String outPath = config.getOutPath();
		FileUtil.mkdirs(outPath);
		Template tpl = BeetlTemplateUtil.getByName(template);
		String style = config.getStyle();
		tpl.binding(TemplateVariable.STYLE.getVariable(), style);
		tpl.binding(TemplateVariable.HIGH_LIGHT_CSS_LINK.getVariable(), config.getHighlightStyleLink());
		tpl.binding(TemplateVariable.BACKGROUND.getVariable(), HighlightStyle.getBackgroundColor(style));
		tpl.binding(TemplateVariable.WEBSOCKET_DOC_LIST.getVariable(), webSocketDocList);
		tpl.binding(TemplateVariable.LANGUAGE.getVariable(), config.getLanguage());
		this.setCssCDN(config, tpl);
		// binding common variable
		this.bindingCommonVariable(config, javaProjectBuilder, tpl, webSocketDocList.isEmpty());
		FileUtil.nioWriteFile(tpl.render(), outPath + DocGlobalConstants.FILE_SEPARATOR + outPutFileName);
	}

	/**
	 * Generate websocket api documentation for all controllers.
	 * @param apiDocList list of api doc
	 * @param config api config
	 * @param template template
	 * @param fileExtension file extension
	 */
	public void buildWebSocketApiDoc(List<WebSocketDoc> apiDocList, ApiConfig config, String template,
			String fileExtension) {
		FileUtil.mkdirs(config.getOutPath());
		for (WebSocketDoc doc : apiDocList) {
			Template mapper = this.buildWebSocketApiDocTemplate(doc, config, template);
			FileUtil.nioWriteFile(mapper.render(),
					config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + doc.getName() + fileExtension);
		}
	}

	/**
	 * build websocket api doc template
	 * @param webSocketDoc websocket api doc
	 * @param config api config
	 * @param template template
	 * @return Template
	 */
	public Template buildWebSocketApiDocTemplate(WebSocketDoc webSocketDoc, ApiConfig config, String template) {
		Template mapper = BeetlTemplateUtil.getByName(template);
		mapper.binding(TemplateVariable.DEPRECATED.getVariable(), webSocketDoc.getDeprecated());
		mapper.binding(TemplateVariable.DESC.getVariable(), webSocketDoc.getDesc());
		mapper.binding(TemplateVariable.URI.getVariable(), webSocketDoc.getUri());
		mapper.binding(TemplateVariable.AUTHOR.getVariable(), webSocketDoc.getAuthor());
		mapper.binding(TemplateVariable.SUB_PROTOCOLS.getVariable(), webSocketDoc.getSubProtocols());
		mapper.binding(TemplateVariable.WEBSOCKET_PATH_PARAMS.getVariable(), webSocketDoc.getPathParams());
		mapper.binding(TemplateVariable.WEBSOCKET_MESSAGE_PARAMS.getVariable(), webSocketDoc.getMessageParams());
		mapper.binding(TemplateVariable.WEBSOCKET_RESPONSE_PARAMS.getVariable(), webSocketDoc.getResponseParams());
		return mapper;
	}

	/**
	 * Build search js.
	 * @param apiDocList list data of Api doc
	 * @param config api config
	 * @param javaProjectBuilder projectBuilder
	 * @param template template
	 * @param outPutFileName output file
	 */
	public void buildSearchJs(List<WebSocketDoc> apiDocList, ApiConfig config, JavaProjectBuilder javaProjectBuilder,
			String template, String outPutFileName) {
		List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
		Template tpl = BeetlTemplateUtil.getByName(template);
		// add order
		List<WebSocketDoc> apiDocs = new ArrayList<>();
		for (WebSocketDoc apiDoc1 : apiDocList) {
			apiDoc1.setOrder(apiDocs.size() + 1);
			apiDocs.add(apiDoc1);
		}
		Map<String, String> titleMap = this.setDirectoryLanguageVariable(config, tpl);
		if (CollectionUtil.isNotEmpty(errorCodeList)) {
			WebSocketDoc apiDoc1 = new WebSocketDoc();
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

}
