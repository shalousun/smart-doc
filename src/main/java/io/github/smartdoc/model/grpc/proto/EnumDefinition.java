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
 * Represents an enum definition from JSON.
 *
 * @author linwumingshi
 */
public class EnumDefinition implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The name of the enum.
	 */
	private String name;

	/**
	 * The long name of the enum.
	 */
	private String longName;

	/**
	 * The full name of the enum.
	 */
	private String fullName;

	/**
	 * The description of the enum.
	 */
	private String description;

	/**
	 * The values of the enum.
	 */
	private List<EnumValue> values;

	public static EnumDefinition builder() {
		return new EnumDefinition();
	}

	public String getName() {
		return name;
	}

	public EnumDefinition setName(String name) {
		this.name = name;
		return this;
	}

	public String getLongName() {
		return longName;
	}

	public EnumDefinition setLongName(String longName) {
		this.longName = longName;
		return this;
	}

	public String getFullName() {
		return fullName;
	}

	public EnumDefinition setFullName(String fullName) {
		this.fullName = fullName;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public EnumDefinition setDescription(String description) {
		this.description = description;
		return this;
	}

	public List<EnumValue> getValues() {
		return values;
	}

	public EnumDefinition setValues(List<EnumValue> values) {
		this.values = values;
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
		EnumDefinition that = (EnumDefinition) o;
		return Objects.equals(name, that.name) && Objects.equals(longName, that.longName)
				&& Objects.equals(fullName, that.fullName) && Objects.equals(description, that.description)
				&& Objects.equals(values, that.values);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, longName, fullName, description, values);
	}

}