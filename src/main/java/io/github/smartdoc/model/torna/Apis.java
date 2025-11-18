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

package io.github.smartdoc.model.torna;

import io.github.smartdoc.model.ApiMethodDoc;
import io.github.smartdoc.constants.TornaConstants;

import java.io.Serializable;
import java.util.List;

/**
 * Torna Api DocItem(cn.torna.sdk.param.DocItem)
 *
 * @author xingzi 2021/2/8 10:07
 * @since 2.0.9
 **/
public class Apis implements Serializable {

	private static final long serialVersionUID = 603355518657235962L;

	/**
	 * api doc name
	 */
	private String name;

	/**
	 * api doc description
	 */
	private String description;

	/**
	 * api doc url
	 */
	private String url;

	/**
	 * http method
	 */
	private String httpMethod;

	/**
	 * content type
	 */
	private String contentType;

	/**
	 * is folder
	 * @see TornaConstants#YES
	 * @see TornaConstants#NO
	 */
	private String isFolder;

	/**
	 * parent id
	 */
	private String parentId;

	/**
	 * is show
	 * @see TornaConstants#YES
	 * @see TornaConstants#NO
	 */
	private String isShow;

	/**
	 * author
	 */
	private String author;

	/**
	 * order index
	 */
	private Integer orderIndex;

	/**
	 * dubbo info
	 */
	private DubboInfo dubboInfo;

	/**
	 * header params
	 * @see ApiMethodDoc#getRequestHeaders()
	 */
	private List<HttpParam> headerParams;

	/**
	 * path params
	 * @see ApiMethodDoc#getPathParams()
	 */
	private List<HttpParam> pathParams;

	/**
	 * query params
	 * @see ApiMethodDoc#getQueryParams()
	 */
	private List<HttpParam> queryParams;

	/**
	 * request params; body params
	 * @see ApiMethodDoc#getRequestParams()
	 */
	private List<HttpParam> requestParams;

	/**
	 * response params
	 * @see ApiMethodDoc#getResponseParams()
	 */
	private List<HttpParam> responseParams;

	/**
	 * error code params
	 */
	private String errorCodeParams;

	/**
	 * items
	 */
	private List<Apis> items;

	/**
	 * is request array
	 */
	private Integer isRequestArray;

	/**
	 * request array type object/number/string/boolean
	 */
	private String requestArrayType;

	/**
	 * is response array
	 */
	private Integer isResponseArray;

	/**
	 * response array type object/number/string/boolean
	 */
	private String responseArrayType;

	/**
	 * deprecated
	 */
	private String deprecated;

	/**
	 * version
	 */
	private String version;

	/**
	 * dubboMethod, such as:com.xx.User com.xxx.UserService.findById(java.lang.Long id)
	 */
	private String dubboMethod;

	public String getVersion() {
		return version;
	}

	public Apis setVersion(String version) {
		this.version = version;
		return this;
	}

	public Integer getIsRequestArray() {
		return isRequestArray;
	}

	public void setIsRequestArray(Integer isRequestArray) {
		this.isRequestArray = isRequestArray;
	}

	public String getRequestArrayType() {
		return requestArrayType;
	}

	public void setRequestArrayType(String requestArrayType) {
		this.requestArrayType = requestArrayType;
	}

	public Integer getIsResponseArray() {
		return isResponseArray;
	}

	public void setIsResponseArray(Integer isResponseArray) {
		this.isResponseArray = isResponseArray;
	}

	public String getResponseArrayType() {
		return responseArrayType;
	}

	public void setResponseArrayType(String responseArrayType) {
		this.responseArrayType = responseArrayType;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getIsFolder() {
		return isFolder;
	}

	public void setIsFolder(String isFolder) {
		this.isFolder = isFolder;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	public List<HttpParam> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(List<HttpParam> queryParams) {
		this.queryParams = queryParams;
	}

	public List<HttpParam> getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(List<HttpParam> requestParams) {
		this.requestParams = requestParams;
	}

	public List<HttpParam> getHeaderParams() {
		return headerParams;
	}

	public void setHeaderParams(List<HttpParam> headerParams) {
		this.headerParams = headerParams;
	}

	public List<HttpParam> getPathParams() {
		return pathParams;
	}

	public Apis setPathParams(List<HttpParam> pathParams) {
		this.pathParams = pathParams;
		return this;
	}

	public List<HttpParam> getResponseParams() {
		return responseParams;
	}

	public void setResponseParams(List<HttpParam> responseParams) {
		this.responseParams = responseParams;
	}

	public String getErrorCodeParams() {
		return errorCodeParams;
	}

	public void setErrorCodeParams(String errorCodeParams) {
		this.errorCodeParams = errorCodeParams;
	}

	public List<Apis> getItems() {
		return items;
	}

	public void setItems(List<Apis> items) {
		this.items = items;
	}

	public DubboInfo getDubboInfo() {
		return dubboInfo;
	}

	public void setDubboInfo(DubboInfo dubboInfo) {
		this.dubboInfo = dubboInfo;
	}

	public String getDeprecated() {
		return deprecated;
	}

	public void setDeprecated(String deprecated) {
		this.deprecated = deprecated;
	}

	public String getDubboMethod() {
		return dubboMethod;
	}

	public void setDubboMethod(String dubboMethod) {
		this.dubboMethod = dubboMethod;
	}

}
