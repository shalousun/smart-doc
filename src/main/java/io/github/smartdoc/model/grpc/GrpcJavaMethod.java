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
package io.github.smartdoc.model.grpc;

import io.github.smartdoc.constants.GrpcMethodTypeEnum;
import io.github.smartdoc.model.JavadocJavaMethod;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.impl.DefaultJavaClass;

import java.io.Serializable;

/**
 * gRPC java method.
 *
 * @author linwumingshi
 * @since 3.0.7
 */
public class GrpcJavaMethod extends JavadocJavaMethod implements Serializable {

	private static final long serialVersionUID = -542118595043785188L;

	/**
	 * grpc method type.
	 *
	 * @see GrpcMethodTypeEnum
	 */
	private String methodType;

	public String getMethodType() {
		return methodType;
	}

	public GrpcJavaMethod setMethodType(String methodType) {
		this.methodType = methodType;
		return this;
	}

	@Override
	public JavaClass getDeclaringClass() {
		return new DefaultJavaClass(this.getName());
	}

}
