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

import io.github.smartdoc.constants.FormDataContentTypeEnum;

/**
 * form data class
 *
 * @author xingzi 2019/12/21 20:20
 */
public class FormData {

	/**
	 * key
	 */
	private String key;

	/**
	 * type
	 */
	private String type;

	/**
	 * description
	 */
	private String description;

	/**
	 * source
	 */
	private Object src;

	/**
	 * value
	 */
	private String value;

	/**
	 * contentType eg: `application/json`,when the param has annotation `@RequestPart`
	 * @see FormDataContentTypeEnum
	 */
	private String contentType;

	/**
	 * openapi items
	 */
	private boolean hasItems;

	public boolean isHasItems() {
		return hasItems;
	}

	public void setHasItems(boolean hasItems) {
		this.hasItems = hasItems;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getSrc() {
		return src;
	}

	public void setSrc(Object src) {
		this.src = src;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(FormDataContentTypeEnum contentType) {
		this.contentType = contentType.getValue();
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public String toString() {
		return "FormData{" + "key='" + key + '\'' + ", type='" + type + '\'' + ", description='" + description + '\''
				+ ", src=" + src + ", value='" + value + '\'' + ", hasItems=" + hasItems + '}';
	}

}
