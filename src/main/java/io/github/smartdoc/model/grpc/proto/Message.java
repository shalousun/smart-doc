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

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Represents a message definition from JSON.
 *
 * @author linwumingshi
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 8050823453899405074L;

	/**
	 * The name of the message.
	 */
	private String name;

	/**
	 * The long name of the message.
	 */
	private String longName;

	/**
	 * The full name of the message.
	 */
	private String fullName;

	/**
	 * Description or additional information about the message.
	 */
	private String description;

	/**
	 * Indicates if the message has extensions (optional, not used in current JSON
	 * structure).
	 */
	private boolean hasExtensions;

	/**
	 * Indicates if the message has fields.
	 */
	private boolean hasFields;

	/**
	 * Indicates if the message has oneofs (optional, not used in current JSON structure).
	 */
	private boolean hasOneofs;

	/**
	 * List of fields within the message.
	 */
	private List<MessageField> fields;

	public static Message builder() {
		return new Message();
	}

	public String getName() {
		return name;
	}

	public Message setName(String name) {
		this.name = name;
		return this;
	}

	public String getLongName() {
		return longName;
	}

	public Message setLongName(String longName) {
		this.longName = longName;
		return this;
	}

	public String getFullName() {
		return fullName;
	}

	public Message setFullName(String fullName) {
		this.fullName = fullName;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Message setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isHasExtensions() {
		return hasExtensions;
	}

	public Message setHasExtensions(boolean hasExtensions) {
		this.hasExtensions = hasExtensions;
		return this;
	}

	public boolean isHasFields() {
		return hasFields;
	}

	public Message setHasFields(boolean hasFields) {
		this.hasFields = hasFields;
		return this;
	}

	public boolean isHasOneofs() {
		return hasOneofs;
	}

	public Message setHasOneofs(boolean hasOneofs) {
		this.hasOneofs = hasOneofs;
		return this;
	}

	public List<MessageField> getFields() {
		return fields;
	}

	public Message setFields(List<MessageField> fields) {
		this.fields = fields;
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
		Message message = (Message) o;
		return hasExtensions == message.hasExtensions && hasFields == message.hasFields
				&& hasOneofs == message.hasOneofs && Objects.equals(name, message.name)
				&& Objects.equals(longName, message.longName) && Objects.equals(fullName, message.fullName)
				&& Objects.equals(description, message.description) && Objects.equals(fields, message.fields);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, longName, fullName, description, hasExtensions, hasFields, hasOneofs, fields);
	}

}
