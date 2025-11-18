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

/**
 * @author yu.sun on 2024/6/9
 * @since 3.0.5
 */
public class ExceptionAdviceMethod {

	private String status;

	private boolean exceptionHandlerMethod;

	public static ExceptionAdviceMethod builder() {
		return new ExceptionAdviceMethod();
	}

	public String getStatus() {
		return status;
	}

	public ExceptionAdviceMethod setStatus(String status) {
		this.status = status;
		return this;
	}

	public boolean isExceptionHandlerMethod() {
		return exceptionHandlerMethod;
	}

	public ExceptionAdviceMethod setExceptionHandlerMethod(boolean exceptionHandlerMethod) {
		this.exceptionHandlerMethod = exceptionHandlerMethod;
		return this;
	}

}
