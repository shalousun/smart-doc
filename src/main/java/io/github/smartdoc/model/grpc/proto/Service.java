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
 * Represents a service definition from JSON.
 *
 * @author linwumingshi
 */
public class Service implements Serializable {

	private static final long serialVersionUID = -7186688057069473270L;

	/**
	 * The name of the service.
	 */
	private String name;

	/**
	 * The long name of the service.
	 */
	private String longName;

	/**
	 * The full name of the service.
	 */
	private String fullName;

	/**
	 * Description or additional information about the service.
	 */
	private String description;

	/**
	 * List of methods defined within the service.
	 */
	private List<ServiceMethod> methods;

	public static Service builder() {
		return new Service();
	}

	public String getName() {
		return name;
	}

	public Service setName(String name) {
		this.name = name;
		return this;
	}

	public String getLongName() {
		return longName;
	}

	public Service setLongName(String longName) {
		this.longName = longName;
		return this;
	}

	public String getFullName() {
		return fullName;
	}

	public Service setFullName(String fullName) {
		this.fullName = fullName;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Service setDescription(String description) {
		this.description = description;
		return this;
	}

	public List<ServiceMethod> getMethods() {
		return methods;
	}

	public Service setMethods(List<ServiceMethod> methods) {
		this.methods = methods;
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
		Service service = (Service) o;
		return Objects.equals(name, service.name) && Objects.equals(longName, service.longName)
				&& Objects.equals(fullName, service.fullName) && Objects.equals(description, service.description)
				&& Objects.equals(methods, service.methods);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, longName, fullName, description, methods);
	}

}
