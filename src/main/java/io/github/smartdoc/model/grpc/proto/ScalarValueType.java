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
 * Represents a scalar value type definition from JSON.
 *
 * @author linwumingshi
 */
public class ScalarValueType implements Serializable {

	private static final long serialVersionUID = -849952410928578937L;

	/**
	 * The protobuf type of the scalar value.
	 */
	private String protoType;

	/**
	 * Notes or additional information about the scalar value type.
	 */
	private String notes;

	/**
	 * The C++ type corresponding to the scalar value type.
	 */
	private String cppType;

	/**
	 * The C# type corresponding to the scalar value type.
	 */
	private String csType;

	/**
	 * The Go type corresponding to the scalar value type.
	 */
	private String goType;

	/**
	 * The Java type corresponding to the scalar value type.
	 */
	private String javaType;

	/**
	 * The PHP type corresponding to the scalar value type.
	 */
	private String phpType;

	/**
	 * The Python type corresponding to the scalar value type.
	 */
	private String pythonType;

	/**
	 * The Ruby type corresponding to the scalar value type.
	 */
	private String rubyType;

	public static ScalarValueType builder() {
		return new ScalarValueType();
	}

	public String getProtoType() {
		return protoType;
	}

	public ScalarValueType setProtoType(String protoType) {
		this.protoType = protoType;
		return this;
	}

	public String getNotes() {
		return notes;
	}

	public ScalarValueType setNotes(String notes) {
		this.notes = notes;
		return this;
	}

	public String getCppType() {
		return cppType;
	}

	public ScalarValueType setCppType(String cppType) {
		this.cppType = cppType;
		return this;
	}

	public String getCsType() {
		return csType;
	}

	public ScalarValueType setCsType(String csType) {
		this.csType = csType;
		return this;
	}

	public String getGoType() {
		return goType;
	}

	public ScalarValueType setGoType(String goType) {
		this.goType = goType;
		return this;
	}

	public String getJavaType() {
		return javaType;
	}

	public ScalarValueType setJavaType(String javaType) {
		this.javaType = javaType;
		return this;
	}

	public String getPhpType() {
		return phpType;
	}

	public ScalarValueType setPhpType(String phpType) {
		this.phpType = phpType;
		return this;
	}

	public String getPythonType() {
		return pythonType;
	}

	public ScalarValueType setPythonType(String pythonType) {
		this.pythonType = pythonType;
		return this;
	}

	public String getRubyType() {
		return rubyType;
	}

	public ScalarValueType setRubyType(String rubyType) {
		this.rubyType = rubyType;
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
		ScalarValueType that = (ScalarValueType) o;
		return Objects.equals(protoType, that.protoType) && Objects.equals(notes, that.notes)
				&& Objects.equals(cppType, that.cppType) && Objects.equals(csType, that.csType)
				&& Objects.equals(goType, that.goType) && Objects.equals(javaType, that.javaType)
				&& Objects.equals(phpType, that.phpType) && Objects.equals(pythonType, that.pythonType)
				&& Objects.equals(rubyType, that.rubyType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(protoType, notes, cppType, csType, goType, javaType, phpType, pythonType, rubyType);
	}

}
