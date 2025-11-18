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
import io.github.smartdoc.constants.MediaType;
import io.github.smartdoc.constants.Methods;
import io.github.smartdoc.factory.BuildTemplateFactory;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.ApiMethodDoc;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.ApiReqParam;
import io.github.smartdoc.model.FormData;
import io.github.smartdoc.model.postman.InfoBean;
import io.github.smartdoc.model.postman.ItemBean;
import io.github.smartdoc.model.postman.RequestItem;
import io.github.smartdoc.model.postman.UrlBean;
import io.github.smartdoc.model.postman.request.ParamBean;
import io.github.smartdoc.model.postman.request.RequestBean;
import io.github.smartdoc.model.postman.request.body.BodyBean;
import io.github.smartdoc.model.postman.request.header.HeaderBean;
import io.github.smartdoc.template.IDocBuildTemplate;
import io.github.smartdoc.utils.DocPathUtil;
import io.github.smartdoc.utils.JsonUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Postman Json Builder
 *
 * @author yu 2019/11/21.
 * @since 1.7.8
 */
public class PostmanJsonBuilder {

	/**
	 * Message
	 */
	private static final String MSG = "Interface name is not set.";

	/**
	 * private constructor
	 */
	private PostmanJsonBuilder() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * build postman json
	 * @param config Smart-doc ApiConfig
	 */
	public static void buildPostmanCollection(ApiConfig config) {
		DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
		builderTemplate.checkAndInit(config, Boolean.TRUE);
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
		postManCreate(config, configBuilder);
	}

