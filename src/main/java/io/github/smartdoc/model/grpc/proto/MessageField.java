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
package io.github.smartdoc.model.grpc.proto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a field within a message from JSON.
 *
 * @author linwumingshi
 */
public class MessageField implements Serializable {

	private static final long serialVersionUID = 5659801848035252488L;

	/**
	 * The name of the field.
	 */
	private String name;

	/**
	 * Description or additional information about the field.
	 */
	private String description;

	/**
	 * The label of the field (optional, not used in current JSON structure).
	 */
	private String label;

	/**
	 * The type of the field.
	 */
	private String type;

	/**
	 * The long type of the field.
	 */
	private String longType;

	/**
	 * The full type of the field.
	 */
	private String fullType;

	/**
	 * Indicates if the field is a map type.
	 */
	@SerializedName("ismap")
	private boolean isMap;

	/**
	 * Indicates if the field is part of a oneof declaration.
	 */
	@SerializedName("isoneof")
	private boolean isOneof;

	/**
	 * The declaration of the oneof (optional, not used in current JSON structure).
	 */
	@SerializedName("oneofdecl")
	private String oneofDecl;

	/**
	 * The default value of the field (optional, not used in current JSON structure).
	 */
	private String defaultValue;

	public static MessageField builder() {
		return new MessageField();
	}

	public String getName() {
		return name;
	}

	public MessageField setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public MessageField setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public MessageField setLabel(String label) {
		this.label = label;
		return this;
	}

	public String getType() {
		return type;
	}

	public MessageField setType(String type) {
		this.type = type;
		return this;
	}

	public String getLongType() {
		return longType;
	}

	public MessageField setLongType(String longType) {
		this.longType = longType;
		return this;
	}

	public String getFullType() {
		return fullType;
	}

	public MessageField setFullType(String fullType) {
		this.fullType = fullType;
		return this;
	}

	public boolean isMap() {
		return isMap;
	}

	public MessageField setMap(boolean map) {
		isMap = map;
		return this;
	}

	public boolean isOneof() {
		return isOneof;
	}

	public MessageField setOneof(boolean oneof) {
		isOneof = oneof;
		return this;
	}

	public String getOneofDecl() {
		return oneofDecl;
	}

	public MessageField setOneofDecl(String oneofDecl) {
		this.oneofDecl = oneofDecl;
		return this;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public MessageField setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MessageField that = (MessageField) o;
		return isMap == that.isMap && isOneof == that.isOneof && Objects.equals(name, that.name)
				&& Objects.equals(description, that.description) && Objects.equals(label, that.label)
				&& Objects.equals(type, that.type) && Objects.equals(longType, that.longType)
				&& Objects.equals(fullType, that.fullType) && Objects.equals(oneofDecl, that.oneofDecl)
				&& Objects.equals(defaultValue, that.defaultValue);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, description, label, type, longType, fullType, isMap, isOneof, oneofDecl,
				defaultValue);
	}

}
