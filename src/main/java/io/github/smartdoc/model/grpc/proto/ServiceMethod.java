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
 * Represents a method within a service from JSON.
 *
 * @author linwumingshi
 */
public class ServiceMethod implements Serializable {

	private static final long serialVersionUID = 8899124161932913904L;

	/**
	 * The name of the method.
	 */
	private String name;

	/**
	 * Description or additional information about the method.
	 */
	private String description;

	/**
	 * The request type of the method.
	 */
	private String requestType;

	/**
	 * The long request type of the method.
	 */
	private String requestLongType;

	/**
	 * The full request type of the method.
	 */
	private String requestFullType;

	/**
	 * Indicates if the method supports request streaming.
	 */
	private boolean requestStreaming;

	/**
	 * The response type of the method.
	 */
	private String responseType;

	/**
	 * The long response type of the method.
	 */
	private String responseLongType;

	/**
	 * The full response type of the method.
	 */
	private String responseFullType;

	/**
	 * Indicates if the method supports response streaming.
	 */
	private boolean responseStreaming;

	public static ServiceMethod builder() {
		return new ServiceMethod();
	}

	public String getName() {
		return name;
	}

	public ServiceMethod setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ServiceMethod setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getRequestType() {
		return requestType;
	}

	public ServiceMethod setRequestType(String requestType) {
		this.requestType = requestType;
		return this;
	}

	public String getRequestLongType() {
		return requestLongType;
	}

	public ServiceMethod setRequestLongType(String requestLongType) {
		this.requestLongType = requestLongType;
		return this;
	}

	public String getRequestFullType() {
		return requestFullType;
	}

	public ServiceMethod setRequestFullType(String requestFullType) {
		this.requestFullType = requestFullType;
		return this;
	}

	public boolean isRequestStreaming() {
		return requestStreaming;
	}

	public ServiceMethod setRequestStreaming(boolean requestStreaming) {
		this.requestStreaming = requestStreaming;
		return this;
	}

	public String getResponseType() {
		return responseType;
	}

	public ServiceMethod setResponseType(String responseType) {
		this.responseType = responseType;
		return this;
	}

	public String getResponseLongType() {
		return responseLongType;
	}

	public ServiceMethod setResponseLongType(String responseLongType) {
		this.responseLongType = responseLongType;
		return this;
	}

	public String getResponseFullType() {
		return responseFullType;
	}

	public ServiceMethod setResponseFullType(String responseFullType) {
		this.responseFullType = responseFullType;
		return this;
	}

	public boolean isResponseStreaming() {
		return responseStreaming;
	}

	public ServiceMethod setResponseStreaming(boolean responseStreaming) {
		this.responseStreaming = responseStreaming;
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
		ServiceMethod that = (ServiceMethod) o;
		return requestStreaming == that.requestStreaming && responseStreaming == that.responseStreaming
				&& Objects.equals(name, that.name) && Objects.equals(description, that.description)
				&& Objects.equals(requestType, that.requestType)
				&& Objects.equals(requestLongType, that.requestLongType)
				&& Objects.equals(requestFullType, that.requestFullType)
				&& Objects.equals(responseType, that.responseType)
				&& Objects.equals(responseLongType, that.responseLongType)
				&& Objects.equals(responseFullType, that.responseFullType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, description, requestType, requestLongType, requestFullType, requestStreaming,
				responseType, responseLongType, responseFullType, responseStreaming);
	}

}
