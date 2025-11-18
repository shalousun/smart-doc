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

import io.github.smartdoc.model.torna.EnumInfo;
import io.github.smartdoc.model.torna.EnumInfoAndValues;
import com.power.common.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.github.smartdoc.constants.DocGlobalConstants.PARAM_PREFIX;

/**
 * Api Parameter
 *
 * @author yu 2019/9/27.
 * @since 1.7.2
 */
public class ApiParam implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -714676579813604423L;

	/**
	 * param class name
	 */
	private String className;

	/**
	 * field id
	 */
	private int id = 1;

	/**
	 * field
	 */
	private String field;

	/**
	 * field type
	 */
	private String type;

	/**
	 * The format of the parameter.
	 */
	private String format;

	/**
	 * genericFullyQualifiedName of type name
	 */
	private String fullyTypeName;

	/**
	 * description
	 */
	private String desc;

	/**
	 * require flag
	 */
	private boolean required;

	/**
	 * version
	 */
	private String version;

	/**
	 * field pid
	 */
	private int pid;

	/**
	 * PathVariableParams flag
	 */
	private boolean pathParam;

	/**
	 * query params flag
	 */
	private boolean queryParam;

	/**
	 * param mock value
	 */
	private String value;

	/**
	 * children params
	 */
	private List<ApiParam> children;

	/**
	 * openapi items
	 */
	private boolean hasItems;

	/**
	 * enum values<br>
	 * Use in openapi api document
	 */
	private List<String> enumValues;

	/**
	 * enum Info<br>
	 * Use in torna api document
	 */
	private EnumInfo enumInfo;

	/**
	 * Valid @Max
	 */
	private String maxLength;

	/**
	 * is config.json config param default false
	 */
	private boolean configParam;

	/**
	 * Self Reference loop
	 */
	private boolean selfReferenceLoop;

	private Map<String, Object> extensions;

	public static ApiParam of() {
		return new ApiParam();
	}

	public EnumInfo getEnumInfo() {
		return enumInfo;
	}

	public ApiParam setEnumInfo(EnumInfo enumInfo) {
		this.enumInfo = enumInfo;
		return this;
	}

	public String getField() {
		return field;
	}

	public ApiParam setField(String field) {
		this.field = field;
		return this;
	}

	public String getSourceField() {
		if (StringUtils.isEmpty(field)) {
			return StringUtils.EMPTY;
		}
		return field.replaceAll(PARAM_PREFIX, "").replaceAll("&nbsp;", "");
	}

	public String getType() {
		return type;
	}

	public ApiParam setType(String type) {
		this.type = type;
		return this;
	}

	public String getDesc() {
		return desc;
	}

	public ApiParam setDesc(String desc) {
		this.desc = desc;
		return this;
	}

	public boolean isRequired() {
		return required;
	}

	public ApiParam setRequired(boolean required) {
		this.required = required;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public ApiParam setVersion(String version) {
		this.version = version;
		return this;
	}

	public int getId() {
		return id;
	}

	public ApiParam setId(int id) {
		this.id = id;
		return this;
	}

	public int getPid() {
		return pid;
	}

	public ApiParam setPid(int pid) {
		this.pid = pid;
		return this;
	}

	public List<ApiParam> getChildren() {
		return children;
	}

	public ApiParam setChildren(List<ApiParam> children) {
		this.children = children;
		return this;
	}

	public boolean isPathParam() {
		return pathParam;
	}

	public ApiParam setPathParam(boolean pathParam) {
		this.pathParam = pathParam;
		return this;
	}

	public boolean isQueryParam() {
		return queryParam;
	}

	public ApiParam setQueryParam(boolean queryParam) {
		this.queryParam = queryParam;
		return this;
	}

	public ApiParam setQueryParamTrue() {
		this.queryParam = true;
		return this;
	}

	public String getValue() {
		return value;
	}

	public ApiParam setValue(String value) {
		this.value = value;
		return this;
	}

	public boolean isHasItems() {
		return hasItems;
	}

	public ApiParam setHasItems(boolean hasItems) {
		this.hasItems = hasItems;
		return this;
	}

	public List<String> getEnumValues() {
		return enumValues;
	}

	public ApiParam setEnumValues(List<String> enumValues) {
		this.enumValues = enumValues;
		return this;
	}

	public String getMaxLength() {
		return maxLength;
	}

	public ApiParam setMaxLength(String maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public boolean isConfigParam() {
		return configParam;
	}

	public ApiParam setConfigParam(boolean configParam) {
		this.configParam = configParam;
		return this;
	}

	public String getClassName() {
		return className;
	}

	public ApiParam setClassName(String className) {
		this.className = className;
		return this;
	}

	public boolean isSelfReferenceLoop() {
		return selfReferenceLoop;
	}

	public ApiParam setSelfReferenceLoop(boolean selfReferenceLoop) {
		this.selfReferenceLoop = selfReferenceLoop;
		return this;
	}

	public String getFullyTypeName() {
		if (Objects.isNull(fullyTypeName)) {
			return type;
		}
		return fullyTypeName;
	}

	public ApiParam setFullyTypeName(String fullyTypeName) {
		this.fullyTypeName = fullyTypeName;
		return this;
	}

	public Map<String, Object> getExtensions() {
		return extensions;
	}

	public ApiParam setExtensions(Map<String, Object> extensions) {
		this.extensions = extensions;
		return this;
	}

	public String getFormat() {
		return format;
	}

	public ApiParam setFormat(String format) {
		this.format = format;
		return this;
	}

	public ApiParam setEnumInfoAndValues(EnumInfoAndValues enumInfoAndValues) {
		if (Objects.isNull(enumInfoAndValues)) {
			return this;
		}
		this.enumInfo = enumInfoAndValues.getEnumInfo();
		this.enumValues = enumInfoAndValues.getEnumValues();
		return this;
	}

	/**
	 * Returns a stream containing the current parameter and all its child parameters.
	 * @return a stream of the current parameter and all its descendants
	 */
	public Stream<ApiParam> flattenStream() {
		Stream<ApiParam> selfStream = Stream.of(this);
		Stream<ApiParam> childrenStream = (this.children == null) ? Stream.empty()
				: this.children.stream().flatMap(ApiParam::flattenStream);
		return Stream.concat(selfStream, childrenStream);
	}

	/**
	 * Traverses this {@link ApiParam} and all its child parameters using a stack-based
	 * depth-first traversal, applying the given consumer to each parameter.
	 * @param consumer the operation to be performed on each {@link ApiParam}
	 */
	public void traverseAndConsume(Consumer<ApiParam> consumer) {
		// Initialize a stack with the current instance
		Stack<ApiParam> stack = new Stack<>();
		stack.push(this);

		// Traverse the parameter tree
		while (!stack.isEmpty()) {
			// Pop the current parameter from the stack
			ApiParam current = stack.pop();
			// Apply the provided consumer to the current parameter
			consumer.accept(current);

			// If the current parameter has children, push them onto the stack
			if (CollectionUtil.isNotEmpty(current.getChildren())) {
				for (ApiParam child : current.getChildren()) {
					stack.push(child);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "ApiParam{" + "className='" + className + '\'' + ", id=" + id + ", field='" + field + '\'' + ", type='"
				+ type + '\'' + ", format='" + format + '\'' + ", fullyTypeName='" + fullyTypeName + '\'' + ", desc='"
				+ desc + '\'' + ", required=" + required + ", version='" + version + '\'' + ", pid=" + pid
				+ ", pathParam=" + pathParam + ", queryParam=" + queryParam + ", value='" + value + '\'' + ", children="
				+ children + ", hasItems=" + hasItems + ", enumValues=" + enumValues + ", enumInfo=" + enumInfo
				+ ", maxLength='" + maxLength + '\'' + ", configParam=" + configParam + ", selfReferenceLoop="
				+ selfReferenceLoop + ", extensions=" + extensions + '}';
	}

}
