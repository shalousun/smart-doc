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

import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaParameter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Doc Java Parameter
 *
 * @author yu3.sun on 2022/10/15
 * @since 2.6.0
 */
public class DocJavaParameter implements Serializable {

	private JavaParameter javaParameter;

	private String genericCanonicalName;

	private String genericFullyQualifiedName;

	private String fullyQualifiedName;

	private String typeValue;

	private List<JavaAnnotation> annotations;

	public JavaParameter getJavaParameter() {
		return javaParameter;
	}

	public void setJavaParameter(JavaParameter javaParameter) {
		this.javaParameter = javaParameter;
	}

	public String getGenericCanonicalName() {
		return genericCanonicalName;
	}

	public void setGenericCanonicalName(String genericCanonicalName) {
		this.genericCanonicalName = genericCanonicalName;
	}

	public String getGenericFullyQualifiedName() {
		return genericFullyQualifiedName;
	}

	public void setGenericFullyQualifiedName(String genericFullyQualifiedName) {
		this.genericFullyQualifiedName = genericFullyQualifiedName;
	}

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}

	public String getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}

	public List<JavaAnnotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<JavaAnnotation> annotations) {
		this.annotations = annotations;
	}

	@Override
	public String toString() {
		return "DocJavaParameter{" + "javaParameter=" + javaParameter + ", genericCanonicalName='"
				+ genericCanonicalName + '\'' + ", genericFullyQualifiedName='" + genericFullyQualifiedName + '\''
				+ ", fullyQualifiedName='" + fullyQualifiedName + '\'' + ", typeValue='" + typeValue + '\''
				+ ", annotations=" + annotations + '}';
	}

	@Override
	public int hashCode() {
		int result = javaParameter != null ? javaParameter.hashCode() : 0;
		result = 31 * result + (genericCanonicalName != null ? genericCanonicalName.hashCode() : 0);
		result = 31 * result + (genericFullyQualifiedName != null ? genericFullyQualifiedName.hashCode() : 0);
		result = 31 * result + (fullyQualifiedName != null ? fullyQualifiedName.hashCode() : 0);
		result = 31 * result + (typeValue != null ? typeValue.hashCode() : 0);
		result = 31 * result + (annotations != null ? annotations.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		DocJavaParameter that = (DocJavaParameter) obj;
		return (Objects.equals(javaParameter, that.javaParameter))
				&& (Objects.equals(genericCanonicalName, that.genericCanonicalName))
				&& (Objects.equals(genericFullyQualifiedName, that.genericFullyQualifiedName))
				&& (Objects.equals(fullyQualifiedName, that.fullyQualifiedName))
				&& (Objects.equals(typeValue, that.typeValue)) && (Objects.equals(annotations, that.annotations));
	}

}
