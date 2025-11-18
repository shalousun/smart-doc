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

package io.github.smartdoc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Api Schema
 *
 * @author yu.sun on 2024/6/9
 * @since 3.0.5
 */
public class ApiSchema<T extends IDoc> implements Serializable {

	private static final long serialVersionUID = -8712793142951321786L;

	List<T> apiDatas;

	/**
	 * http exception response status
	 */
	private List<ApiExceptionStatus> apiExceptionStatuses;

	public List<T> getApiDatas() {
		if (apiDatas == null) {
			apiDatas = new ArrayList<>();
		}
		return apiDatas;
	}

	public void setApiDatas(List<T> apiDatas) {
		this.apiDatas = apiDatas;
	}

	public List<ApiExceptionStatus> getApiExceptionStatuses() {
		if (apiExceptionStatuses == null) {
			apiExceptionStatuses = new ArrayList<>();
		}
		return apiExceptionStatuses;
	}

	public void setApiExceptionStatuses(List<ApiExceptionStatus> apiExceptionStatuses) {
		this.apiExceptionStatuses = apiExceptionStatuses;
	}

}