	/**
	 * Only for smart-doc maven plugin and gradle plugin.
	 * @param config ApiConfig Object
	 * @param projectBuilder QDOX avaProjectBuilder
	 */
	public static void buildPostmanCollection(ApiConfig config, JavaProjectBuilder projectBuilder) {
		DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
		builderTemplate.checkAndInit(config, Boolean.TRUE);
		if (StringUtil.isNotEmpty(config.getServerEnv())) {
			config.setServerUrl(config.getServerEnv());
		}
		config.setParamsDataToTree(false);
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, projectBuilder);
		postManCreate(config, configBuilder);
	}

	/**
	 * Build the first layer of Postman Item
	 * @param apiDoc Documentation for each Controller
	 * @return First layer of Postman Item
	 */
	private static ItemBean buildItemBean(ApiDoc apiDoc) {
		ItemBean parentItemBean = new ItemBean();
		parentItemBean.setName(StringUtil.isEmpty(apiDoc.getDesc()) ? MSG : apiDoc.getDesc());
		List<ItemBean> childItemBeans = new ArrayList<>();

		if (CollectionUtil.isNotEmpty(apiDoc.getChildrenApiDocs())) {
			apiDoc.getChildrenApiDocs().forEach(childApiDoc -> {
				ItemBean childItemBean = buildItemBean(childApiDoc);
				childItemBeans.add(childItemBean);
			});
		}
		else {
			List<ApiMethodDoc> apiMethodDocs = apiDoc.getList();
			apiMethodDocs.forEach(apiMethodDoc -> {
				ItemBean methodItemBean = buildItem(apiMethodDoc);
				childItemBeans.add(methodItemBean);
			});
		}

		parentItemBean.setItem(childItemBeans);
		return parentItemBean;
	}

	/**
	 * Build the second layer of Postman item
	 * @param apiMethodDoc Documentation for each method
	 * @return The second layer of Postman item
	 */
	private static ItemBean buildItem(ApiMethodDoc apiMethodDoc) {
		ItemBean item = new ItemBean();
		RequestBean requestBean = new RequestBean();

		item.setName(StringUtil.isEmpty(apiMethodDoc.getDesc()) ? MSG : apiMethodDoc.getDesc());
		item.setDescription(apiMethodDoc.getDetail());

		requestBean.setDescription(apiMethodDoc.getDesc());
		requestBean.setMethod(apiMethodDoc.getType());
		requestBean.setHeader(buildHeaderBeanList(apiMethodDoc));

		requestBean.setBody(buildBodyBean(apiMethodDoc));
		requestBean.setUrl(buildUrlBean(apiMethodDoc));

		item.setRequest(requestBean);
		return item;

	}

	private static UrlBean buildUrlBean(ApiMethodDoc apiMethodDoc) {
		UrlBean urlBean = new UrlBean(apiMethodDoc.getServerUrl());
		String url = Optional.ofNullable(apiMethodDoc.getRequestExample().getUrl()).orElse(apiMethodDoc.getUrl());
		urlBean.setRaw(DocPathUtil.toPostmanPath(url));
		String shortUrl = DocPathUtil.toPostmanPath(apiMethodDoc.getPath());
		String[] paths;
		if (StringUtil.isNotEmpty(shortUrl)) {
			paths = shortUrl.split(DocGlobalConstants.PATH_DELIMITER);
		}
		else {
			paths = new String[0];
		}
		List<String> pathList = new ArrayList<>();
		String serverPath = CollectionUtil.isNotEmpty(urlBean.getPath()) ? urlBean.getPath().get(0) : "";
		// Add server path
		if (CollectionUtil.isNotEmpty(urlBean.getPath()) && StringUtil.isNotEmpty(shortUrl)
				&& !shortUrl.contains(serverPath)) {
			String[] serverPaths = serverPath.split(DocGlobalConstants.PATH_DELIMITER);
			pathList.addAll(Arrays.asList(serverPaths));
		}
		// Add mapping path
		for (String str : paths) {
			if (StringUtil.isNotEmpty(str)) {
				pathList.add(str);
			}
		}
		if (StringUtil.isNotEmpty(shortUrl) && shortUrl.endsWith(DocGlobalConstants.PATH_DELIMITER)) {
			pathList.add("");
		}

		urlBean.setPath(pathList);

		List<ParamBean> queryParams = new ArrayList<>();
		if (!apiMethodDoc.getType().equals(Methods.POST.getValue())
				|| apiMethodDoc.getContentType().contains(MediaType.APPLICATION_JSON)) {
			for (ApiParam apiParam : apiMethodDoc.getQueryParams()) {
				ParamBean queryParam = new ParamBean();
				queryParam.setDescription(apiParam.getDesc());
				queryParam.setKey(apiParam.getField());
				queryParam.setValue(apiParam.getValue());
				queryParams.add(queryParam);
			}
		}
		urlBean.setQuery(queryParams);

		List<ParamBean> variables = new ArrayList<>();
		for (ApiParam apiParam : apiMethodDoc.getPathParams()) {
			ParamBean queryParam = new ParamBean();
			queryParam.setDescription(apiParam.getDesc());
			queryParam.setKey(apiParam.getField());
			queryParam.setValue(apiParam.getValue());
			variables.add(queryParam);
		}
		urlBean.setVariable(variables);
		return urlBean;
	}

	/**
	 * Build payload
	 * @return Body payload
	 */
	private static BodyBean buildBodyBean(ApiMethodDoc apiMethodDoc) {
		BodyBean bodyBean;
		if (apiMethodDoc.getContentType().contains(MediaType.APPLICATION_JSON)) {
			// Json request
			bodyBean = new BodyBean(Boolean.FALSE);
			bodyBean.setMode(convertContentTypeToPostmanType(apiMethodDoc.getContentType()));
			if (apiMethodDoc.getRequestExample() != null) {
				bodyBean.setRaw(apiMethodDoc.getRequestExample().getJsonBody());
			}
		}
		else {
			if (apiMethodDoc.getType().equals(Methods.POST.getValue())) {
				// Formdata
				bodyBean = new BodyBean(Boolean.TRUE);
				bodyBean.setMode(convertContentTypeToPostmanType(apiMethodDoc.getContentType()));
				if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestExample().getFormDataList())) {
					bodyBean.setFormdata(apiMethodDoc.getRequestExample().getFormDataList());
				}
				else {
					// if method is post and formdata list size is 0, convert query param
					// to formdata
					List<ApiParam> queryParams = apiMethodDoc.getQueryParams();
					List<FormData> formDataList = new ArrayList<>(queryParams.size());
					for (ApiParam apiParam : queryParams) {
						FormData formData = new FormData();
						formData.setDescription(apiParam.getDesc());
						formData.setKey(apiParam.getField());
						formData.setType(apiParam.getType());
						formData.setValue(apiParam.getValue());
						formDataList.add(formData);
					}
					bodyBean.setFormdata(formDataList);
				}
			}
			else {
				bodyBean = new BodyBean(Boolean.FALSE);
			}
		}
		return bodyBean;
	}

	/**
	 * Build header
	 * @return List of header
	 */
	private static List<HeaderBean> buildHeaderBeanList(ApiMethodDoc apiMethodDoc) {
		List<HeaderBean> headerBeans = new ArrayList<>();
		List<ApiReqParam> headers = apiMethodDoc.getRequestHeaders();
		headers.forEach(apiReqHeader -> {
			HeaderBean headerBean = new HeaderBean();
			headerBean.setKey(apiReqHeader.getName());
			headerBean.setName(apiReqHeader.getName());
			headerBean.setValue(apiReqHeader.getValue());
			headerBean.setDisabled(!apiReqHeader.isRequired());
			headerBean.setDescription(apiReqHeader.getDesc());
			headerBeans.add(headerBean);
		});

		return headerBeans;
	}

	private static void postManCreate(ApiConfig config, ProjectDocConfigBuilder configBuilder) {
		IDocBuildTemplate<ApiDoc> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework(),
				config.getClassLoader());
		Objects.requireNonNull(docBuildTemplate, "doc build template is null");
		config.setShowJavaType(true);
		List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder).getApiDatas();
		apiDocList = docBuildTemplate.handleApiGroup(apiDocList, config);
		RequestItem requestItem = new RequestItem();
		requestItem.setInfo(new InfoBean(config.getProjectName()));
		List<ItemBean> itemBeans = new ArrayList<>();
		apiDocList.forEach(apiDoc -> {
			ItemBean itemBean = buildItemBean(apiDoc);
			itemBeans.add(itemBean);
		});
		requestItem.setItem(itemBeans);
		String filePath = config.getOutPath();
		filePath = filePath + DocGlobalConstants.POSTMAN_JSON;
		String data = JsonUtil.toPrettyJson(requestItem);
		FileUtil.nioWriteFile(data, filePath);
	}

	/**
	 * Converts the request Content-Type to its corresponding Postman request type.
	 * <p>
	 * Postman is a popular API testing tool that supports various request types. This
	 * method aims to map common Content-Types to the formats recognized by Postman,
	 * facilitating more accurate HTTP request simulations.
	 * @param contentType The MIME type of the data in the request, indicating how it
	 * should be processed.
	 * @return The Postman request type corresponding to the given Content-Type.
	 * <p>
	 * Note: This mapping covers typical use cases and Postman's supported range; it may
	 * not include all possible Content-Types.
	 */
	private static String convertContentTypeToPostmanType(String contentType) {
		switch (contentType) {
			case "application/json":
			case "application/xml":
				return "raw";
			case "multipart/form-data":
				return "formdata";
			case "application/x-www-form-urlencoded":
				return "x-www-form-urlencoded";
			default:
				return "none";
		}
	}

}
