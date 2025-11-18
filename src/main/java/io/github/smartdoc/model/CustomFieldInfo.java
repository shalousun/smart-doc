/*
 * smart-doc https://github.com/smart-doc-group/smart-doc
 *
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
package io.github.smartdoc.model;

import java.io.Serializable;

/**
 * CustomFieldInfo
 *
 * @author linwumingshi
 * @since 3.0.9
 */
public class CustomFieldInfo implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7310122325722122250L;

	/**
	 * custom response field
	 */
	private CustomField customResponseField;

	/**
	 * custom request field
	 */
	private CustomField customRequestField;

	/**
	 * ignore
	 */
	private Boolean ignore;

	/**
	 * field name
	 */
	private String fieldName;

	/**
	 * field value
	 */
	private String fieldValue;

	/**
	 * is required
	 */
	private Boolean strRequired;

	/**
	 * comment
	 */
	private String comment;

	public CustomField getCustomResponseField() {
		return customResponseField;
	}

	public CustomFieldInfo setCustomResponseField(CustomField customResponseField) {
		this.customResponseField = customResponseField;
		return this;
	}

	public CustomField getCustomRequestField() {
		return customRequestField;
	}

	public CustomFieldInfo setCustomRequestField(CustomField customRequestField) {
		this.customRequestField = customRequestField;
		return this;
	}

	public Boolean getIgnore() {
		return ignore;
	}

	public CustomFieldInfo setIgnore(Boolean ignore) {
		this.ignore = ignore;
		return this;
	}

	public String getFieldName() {
		return fieldName;
	}

	public CustomFieldInfo setFieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public CustomFieldInfo setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
		return this;
	}

	public Boolean getStrRequired() {
		return strRequired;
	}

	public CustomFieldInfo setStrRequired(Boolean strRequired) {
		this.strRequired = strRequired;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public CustomFieldInfo setComment(String comment) {
		this.comment = comment;
		return this;
	}

}
