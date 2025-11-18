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
package io.github.smartdoc.constants;

/**
 * gRPC Method Type Enum.
 *
 * @author linwumingshi
 * @since 3.0.7
 */
public enum GrpcMethodTypeEnum {

	/**
	 * Unary RPC: a single request followed by a single response.
	 */
	UNARY("Unary RPC", false, false),

	/**
	 * Server Streaming RPC: a single request followed by multiple server responses.
	 */
	SERVER_STREAMING("Server Streaming RPC", false, true),

	/**
	 * Client Streaming RPC: multiple client requests followed by a single server
	 * response.
	 */
	CLIENT_STREAMING("Client Streaming RPC", true, false),

	/**
	 * Bidirectional Streaming RPC: multiple client requests and multiple server
	 * responses.
	 */
	BIDIRECTIONAL_STREAMING("Bidirectional Streaming RPC", true, true);

	/**
	 * Type
	 */
	private final String type;

	/**
	 * Request Streaming
	 */
	private final boolean requestStreaming;

	/**
	 * Response Streaming
	 */
	private final boolean responseStreaming;

	/**
	 * Constructor
	 * @param type type
	 * @param requestStreaming Request Streaming
	 * @param responseStreaming Response Streaming
	 */
	GrpcMethodTypeEnum(String type, boolean requestStreaming, boolean responseStreaming) {
		this.type = type;
		this.requestStreaming = requestStreaming;
		this.responseStreaming = responseStreaming;
	}

	public String getType() {
		return type;
	}

	/**
	 * Request Streaming
	 * @return Request Streaming
	 */
	public boolean isRequestStreaming() {
		return requestStreaming;
	}

	/**
	 * Response Streaming
	 * @return Response Streaming
	 */
	public boolean isResponseStreaming() {
		return responseStreaming;
	}

	/**
	 * Get Type
	 * @param requestStreaming Request Streaming
	 * @param responseStreaming Response Streaming
	 * @return Type
	 */
	public static String fromStreaming(boolean requestStreaming, boolean responseStreaming) {
		for (GrpcMethodTypeEnum type : values()) {
			if (type.requestStreaming == requestStreaming && type.responseStreaming == responseStreaming) {
				return type.getType();
			}
		}
		// default type
		return UNARY.getType();
	}

}
