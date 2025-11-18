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

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author yu.sun on 2024/6/9
 * @since 3.0.5
 */
public class ApiExceptionStatus implements Comparable<ApiExceptionStatus> {

	private String status;

	private String author;

	private String desc;

	private String detail;

	private String responseUsage;

	/**
	 * http exception response params
	 */
	private List<ApiParam> exceptionResponseParams;

	public static ApiExceptionStatus of() {
		return new ApiExceptionStatus();
	}

	public String getStatus() {
		return status;
	}

	public ApiExceptionStatus setStatus(String status) {
		this.status = status;
		return this;
	}

	public List<ApiParam> getExceptionResponseParams() {
		return exceptionResponseParams;
	}

	public ApiExceptionStatus setExceptionResponseParams(List<ApiParam> exceptionResponseParams) {
		this.exceptionResponseParams = exceptionResponseParams;
		return this;
	}

	public String getAuthor() {
		return author;
	}

	public ApiExceptionStatus setAuthor(String author) {
		this.author = author;
		return this;
	}

	public String getDesc() {
		return desc;
	}

	public ApiExceptionStatus setDesc(String desc) {
		this.desc = desc;
		return this;
	}

	public String getDetail() {
		return detail;
	}

	public ApiExceptionStatus setDetail(String detail) {
		this.detail = detail;
		return this;
	}

	public String getResponseUsage() {
		return responseUsage;
	}

	public ApiExceptionStatus setResponseUsage(String responseUsage) {
		this.responseUsage = responseUsage;
		return this;
	}

	@Override
	public int compareTo(@NotNull ApiExceptionStatus o) {
		if (Objects.nonNull(o.getDesc())) {
			return status.compareTo(o.status);
		}
		return 0;
	}

}
