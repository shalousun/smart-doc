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
package io.github.smartdoc.model.javadoc;

import io.github.smartdoc.model.ApiDocDict;
import io.github.smartdoc.model.ApiErrorCode;
import io.github.smartdoc.model.RevisionLog;

import java.util.List;

public class JavadocApiAllData {

	/**
	 * project name
	 */
	private String projectName;

	/**
	 * project id
	 */
	private String projectId;

	/**
	 * docLanguage
	 */
	private String language;

	/**
	 * doc list
	 */
	private List<JavadocApiDoc> apiDocList;

	/**
	 *
	 */
	private List<ApiDocDict> apiDocDictList;

	/**
	 * error code list
	 */
	private List<ApiErrorCode> errorCodeList;

	/**
	 * List of change log
	 */
	private List<RevisionLog> revisionLogs;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<JavadocApiDoc> getApiDocList() {
		return apiDocList;
	}

	public void setApiDocList(List<JavadocApiDoc> apiDocList) {
		this.apiDocList = apiDocList;
	}

	public List<ApiDocDict> getApiDocDictList() {
		return apiDocDictList;
	}

	public void setApiDocDictList(List<ApiDocDict> apiDocDictList) {
		this.apiDocDictList = apiDocDictList;
	}

	public List<ApiErrorCode> getErrorCodeList() {
		return errorCodeList;
	}

	public void setErrorCodeList(List<ApiErrorCode> errorCodeList) {
		this.errorCodeList = errorCodeList;
	}

	public List<RevisionLog> getRevisionLogs() {
		return revisionLogs;
	}

	public void setRevisionLogs(List<RevisionLog> revisionLogs) {
		this.revisionLogs = revisionLogs;
	}

}
