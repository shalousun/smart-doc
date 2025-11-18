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

/**
 * This class handles operations related to exception advice annotations.
 * It provides a container for storing and accessing the name of the exception advice annotation.
 * @author yu 2024/6/8
 */
package io.github.smartdoc.model.annotation;

/**
 * @author yu.sun on 2024/6/9
 * @since 3.0.5
 */
public class ExceptionAdviceAnnotation {

	private String annotationName;

	public static ExceptionAdviceAnnotation builder() {
		return new ExceptionAdviceAnnotation();
	}

	public String getAnnotationName() {
		return annotationName;
	}

	public ExceptionAdviceAnnotation setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
		return this;
	}

}
