/*
 * Copyright (C) 2018-2025 smart-doc
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

package io.github.smartdoc.model.openapi;

import io.github.smartdoc.constants.OpenApiTagNameTypeEnum;
import io.github.smartdoc.model.ApiDoc;

import java.util.Objects;

/**
 * open api tag
 *
 * @author <a href="mailto:cqmike0315@gmail.com">chenqi</a>
 * @version 1.0
 */
public class OpenApiTag {

	/**
	 * the tag name
	 */
	private String name;

	/**
	 * the tag description
	 */
	private String description;

	public OpenApiTag() {
	}

	public OpenApiTag(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * create open api tag
	 * @param name tag name
	 * @param description tag description
	 * @return OpenApiTag
	 */
	public static OpenApiTag of(String name, String description) {
		return new OpenApiTag(name, description);
	}

	/**
	 * create open api tag
	 * @param openApiTagNameType open api tag name type
	 * @param apiDoc api doc
	 * @return OpenApiTag
	 */
	public static OpenApiTag of(OpenApiTagNameTypeEnum openApiTagNameType, ApiDoc apiDoc) {
		if (Objects.isNull(openApiTagNameType)) {
			return of(apiDoc.getName(), apiDoc.getDesc());
		}
		switch (openApiTagNameType) {
			case DESCRIPTION:
				return new OpenApiTag(apiDoc.getDesc(), apiDoc.getDesc());
			case PACKAGE_NAME:
				return new OpenApiTag(apiDoc.getPackageName(), apiDoc.getDesc());
			default:
				return of(apiDoc.getName(), apiDoc.getDesc());
		}
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		OpenApiTag that = (OpenApiTag) o;
		return Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getDescription());
	}

}
