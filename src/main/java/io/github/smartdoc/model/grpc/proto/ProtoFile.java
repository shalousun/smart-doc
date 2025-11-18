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
import java.util.List;
import java.util.Objects;

/**
 * Represents a protobuf file definition from JSON.
 *
 * @author linwumingshi
 */
public class ProtoFile implements Serializable {

	private static final long serialVersionUID = 2613283301423850262L;

	/**
	 * The name of the protobuf file.
	 */
	private String name;

	/**
	 * Description or additional information about the protobuf file.
	 */
	private String description;

	/**
	 * The package name of the protobuf file.
	 */
	@SerializedName("package")
	private String packageName;

	/**
	 * Indicates if the protobuf file has enums (optional, not used in current JSON
	 * structure).
	 */
	private boolean hasEnums;

	/**
	 * Indicates if the protobuf file has extensions (optional, not used in current JSON
	 * structure).
	 */
	private boolean hasExtensions;

	/**
	 * Indicates if the protobuf file has messages.
	 */
	private boolean hasMessages;

	/**
	 * Indicates if the protobuf file has services.
	 */
	private boolean hasServices;

	/**
	 * List of enums defined within the protobuf file (optional, not used in current JSON
	 * structure).
	 */
	private List<EnumDefinition> enums;

	/**
	 * List of extensions defined within the protobuf file (optional, not used in current
	 * JSON structure).
	 */
	private List<Object> extensions;

	/**
	 * List of messages defined within the protobuf file.
	 */
	private List<Message> messages;

	/**
	 * List of services defined within the protobuf file.
	 */
	private List<Service> services;

	public static ProtoFile builder() {
		return new ProtoFile();
	}

	public String getName() {
		return name;
	}

	public ProtoFile setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ProtoFile setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getPackageName() {
		return packageName;
	}

	public ProtoFile setPackageName(String packageName) {
		this.packageName = packageName;
		return this;
	}

	public boolean isHasEnums() {
		return hasEnums;
	}

	public ProtoFile setHasEnums(boolean hasEnums) {
		this.hasEnums = hasEnums;
		return this;
	}

	public boolean isHasExtensions() {
		return hasExtensions;
	}

	public ProtoFile setHasExtensions(boolean hasExtensions) {
		this.hasExtensions = hasExtensions;
		return this;
	}

	public boolean isHasMessages() {
		return hasMessages;
	}

	public ProtoFile setHasMessages(boolean hasMessages) {
		this.hasMessages = hasMessages;
		return this;
	}

	public boolean isHasServices() {
		return hasServices;
	}

	public ProtoFile setHasServices(boolean hasServices) {
		this.hasServices = hasServices;
		return this;
	}

	public List<EnumDefinition> getEnums() {
		return enums;
	}

	public ProtoFile setEnums(List<EnumDefinition> enums) {
		this.enums = enums;
		return this;
	}

	public List<Object> getExtensions() {
		return extensions;
	}

	public ProtoFile setExtensions(List<Object> extensions) {
		this.extensions = extensions;
		return this;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public ProtoFile setMessages(List<Message> messages) {
		this.messages = messages;
		return this;
	}

	public List<Service> getServices() {
		return services;
	}

	public ProtoFile setServices(List<Service> services) {
		this.services = services;
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
		;
		ProtoFile protoFile = (ProtoFile) o;
		return hasEnums == protoFile.hasEnums && hasExtensions == protoFile.hasExtensions
				&& hasMessages == protoFile.hasMessages && hasServices == protoFile.hasServices
				&& Objects.equals(name, protoFile.name) && Objects.equals(description, protoFile.description)
				&& Objects.equals(packageName, protoFile.packageName) && Objects.equals(enums, protoFile.enums)
				&& Objects.equals(extensions, protoFile.extensions) && Objects.equals(messages, protoFile.messages)
				&& Objects.equals(services, protoFile.services);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, description, packageName, hasEnums, hasExtensions, hasMessages, hasServices, enums,
				extensions, messages, services);
	}

}
