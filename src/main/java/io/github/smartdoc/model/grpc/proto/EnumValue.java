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
import java.util.Objects;

/**
 * Represents an enum value definition from JSON.
 *
 * @author linwumingshi
 */
public class EnumValue implements Serializable {

	private static final long serialVersionUID = 5233061948196060934L;

	/**
	 * The name of the enum value.
	 */
	private String name;

	/**
	 * The number associated with the enum value (optional, not used in current JSON
	 * structure).
	 */
	private int number;

	/**
	 * The description of the enum value.
	 */
	private String description;

	public static EnumValue builder() {
		return new EnumValue();
	}

	public String getName() {
		return name;
	}

	public EnumValue setName(String name) {
		this.name = name;
		return this;
	}

	public int getNumber() {
		return number;
	}

	public EnumValue setNumber(int number) {
		this.number = number;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public EnumValue setDescription(String description) {
		this.description = description;
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
		EnumValue enumValue = (EnumValue) o;
		return number == enumValue.number && Objects.equals(name, enumValue.name)
				&& Objects.equals(description, enumValue.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, number, description);
	}

}
